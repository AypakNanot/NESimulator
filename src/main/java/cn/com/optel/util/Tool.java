package cn.com.optel.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Tool {
	public static Log LOG = LogFactory.getLog(Tool.class);
	public static ResourceBundle configRB = ResourceBundle.getBundle("config");
	public static int PARALLELISM = Runtime.getRuntime().availableProcessors();
	
	public static int getNENum() {
		String nuNum = configRB.getString("neNum");
		return Integer.valueOf(nuNum);
	}
	
	public static InetAddress getLocalAddr() {
		InetAddress localAddr = InetAddress.getLoopbackAddress();
		String addr = configRB.getString("localAddr");
		IpAddr ipAddrnew = new IpAddr(addr);
		try {
			localAddr = InetAddress.getByAddress(ipAddrnew.toByteArray());
		} catch (UnknownHostException e) {
			LOG.fatal(e.getMessage(), e);
		}
		return localAddr;
	}
	
	public static int getStartPort() {
		String startPort = configRB.getString("startPort");
		return Integer.valueOf(startPort);
	}
	
	public static boolean getPerfGenerate() {
		String PerfGenerate = configRB.getString("perfGenerate");
		return Boolean.valueOf(PerfGenerate);
	}
	
	public static boolean getAlarmGenerate() {
		String alarmGenerate = configRB.getString("alarmGenerate");
		return Boolean.valueOf(alarmGenerate);
	}
	
	public static int getmTrapNeNum() {
		String trapNeNum = configRB.getString("TrapNeNum");
		return Integer.valueOf(trapNeNum);
	}
	
	public static byte[] getPerfTrap() {
		String perfTrap = configRB.getString("perfTrap");
		return parseBytes(perfTrap);
	}
	
	public static byte[] getAlarmTrap() {
		String alarmTrap = configRB.getString("alarmTrap");
		return parseBytes(alarmTrap);
	}
	
	public static byte[] getPerfQueryResult() {
		String perfQueryResult = configRB.getString("perfQueryResult");
		return parseBytes(perfQueryResult);
	}
	
	public static byte[] getPerfQueryResult(String param) {
		String perfQueryResult = configRB.getString(param);
		if(perfQueryResult==null || "".equals(perfQueryResult)){
			return new byte[0];
		}
		return parseBytes(perfQueryResult);
	}
	
	public static byte[] getAlmSynResult() {
		String almSynResult = configRB.getString("almSynResult");
		if (almSynResult.length() == 0) {
			return null;
		} else {
			return parseBytes(almSynResult);
		}
	}
	
	public static byte[] parseBytes(String string) {
		String[] strings = string.split(" ");
		byte[] bytes = new byte[strings.length];
		for (int i = 0; i < strings.length; i++) {
			bytes[i] = (byte) Short.parseShort(strings[i], 16);
		}
		return bytes;
	}
	
	public static int getInitialDelay() {
		String initialDelay = configRB.getString("initialDelay");
		return Integer.valueOf(initialDelay);
	}
	
	public static int getPeriod() {
		String period = configRB.getString("period");
		return Integer.valueOf(period);
	}
	
	public static void print(String info, byte[] array) {
		if (array.length < 24) {
			return;
		}
		if (array.length > 5000) {
			array = Arrays.copyOf(array, 24);
		}
		StringBuilder buffer = new StringBuilder();
		char[] sepChar = new char[30];
		Arrays.fill(sepChar, '-');
		String sepStr = new String(sepChar);
		buffer.append("\r\n");
		buffer.append(sepStr);
		buffer.append(info);
		buffer.append(sepStr);
		buffer.append("\r\n");
		
		for (byte element : array) {
			String hexStr = Integer.toHexString(element & 0xFF);
			if (hexStr.length() == 1) {
				hexStr = "0" + hexStr;
			}
			buffer.append(hexStr.toUpperCase());
			buffer.append(" ");
		}
		buffer.append("\r\n");
		buffer.append(sepStr);
		buffer.append(info);
		buffer.append(sepStr);
		LOG.debug(buffer);
	}

	
}
