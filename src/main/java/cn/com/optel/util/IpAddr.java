package cn.com.optel.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.StringTokenizer;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * <p>
 * Company: dtt
 * </p>
 *
 * @author wxw
 * @version 1.0
 */
public class IpAddr {
	
	private int ip;
	private String ipStr;
	/**
	 * 默认超时(ms)
	 */
	public static final int TIMEOUT = 9000;
	/**
	 * 重试次数
	 */
	public static final int RETRY_TIMES = 3;
	
	public IpAddr(int ip) {
		this.setIp(ip);
	}
	
	public IpAddr(String ipStr) {
		this.setIpStr(ipStr);
	}
	
	public IpAddr(byte[] byteArray) {
		if (byteArray.length != 4) {
			this.setIp(0);
		}
		
		StringBuffer strBuf = new StringBuffer(Integer.toString(0xff & byteArray[0]));
		for (int i = 1; i < byteArray.length; i++) {
			strBuf.append(".");
			strBuf.append(Integer.toString(0xff & byteArray[i]));
		}
		this.setIpStr(strBuf.toString());
	}
	
	public byte[] toByteArray() {
		byte[] byteArray = new byte[4];
		for (int i = 0; i < 4; i++) {
			byteArray[i] = (byte) ((this.ip >> (3 - i) * 8) & 0xff);
		}
		return byteArray;
	}
	
	public int getIp() {
		return ip;
	}
	
	public String getIpStr() {
		return ipStr;
	}
	
	public void setIp(int ip) {
		this.ip = ip;
		this.ipStr = toString(ip);
	}
	
	public void setIpStr(String ipStr) {
		this.ipStr = ipStr;
		this.ip = getIp(ipStr);
	}
	
	public String toString() {
		return this.ipStr;
	}
	
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof IpAddr)) {
			return false;
		}
		return (((IpAddr) obj).getIp() == this.ip);
	}
	
	public int hashCode() {
		return this.ip;
	}
	
	/**
	 * 获得当前主机的ip地址。
	 */
	public static int getHostIp() {
		// 最好不要使用getHostName()方法获得主机名，因为不同平台的主机名格式可能不同.
		// 为了屏蔽这种差异，可以使用getAddress()方法得到格式相同的IP地址。
		int[] ip = new int[4];
		try {
			InetAddress ipAddr = InetAddress.getLocalHost();
			// the highest order byte of the address is in getAddress()[0].
			byte[] hostAddr = ipAddr.getAddress();
			ip[0] = (((int) hostAddr[0]) << 24) & 0xff000000;
			ip[1] = (((int) hostAddr[1]) << 16) & 0x00ff0000;
			ip[2] = (((int) hostAddr[2]) << 8) & 0x0000ff00;
			ip[3] = (((int) hostAddr[3]) << 0) & 0x000000ff;
		} catch (UnknownHostException ex) {
			ex.printStackTrace();
		}
		return ip[0] | ip[1] | ip[2] | ip[3];
	}
	
	public static String toString(int[] ipList) {
		StringBuffer strBuf = new StringBuffer();
		for (int i = 0; i < ipList.length; i++) {
			strBuf.append(toString(ipList[i]));
			strBuf.append(";");
		}
		return strBuf.toString();
	}
	
	/**
	 * 把整数类型的IP地址转换为字符串格式。
	 * 
	 * @param ipAddr 整数类型的IP地址。
	 * @return 字符串格式的IP地址，如"172.18.15.1"。
	 */
	public static String toString(int ipAddr) {
		if (ipAddr == 0) {
			return "0.0.0.0";
		}
		
		int[] ip = new int[4];
		StringBuffer strBuf = new StringBuffer();
		for (int i = 3; i >= 0; i--) {
			ip[i] = (ipAddr >> (i * 8)) & 0x000000ff;
			strBuf.append(ip[i]);
			if (i > 0) {
				strBuf.append(".");
			}
		}
		return strBuf.toString();
	}
	
	/**
	 * 把字符串类型的IP地址转换为整数格式。
	 * 
	 * @param ipAddr 字符串类型的IP地址，如"172.18.15.1"。
	 * @return 整数格式的IP地址。
	 */
	public static int getIp(String ipAddr) {
		if (ipAddr == null || ipAddr.equals("")) {
			return 0;
		}
		StringTokenizer st = new StringTokenizer(ipAddr, ".");
		int count = st.countTokens();
		if (count != 4) {
			return 0;
		}
		int ip = 0;
		for (int i = 0; i < count; i++) {
			try {
				int tmp = Integer.parseInt(st.nextToken());
				if (tmp < 0 || tmp > 255) {
					return 0;
				}
				ip |= tmp << ((3 - i) * 8);
			} catch (NumberFormatException ex) {
				return 0;
			}
		}
		return ip;
	}
	
	/**
	 * ping
	 *
	 * @param ip int
	 * @param timeout int
	 * @return int
	 */
	public static int ping(int ip) {
		return ping(ip, TIMEOUT);
	}
	
	public static int ping(int ip, int timeout) {
		try {
			InetAddress ia = InetAddress.getByAddress(new IpAddr(ip).toByteArray());
			int wait = timeout;
			if (wait <= 0) {
				wait = TIMEOUT;
			}
			
			/**
			 * @todo 待修改 InetAddress的isReachable的底层实现决定是ICMP ping
			 * 还是TCP的ECHO，由于设备可能不会完全实现TCP/IP协议栈，
			 * 因此不能肯定设备支持TCP的ECHO，所以必须保证ping的实现是 ICMP的ping。
			 */
			if (ia.isReachable(wait)) {
				return 1;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return 0;
	}
	
	/**
	 * agentPing
	 *
	 * @param agentIp int
	 * @param ip int
	 * @param timeout int
	 * @return int
	 */
	public static int agentPing(int agentIp, int ip, int timeout) {
		if (agentIp == 0) {
			return ping(ip, timeout);
		}
		
		if (ping(agentIp, timeout) == 0) {
			return 0;
		}
		
		/**
		 * @todo
		 */
		return 1;
	}
	
	public static int agentPing(int agentIp, int ip) {
		return agentPing(agentIp, ip, TIMEOUT);
	}
	
	/**
	 * 把给定的ip地址字符串转换成long型
	 * 
	 * @param ipAddress String
	 * @return long
	 */
	public static long convertIP(String ipAddress) {
		long rtn = 0;
		String[] ss = ipAddress.split("\\.");
		for (int i = 0; i < ss.length; i++) {
			rtn <<= 8;
			rtn += Long.parseLong(ss[i]);
		}
		for (int i = ss.length; i < 4; i++) {
			rtn <<= 8;
		}
		return rtn;
	}
	
	/**
	 * 将int型的ip转换成long型
	 * 
	 * @param ip int
	 * @return long
	 */
	public static long convertIP(int ip) {
		String ipAddress = toString(ip);
		return convertIP(ipAddress);
	}
	
	/**
	 * MethodName: getSubMask
	 * <ul>
	 * <li>根据ip地址的第一个字节获取子网掩码</li>
	 * </ul>
	 * 
	 * @param ipAddr
	 * @return String
	 */
	public static String getSubMask(String ipAddr) {
		if (ipAddr == null || "".equals(ipAddr.trim())) {
			return "";
		}
		int ipFirstChar = Integer.parseInt(ipAddr.substring(0, ipAddr.indexOf(".")));
		if (ipFirstChar <= 127) {
			return "255.0.0.0";
		} else if (ipFirstChar <= 191) {
			return "255.255.0.0";
		} else if (ipFirstChar <= 223) {
			return "255.255.255.0";
		} else {
			return "0.0.0.0";
		}
	}
}
