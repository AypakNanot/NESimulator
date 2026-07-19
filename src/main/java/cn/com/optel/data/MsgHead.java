/**
 *
 */
package cn.com.optel.data;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * @author qudy 消息长度 1Long 版本信息 1Byte 选项（是否应答标志以及其他） 1Byte 备用字节 1Word 序列号 1Long
 * 目的地址（网元标识） 1Long 源地址（网元标识） 1Long 网管命令编码 1Word 返回结果 Word
 */
public class MsgHead {
	
	/**
	 * 消息头部的byte长度
	 */
	public static int HEAD_BYTE_LEN = 24;
	
	int beginPos = 0;
	
	ByteBuffer bufWrap;
	
	public static byte version = 5;
	
	protected MsgHead(byte[] buf) {
		// assert (buf != null && buf.length >= HEAD_BYTE_LEN) ;
		bufWrap = ByteBuffer.wrap(buf);
	}
	
	protected MsgHead(byte[] buf, int aPos) {
		// assert (buf != null && buf.length >= (HEAD_BYTE_LEN + aPos)) ;
		bufWrap = ByteBuffer.wrap(buf);
		beginPos = aPos;
	}
	
	public static MsgHead wrap(byte[] buf) {
		return new MsgHead(buf);
	}
	
	public static MsgHead wrap(byte[] buf, int aPos) {
		return new MsgHead(buf, aPos);
	}
	
	/**
	 * @return the cmdCode
	 */
	public short getCmdCode() {
		return bufWrap.getShort(beginPos + 20);
	}
	
	/**
	 * @param cmdCode the cmdCode to set
	 */
	public MsgHead setCmdCode(short cmdCode) {
		bufWrap.putShort(beginPos + 20, cmdCode);
		return this;
	}
	
	/**
	 * @return the msgLen
	 */
	public int getMsgLen() {
		return bufWrap.getInt(beginPos + 0);
	}
	
	/**
	 * @param msgLen the msgLen to set
	 */
	public MsgHead setMsgLen(int msgLen) {
		bufWrap.putInt(beginPos + 0, msgLen);
		return this;
	}
	
	/**
	 * @return the opt
	 */
	public byte getOpt() {
		return bufWrap.get(beginPos + 5);
	}
	
	/**
	 * @param opt the opt to set
	 */
	public MsgHead setOpt(byte opt) {
		bufWrap.put(beginPos + 5, opt);
		return this;
	}
	
	/**
	 * @return the reserve
	 */
	public short getReserve() {
		return bufWrap.getShort(beginPos + 6);
	}
	
	/**
	 * @param reserve the reserve to set
	 */
	public MsgHead setReserve(short reserve) {
		bufWrap.putShort(beginPos + 6, reserve);
		return this;
	}
	
	/**
	 * @return the seqNum
	 */
	public int getSeqNum() {
		return bufWrap.getInt(beginPos + 8);
	}
	
	/**
	 * @param seqNum the seqNum to set
	 */
	public MsgHead setSeqNum(int seqNum) {
		bufWrap.putInt(beginPos + 8, seqNum);
		return this;
	}
	
	/**
	 * @return the source
	 */
	public int getSource() {
		return bufWrap.getInt(beginPos + 16);
	}
	
	/**
	 * @param source the source to set
	 */
	public MsgHead setSource(int source) {
		bufWrap.putInt(beginPos + 16, source);
		return this;
	}
	
	/**
	 * @return the target
	 */
	public int getTarget() {
		return bufWrap.getInt(beginPos + 12);
	}
	
	/**
	 * @param target the target to set
	 */
	public MsgHead setTarget(int target) {
		bufWrap.putInt(beginPos + 12, target);
		return this;
	}
	
	/**
	 * @return the version
	 */
	public byte getVersion() {
		return bufWrap.get(beginPos + 4);
	}
	
	/**
	 * @param version the version to set
	 */
	public MsgHead setVersion() {
		bufWrap.put(beginPos + 4, version);
		return this;
	}
	
	public short getResult() {
		return bufWrap.getShort(beginPos + 22);
	}
	
	public MsgHead setResult(short result) {
		bufWrap.putShort(beginPos + 22, result);
		return this;
	}
	
	public byte[] getHeadData() {
		return Arrays.copyOf(bufWrap.array(), HEAD_BYTE_LEN);
	}
}
