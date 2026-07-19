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
public class UserRequestResponseMsg extends Message {
	public static final short CMD_CODE = (short) 0x8003;
	public static final int CMD_SIZE = 536;
	
	protected UserRequestResponseMsg(byte[] data, Channel channel) {
		super(data, channel);
		msgHead.setMsgLen(MsgHead.HEAD_BYTE_LEN + CMD_SIZE - 4);
		msgHead.setCmdCode(CMD_CODE);
		dataWrap.put(MsgHead.HEAD_BYTE_LEN + 16, (byte) 1); // bAuthrity
		dataWrap.put(MsgHead.HEAD_BYTE_LEN + 17, (byte) 1); // bConfirmAuthrity
	}
	
	public static UserRequestResponseMsg create(byte[] msgHead, Channel channel) {
		byte[] data = new byte[MsgHead.HEAD_BYTE_LEN + CMD_SIZE];
		System.arraycopy(msgHead, 0, data, 0, MsgHead.HEAD_BYTE_LEN);
		return new UserRequestResponseMsg(data, channel);
	}
}
