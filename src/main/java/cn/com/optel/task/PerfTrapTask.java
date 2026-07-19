/**
 * AlarmTrapTask.java 
 *
 * date          author
 * ───────────────────────
 * 2015年3月23日      Administrator
 *
 * Copyright (c) 2015, H-OPTEL All Rights Reserved.
 */

package cn.com.optel.task;

import cn.com.optel.ServerSimulator;
import cn.com.optel.comm.Channel;
import cn.com.optel.util.Tool;

/**
 * ClassName: AlarmTrapTask
 * <ul>
 * <li>(这里描述这个类的功能)</li>
 * </ul>
 * 
 * @author Administrator
 * @see
 */
public class PerfTrapTask extends Thread {

	public PerfTrapTask() {
	}
	
	@Override
	public void run() {
		int i = 0;
		int TARP_NE_NUM = Tool.getmTrapNeNum();
		if (TARP_NE_NUM > 0) {
			Tool.LOG.fatal("start to trap perf..." + " trap ne count:" + TARP_NE_NUM);
			for (Channel neChannel : ServerSimulator.getNeChannelMap().values()) {
				new PerfTrapRunnable(neChannel).run();
				if (++i >= TARP_NE_NUM)
					break;
			}
		}
	}
}
