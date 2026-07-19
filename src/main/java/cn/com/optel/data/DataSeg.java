/**
 * 
 */
package cn.com.optel.data;

import java.nio.ByteBuffer;

/**
 * @author qudy 帧头标记 4 帧头标记固定填入"\x0\x1\x0\x1" 帧类型 2 帧类型定义如下：0x0002：数据分片帧 帧长度 2
 * 帧长度＝信息编号＋数据片序号＋数据片长度＋数据片区 信息编号 4 信息数据编号：0－0xffffffff循环使用和数据包头帧的编号对应 数据片序号 4
 * 信息数据分片后的序号 数据片长度 2 此分片的实际长度 数据片区 1－4096 实际分片后的数据区 xxxxxxxxxx回应标记 1 固定填0
 * xxxxxxxxxxx??这个字段要等optel确认 帧尾标记 4 帧尾标记固定填入"\x2\x4\x2\x4"
 */
public class DataSeg extends Segment {
	
	int realDataLen = 0;
	
	/**
	 * 构造一个数据帧，传入的是具体的数据片
	 * 
	 * @param realData
	 */
	public DataSeg(byte[] realData) {
		this(realData, 0, realData.length);
	}
	
	/**
	 * 构造一个数据帧，传入的是具体的数据片。
	 * 
	 * @param realData 具体的数据片所在的内存区
	 * @param aPos 具体数据片在内存区中的起始位置
	 * @param dataLen 数据片的长度
	 */
	public DataSeg(byte[] realData, int aPos, int dataLen) {
		realDataLen = dataLen;
		
		// @todo : 如果确认【回应标记】应该加上，则应该再加上1个byte的长度
		segData = new byte[18 + dataLen + 4];
		bufWrap = ByteBuffer.wrap(segData);
		
		head = SegHead.wrap(segData);
		head.setFlag();
		head.setType(SEG_TYPE_DATA);
		head.setLen((short) (10 + dataLen));
		
		setDataLen((short) dataLen);
		System.arraycopy(realData, aPos, segData, 18, dataLen);
		
		setTail();
	}
	
	/**
	 * 封装一个数据区为数据片，不生成新的数据区存放数据
	 * 
	 * @param data 存放数据帧的数据区
	 * @param aPos 数据帧在数据区中的开始位置
	 */
	public DataSeg(byte[] data, int aPos) {
		segData = data;
		beginPos = aPos;
		bufWrap = ByteBuffer.wrap(segData);
		head = SegHead.wrap(segData, beginPos);
		
		realDataLen = getDataLen();
	}
	
	/**
	 * 信息编号 4 信息数据编号：0－0xffffffff循环使用和数据包头帧的编号对应
	 * 
	 * @param num
	 * @return
	 */
	public DataSeg setInfoSeq(int num) {
		bufWrap.putInt(beginPos + 8, num);
		return this;
	}
	
	/**
	 * 数据片序号 4 信息数据分片后的序号
	 * 
	 * @param num
	 * @return
	 */
	public DataSeg setDataIdx(int num) {
		bufWrap.putInt(beginPos + 12, num);
		return this;
	}
	
	/**
	 * 数据片长度 2 此分片的实际长度
	 * 
	 * @param size
	 * @return
	 */
	public DataSeg setDataLen(short size) {
		bufWrap.putShort(beginPos + 16, size);
		return this;
	}
	
	public DataSeg setTail() {
		bufWrap.putInt(beginPos + 18 + realDataLen, SEG_TAIL_FLAG);
		return this;
	}
	
	/**
	 * 信息编号 4 信息数据编号：0－0xffffffff循环使用和数据包头帧的编号对应
	 * 
	 * @return
	 */
	public int getInfoSeq() {
		return bufWrap.getInt(beginPos + 8);
	}
	
	/**
	 * 数据片序号 4 信息数据分片后的序号
	 * 
	 * @return
	 */
	public int getDataIdx() {
		return bufWrap.getInt(beginPos + 12);
	}
	
	/**
	 * 数据片长度 2 此分片的实际长度
	 * 
	 * @return
	 */
	public short getDataLen() {
		return bufWrap.getShort(beginPos + 16);
	}
	
	public int getTail() {
		return bufWrap.getInt(beginPos + 18 + realDataLen);
	}
	
	public boolean validate() {
		boolean retVal = false;
		retVal = (segData != null) && (head.getType() == SEG_TYPE_DATA)
				// @todo : 如果确认【回应标记】应该加上，则应该再加上1个byte的长度
				&& (segData.length >= (beginPos + realDataLen + 22)) && head.isHead()
				&& (getTail() == SEG_TAIL_FLAG);
		
		return retVal;
	}
	
}
