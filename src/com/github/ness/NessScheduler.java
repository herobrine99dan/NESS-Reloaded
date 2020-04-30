package com.github.ness;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;

public class NessScheduler implements Runnable, Executor, AutoCloseable {

	private BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>();
	
	@Override
	public void run() {
		while (true) {

			// Process anticheat checks

			
			
			// Run tasks
			for (int n = 0; n < 5; ) {
				Runnable cmd = taskQueue.poll();
				if (cmd == null) {
					break;
				}
				cmd.run();
			}

			// Sleep briefly
			try {
				Thread.sleep(10L);
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
		}
	}
	
	@Override
	public void execute(Runnable command) {
		taskQueue.add(command);
	}
	
	@Override
	public void close() {
		taskQueue.clear();
	}

}
