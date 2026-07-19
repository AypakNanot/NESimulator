package cn.com.optel.comm;

import java.util.concurrent.Callable;

import cn.com.optel.data.Message;
import cn.com.optel.data.Segment;
import cn.com.optel.util.Tool;

/**
 * 使用通道同设备进行一次交互的抽象，这个类应该做为配合future使用的callable类。
 * 
 * @author 屈东苑
 * @version 1.0
 * @created 14-一月-2008 17:00:37
 */
public class Interaction implements Callable<SessionResult> {
	
	protected Channel channel;
	
	protected int timeout;
	
	/**
	 * 需要发送的数据
	 */
	protected byte[] sendData;
	
	protected Interaction() {
		
	}
	
	public Interaction(Channel ch, byte[] sendData, int timeout) {
		channel = ch;
		this.sendData = sendData;
		this.timeout = timeout;
	}
	
	public SessionResult call() throws Exception {
		SessionResult retVal = null;
		Message sendMsg = Message.create(sendData, channel);
		Tool.print(channel + " send:", sendMsg.getMsgData());
		Tool.LOG.debug(channel + " start to send "
				+ Integer.toHexString(sendMsg.getMsgHead().getCmdCode()));
		for (Segment seg : sendMsg.splitSeg()) {
			channel.write(seg.getSegData());
		}
		Tool.LOG.debug(channel + " send " + Integer.toHexString(sendMsg.getMsgHead().getCmdCode())
				+ " successfully.");
		return retVal;
	}
	
}