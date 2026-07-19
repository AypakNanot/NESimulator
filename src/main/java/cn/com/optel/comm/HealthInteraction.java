/**
 * 
 */
package cn.com.optel.comm;

import cn.com.optel.data.HealthSeg;

class HealthInteraction extends Interaction {
	
	public HealthInteraction(Channel ch) {
		timeout = 20;
		channel = ch;
	}
	
	public SessionResult call() throws Exception {
		SessionResult retVal = new SessionResult(null);
		HealthSeg seg = new HealthSeg(true);
		channel.write(seg.getSegData());
		
		return retVal;
	}
	
}
