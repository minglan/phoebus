package org.phoebus.alarm.poster.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.phoebus.alarm.poster.AlarmPosterService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A REST service for querying the alarm message history
 *
 * @author Kunal Shroff
 */
@RestController
public class SearchController {

    static final Logger logger = Logger.getLogger(SearchController.class.getName());

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * @return Information about the alarm logging service
     */
    @GetMapping
    public String info() {

        Map<String, Object> alarmPosterServiceInfo = new LinkedHashMap<String, Object>();
        alarmPosterServiceInfo.put("name", "Alarm Poster Service");
        //alarmLoggingServiceInfo.put("version", version);

        Map<String, String> elasticInfo = new LinkedHashMap<String, String>();
        try {
           

            elasticInfo.put("status", "Connected");
        } catch (Exception e) {
         e.printStackTrace();

        }
        alarmPosterServiceInfo.put("elastic", elasticInfo);
        try {
            return objectMapper.writeValueAsString(alarmPosterServiceInfo);
        } catch (JsonProcessingException e) {
            AlarmPosterService.logger.log(Level.WARNING, "Failed to create Alarm Poster service info resource.", e);
            return "Failed to gather Alarm Poster service info";
        }
    }



}
