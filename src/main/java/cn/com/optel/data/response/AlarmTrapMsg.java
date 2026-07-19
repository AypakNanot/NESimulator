/**
 * AlarmTrapSeg.java 
 *
 * date          author
 * ───────────────────────
 * 2015年3月23日      Administrator
 *
 * Copyright (c) 2015, H-OPTEL All Rights Reserved.
 */

package cn.com.optel.data.response;

import java.util.Calendar;
import java.util.Date;

import cn.com.optel.comm.Channel;
import cn.com.optel.data.MsgHead;
import cn.com.optel.data.TrapMessage;

/**
 * ClassName: AlarmTrapSeg
 * <ul>
 * <li>(这里描述这个类的功能)</li>
 * </ul>
 * 
 * @author Administrator
 * @see
 */
public class AlarmTrapMsg extends TrapMessage {
	public static final short CMD_CODE = (short) 0x1501;
//	自动性能trap上报命令嘛
//	public static final short CMD_CODE = (short) 0x0D01;
	public static final int LENGTH_PER_ALARM = 20;
	
	protected AlarmTrapMsg(byte[] data, Channel channel) {
		super(data, channel);
		msgHead.setMsgLen(data.length - 4);
		msgHead.setCmdCode(CMD_CODE);
		// alarmTime
		long time = (new Date().getTime() + Calendar.getInstance().get(Calendar.ZONE_OFFSET)) / 1000;
		int dataNum = (data.length - MsgHead.HEAD_BYTE_LEN) / LENGTH_PER_ALARM;
		for (int i = 0; i < dataNum; i++) {
			int timePos = MsgHead.HEAD_BYTE_LEN + i * LENGTH_PER_ALARM + 16;
			dataWrap.putInt(timePos, (int) time);
		}
	}
	
	public static AlarmTrapMsg create(byte[] msgData, Channel channel, int alarmState) {
		byte[] data = new byte[MsgHead.HEAD_BYTE_LEN + msgData.length];
		int i, num = msgData.length/LENGTH_PER_ALARM;
		for(i = 0; i < num; i++)
		{
			msgData[num*i+12] = (byte)alarmState;
		}
		System.arraycopy(msgData, 0, data, MsgHead.HEAD_BYTE_LEN, msgData.length);
		return new AlarmTrapMsg(data, channel);
	}
}
