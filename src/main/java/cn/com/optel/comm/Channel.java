/**
 * Channel.java 
 *
 * date          author
 * ───────────────────────
 * 2015年3月18日      Administrator
 *
 * Copyright (c) 2015, H-OPTEL All Rights Reserved.
 */

package cn.com.optel.comm;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.Future;

import cn.com.optel.ReadCompletionHandler;
import cn.com.optel.ServerSimulator;
import cn.com.optel.util.Tool;

/**
 * ClassName: Channel
 * <ul>
 * <li>(这里描述这个类的功能)</li>
 * </ul>
 * 
 * @author Administrator
 * @see
 */
public class Channel {
	
	private AsynchronousSocketChannel socketChannel;
	private AsynchronousServerSocketChannel serverSocketChannel;
	private Receiver receiver = new Receiver(this);
	private Worker worker = new Worker();
	private InetSocketAddress neAddress;
	private int neId;
	private int emsId;
	private ByteBuffer readBuffer = ByteBuffer.allocate(2048);
	private static ReadCompletionHandler readHandler = new ReadCompletionHandler();
	
	public Channel(AsynchronousSocketChannel socketChannel,
			AsynchronousServerSocketChannel serverSocketChannel) {
		this.socketChannel = socketChannel;
		this.serverSocketChannel = serverSocketChannel;
		try {
			neAddress = (InetSocketAddress) socketChannel.getLocalAddress();
			byte[] ip = neAddress.getAddress().getAddress();
			int port = neAddress.getPort();
			neId = (ip[2] << 24) | (ip[3] << 16) | (port & 0xFFFF);
		} catch (IOException e) {
			Tool.LOG.fatal(e.getMessage(), e);
		}
	}
	
	public ByteBuffer getReadBuffer() {
		return readBuffer;
	}
	
	public ReadCompletionHandler getReadHandler() {
		return readHandler;
	}
	
	public void read(ByteBuffer readBuffer) {
		if (readBuffer.position() != 0) {
			readBuffer.flip();
			receiver.put(readBuffer);
			readBuffer.clear();
			Processors.I().submit(receiver);
		} else {
			Tool.LOG.fatal(this + " receive:" + readBuffer);
			close();
		}
	}
	
	public void submit(Interaction interaction) throws Exception {
		worker.depute(interaction);
	}
	
	public void write(byte[] writeData) throws Exception {
		try {
			Future<Integer> future = socketChannel.write(ByteBuffer.wrap(writeData));
			future.get();
		} catch (Exception e) {
			Tool.LOG.fatal(this + " write failure", e);
			close();
		}
	}
	
	public int getNeId() {
		return neId;
	}
	
	public int getEmsId() {
		return emsId;
	}
	
	public void setEmsId(int emsId) {
		this.emsId = emsId;
	}
	
	public InetSocketAddress getNeAddress() {
		return neAddress;
	}
	
	public String toString() {
		return getNeAddress().toString();
	}
	
	public AsynchronousSocketChannel getSocketChannel() {
		return socketChannel;
	}
	
	public synchronized void close() {
		try {
			if (socketChannel.isOpen()) {
				socketChannel.close();
				ServerSimulator.getNeChannelMap().remove(neId);
				Tool.LOG.fatal("connected ne count:" + ServerSimulator.getNeChannelMap().size());
				serverSocketChannel.accept(serverSocketChannel, ServerSimulator.acceptHandler);
			}
		} catch (Exception e) {
			Tool.LOG.fatal(this + " close failure", e);
		}
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + neId;
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Channel other = (Channel) obj;
		if (neId != other.neId)
			return false;
		return true;
	}
}
