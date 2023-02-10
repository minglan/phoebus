/**
 *
 */
package org.phoebus.alarm.poster;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.phoebus.alamr.poster.oshi.OperateSystemUtil;
import org.phoebus.alarm.poster.httpclient.MessagePost;
import org.phoebus.applications.alarm.messages.AlarmTalkMessage;
import org.phoebus.util.output.Display;

import static org.phoebus.alarm.poster.AlarmPosterService.logger;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

/**
 * A Utility service to allow for batched indexing of alarm state, config, and command messages to an elastic backend
 *
 * @author Kunal Shroff {@literal <kunalshroff9@gmail.gov>}
 *
 */
@SuppressWarnings("nls")
public class MessagePosterHelper {
    Properties props = PropertiesHelper.getProperties();


    private static MessagePosterHelper instance;
  
    private static final AtomicBoolean esInitialized = new AtomicBoolean();

    private static final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(4);
    ScheduledFuture<?> job;
    // State messages to be indexed
    BlockingQueue<SimpleImmutableEntry<String,AlarmTalkMessage>> stateMessagedQueue = new LinkedBlockingDeque<>();

    private final ObjectMapper mapper = new ObjectMapper();

    private MessagePosterHelper() {
        try {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                logger.info("Shutting down the ElasticClientHelper.");
                
            }));

            // Create the low-level client
          
            // Initialize the elastic templates
            esInitialized.set(!Boolean.parseBoolean(props.getProperty("es_create_templates")));

            // Start the executor for periodically logging into es
            job = scheduledExecutorService.scheduleAtFixedRate(new flush2AccAlarmServer(stateMessagedQueue),
                    0, 250, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            try {
                job.cancel(false);
              
            } catch (Exception ex) {
                logger.log(Level.WARNING, "Failed to close the elastic client", ex);
            }
        }
    }

    public static MessagePosterHelper getInstance() {
        if (instance == null) {
            instance = new MessagePosterHelper();
        }
        return instance;
    }


    /**
     * Index an alarm state message
     * @param indexName
     * @param alarmStateMessage
     */
    public void indexAlarmStateDocuments(String indexName, AlarmTalkMessage alarmStateMessage) {
        try {
            stateMessagedQueue.put(new SimpleImmutableEntry<>(indexName,alarmStateMessage));
        } catch (InterruptedException e) {
            logger.log(Level.SEVERE, "failed to log message " + alarmStateMessage + " to index " + indexName, e);
        }
    }

 

    /**
     * A helper class which implements 2 queues for allowing bulk logging of state and config messages
     */
    private static class flush2AccAlarmServer implements Runnable {

        private final BlockingQueue<SimpleImmutableEntry<String,AlarmTalkMessage>> stateMessagedQueue;
        Map<String, Object> paramsMap	=new HashMap<String, Object>();
        public flush2AccAlarmServer(BlockingQueue<SimpleImmutableEntry<String,AlarmTalkMessage>> stateMessagedQueue) {
            this.stateMessagedQueue = stateMessagedQueue;
         
        }

        @Override
        public void run() {

            if(stateMessagedQueue.size() > 0){
                logger.log(Level.INFO, "batch execution of : " + stateMessagedQueue.size() + " state messages ");
             
                Collection<SimpleImmutableEntry<String,AlarmTalkMessage>> statePairs = new ArrayList<>();
                stateMessagedQueue.drainTo(statePairs);
                
                statePairs.forEach( pair ->{AlarmTalkMessage message=pair.getValue();
                	Display.output(message.getSeverity(),message.getTalk());
                	String machInfo="CPU: "+OperateSystemUtil.getCpuInfo().get("cpu当前使用率")+" MEM: "+OperateSystemUtil.getMemoryInfo().get("usageRate");
                	paramsMap.put("para1", 90+"           特别推送 \n"+message.getSeverity()+message.getTalk()+"\n"+machInfo);
                	try {
						MessagePost.say(paramsMap);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                });


            }
        }
        private static Properties props = new Properties();
        {
            props.putAll(PropertiesHelper.getProperties());
        }

      

    }
}
