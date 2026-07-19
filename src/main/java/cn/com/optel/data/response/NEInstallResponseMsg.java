/**
 * UserRequestMsg.java 
 *
 * date          author
 * ───────────────────────
 * 2015年3月20日      Administrator
 *
 * Copyright (c) 2015, H-OPTEL All Rights Reserved.
 */

package cn.com.optel.data.response;

import cn.com.optel.comm.Channel;
import cn.com.optel.data.Message;
import cn.com.optel.data.MsgHead;

/**
 * ClassName: UserRequestMsg
 * <ul>
 * <li>(这里描述这个类的功能)</li>
 * </ul>
 * 
 * @author Administrator
 * @see
 */
public class NEInstallResponseMsg extends Message {
	public static final short CMD_CODE = (short) 0x2401;
	public static final int CMD_SIZE = 272;
	
	protected NEInstallResponseMsg(byte[] data, Channel channel) {
		super(data, channel);
		msgHead.setMsgLen(MsgHead.HEAD_BYTE_LEN + CMD_SIZE - 4);
		msgHead.setCmdCode(CMD_CODE);
		// lNE_ID
		dataWrap.putInt(MsgHead.HEAD_BYTE_LEN + 0, channel.getNeId());
		// wDeviceType MssEdge 25-2U
		dataWrap.putShort(MsgHead.HEAD_BYTE_LEN + 4, (short) 0x1713);
	}
	
	public static NEInstallResponseMsg create(byte[] msgHead, Channel channel) {
		byte[] data = new byte[MsgHead.HEAD_BYTE_LEN + CMD_SIZE];
		System.arraycopy(msgHead, 0, data, 0, MsgHead.HEAD_BYTE_LEN);
		return new NEInstallResponseMsg(data, channel);
	}
}
