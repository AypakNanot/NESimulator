/**
 * 
 */
package cn.com.optel.data;

import java.nio.ByteBuffer;

/**
 * @author qudy 帧头标记 4 帧头标记固定填入"\x0\x1\x0\x1" 帧类型 2 帧类型定义如下：0x0101：数据确认帧 帧长度 2 =
 * 信息编号 + 没收到的分片数量 + 没收到的分片序号 信息编号 4 信息数据编号：0－0xffffffff循环使用,填入收到数据的信息编号
 * 没收到的分片数量 4 没收到的分片数量 没收到的分片序号 4×没收到的分片数量 帧尾标记 4 帧尾标记固定填入"\x2\x4\x2\x4"
 */
public class ConfirmSeg extends Segment {
	
	int noRecivedCnt = 0;
	
	public ConfirmSeg() {
		this(0);
	}
	
	public ConfirmSeg(int noRcvCnt) {
		noRecivedCnt = noRcvCnt;
		
		segData = new byte[16 + noRcvCnt * 4 + 4];
		bufWrap = ByteBuffer.wrap(segData);
		
		head = SegHead.wrap(segData);
		head.setFlag();
		head.setType(SEG_TYPE_CONFIRM);
		head.setLen((short) (8 + noRecivedCnt * 4));
		setSegCnt(noRcvCnt);
		setTail();
	}
	
	public ConfirmSeg(byte[] data, int aPos) {
		segData = data;
		beginPos = aPos;
		bufWrap = ByteBuffer.wrap(segData);
		head = SegHead.wrap(segData, beginPos);
		noRecivedCnt = getSegCnt();
	}
	
	public ConfirmSeg setInfoSeq(int num) {
		bufWrap.putInt(beginPos + 8, num);
		return this;
	}
	
	public ConfirmSeg setSegCnt(int num) {
		bufWrap.putInt(beginPos + 12, num);
		return this;
	}
	
	public ConfirmSeg setDataIdx(int idx, int seq) {
		bufWrap.putInt(beginPos + 16 + 4 * idx, seq);
		return this;
	}
	
	public ConfirmSeg setTail() {
		bufWrap.putInt(beginPos + 16 + noRecivedCnt * 4, SEG_TAIL_FLAG);
		return this;
	}
	
	public int getInfoSeq() {
		return bufWrap.getInt(beginPos + 8);
	}
	
	public int getSegCnt() {
		return bufWrap.getInt(beginPos + 12);
	}
	
	public short getDataIdx(int idx) {
		return bufWrap.getShort(beginPos + 16 + 4 * idx);
	}
	
	public int getTail() {
		return bufWrap.getInt(beginPos + 16 + noRecivedCnt * 4);
	}
	
	public boolean validate() {
		boolean retVal = false;
		retVal = (segData != null) && (head.getType() == SEG_TYPE_CONFIRM)
				&& (segData.length >= (beginPos + 16 + noRecivedCnt * 4 + 4)) && head.isHead()
				&& (getTail() == SEG_TAIL_FLAG);
		
		return retVal;
	}
	
}
