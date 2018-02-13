package com.dlalo.truenorth.springboot.backendchallenge.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

public class Utilities {
	
	public static final int MAXIMUM_MONTHS_AHEAD = 1;
	public static final int MINIMUM_DAYS_AHEAD = 1;
	public static final int MAXIMUM_RESERVE_DAYS = 3;
	
	public static LocalDate getDateFromUnixTime(Long milliSeconds) {
		if (milliSeconds != null) {
			return Instant.ofEpochMilli(milliSeconds * 1000).atZone(ZoneId.systemDefault()).toLocalDate();
		} else {
			return null;
		}
	}

}
