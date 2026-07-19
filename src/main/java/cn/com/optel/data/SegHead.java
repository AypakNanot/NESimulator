/**
 * 
 */
package cn.com.optel.data;

import java.nio.ByteBuffer;

/**
 * @author qudy 帧头标记 4 帧头标记固定填入"\x0\x1\x0\x1" 帧类型 2 帧类型定义如下： 帧长度 2 固定填0
 */
public class SegHead {
	public static final int SEG_HEAD_FLAG = 0x00010001;
	
	public static final int SIZE = 8;
	
	protected ByteBuffer bufWrap;
	
	protected int beginPos = 0;
	
	protected SegHead(byte[] data) {
		bufWrap = ByteBuffer.wrap(data);
	}
	
	protected SegHead(byte[] data, int aPos) {
		bufWrap = ByteBuffer.wrap(data);
		beginPos = aPos;
	}
	
	public static SegHead wrap(byte[] data) {
		return new SegHead(data);
	}
	
	public static SegHead wrap(byte[] data, int aPos) {
		return new SegHead(data, aPos);
	}
	
	public boolean isHead() {
		boolean retVal = false;
		retVal = (getFlag() == SEG_HEAD_FLAG);
		return retVal;
	}
	
	public int getFlag() {
		return bufWrap.getInt(beginPos + 0);
	}
	
	public void setFlag() {
		bufWrap.putInt(beginPos + 0, SEG_HEAD_FLAG);
	}
	
	public short getType() {
		return bufWrap.getShort(beginPos + 4);
	}
	
	public void setType(short type) {
		bufWrap.putShort(beginPos + 4, type);
	}
	
	public short getLen() {
		return bufWrap.getShort(beginPos + 6);
	}
	
	public void setLen(short len) {
		bufWrap.putShort(beginPos + 6, len);
	}
}
