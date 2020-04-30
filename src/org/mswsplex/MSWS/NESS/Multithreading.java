package org.mswsplex.MSWS.NESS;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

public class Multithreading {

	private final Executor asyncExecutor;
	private final Executor syncExecutor;
	
	Multithreading(Executor asyncExecutor, Executor syncExecutor) {
		this.asyncExecutor = asyncExecutor;
		this.syncExecutor = syncExecutor;
	}
	
	public CompletableFuture<?> runAsync(Runnable command) {
		return CompletableFuture.runAsync(command, asyncExecutor);
	}
	
	public <T> CompletableFuture<T> supplyAsync(Supplier<T> supplier) {
		return CompletableFuture.supplyAsync(supplier, asyncExecutor);
	}
	
	public CompletableFuture<?> runSynced(Runnable command) {
		return CompletableFuture.runAsync(command, syncExecutor);
	}
	
	public <T> CompletableFuture<T> supplySynced(Supplier<T> syncSupplier) {
		return CompletableFuture.supplyAsync(syncSupplier, syncExecutor);
	}
	
}
