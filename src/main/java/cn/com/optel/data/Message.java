/**
 *
 */
package cn.com.optel.data;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import cn.com.optel.comm.Channel;

/**
 * @author qudy
 */
public class Message {
	/**
	 * 用户发送的消息数据，通过此类进行处理过后，此内存区中的数据将发生变化。
	 */
	byte[] msgData;
	
	/**
	 * 发送，接收当前消息的通道
	 */
	Channel channel;
	
	/**
	 * 消息头部
	 */
	protected MsgHead msgHead;
	
	protected ByteBuffer dataWrap;
	
	protected Message(byte[] data, Channel ch) {
		msgData = data;
		channel = ch;
		msgHead = MsgHead.wrap(msgData);
		msgHead.setVersion();
		msgHead.setSource(channel.getNeId());
		msgHead.setTarget(channel.getEmsId());
		dataWrap = ByteBuffer.wrap(data);
	}
	
	protected Message(byte[] data, Channel ch, boolean wrapPlaceHolder) {
		msgData = data;
		channel = ch;
		msgHead = MsgHead.wrap(msgData);
		dataWrap = ByteBuffer.wrap(data);
	}
	
	public static Message create(byte[] data, Channel ch) {
		return new Message(data, ch);
	}
	
	public static Message wrap(byte[] data, Channel ch) {
		return new Message(data, ch, true);
	}
	
	public List<Segment> splitSeg() {
		List<Segment> retVal = new ArrayList<Segment>();
		int segCnt = (msgData.length + FirstSeg.SEG_SIZE - 1) / FirstSeg.SEG_SIZE;
		int segIdx = 0;
		
		FirstSeg first = new FirstSeg().setInfoSeq(msgHead.getSeqNum()).setInfoLen(msgData.length)
				.setSegCount(segCnt);
		retVal.add(first);
		
		for (segIdx = 0; segIdx < (segCnt - 1); segIdx++) {
			DataSeg data = new DataSeg(msgData, segIdx * first.getSegSize(), first.getSegSize())
					.setInfoSeq(first.getInfoSeq()).setDataIdx(segIdx);
			retVal.add(data);
		}
		
		DataSeg lastData = new DataSeg(msgData, segIdx * first.getSegSize(), msgData.length
				- segIdx * first.getSegSize()).setInfoSeq(first.getInfoSeq()).setDataIdx(segIdx);
		retVal.add(lastData);
		
		return retVal;
	}
	
	public static Message combineMsg(FirstSeg firstSeg, List<DataSeg> dataSegs, Channel ch) {
		Message retVal = null;
		if (dataSegs.size() == firstSeg.getSegCount()) {
			int readIdx = 0;
			int curPos = 0;
			byte[] msgBuf = new byte[firstSeg.getInfoLen()];
			
			for (readIdx = 0; readIdx < firstSeg.getSegCount(); readIdx++) {
				for (DataSeg seg : dataSegs) {
					if (seg.getDataIdx() == readIdx) {
						System.arraycopy(seg.getSegData(), 18, msgBuf, curPos, seg.getDataLen());
						curPos += seg.getDataLen();
					}
				}
			}
			
			retVal = Message.wrap(msgBuf, ch);
		}
		return retVal;
	}
	
	public byte[] getMsgData() {
		return msgData;
	}
	
	public Channel getChannel() {
		return channel;
	}
	
	public MsgHead getMsgHead() {
		return msgHead;
	}
	
	public ByteBuffer getDataWrap() {
		return dataWrap;
	}
}
