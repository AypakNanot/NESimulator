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
import cn.com.optel.data.Message;
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
public class PerfTrapMsg extends Message {
	
	public static final short CMD_CODE = (short) 0x0D01;
	public static final int LENGTH_PER_PERF = 24;
	
	protected PerfTrapMsg(byte[] data, Channel channel) {
		super(data, channel);
		msgHead.setMsgLen(data.length - 4);
		msgHead.setCmdCode(CMD_CODE);
		// perfTime
		long time = (new Date().getTime() + Calendar.getInstance().get(Calendar.ZONE_OFFSET)) / 1000;
		time = time - time % (15 * 60);
		int dataNum = (data.length - MsgHead.HEAD_BYTE_LEN) / LENGTH_PER_PERF;
		for (int i = 0; i < dataNum; i++) {
			int timePos = MsgHead.HEAD_BYTE_LEN + i * LENGTH_PER_PERF + 20;
			dataWrap.putInt(timePos, (int) time);
		}
	}
	
	public static HisPerfResponseMsg create(byte[] headData, byte[] msgData, Channel channel) {
		byte[] data = new byte[MsgHead.HEAD_BYTE_LEN + msgData.length];
		System.arraycopy(headData, 0, data, 0, MsgHead.HEAD_BYTE_LEN);
		System.arraycopy(msgData, 0, data, MsgHead.HEAD_BYTE_LEN, msgData.length);
		return new HisPerfResponseMsg(data, channel);
	}
}
