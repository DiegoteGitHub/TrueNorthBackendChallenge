package com.dlalo.truenorth.springboot.backendchallenge.util;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import com.dlalo.truenorth.springboot.backendchallenge.model.Reserve;

public class RequestThread implements Runnable {
	
	private Reserve reserve;
	private String serviceUri;
	
	private static final Logger logger = LoggerFactory.getLogger(RequestThread.class);
	
	public RequestThread() {
	}
	
	public RequestThread(Reserve reserve, String serviceUri) {
		this.reserve = reserve;
		this.serviceUri = serviceUri;
	}
	

	@Override
	public void run() {
		HttpHeaders headers = new HttpHeaders();
    	headers.setContentType(MediaType.APPLICATION_JSON);
    	HttpEntity<Reserve> requestEntity = new HttpEntity<Reserve>(reserve, headers);
    	RestTemplate restTemplate = new RestTemplate();
    	try {
			URI reserveUri = restTemplate.postForLocation(serviceUri, requestEntity);
			if (reserveUri == null) {
				logger.error(Thread.currentThread().getName() + " Cannot reserve campsite ");
			} else {
				logger.info(Thread.currentThread().getName() + " Successfully reserved campsite! Uri => " + reserveUri);
			}
    	} catch (Exception e) {
    		logger.error("Problem reserving campsite");
    	}
	}

}
