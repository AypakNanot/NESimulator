/**
 * 
 */
package cn.com.optel.data;

import java.nio.ByteBuffer;

/**
 * @author qudy 包头帧所包含的字段： 帧头标记 4 帧头标记固定填入"\x0\x1\x0\x1" 帧类型 2
 * 帧类型定义如下：0x0001：数据包头帧0x0002：数据分片帧0x0101：数据确认帧0x0003：健康帧0x0103：健康确认帧 帧长度 2
 * 固定填17 信息编号 4 信息数据编号：0－0xffffffff循环使用 信息类型 2 信息类型0x0003：应用数据类型 分片大小 2
 * 分片大小（单位字节） 固定填4096 分片数量 4 数据经过分片后的数据片的个数 信息长度 4 整个被分片数据信息的长度 回应标记 1 固定填0 帧尾标记
 * 4 帧尾标记固定填入"\x2\x4\x2\x4"
 */
public class FirstSeg extends Segment {
	
	public static final int FIRST_SEG_LEN = 29;
	
	public static final int SEG_SIZE = 4096;
	
	/**
	 * 构造函数，此构造函数是构造一个包头帧，包括生成一个包头帧的数据区
	 */
	public FirstSeg() {
		segData = new byte[FIRST_SEG_LEN];
		bufWrap = ByteBuffer.wrap(segData);
		
		head = SegHead.wrap(segData);
		head.setFlag();
		head.setType(SEG_TYPE_FIRST);
		head.setLen((short) 17);
		
		setInfoType((short) 0x0);
		setSegSize((short) 4096);
		
		setTail();
	}
	
	/**
	 * 构造函数，此构造函数是对已经存在的数据区进行封装用来解析的构造函数。
	 * 
	 * @param data
	 * @param aPos
	 */
	public FirstSeg(byte[] data, int aPos) {
		segData = data;
		beginPos = aPos;
		bufWrap = ByteBuffer.wrap(segData);
		head = SegHead.wrap(segData, beginPos);
	}
	
	public boolean validate() {
		boolean retVal = false;
		retVal = (segData != null) && (head.getType() == SEG_TYPE_FIRST)
				&& (segData.length >= (beginPos + FIRST_SEG_LEN)) && head.isHead()
				&& (getTail() == SEG_TAIL_FLAG);
		
		return retVal;
	}
	
	/**
	 * 信息编号 4 信息数据编号：0－0xffffffff循环使用
	 * 
	 * @param num
	 * @return
	 */
	public FirstSeg setInfoSeq(int num) {
		bufWrap.putInt(beginPos + 8, num);
		return this;
	}
	
	/**
	 * 信息类型 2 信息类型0x0003：应用数据类型
	 * 
	 * @param type
	 * @return
	 */
	public FirstSeg setInfoType(short type) {
		bufWrap.putShort(beginPos + 12, type);
		return this;
	}
	
	/**
	 * 分片大小 2 分片大小（单位字节） 固定填4096
	 * 
	 * @param size
	 * @return
	 */
	public FirstSeg setSegSize(short size) {
		bufWrap.putShort(beginPos + 14, size);
		return this;
	}
	
	/**
	 * 分片数量 4 数据经过分片后的数据片的个数
	 * 
	 * @param cnt
	 * @return
	 */
	public FirstSeg setSegCount(int cnt) {
		bufWrap.putInt(beginPos + 16, cnt);
		return this;
	}
	
	/**
	 * 信息长度 4 整个被分片数据信息的长度
	 * 
	 * @param len
	 * @return
	 */
	public FirstSeg setInfoLen(int len) {
		bufWrap.putInt(beginPos + 20, len);
		return this;
	}
	
	/**
	 * 回应标记 1 固定填0
	 * 
	 * @param flag
	 * @return
	 */
	public FirstSeg setReply(byte flag) {
		bufWrap.put(beginPos + 24, flag);
		return this;
	}
	
	public FirstSeg setTail() {
		bufWrap.putInt(beginPos + 25, SEG_TAIL_FLAG);
		return this;
	}
	
	/**
	 * 信息编号 4 信息数据编号：0－0xffffffff循环使用
	 * 
	 * @return
	 */
	public int getInfoSeq() {
		return bufWrap.getInt(beginPos + 8);
	}
	
	/**
	 * 信息类型 2 信息类型0x0003：应用数据类型
	 * 
	 * @return
	 */
	public short getInfoType() {
		return bufWrap.getShort(beginPos + 12);
	}
	
	/**
	 * 分片大小 2 分片大小（单位字节） 固定填4096
	 * 
	 * @return
	 */
	public short getSegSize() {
		return bufWrap.getShort(beginPos + 14);
	}
	
	/**
	 * 分片数量 4 数据经过分片后的数据片的个数
	 * 
	 * @return
	 */
	public int getSegCount() {
		return bufWrap.getInt(beginPos + 16);
	}
	
	/**
	 * 信息长度 4 整个被分片数据信息的长度
	 * 
	 * @return
	 */
	public int getInfoLen() {
		return bufWrap.getInt(beginPos + 20);
	}
	
	/**
	 * 回应标记 1 固定填0
	 * 
	 * @return
	 */
	public byte getReply() {
		return bufWrap.get(beginPos + 24);
	}
	
	public int getTail() {
		return bufWrap.getInt(beginPos + 25);
	}
}
