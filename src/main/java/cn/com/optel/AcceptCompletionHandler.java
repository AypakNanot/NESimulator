/**
 * AcceptyCompletionHandler.java 
 *
 * date          author
 * ───────────────────────
 * 2015年6月15日      Administrator
 *
 * Copyright (c) 2015, H-OPTEL All Rights Reserved.
 */

package cn.com.optel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

import cn.com.optel.comm.Channel;
import cn.com.optel.util.Tool;

/**
 * ClassName: AcceptyCompletionHandler
 * <ul>
 * <li>(这里描述这个类的功能)</li>
 * </ul>
 * 
 * @author Administrator
 * @see
 */
public class AcceptCompletionHandler implements
		CompletionHandler<AsynchronousSocketChannel, AsynchronousServerSocketChannel> {
	@Override
	public void completed(AsynchronousSocketChannel sc, AsynchronousServerSocketChannel ssc) {
		try {
			InetSocketAddress neAddress = (InetSocketAddress) sc.getLocalAddress();
			byte[] ip = neAddress.getAddress().getAddress();
			int port = neAddress.getPort();
			int neId = (ip[2] << 24) | (ip[3] << 16) | (port & 0xFFFF);
			Channel channel = ServerSimulator.getNeChannelMap().get(neId);
			if (channel == null || !channel.getSocketChannel().isOpen()) {
				channel = new Channel(sc, ssc);
				ServerSimulator.getNeChannelMap().put(channel.getNeId(), channel);
				Tool.LOG.info(channel + " start to login...");
				channel.getSocketChannel().read(channel.getReadBuffer(), channel,
						channel.getReadHandler());
			}
		} catch (IOException e) {
			Tool.LOG.error(e.getMessage(), e);
		}
	}
	
	@Override
	public void failed(Throwable exc, AsynchronousServerSocketChannel attachment) {
		Tool.LOG.error(attachment, exc);
	}
	
}
