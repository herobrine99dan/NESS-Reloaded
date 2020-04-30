package com.github.ness;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;

public class NessScheduler implements Runnable, Executor, AutoCloseable {

	private BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>();
	
	@Override
	public void run() {
		while (true) {

			try {
				Runnable cmd = taskQueue.take();
				cmd.run();
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			} catch (Exception ex) {
				// Encountered an error
				// We can log the exception properly later
				ex.printStackTrace();
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
