/**
 *
 */
package cn.com.optel.comm;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import cn.com.optel.ServerSimulator;
import cn.com.optel.data.ConfirmSeg;
import cn.com.optel.data.DataSeg;
import cn.com.optel.data.FirstSeg;
import cn.com.optel.data.Message;
import cn.com.optel.data.MsgHead;
import cn.com.optel.data.SegFactory;
import cn.com.optel.data.SegHead;
import cn.com.optel.data.Segment;
import cn.com.optel.data.response.AlarmSynResponseMsg;
import cn.com.optel.data.response.HisPerfResponseMsg;
import cn.com.optel.data.response.NEInstallResponseMsg;
import cn.com.optel.data.response.PerfResponseMsg;
import cn.com.optel.data.response.UserRequestResponseMsg;
import cn.com.optel.util.Tool;

public class Receiver extends Thread {
	
	public static final int RECEIVE_BUFFER_SIZE = 4096 * 2;
	
	Channel channel;
	private BlockingQueue<ByteBuffer> byteBufferQueue = new LinkedBlockingQueue<ByteBuffer>();
	private BlockingQueue<Message> rcvMsgs = new LinkedBlockingQueue<Message>();
	private final Map<Integer, List<DataSeg>> rcvDataMap = new HashMap<Integer, List<DataSeg>>();
	private final Map<Integer, FirstSeg> rcvFirstMap = new HashMap<Integer, FirstSeg>();
	private ByteBuffer readBuf = ByteBuffer.allocate(RECEIVE_BUFFER_SIZE);
	private final ReentrantLock bufLock = new ReentrantLock();
	
	public Receiver(Channel channel) {
		this.channel = channel;
	}
	
	@Override
	public void run() {
		try {
			process();
		} catch (Exception e) {
			Tool.LOG.fatal(e.getMessage(), e);
		}
	}
	
	void clear() {
		bufLock.lock();
		try {
			rcvDataMap.clear();
			rcvMsgs.clear();
			rcvFirstMap.clear();
			readBuf.rewind();
		} finally {
			bufLock.unlock();
		}
	}
	
	void process() throws Exception {
		bufLock.lock();
		try {
			while (true) {
				ByteBuffer buffer = byteBufferQueue.poll();
				if (buffer != null) {
					readBuf.put(buffer);
					checkSeg();
				} else {
					break;
				}
			}
		} catch (Throwable tr) {
			tr.printStackTrace();
		} finally {
			bufLock.unlock();
		}
	}
	
	/**
	 * 获取接收到的消息，阻塞式的，阻塞timeout秒的时间。
	 * 
	 * @param timeout
	 * @return
	 */
	Message take(int timeout) throws Exception {
		Message retVal = null;
		try {
			retVal = rcvMsgs.poll(timeout, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
		}
		
		if (retVal == null)
			throw new Exception("ReceiveTimeout");
		return retVal;
	}
	
	private void checkSeg() throws Exception {
		byte[] buf = readBuf.array();
		for (int i = 0; i < readBuf.position();) {
			if (readBuf.getInt(i) == SegHead.SEG_HEAD_FLAG) {
				if (SegFactory.validateSeg(buf, i, readBuf.position())) {
					Segment seg = SegFactory.spawnSeg(buf, i);
					
					dealWith(seg);
					
					System.arraycopy(buf, i + seg.getSize(), buf, 0,
							readBuf.position() - (i + seg.getSize()));
					readBuf.position(readBuf.position() - (i + seg.getSize()));
					i = 0;
					continue;
				}
			}
			
			i++;
		}
	}
	
	private void dealWith(Segment seg) throws Exception {
		
		switch (seg.segType()) {
		
		case Segment.SEG_TYPE_DATA:
			DataSeg dataSeg = (DataSeg) seg;
			
			/*
			 * 接收到期待的数据帧，则添加到数据帧缓冲中
			 */
			FirstSeg firstSeg = rcvFirstMap.get(dataSeg.getInfoSeq());
			
			if (firstSeg != null) {
				List<DataSeg> revDataSegs = rcvDataMap.get(dataSeg.getInfoSeq());
				if (revDataSegs == null) {
					revDataSegs = new LinkedList<DataSeg>();
				}
				revDataSegs.add(dataSeg);
				rcvDataMap.put(dataSeg.getInfoSeq(), revDataSegs);
				
				/*
				 * 最后一帧
				 */
				if (dataSeg.getDataIdx() >= firstSeg.getSegCount() - 1) {
					sendConfirm(firstSeg);
					
					combineMsg(firstSeg, revDataSegs);
					rcvFirstMap.remove(dataSeg.getInfoSeq());
					rcvDataMap.remove(dataSeg.getInfoSeq());
					if (rcvFirstMap.size() > 20) {
						rcvFirstMap.clear();
						rcvDataMap.clear();
					}
				}
			}
			break;
		
		case Segment.SEG_TYPE_FIRST:
			FirstSeg firstSeg2 = (FirstSeg) seg;
			rcvDataMap.put(firstSeg2.getInfoSeq(), new LinkedList<DataSeg>());
			rcvFirstMap.put(firstSeg2.getInfoSeq(), firstSeg2);
			break;
		
		case Segment.SEG_TYPE_HEALTH:
			Tool.LOG.info(channel + " send heartbeat.");
			sendHealthConfirm();
			break;
		
		case Segment.SEG_TYPE_CONFIRM:
			// 抛弃数据确认帧
		case Segment.SEG_TYPE_HEALTH_CONFIRM:
			// 抛弃健康确认帧
			break;
		}
	}
	
	private void sendConfirm(FirstSeg firstSeg) {
		/*
		 * 需要发送确认帧
		 */
		if (firstSeg != null && (firstSeg.getReply() & 0x1) == 0x1) {
			ConfirmSeg confirm = new ConfirmSeg();
			try {
				channel.write(confirm.getSegData());
			} catch (Exception e) {
			}
		}
	}
	
	private void sendHealthConfirm() {
		try {
			channel.submit(new HealthInteraction(channel));
		} catch (Exception e) {
			Tool.LOG.fatal(e.getMessage(), e);
		}
	}
	
	private void combineMsg(FirstSeg firstSeg, List<DataSeg> dataSegs) throws Exception {
		Message msg = Message.combineMsg(firstSeg, dataSegs, channel);
		if (msg != null) {
			sendResponse(msg);
		}
	}
	
	private void sendResponse(Message msg) throws Exception {
		Tool.print(channel + " receive:", msg.getMsgData());
		MsgHead receiveHead = msg.getMsgHead();
		byte[] headData = receiveHead.getHeadData();
		short command = receiveHead.getCmdCode();
		byte[] msgData = null;
		int count = 0;
		Message sendMessage = Message.create(headData, channel);
		switch (command) {
		case UserRequestResponseMsg.CMD_CODE:
			channel.setEmsId(receiveHead.getSource());
			sendMessage = UserRequestResponseMsg.create(headData, channel);
			break;
		case NEInstallResponseMsg.CMD_CODE:
			sendMessage = NEInstallResponseMsg.create(headData, channel);
			break;
		case PerfResponseMsg.CMD_CODE:
			msgData = Tool.getPerfQueryResult();
			sendMessage = PerfResponseMsg.create(headData, msgData, channel);
			break;
		case AlarmSynResponseMsg.CMD_CODE:
			msgData = Tool.getAlmSynResult();
			sendMessage = AlarmSynResponseMsg.create(headData, msgData, channel);
			if(msgData != null)
			{
				count = msgData.length/AlarmSynResponseMsg.LENGTH_ALM_SYN;
			}
			break;
		case HisPerfResponseMsg.CMD_CODE:
			byte[] array = msg.getDataWrap().array();
			if(array == null || array.length <= 27){
				Tool.LOG.fatal("报文里面没有 报文体");
			}
			String param = "perfQueryResult";
			byte c = array[26];
			byte d = array[27];
			short sr = (short) (c << 8 | d);
			switch(sr){
			case 0X0101:
				param+=1;
				break;
			case 0X0C04:
				param+=2;
				break;
			case 0X0000:
				param+=3;
				break;
			case 0X3401:
				param+=4;
				break;
			case 0X3402:
				param+=5;
				break;
			case 0X3303:
				param+=6;
				break;
			case 0X3302:
				param+=7;
				break;
			default:
				break;
			}
			msgData = Tool.getPerfQueryResult(param);
			sendMessage = HisPerfResponseMsg.create(headData, msgData, channel);
			break;
		default:
			break;
		}
		Interaction interaction = new Interaction(channel, sendMessage.getMsgData(), 60);
		channel.submit(interaction);
		if (NEInstallResponseMsg.CMD_CODE == command) {
			Tool.LOG.fatal(channel + " login successfully.");
			Tool.LOG.fatal("connected ne count:" + ServerSimulator.getNeChannelMap().size());
		}
		if (AlarmSynResponseMsg.CMD_CODE == command) {
			Tool.LOG.debug(channel + " AlarmSyn successfully and alarm count is "+ count);
		}
	}
	
	public void put(ByteBuffer readBuffer) {
		try {
			ByteBuffer byteBuffer = ByteBuffer.allocate(readBuffer.limit());
			byteBuffer.put(readBuffer);
			byteBuffer.flip();
			byteBufferQueue.put(byteBuffer);
		} catch (InterruptedException e) {
			Tool.LOG.fatal(e.getMessage(), e);
		}
	}
	
}
