package com.playground.services;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Test;

public class Executor {

    @Test
    public void test() throws InterruptedException, ExecutionException {

	ExecutorService executor = Executors.newWorkStealingPool();

	long start = System.currentTimeMillis();
	Set<Object> result = IntStream.range(1, 200000).boxed().map(i -> {
	    return new Callable<String>() {
		public String call() throws Exception {
		    TimeUnit.MILLISECONDS.sleep(i % 9);
		    return "Task " + i;
		}
	    };
	}).collect(Collectors.toSet());

	executor.invokeAll((Collection<? extends Callable<String>>) result).parallelStream().map(future -> {
	    try {
		return future.get();
	    } catch (Exception e) {
		throw new IllegalStateException(e);
	    }
	}).forEach(System.out::println);
	System.out.println("Elapsed Time in seconds: " + (System.currentTimeMillis() - start)/1000);
    }
}
