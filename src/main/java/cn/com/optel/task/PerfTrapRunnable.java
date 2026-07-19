/**
 * NETrapRunnable.java 
 *
 * date          author
 * ───────────────────────
 * 2015年3月23日      Administrator
 *
 * Copyright (c) 2015, H-OPTEL All Rights Reserved.
 */

package cn.com.optel.task;

import cn.com.optel.comm.Channel;
import cn.com.optel.comm.Interaction;
import cn.com.optel.data.Message;
import cn.com.optel.data.response.PerfTrapMsg;
import cn.com.optel.util.Tool;

/**
 * ClassName: NETrapRunnable
 * <ul>
 * <li>(这里描述这个类的功能)</li>
 * </ul>
 * 
 * @author Administrator
 * @see
 */
public class PerfTrapRunnable implements Runnable {
	private Channel channel;
	
	public PerfTrapRunnable(Channel channel) {
		this.channel = channel;
	}
	
	@Override
	public void run() {
		try {
			byte[] msgData = Tool.getPerfTrap();
			Message trapMessage = PerfTrapMsg.create(msgData, channel);
			Interaction action = new Interaction(channel, trapMessage.getMsgData(), 60);
			Tool.LOG.fatal(channel + "" + msgData.length/20);
			Tool.print(channel+"", trapMessage.getMsgData());
			channel.submit(action);
		} catch (Exception e) {
			Tool.LOG.fatal(e.getMessage(), e);
		}
	}
}
