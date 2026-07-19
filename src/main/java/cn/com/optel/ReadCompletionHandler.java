/**
 * ReadCompletionHandler.java 
 *
 * date          author
 * ───────────────────────
 * 2015年6月15日      Administrator
 *
 * Copyright (c) 2015, H-OPTEL All Rights Reserved.
 */

package cn.com.optel;

import java.nio.channels.CompletionHandler;

import cn.com.optel.comm.Channel;
import cn.com.optel.util.Tool;

/**
 * ClassName: ReadCompletionHandler
 * <ul>
 * <li>(这里描述这个类的功能)</li>
 * </ul>
 * 
 * @author Administrator
 * @see
 */
public class ReadCompletionHandler implements CompletionHandler<Integer, Channel> {
	@Override
	public void completed(Integer result, Channel channel) {
		channel.read(channel.getReadBuffer());
		channel.getSocketChannel().read(channel.getReadBuffer(), channel, this);
	}
	
	@Override
	public void failed(Throwable exc, Channel channel) {
		Tool.LOG.fatal(channel + " read failure.", exc);
		channel.close();
	}
	
}
