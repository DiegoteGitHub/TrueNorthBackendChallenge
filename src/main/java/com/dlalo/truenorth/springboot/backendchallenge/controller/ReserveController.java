package com.dlalo.truenorth.springboot.backendchallenge.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.dlalo.truenorth.springboot.backendchallenge.model.Reserve;
import com.dlalo.truenorth.springboot.backendchallenge.service.CampsiteService;
import com.dlalo.truenorth.springboot.backendchallenge.util.CustomErrorType;

@RestController
@RequestMapping("/reserve")
public class ReserveController {
	
	private static final Logger logger = LoggerFactory.getLogger(ReserveController.class);
	
	@Autowired
	CampsiteService service;
	
	/* UPDATE RESERVE */
    @RequestMapping(value = "/{reserveId}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateReserve(
    		@RequestBody Reserve reserve,
    		@PathVariable(value="reserveId") Long reserveId, UriComponentsBuilder ucBuilder) {
    	
		HttpHeaders headers = new HttpHeaders();
		if (!service.existsReserve(reserveId)) {
			CustomErrorType error = new CustomErrorType("Reserve not found");
			headers.setLocation(ucBuilder.path("/reserve/{reserveId}").buildAndExpand(reserveId).toUri());
			return new ResponseEntity<CustomErrorType>(error, headers, HttpStatus.BAD_REQUEST);
		}	else {
			CustomErrorType msg = new CustomErrorType("Reserve updated");
			service.updateReserve(reserve, reserveId);
			headers.setLocation(ucBuilder.path("/reserve/{reserveId}").buildAndExpand(reserveId).toUri());
			return new ResponseEntity<CustomErrorType>(msg, headers, HttpStatus.OK);
		}
    }
    
    /* CANCEL RESERVE */
    @RequestMapping(value = "/{reserveId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteReserve(@PathVariable(value="reserveId") Long reserveId, UriComponentsBuilder ucBuilder) {
    	
		HttpHeaders headers = new HttpHeaders();
		if (!service.existsReserve(reserveId)) {
			CustomErrorType error = new CustomErrorType("Reserve not found");
			headers.setLocation(ucBuilder.path("/reserve/{reserveId}").buildAndExpand(reserveId).toUri());
			return new ResponseEntity<CustomErrorType>(error, headers, HttpStatus.BAD_REQUEST);
		}	else {
			service.deleteReserve(reserveId);
			CustomErrorType msg = new CustomErrorType("Reserve deleted");
			headers.setLocation(ucBuilder.path("/reserve/{reserveId}").buildAndExpand(reserveId).toUri());
			return new ResponseEntity<CustomErrorType>(msg, headers, HttpStatus.OK);
		}
    }
}
