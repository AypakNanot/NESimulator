package cn.com.optel.data.response;

import java.util.Calendar;
import java.util.Date;

import cn.com.optel.comm.Channel;
import cn.com.optel.data.Message;
import cn.com.optel.data.MsgHead;

public class AlarmSynResponseMsg extends Message {
	public static final short CMD_CODE = (short) 0x1409;
	public static final int LENGTH_ALM_SYN = 20;

	protected AlarmSynResponseMsg(byte[] data, Channel channel) {
		super(data, channel);
		msgHead.setMsgLen(data.length - 4);
		msgHead.setCmdCode(CMD_CODE);
		// alarmTime
		long time = (new Date().getTime() + Calendar.getInstance().get(
				Calendar.ZONE_OFFSET)) / 1000;
		int dataNum = (data.length - MsgHead.HEAD_BYTE_LEN) / LENGTH_ALM_SYN;
		for (int i = 0; i < dataNum; i++) {
			int timePos = MsgHead.HEAD_BYTE_LEN + i * LENGTH_ALM_SYN + 16;
			dataWrap.putInt(timePos, (int) time);
		}
	}

	public static AlarmSynResponseMsg create(byte[] headData, byte[] msgData,
			Channel channel) {
		int msgDatalength = 0;
		if(msgData != null)
			msgDatalength = msgData.length;
		byte[] data = new byte[MsgHead.HEAD_BYTE_LEN + msgDatalength];
		System.arraycopy(headData, 0, data, 0, MsgHead.HEAD_BYTE_LEN);
		if(msgData != null)
			System.arraycopy(msgData, 0, data, MsgHead.HEAD_BYTE_LEN,
					msgData.length);
		return new AlarmSynResponseMsg(data, channel);
	}
}
