package com.dlalo.truenorth.springboot.backendchallenge;

import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import com.dlalo.truenorth.springboot.backendchallenge.model.Campsite;
import com.dlalo.truenorth.springboot.backendchallenge.model.Reserve;
import com.dlalo.truenorth.springboot.backendchallenge.util.ConcurrentRequestGenerator;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BackendChallengeApplicationTests {
	
	public static final String REST_SERVICE_URI = "http://localhost:8080";
	
	private static final Logger logger = LoggerFactory.getLogger(BackendChallengeApplicationTests.class);

    @Test
    @SuppressWarnings("unchecked")
    public void noCampsiteIdShouldReturnAllCampsitesAvailability() throws Exception {
    	
    	// Check availability starting from tomorrow
    	Long fromDate = Instant.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()).toEpochMilli();
    	// And to three days ahead
    	Long toDate = Instant.from(LocalDate.now().plusDays(4).atStartOfDay(ZoneId.systemDefault()).toInstant()).toEpochMilli();
    	RestTemplate restTemplate = new RestTemplate();
    	String url = REST_SERVICE_URI + "/campsite?fromDate=" + fromDate.longValue() + "&toDate=" + toDate.longValue();
		List<LinkedHashMap<String, Object>> campsitesMap = restTemplate.getForObject(url, List.class);    
        if (campsitesMap != null) {
        	// Two campsites were created
        	assertTrue(campsitesMap.size() == 2);
        	// No reserves
        	List<String> reserves1 = (List<String>) campsitesMap.get(0).get("reserves");
        	List<String> reserves2 = (List<String>) campsitesMap.get(1).get("reserves");
        	assertTrue(reserves1.isEmpty());
        	assertTrue(reserves2.isEmpty());
        	// Available days for the two campsites should be 3 because there are no reserves
        	List<String> avaliableDays1 = (List<String>) campsitesMap.get(0).get("availableDays");
        	List<String> avaliableDays2 = (List<String>) campsitesMap.get(1).get("availableDays");
        	assertTrue(avaliableDays1.size() == 3);
        	assertTrue(avaliableDays2.size() == 3);
        } else {
            logger.info("No campsites exist");
        }
    }
    
    @Test
    public void getFirstCampsiteAvailabilityBetweenDates() throws Exception {
    	
    	// Check availability starting from tomorrow
    	Long fromDate = Instant.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()).toEpochMilli();
    	// And to three days ahead
    	Long toDate = Instant.from(LocalDate.now().plusDays(4).atStartOfDay(ZoneId.systemDefault()).toInstant()).toEpochMilli();
    	RestTemplate restTemplate = new RestTemplate();
    	String url = REST_SERVICE_URI + "/campsite/1?fromDate=" + fromDate.longValue() + "&toDate=" + toDate.longValue();
    	
		Campsite campsite = restTemplate.getForObject(url, Campsite.class);
		// Campsite must exist
        assertTrue(campsite != null);
        // No reserves
        assertTrue(campsite.getReserves().isEmpty());
        // Three available days because there are no reserves
        assertTrue(campsite.getAvailableDays().size() == 3);
    }
    
    @Test
    public void getNotExistingCampsite() throws Exception {
    	
    	RestTemplate restTemplate = new RestTemplate();
    	String url = REST_SERVICE_URI + "/campsite/12";    	
		Campsite campsite = restTemplate.getForObject(url, Campsite.class);
		// Campsite must not exist
		assertTrue(campsite == null);
    }
    
    @Test
    public void makeSuccessfulReserve() throws Exception {
    	
    	Long arrivalDate = Instant.from(LocalDate.now().plusDays(4).atStartOfDay(ZoneId.systemDefault()).toInstant()).toEpochMilli();
    	Long departureDate = Instant.from(LocalDate.now().plusDays(7).atStartOfDay(ZoneId.systemDefault()).toInstant()).toEpochMilli();
    	RestTemplate restTemplate = new RestTemplate();
    	String url = REST_SERVICE_URI + "/campsite/1/reserve";
    	Reserve reserve = new Reserve("diego.lalo@gmail.com", "Diego Lalo", arrivalDate, departureDate);
		URI reserveUri = restTemplate.postForLocation(url, reserve, Reserve.class);
		// Retrieve created reserve
		reserve = restTemplate.getForObject(reserveUri, Reserve.class);
		// Check contents
		assertTrue(reserve != null);
		assertTrue(reserve.getEmail().equals("diego.lalo@gmail.com"));
		assertTrue(reserve.getFullName().equals("Diego Lalo"));
		assertTrue(reserve.getArrivalDate().equals(arrivalDate));
		assertTrue(reserve.getDepartureDate().equals(departureDate));
		restTemplate.delete(reserveUri);
    }
    
    @Test
    public void updateReserve() throws Exception {
    	
    	// Create reserve to be updated
    	Long arrivalDate = Instant.from(LocalDate.now().plusDays(4).atStartOfDay(ZoneId.systemDefault()).toInstant()).toEpochMilli();
    	Long departureDate = Instant.from(LocalDate.now().plusDays(7).atStartOfDay(ZoneId.systemDefault()).toInstant()).toEpochMilli();
    	RestTemplate restTemplate = new RestTemplate();
    	String url = REST_SERVICE_URI + "/campsite/1/reserve";
    	Reserve reserve = new Reserve("diego.lalo@gmail.com", "Diego Lalo", arrivalDate, departureDate);
		URI reserveUri = restTemplate.postForLocation(url, reserve, Reserve.class);
		// Retrieve created reserve
		reserve = restTemplate.getForObject(reserveUri, Reserve.class);
    	// Create new values to update the created reserve
    	Long updatedArrivalDate = Instant.from(LocalDate.now().plusDays(11).atStartOfDay(ZoneId.systemDefault()).toInstant()).toEpochMilli();
    	Long updatedDepartureDate = Instant.from(LocalDate.now().plusDays(14).atStartOfDay(ZoneId.systemDefault()).toInstant()).toEpochMilli();
    	String updatedEmail = "UPDATED.RESERVE@gmail.com";
    	String updatedFullName = "Diego Lalo UPDATED";
    	reserve.setEmail(updatedEmail);
    	reserve.setFullName(updatedFullName);
    	reserve.setArrivalDate(updatedArrivalDate);
    	reserve.setDepartureDate(updatedDepartureDate);
    	restTemplate.put(reserveUri, reserve);
    	// Retrieve updated reserve
		reserve = restTemplate.getForObject(reserveUri, Reserve.class);
		// Check contents
		assertTrue(reserve != null);
		assertTrue(reserve.getEmail().equals(updatedEmail));
		assertTrue(reserve.getFullName().equals(updatedFullName));
		assertTrue(reserve.getArrivalDate().equals(updatedArrivalDate));
		assertTrue(reserve.getDepartureDate().equals(updatedDepartureDate));
		restTemplate.delete(reserveUri);
    }
    
    @Test
    public void makeConcurrentReserves() throws Exception {
    	// Constants
    	final int NUM_POOL_THREADS = 10;
    	final int NUM_PARALLEL_REQUESTS = 10;
    	// The reserve to be issued in parallel
    	Long arrivalDate = Instant.from(LocalDate.now().plusDays(7).atStartOfDay(ZoneId.systemDefault()).toInstant()).toEpochMilli();
    	Long departureDate = Instant.from(LocalDate.now().plusDays(10).atStartOfDay(ZoneId.systemDefault()).toInstant()).toEpochMilli();
    	String url = REST_SERVICE_URI + "/campsite/1/reserve";
    	Reserve reserve = new Reserve("diego.lalo@gmail.com", "Diego Lalo", arrivalDate, departureDate);
    	ConcurrentRequestGenerator generator = new ConcurrentRequestGenerator(NUM_POOL_THREADS, NUM_PARALLEL_REQUESTS, reserve, url);
    	generator.run();
    }
    
    @Test
    public void cancelReserve() throws Exception {
    	
    	// Create reserve to be deleted
    	Long arrivalDate = Instant.from(LocalDate.now().plusDays(4).atStartOfDay(ZoneId.systemDefault()).toInstant()).toEpochMilli();
    	Long departureDate = Instant.from(LocalDate.now().plusDays(7).atStartOfDay(ZoneId.systemDefault()).toInstant()).toEpochMilli();
    	RestTemplate restTemplate = new RestTemplate();
    	String url = REST_SERVICE_URI + "/campsite/1/reserve";
    	Reserve reserve = new Reserve("diego.lalo@gmail.com", "Diego Lalo", arrivalDate, departureDate);
		URI reserveUri = restTemplate.postForLocation(url, reserve, Reserve.class);
		// Cancel reserve
		restTemplate.delete(reserveUri);
		// Verify cancellation
		reserve = restTemplate.getForObject(reserveUri, Reserve.class);
		assertTrue(reserve == null);
    }
//
//    @Test
//    public void paramGreetingShouldReturnTailoredMessage() throws Exception {
//
//        this.mockMvc.perform(get("/greeting").param("name", "Spring Community"))
//                .andDo(print()).andExpect(status().isOk())
//                .andExpect(jsonPath("$.content").value("Hello, Spring Community!"));
//    }
}
