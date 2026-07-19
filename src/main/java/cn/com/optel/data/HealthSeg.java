/**
 * 
 */
package cn.com.optel.data;

import java.nio.ByteBuffer;

/**
 * @author qudy 帧头标记 4 帧头标记固定填入"\x0\x1\x0\x1" 帧类型 2
 * 帧类型定义如下：0x0003：健康测试帧,0x0103：健康确认帧 帧长度 2 固定填0 帧尾标记 4 帧尾标记固定填入"\x2\x4\x2\x4"
 */
public class HealthSeg extends Segment {
	
	public static final int HEALTH_SEG_LEN = 12;
	
	public HealthSeg(boolean confirm) {
		this();
		head.setType(confirm ? SEG_TYPE_HEALTH_CONFIRM : SEG_TYPE_HEALTH);
		setTail();
	}
	
	public HealthSeg() {
		segData = new byte[HEALTH_SEG_LEN];
		bufWrap = ByteBuffer.wrap(segData);
		
		head = SegHead.wrap(segData);
		head.setFlag();
		head.setType(SEG_TYPE_HEALTH);
		head.setLen((short) 0);
		setTail();
	}
	
	public HealthSeg(byte[] data, int aPos) {
		segData = data;
		beginPos = aPos;
		bufWrap = ByteBuffer.wrap(segData);
		head = SegHead.wrap(segData, beginPos);
	}
	
	public HealthSeg setTail() {
		bufWrap.putInt(beginPos + 8, SEG_TAIL_FLAG);
		return this;
	}
	
	public int getTail() {
		return bufWrap.getInt(beginPos + 8);
	}
	
	public boolean validate() {
		boolean retVal = false;
		retVal = (segData != null)
				&& ((head.getType() == SEG_TYPE_HEALTH) || (head.getType() == SEG_TYPE_HEALTH_CONFIRM))
				&& (segData.length >= (beginPos + 8)) && head.isHead()
				&& (getTail() == SEG_TAIL_FLAG);
		
		return retVal;
	}
	
}
