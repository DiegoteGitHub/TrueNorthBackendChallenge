package com.dlalo.truenorth.springboot.backendchallenge.service;

import java.time.LocalDate;

import com.dlalo.truenorth.springboot.backendchallenge.model.Campsite;
import com.dlalo.truenorth.springboot.backendchallenge.model.Reserve;

public interface CampsiteService {

	Campsite getCampsiteAvailability(Long campsiteId, LocalDate arrivalDate, LocalDate departureDate);
	
	boolean existsCampsite(Long campsiteId);

	void reserve(Reserve reserve, Long campsiteId);
	
}
