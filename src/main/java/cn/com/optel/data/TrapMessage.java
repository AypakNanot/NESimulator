/**
 * TrapMessage.java 
 *
 * date          author
 * ───────────────────────
 * 2015年3月23日      Administrator
 *
 * Copyright (c) 2015, H-OPTEL All Rights Reserved.
 */

package cn.com.optel.data;

import cn.com.optel.comm.Channel;

/**
 * ClassName: TrapMessage
 * <ul>
 * <li>(这里描述这个类的功能)</li>
 * </ul>
 * 
 * @author Administrator
 * @see
 */
public class TrapMessage extends Message {
	protected TrapMessage(byte[] data, Channel ch) {
		super(data, ch);
		msgHead.setSeqNum(0);
		msgHead.setTarget(0);
	}
}
