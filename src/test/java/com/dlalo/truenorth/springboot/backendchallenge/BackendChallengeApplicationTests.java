package com.dlalo.truenorth.springboot.backendchallenge;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import com.dlalo.truenorth.springboot.backendchallenge.model.Campsite;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BackendChallengeApplicationTests {
	
	public static final String REST_SERVICE_URI = "http://localhost:8080";
	
	private static final Logger logger = LoggerFactory.getLogger(BackendChallengeApplicationTests.class);

    @Test
    public void noParamShouldReturnAllCampsitesAvailability() throws Exception {
    	
    	Long fromDate = Instant.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()).toEpochMilli();
    	Long toDate = Instant.from(LocalDate.now().plusDays(4).atStartOfDay(ZoneId.systemDefault()).toInstant()).toEpochMilli();
    	RestTemplate restTemplate = new RestTemplate();
    	String url = REST_SERVICE_URI + "/campsite?fromDate=" + fromDate.longValue() + "&toDate=" + toDate.longValue();
    	
        @SuppressWarnings("unchecked")
		List<LinkedHashMap<String, Object>> campsitesMap = restTemplate.getForObject(url, List.class);
    
        if (campsitesMap != null) {
            for (LinkedHashMap<String, Object> map : campsitesMap) {
            	Set<String> keys = map.keySet();
                for(String k:keys){
                    logger.info(k + " -- " + map.get(k));
                }
            }
        } else {
            logger.info("No campsites exist");
        }
    }
    
    @Test
    public void getFirstCampsiteAvailability() throws Exception {
    	
    	Long fromDate = Instant.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()).toEpochMilli();
    	Long toDate = Instant.from(LocalDate.now().plusDays(4).atStartOfDay(ZoneId.systemDefault()).toInstant()).toEpochMilli();
    	RestTemplate restTemplate = new RestTemplate();
    	String url = REST_SERVICE_URI + "/campsite/1?fromDate=" + fromDate.longValue() + "&toDate=" + toDate.longValue();
    	
        @SuppressWarnings("unchecked")
		Campsite campsite = restTemplate.getForObject(url, Campsite.class);
    
        logger.info("Campsite ID => " + campsite.getId() + " Name => " + campsite.getName() + " Available days " + campsite.getAvailableDays());
    }
//
//    @Test
//    public void paramGreetingShouldReturnTailoredMessage() throws Exception {
//
//        this.mockMvc.perform(get("/greeting").param("name", "Spring Community"))
//                .andDo(print()).andExpect(status().isOk())
//                .andExpect(jsonPath("$.content").value("Hello, Spring Community!"));
//    }

	@Test
	public void contextLoads() {
	}
}
