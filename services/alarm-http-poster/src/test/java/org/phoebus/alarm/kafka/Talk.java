package org.phoebus.alarm.kafka;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.TopicPartition;
import org.phoebus.util.output.Display;
@SuppressWarnings("nls")
public class Talk {
	public static void main(String[] args) throws InterruptedException, ExecutionException {
        Properties props = new Properties();
        UUID uuid = UUID.randomUUID();
//kafka集群多个kafka,获取topic是按顺序获取的，比如第一个192.168.11.2是正常的就只会获取第一个kafka的topic，后面的kafka就不会去获取,如果第一个服务异常才会去获取第二个kafka
        props.put("bootstrap.servers", "10.1.204.91:9092,10.1.204.92:9092,10.1.204.93:9092");
        //KafkaUtils.getTopicNames(zkAddress)
//        ListTopicsResult result = KafkaAdminClient.create(props).listTopics();
//        KafkaFuture<Set<String>> set = result.names();
//        System.out.println(set.get());

        // group.id，指定了消费者所属群组
        props.put("group.id",uuid.toString());
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        KafkaConsumer<String, String> consumer =    new KafkaConsumer<String, String>(props);
     // topic name is “AcceleratorTalk”
        consumer.subscribe(Collections.singletonList("Accelerator"), new ConsumerRebalanceListener() {
            @Override
            public void onPartitionsRevoked(Collection<TopicPartition> collection) {

            }

            @Override
            public void onPartitionsAssigned(Collection<TopicPartition> collection) {
                Map<TopicPartition,Long> beginningOffset = consumer.beginningOffsets(collection);

                //读取历史数据 --from-beginning
                for(Map.Entry<TopicPartition,Long> entry : beginningOffset.entrySet()){
                    // 基于seek方法
                    //TopicPartition tp = entry.getKey();
                    //long offset = entry.getValue();
                    //consumer.seek(tp,offset);

                    // 基于seekToBeginning方法
                    consumer.seekToBeginning(collection);
                }
            }});
   
        
//        consumer.subscribe(Collections.singletonList("AcceleratorTalk"));
        int i=0;
            while (true) {
                // 100 是超时时间（ms），在该时间内 poll 会等待服务器返回数据
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100)); 

                // poll 返回一个记录列表。
                // 每条记录都包含了记录所属主题的信息、记录所在分区的信息、记录在分区里的偏移量，以及记录的键值对。
                for (ConsumerRecord<String, String> record : records) {
                	LocalDateTime ldt= 	LocalDateTime.ofInstant(Instant.ofEpochMilli(record.timestamp()), ZoneId.systemDefault()) ;
//                if(record.value().contains("主铁电源25")) 
                	Display.output(i++,
                        record.topic(), record.partition(), record.offset(), ldt,
                        record.key(), record.value());
                        

            }
        } 
}}
