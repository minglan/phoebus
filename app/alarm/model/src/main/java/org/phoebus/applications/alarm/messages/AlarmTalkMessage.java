package org.phoebus.applications.alarm.messages;

import static org.phoebus.applications.alarm.AlarmSystem.logger;
import static org.phoebus.applications.alarm.messages.AlarmMessageUtil.objectMapper;

import java.time.Instant;
import java.util.logging.Level;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;

/** Talk message */
@JsonInclude(Include.NON_NULL)
public class AlarmTalkMessage {

    private String severity;
    private boolean standout;
    private String talk;
    private String config;
    private Instant message_time;
    /** Constructor */
    public AlarmTalkMessage() {
        super();
    }

    /** @return Severity */
    public String getSeverity() {
        return severity;
    }

    /** @param severity New value */
    public void setSeverity(String severity) {
        this.severity = severity;
    }

    /** @return Should message stand out, not be suppressed in flurry of messages? */
    public boolean isStandout() {
        return standout;
    }

    /** @param standout New value */
   public void setStandout(boolean standout) {
        this.standout = standout;
    }

    /** @return Annunciation text */
    public String getTalk() {
        return talk;
    }

    /** @param talk New value */
    public void setTalk(String talk) {
        this.talk = talk;
    }


    /** @return data */
    public String getConfig() {
        return config;
    }

    /** @param config New value */
    public void setConfig(String config) {
        this.config = config;
    }
    
    /** @return data */
    public Instant getMessage_time() {
        return message_time;
    }

    /** @param message_time New value */
    public void setMessage_time(Instant message_time) {
        this.message_time = message_time;
    }
    
    @Override
    public String toString() {
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            logger.log(Level.WARNING, "failed to parse the alarm talk message ", e);
        }
        return "";
    }
}
