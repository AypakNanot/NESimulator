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

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Arrays;

import cn.com.optel.comm.Channel;
import cn.com.optel.comm.Interaction;
import cn.com.optel.data.Message;
import cn.com.optel.data.response.AlarmTrapMsg;
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
public class AlarmTrapRunnable implements Runnable {
	private Channel channel;
	private int alarmState;//1-产生，2-消失
	
	public AlarmTrapRunnable(Channel channel,int alarmState) {
		this.channel = channel;
		this.alarmState = alarmState;
	}
	
	@Override
	public void run() {
		try {
			byte[] msgData = Tool.getAlarmTrap();
			String str;
			if(alarmState ==1 )
				str = " Generate alarm:";
			else
				str = " Disappear alarm:";
			Message trapMessage = AlarmTrapMsg.create(msgData, channel,alarmState);
			Interaction action = new Interaction(channel, trapMessage.getMsgData(), 60);
			Tool.LOG.fatal(channel + str + msgData.length/20);
			Tool.print(channel + str, trapMessage.getMsgData());
			channel.submit(action);
			
			//upd 发送
//			byte[] sendData = trapMessage.getMsgData();
//			byte[] nnData = new byte[95];
//			System.arraycopy(sendData, 24, nnData, 0, 95);
//			InetSocketAddress neAddress = channel.getNeAddress();
//			InetAddress address = neAddress.getAddress();
//			for (int i = 0; i < msgData.length; i++) {
//				System.out.print(msgData[i]+" ");
//			}
//			System.out.println("----------------------------------------------------");
//			DatagramPacket dp = new DatagramPacket(msgData, msgData.length,address,9910);
//			try(DatagramSocket socket = new DatagramSocket();){
//				for (int i = 0; i < 50000; i++) {
//					socket.send(dp);
//				}
//			}
		} catch (Exception e) {
			Tool.LOG.fatal(e.getMessage(), e);
		}
	}
}
