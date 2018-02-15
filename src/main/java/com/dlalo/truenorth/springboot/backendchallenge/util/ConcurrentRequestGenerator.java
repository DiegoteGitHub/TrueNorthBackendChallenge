package com.dlalo.truenorth.springboot.backendchallenge.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.dlalo.truenorth.springboot.backendchallenge.model.Reserve;

public class ConcurrentRequestGenerator {
	
	private int numPoolThreads;
	private int numParallelReqs;
	private Reserve reserve;
	private String serviceUri;
	
	public ConcurrentRequestGenerator() {
	}
	
	public ConcurrentRequestGenerator(int numPoolThreads, int numParallelReqs, Reserve reserve, String serviceUri) {
		this.numPoolThreads = numPoolThreads;
		this.numParallelReqs = numParallelReqs;
		this.reserve = reserve;
		this.serviceUri = serviceUri;
	}
	
	public void run() throws InterruptedException {
		ExecutorService es = Executors.newFixedThreadPool(numPoolThreads);
		
		for (int i = 0; i<numParallelReqs; i++) {
			es.execute(new RequestThread(reserve, serviceUri));
		}
		es.shutdown();
		es.awaitTermination(2, TimeUnit.MINUTES);
	}

}
