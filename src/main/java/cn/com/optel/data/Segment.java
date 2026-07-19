/**
 * 
 */
package cn.com.optel.data;

import java.nio.ByteBuffer;

/**
 * @author qudy
 */
public abstract class Segment {
	
	public static final int SEG_TAIL_FLAG = 0x02040204;
	
	// 0x0001：数据包头帧
	public static final short SEG_TYPE_FIRST = 0x1;
	// 0x0002：数据分片帧
	public static final short SEG_TYPE_DATA = 0x2;
	// 0x0101：数据确认帧
	public static final short SEG_TYPE_CONFIRM = 0x101;
	// 0x0003：健康帧
	public static final short SEG_TYPE_HEALTH = 0x3;
	// 0x0103：健康确认帧
	public static final short SEG_TYPE_HEALTH_CONFIRM = 0x103;
	
	/**
	 * 存放帧数据的内存区
	 */
	protected byte[] segData;
	
	/**
	 * 当前帧在帧数据内存区中的开始位置
	 */
	protected int beginPos = 0;
	
	protected SegHead head;
	
	protected ByteBuffer bufWrap;
	
	/**
	 * @return the segData
	 */
	public byte[] getSegData() {
		return segData;
	}
	
	public SegHead getHead() {
		return head;
	}
	
	public short segType() {
		return (head != null) ? head.getType() : 0;
	}
	
	public int getSize() {
		return SegHead.SIZE + head.getLen() + 4;
	}
	
	public abstract boolean validate();
	
	public abstract int getTail();
}
