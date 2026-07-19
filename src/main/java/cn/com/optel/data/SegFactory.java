/**
 * 
 */
package cn.com.optel.data;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * @author qudy
 */
public class SegFactory {
	public static Segment wrap(byte[] buf, int seek) {
		Segment retVal = null;
		SegHead head = SegHead.wrap(buf, seek);
		switch (head.getType()) {
		case Segment.SEG_TYPE_CONFIRM:
			retVal = new ConfirmSeg(buf, seek);
			break;
		case Segment.SEG_TYPE_DATA:
			retVal = new DataSeg(buf, seek);
			break;
		case Segment.SEG_TYPE_FIRST:
			retVal = new FirstSeg(buf, seek);
			break;
		case Segment.SEG_TYPE_HEALTH:
		case Segment.SEG_TYPE_HEALTH_CONFIRM:
			retVal = new HealthSeg(buf, seek);
			break;
		}
		return retVal;
	}
	
	public static Segment spawnSeg(byte[] buf, int seek) {
		Segment retVal = null;
		SegHead head = SegHead.wrap(buf, seek);
		byte[] sniped = new byte[SegHead.SIZE + head.getLen() + 4];
		System.arraycopy(buf, seek, sniped, 0, sniped.length);
		switch (head.getType()) {
		case Segment.SEG_TYPE_CONFIRM:
			retVal = new ConfirmSeg(sniped, 0);
			break;
		case Segment.SEG_TYPE_DATA:
			retVal = new DataSeg(sniped, 0);
			break;
		case Segment.SEG_TYPE_FIRST:
			retVal = new FirstSeg(sniped, 0);
			break;
		case Segment.SEG_TYPE_HEALTH:
		case Segment.SEG_TYPE_HEALTH_CONFIRM:
			retVal = new HealthSeg(sniped, 0);
			break;
		}
		return retVal;
	}
	
	private static short[] supportTypes = new short[] {Segment.SEG_TYPE_FIRST,
			Segment.SEG_TYPE_DATA, Segment.SEG_TYPE_HEALTH, Segment.SEG_TYPE_CONFIRM,
			Segment.SEG_TYPE_HEALTH_CONFIRM};
	
	public static boolean validateSeg(byte[] buf, int seek) {
		boolean retVal = false;
		if ((buf.length - seek) > SegHead.SIZE) {
			SegHead head = SegHead.wrap(buf, seek);
			
			if (Arrays.binarySearch(supportTypes, head.getType()) >= 0
					&& seek + SegHead.SIZE + head.getLen() + 4 <= buf.length) {
				ByteBuffer wrap = ByteBuffer.wrap(buf);
				retVal = (wrap.getInt(seek + SegHead.SIZE + head.getLen() + 1) == Segment.SEG_TAIL_FLAG);
			}
		}
		return retVal;
	}
	
	public static boolean validateSeg(byte[] buf, int seek, int limit) {
		boolean retVal = false;
		if ((limit - seek) > SegHead.SIZE) {
			SegHead head = SegHead.wrap(buf, seek);
			
			if (Arrays.binarySearch(supportTypes, head.getType()) >= 0
					&& seek + SegHead.SIZE + head.getLen() + 4 <= limit) {
				ByteBuffer wrap = ByteBuffer.wrap(buf);
				retVal = (wrap.getInt(seek + SegHead.SIZE + head.getLen()) == Segment.SEG_TAIL_FLAG);
			}
		}
		return retVal;
	}
}
