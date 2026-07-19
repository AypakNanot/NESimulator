package cn.com.optel.data.response;

import cn.com.optel.comm.Channel;
import cn.com.optel.data.Message;
import cn.com.optel.data.MsgHead;

public class GeneralResponseMsg extends Message{
	protected GeneralResponseMsg(byte[] data, Channel channel, short cmdCode) {
		super(data, channel);
		msgHead.setMsgLen(data.length - 4);
		msgHead.setCmdCode(cmdCode);
	}
	
	public static GeneralResponseMsg create(byte[] headData, byte[] msgData, Channel channel, short cmdCode) {
		byte[] data = new byte[MsgHead.HEAD_BYTE_LEN + msgData.length];
		System.arraycopy(headData, 0, data, 0, MsgHead.HEAD_BYTE_LEN);
		System.arraycopy(msgData, 0, data, MsgHead.HEAD_BYTE_LEN, msgData.length);
		return new GeneralResponseMsg(data, channel, cmdCode);
	}
}
