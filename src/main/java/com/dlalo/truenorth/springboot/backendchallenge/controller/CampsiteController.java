package com.dlalo.truenorth.springboot.backendchallenge.controller;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;

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


@RestController
@RequestMapping("/campsite")
public class CampsiteController {
	
	private static final Logger logger = LoggerFactory.getLogger(CampsiteController.class);
	
	@Autowired
	CampsiteService service;
	
	@ExceptionHandler(CampsiteExceptionHandler.class)	
	@RequestMapping(path = "/{cmpsId}" , method = RequestMethod.GET, produces= { "application/json" })
	public ResponseEntity<Campsite> getCampsiteAvailabilityDateRange(
			@PathVariable(value="cmpsId") Long cmpsId,
			@RequestParam(value="fromDate", required = false) Long fromDate, 
			@RequestParam(value="toDate", required = false) Long toDate,
			UriComponentsBuilder ucBuilder) {
		
		HttpHeaders headers = new HttpHeaders();
		if (!this.validateCampsite(cmpsId)) {
			headers.setLocation(ucBuilder.path("/campsite/{cmpsId}").buildAndExpand(cmpsId).toUri());
			return new ResponseEntity<Campsite>(headers, HttpStatus.NO_CONTENT);
		}	else {
			Campsite campsite = service.getCampsiteAvailability(cmpsId, getDateFromUnixTime(fromDate), getDateFromUnixTime(toDate));
			headers.setLocation(ucBuilder.path("/campsite/{cmpsId}").buildAndExpand(campsite.getId()).toUri());
			return new ResponseEntity<Campsite>(campsite, headers, HttpStatus.OK);
		}
	}
	
	@RequestMapping(method = RequestMethod.GET, produces= { "application/json" })
	String getAllCampsitesAvailability() {
		return "You are looking for availability of all campsites ";
	}
	

    @RequestMapping(value = "/{cmpsId}/reserve", method = RequestMethod.POST)
    public ResponseEntity<Reserve> reserveCampsite(
    		@RequestBody Reserve reserve,
    		@PathVariable(value="cmpsId") Long cmpsId, UriComponentsBuilder ucBuilder) {
    	
        logger.debug("Reserving campsite => " + cmpsId + " with reserve => " + reserve);
 
//        if (campsiteService.validateReserve(campsite)) {
//            logger.error("Unable to create. A User with name {} already exist", user.getName());
//            return new ResponseEntity(new CustomErrorType("Unable to create. A User with name " + 
//            user.getName() + " already exist."),HttpStatus.CONFLICT);
//        }
//        userService.saveUser(user);
 
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/campsite/{cmpsId}/reserve").buildAndExpand(reserve.getId()).toUri());
        return new ResponseEntity<Reserve>(reserve, headers, HttpStatus.CREATED);
    }

	private boolean validateCampsite(Long cmpsId) {
		logger.debug("Validating existence of campsite => " + cmpsId);
		return service.existsCampsite(cmpsId);
	}
	
	private LocalDate getDateFromUnixTime(Long milliSeconds) {
		if (milliSeconds != null) {
			return Instant.ofEpochMilli(milliSeconds * 1000).atZone(ZoneId.systemDefault()).toLocalDate();
		} else {
			return null;
		}
	}

}
