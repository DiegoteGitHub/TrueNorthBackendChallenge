package com.dlalo.truenorth.springboot.backendchallenge.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dlalo.truenorth.springboot.backendchallenge.model.Campsite;
import com.dlalo.truenorth.springboot.backendchallenge.model.Reserve;
import com.dlalo.truenorth.springboot.backendchallenge.repository.CampsiteRepository;

@Service("campsiteService")
public class CampsiteServiceImpl implements CampsiteService {
	
	@Autowired
	CampsiteRepository campsiteRepository;
	
	private static final int DEFAULT_RANGE_MONTHS = 1;
	private static final int MINIMUM_DAYS_AHEAD = 1;
	
	private static final Logger logger = LoggerFactory.getLogger(CampsiteServiceImpl.class);

	@Override
	public Campsite getCampsiteAvailability(Long campsiteId, LocalDate fromDate, LocalDate toDate) {
		Optional<Campsite> optCampsite = campsiteRepository.findById(campsiteId);
		LocalDate today = LocalDate.now();
			
		if (fromDate != null && toDate != null) {
			if (fromDate.isEqual(toDate))
				throw new RuntimeException("fromDate and toDate could not be the same");
			if (fromDate.isBefore(today) || fromDate.isEqual(today))
				throw new RuntimeException("fromDate could not be equal or before current date");
			if (toDate.isBefore(today) || toDate.isEqual(today))
				throw new RuntimeException("toDate could not be equal or before current date");
			if (toDate.isBefore(fromDate))
				throw new RuntimeException("toDate should be after fromDate");
		} else {
			if (fromDate != null ^ toDate != null) {
				throw new RuntimeException("Cannot send only one date");
			} else {
				fromDate = today.plusDays(MINIMUM_DAYS_AHEAD);
				toDate = fromDate.plusMonths(DEFAULT_RANGE_MONTHS);
			}
		}
		
		logger.debug("From date => " + fromDate);
		logger.debug("To date => " + toDate);

		Campsite campsite = optCampsite.get();
		if (campsite != null) {
			campsite.setAvailableDays(new ArrayList<LocalDate>());
			for (LocalDate i = fromDate; i.isBefore(toDate); i = i.plusDays(1)) {
				if (campsite.getReserves().isEmpty()) {
					campsite.getAvailableDays().add(i);
					logger.debug("Available date => " + i);
				} else {
					for (Reserve r: campsite.getReserves()) {
						if (!dateOverlapsWithReserve(i, r)) {
							campsite.getAvailableDays().add(i);
							logger.debug("Available date => " + i);
						} else {
							break;
						}
					}
				}
			}
		}
		return campsite;		
	}

	private boolean dateOverlapsWithReserve(LocalDate i, Reserve r) {
		if ( (i.isEqual(r.getArrivalDate()) || i.isAfter(r.getArrivalDate())) && i.isBefore(r.getDepartureDate()) )
				return true;
		else
			return false;
	}

	@Override
	public boolean existsCampsite(Long campsiteId) {
		return campsiteRepository.existsById(campsiteId);
	}

}
