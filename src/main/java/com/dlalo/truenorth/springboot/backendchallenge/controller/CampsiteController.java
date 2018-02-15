package com.dlalo.truenorth.springboot.backendchallenge.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.dlalo.truenorth.springboot.backendchallenge.exception.CampsiteExceptionHandler;
import com.dlalo.truenorth.springboot.backendchallenge.model.Campsite;
import com.dlalo.truenorth.springboot.backendchallenge.model.Reserve;
import com.dlalo.truenorth.springboot.backendchallenge.service.CampsiteService;
import com.dlalo.truenorth.springboot.backendchallenge.util.CustomErrorType;
import com.dlalo.truenorth.springboot.backendchallenge.util.Utilities;


@RestController
@RequestMapping("/campsite")
public class CampsiteController {
	
	private static final Logger logger = LoggerFactory.getLogger(CampsiteController.class);
	
	@Autowired
	CampsiteService service;
	
	@ExceptionHandler(CampsiteExceptionHandler.class)	
	@RequestMapping(path = "/{cmpsId}" , method = RequestMethod.GET, produces= { "application/json" })
	public ResponseEntity<?> getCampsiteAvailabilityDateRange(
			@PathVariable(value="cmpsId") Long cmpsId,
			@RequestParam(value="fromDate", required = false) Long fromDate, 
			@RequestParam(value="toDate", required = false) Long toDate,
			UriComponentsBuilder ucBuilder) {
		
		HttpHeaders headers = new HttpHeaders();
		if (!service.existsCampsite(cmpsId)) {
			headers.setLocation(ucBuilder.path("/campsite/{cmpsId}").buildAndExpand(cmpsId).toUri());
        	CustomErrorType error =  new CustomErrorType("Campsite with ID => " + cmpsId + " does not exist");
        	return new ResponseEntity<CustomErrorType>(error, headers, HttpStatus.BAD_REQUEST);
		} else {
			Campsite campsite = service.getCampsiteAvailability(cmpsId, Utilities.getDateFromUnixTime(fromDate), Utilities.getDateFromUnixTime(toDate));
			headers.setLocation(ucBuilder.path("/campsite/{cmpsId}").buildAndExpand(campsite.getId()).toUri());
			return new ResponseEntity<Campsite>(campsite, headers, HttpStatus.OK);
		}
	}
	
	@RequestMapping(method = RequestMethod.GET, produces= { "application/json" })
	ResponseEntity<List<Campsite>>getAllCampsitesAvailability(
			@RequestParam(value="fromDate", required = false) Long fromDate, 
			@RequestParam(value="toDate", required = false) Long toDate, UriComponentsBuilder ucBuilder) {
		
		List<Campsite> campsites = service.getCampsitesAvailability(Utilities.getDateFromUnixTime(fromDate), Utilities.getDateFromUnixTime(toDate));
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/campsite").build().toUri());
		return new ResponseEntity<List<Campsite>>(campsites, headers, HttpStatus.OK);
	}
	

    @RequestMapping(value = "/{cmpsId}/reserve", method = RequestMethod.POST)
    public ResponseEntity<?> reserveCampsite(
    		@RequestBody Reserve reserve,
    		@PathVariable(value="cmpsId") Long cmpsId, UriComponentsBuilder ucBuilder) {
    	
        logger.debug("Attempting to reserve campsite => " + cmpsId + " with reserve => " + reserve);
        
        HttpHeaders headers = new HttpHeaders();
        
        if (!service.existsCampsite(cmpsId)) {
        	logger.error("Campsite with ID => " + cmpsId + " does not exist");  
        	CustomErrorType error =  new CustomErrorType("Campsite with ID => " + cmpsId + " does not exist");
        	return new ResponseEntity<CustomErrorType>(error, headers, HttpStatus.BAD_REQUEST);
        }
        try {
        	service.reserve(reserve, cmpsId);
        	logger.debug("Created reserve with ID => " + reserve.getId());
        	headers.setLocation(ucBuilder.path("/reserve/{reserveId}").buildAndExpand(reserve.getId()).toUri());
            return new ResponseEntity<Reserve>(reserve, headers, HttpStatus.CREATED);
        } catch (RuntimeException r) {
        	logger.error("Unable to reserve campsite, cause => " + r.getMessage());
        	headers.setLocation(ucBuilder.path("/{cmpsId}/reserve").buildAndExpand(cmpsId).toUri());
        	CustomErrorType error =  new CustomErrorType("Unable to reserve campsite, cause => " + r.getMessage());
        	return new ResponseEntity<CustomErrorType>(error, headers, HttpStatus.BAD_REQUEST);
        }
    }
}
