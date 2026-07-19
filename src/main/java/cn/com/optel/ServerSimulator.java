package cn.com.optel;

import java.io.FileWriter;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import cn.com.optel.comm.Channel;
import cn.com.optel.task.AlarmTrapTask;
import cn.com.optel.util.Tool;

public class ServerSimulator {
	protected static final int NE_NUM = Tool.getNENum();
	protected static final int STAR_TPORT = Tool.getStartPort();
	protected static List<Integer> runningServerPorts = new LinkedList<Integer>();
	public static Map<Integer, Channel> neChannelMap = new ConcurrentHashMap<Integer, Channel>();
	public static AcceptCompletionHandler acceptHandler = new AcceptCompletionHandler();
	
	public static synchronized Map<Integer, Channel> getNeChannelMap() {
		return neChannelMap;
	}
	
	public static synchronized void setNeChannelMap(Map<Integer, Channel> neChannelMap) {
		ServerSimulator.neChannelMap = neChannelMap;
	}
	
	public static void main(String[] args) throws Exception {
		Logger.getRootLogger().setLevel(Level.FATAL);
		int serverPort = STAR_TPORT;
		ExecutorService threadPool = Executors.newFixedThreadPool(Tool.PARALLELISM * 100);
		AsynchronousChannelGroup channelGroup = AsynchronousChannelGroup.withThreadPool(threadPool);
		while (runningServerPorts.size() < NE_NUM) {
			try {
				AsynchronousServerSocketChannel ssc = AsynchronousServerSocketChannel
						.open(channelGroup);
				ssc.bind(new InetSocketAddress(Tool.getLocalAddr(), serverPort));
				ssc.accept(ssc, acceptHandler);
				runningServerPorts.add(serverPort);
				Tool.LOG.fatal("port:" + serverPort + " start...");
			} catch (Exception e) {
				Tool.LOG.fatal(e.getMessage() + " port:" + STAR_TPORT, e);
			} finally {
				serverPort++;
			}
		}
		
		// 将服务器启动端口信息输出到文件
		String hostAddress = Tool.getLocalAddr().getHostAddress();
		FileWriter writer = new FileWriter("serverPort.csv");
		for (int runningServerPort : runningServerPorts) {
			writer.write(hostAddress);
			writer.write(",");
			writer.write(String.valueOf(runningServerPort));
			writer.write("\n");
		}
		writer.flush();
		writer.close();
		
		if (Tool.getAlarmGenerate()) {
			// 自动产生告警
			AlarmTrapTask generateTask = new AlarmTrapTask(1);
			AlarmTrapTask disappearTask = new AlarmTrapTask(2);
			ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
			scheduler.scheduleAtFixedRate(generateTask, Tool.getInitialDelay(),
					Tool.getPeriod(), TimeUnit.SECONDS);
			scheduler.scheduleAtFixedRate(disappearTask, Tool.getInitialDelay()+Tool.getPeriod()/2,
					Tool.getPeriod(), TimeUnit.SECONDS);
		}
//		自动trap上报性能
//		if (Tool.getPerfGenerate()) {
//			PerfTrapTask perftrapTask = new PerfTrapTask();
//			ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
//			scheduler.scheduleAtFixedRate(perftrapTask, Tool.getInitialDelay(),Tool.getPeriod(), TimeUnit.SECONDS);
//		}
		TimeUnit.DAYS.sleep(365);
	}
}
