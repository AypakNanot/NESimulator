package cn.com.optel.comm;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.com.optel.util.Tool;

public class Processors {
	
	private static Processors inst = null;
	
	private ExecutorService service = Executors.newFixedThreadPool(Tool.PARALLELISM * 200);
	
	public synchronized static Processors I() {
		if (inst == null) {
			inst = new Processors();
		}
		return inst;
	}
	
	public void submit(Runnable runner) {
		service.submit(runner);
	}
}
