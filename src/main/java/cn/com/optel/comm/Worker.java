package cn.com.optel.comm;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;

class Worker implements Runnable {
	/**
	 * 存放当前通道的所有交互活动
	 */
	protected BlockingQueue<FutureTask<SessionResult>> actions = new LinkedBlockingQueue<FutureTask<SessionResult>>();
	
	Worker() {
	}
	
	public Future<SessionResult> depute(Interaction action) throws Exception {
		FutureTask<SessionResult> future = new FutureTask<SessionResult>(action);
		actions.put(future);
		Processors.I().submit(this);
		return future;
	}
	
	public synchronized void run() {
		while (true) {
			FutureTask<SessionResult> action = actions.poll();
			if (action != null && !action.isCancelled()) {
				action.run();
			} else {
				break;
			}
		}
	}
}
