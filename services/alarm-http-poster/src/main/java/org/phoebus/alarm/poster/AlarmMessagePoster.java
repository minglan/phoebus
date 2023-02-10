package org.phoebus.alarm.poster;

import static org.phoebus.alarm.poster.AlarmPosterService.logger;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ConsumerGroupDescription;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.kstream.TransformerSupplier;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.processor.TimestampExtractor;
import org.phoebus.applications.alarm.AlarmSystem;
import org.phoebus.applications.alarm.client.KafkaHelper;
import org.phoebus.applications.alarm.messages.AlarmTalkMessage;
import org.phoebus.applications.alarm.messages.MessageParser;
import org.phoebus.framework.preferences.AnnotatedPreferences;
import org.phoebus.framework.preferences.PreferencesReader;
import org.phoebus.util.output.Display;


public class AlarmMessagePoster implements Runnable {

	private final String topic;
	private final Serde<AlarmTalkMessage> alarmMessageSerde;

	private final Pattern pattern = Pattern.compile("(\\w*://\\S*)");

	private static final String STATE_INDEX_FORMAT = "_alarms_state";

	private static final Pattern digitalPattern = Pattern.compile("^(\\d{2}+(.*?))");

	/**
	 * Create a alarm logger for the alarm messages (both state and configuration)
	 * for a given alarm server topic. This runnable will create the kafka streams
	 * for the given alarm messages which match the format 'topic'
	 * 
	 * @param topic - the alarm topic in kafka
	 */
	public AlarmMessagePoster(String topic) {
		super();
		this.topic = topic;
		MessageParser<AlarmTalkMessage> messageParser = new MessageParser<>(AlarmTalkMessage.class);
		alarmMessageSerde = Serdes.serdeFrom(messageParser, messageParser);

	}

	@Override
	public void run() {
		logger.info("Starting the alarm messages stream consumer for " + topic);

		Properties props = new Properties();
		props.putAll(PropertiesHelper.getProperties());

		Properties kafkaProps = KafkaHelper.loadPropsFromFile(props.getProperty("kafka_properties", ""));
		kafkaProps.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-" + topic + "-alarm-messages");

		if (props.containsKey(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG)) {
			kafkaProps.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, props.get(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG));
		} else {
			kafkaProps.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		}

		// Attach a message time stamp.
		StreamsBuilder builder = new StreamsBuilder();

		KStream<String, AlarmTalkMessage> alarms = builder.stream(topic,
				Consumed.with(Serdes.String(), alarmMessageSerde).withTimestampExtractor(new TimestampExtractor() {

					@Override
					public long extract(ConsumerRecord<Object, Object> record, long previousTimestamp) {
						Display.output(record.timestamp(), timestampToLDT(record.timestamp()));
						Display.output(previousTimestamp, timestampToLDT(previousTimestamp));
						return record.timestamp();
					}
				}));

		alarms = alarms.filter((k, v) -> {
			Display.output();
			return v != null;
		});

		alarms.transform(new TransformerSupplier<String, AlarmTalkMessage, KeyValue<String, AlarmTalkMessage>>() {

			@Override
			public Transformer<String, AlarmTalkMessage, KeyValue<String, AlarmTalkMessage>> get() {
				return new Transformer<String, AlarmTalkMessage, KeyValue<String, AlarmTalkMessage>>() {
					private ProcessorContext context;

					@Override
					public void init(ProcessorContext context) {
						this.context = context;
					}

					@Override
					public KeyValue<String, AlarmTalkMessage> transform(String key, AlarmTalkMessage value) {
						key = key.replace("\\", "");
						value.setConfig(key);
						value.setMessage_time(Instant.ofEpochMilli(context.timestamp()));
						Display.output(key, value);
						return new KeyValue<String, AlarmTalkMessage>(key, value);
					}

					@Override
					public void close() {

					}

				};
			}
		});
// 将server发过来的消息中各种关键字进行替换
		alarms = alarms.map((key, value) -> {
			logger.config("Processing alarm talk message with key : " + key != null ? key
					: "null" + " " + value != null ? value.toString() : "null");

			String severity = value.getSeverity();
			final boolean standout = value.isStandout();
			final String message = value.getTalk();
			final String time = value.getMessage_time().atZone(ZoneId.systemDefault())
					.format(DateTimeFormatter.ofPattern("yyyy/MM/d HH:mm:ss"));
			String value1 = null;
			String modify = null;
			if (message.contains("FLAG")) {
				String[] split = message.split("FLAG");
				value1 = split[split.length - 1];
				modify = split[0];
			} else {
				modify = message;
				Display.output("不包含FLAG关键字", message);

			}
			if (modify.contains("Alarm:") || modify.contains("alarm:")) {
				String[] split = modify.split("[aA]larm:");

				String sub = split[split.length - 1].trim();
				modify = noToSys(sub);
//				Matcher matcher = digitalPattern.matcher(sub);
//				boolean beginWith2Digitals = matcher.matches();
//				Display.output("两位数字开头",beginWith2Digitals,sub);
//				if (beginWith2Digitals) {
//					modify = sub.substring(2);
//					String no=sub.substring(0, 2);
//
//					Display.output(no,modify);
//				
//					String sys="";
//					switch (no){
//					case "01":sys="束测";break;
//					case "02":sys="直线高频";break;
//					case "03":sys="直线真空";break;
//					case "04":sys="DTL";break;
//					case "05":sys="环高频";break;
//					case "06":sys="脉冲电源";break;
//					case "07":sys="直线电源";break;
//					case "08":sys="前端";break;
//					case "09":sys="控制";break;
//					case "10":sys="通用信息";break;
//					case "11":sys="机械磁铁";break;
//					case "12":sys="RFQ";break;
//					case "13":sys="PPS";break;
//					case "14":sys="漏水检测";break;
//					case "15":sys="服务器与网络";break;
//					case "16":sys="环真空";break;
//					case "17":sys="RTBT真空";break;
//					case "18":sys="环电源";break;
//					case "19":sys="RTBT电源";break;
//					default:		            break;
//
//					}
//					
//					modify= sys+"\n"+modify;
//				}
//					
//				else{
//					
//				}
			} else {
				modify = noToSys(modify);
				Display.output("不包含alarm关键字", modify);

			}

			if (modify.contains("{1}")) {
				modify = modify.replace("{1}", value1);
			}
			if (modify.contains("{0}")) {
				modify = modify.replace("{0}", severity);
			}
			Display.output(message, "|", modify, "|");
			modify = modify + "\n" + time;
			value.setTalk(modify);
			return new KeyValue<String, AlarmTalkMessage>(key, value);
		});

//        processAlarmStateStream(alarmBranches[0], props);

		alarms.foreach((k, v) -> {
			Display.output(k, v);
//            MessagePosterHelper.getInstance().indexAlarmStateDocuments(stateIndexNameHelper.getIndexName(Instant.now()), v);
			MessagePosterHelper.getInstance().indexAlarmStateDocuments(k, v);
		});

		Display.output(kafkaProps);
	
		Display.output(kafkaProps);
		final KafkaStreams streams = new KafkaStreams(builder.build(), kafkaProps);
		final CountDownLatch latch = new CountDownLatch(1);

		// attach shutdown handler to catch control-c
		Runtime.getRuntime().addShutdownHook(new Thread("streams-" + topic + "-alarm-messages-shutdown-hook") {
			@Override
			public void run() {
//				streams.close(10, TimeUnit.SECONDS);
				streams.close(Duration.ofSeconds(10));
				System.out.println("\nShutting streams Done.");
				latch.countDown();
			}
		});

		try {
			streams.start();
			latch.await();
		} catch (Throwable e) {
			System.exit(1);
		}
		System.exit(0);
	}

//    private void processAlarmStateStream(KStream<String, AlarmTalkMessage> alarmStateBranch, Properties props) {
//
//        KStream<String, AlarmStateMessage> transformedAlarms = alarmStateBranch
//                .transform(new TransformerSupplier<String, AlarmTalkMessage, KeyValue<String, AlarmStateMessage>>() {
//
//                    @Override
//                    public Transformer<String, AlarmTalkMessage, KeyValue<String, AlarmStateMessage>> get() {
//                        return new Transformer<>() {
//                            private ProcessorContext context;
//
//                            @Override
//                            public KeyValue<String, AlarmStateMessage> transform(String key, AlarmTalkMessage value) {
//                                key = key.replace("\\", "");
//                                AlarmStateMessage newValue = value.getAlarmStateMessage();
//                                Matcher matcher = pattern.matcher(key);
//                                newValue.setConfig(key);
//                                matcher.find();
//                                String[] tokens = AlarmTreePath.splitPath(key);
//                                final String pv = tokens[tokens.length - 1];
//                                newValue.setPv(pv);
//
//                                newValue.setMessage_time(Instant.ofEpochMilli(context.timestamp()));
//                                return new KeyValue<>(key, newValue);
//                            }
//
//                            @Override
//                            public void init(ProcessorContext context) {
//                                this.context = context;
//                            }
//
//                            @Override
//                            public void close() {
//                                // TODO Auto-generated method stub
//
//                            }
//                        };
//                    }
//                });
//
//        KStream<String, AlarmStateMessage> filteredAlarms = transformedAlarms.filter((k, v) -> {
//            return v != null ? v.isLeaf() : false;
//        });
//
//        // Commit to elastic
//        filteredAlarms.foreach((k, v) -> {
//            MessagePosterHelper.getInstance().indexAlarmStateDocuments(stateIndexNameHelper.getIndexName(v.getMessage_time()), v);
//        });
//
//    }
	private String noToSys(String str) {
		Matcher matcher = digitalPattern.matcher(str);
		boolean beginWith2Digitals = matcher.matches();
		Display.output("两位数字开头", beginWith2Digitals, str);
		String answer = "";
		String no = "";
		if (beginWith2Digitals) {
			answer = str.substring(2);
			no = str.substring(0, 2);

			Display.output(no, answer);

			String sys = "";
			switch (no) {
			case "01":
				sys = "束测";
				break;
			case "02":
				sys = "直线高频";
				break;
			case "03":
				sys = "直线真空";
				break;
			case "04":
				sys = "DTL";
				break;
			case "05":
				sys = "环高频";
				break;
			case "06":
				sys = "脉冲电源";
				break;
			case "07":
				sys = "直线电源";
				break;
			case "08":
				sys = "前端";
				break;
			case "09":
				sys = "控制";
				break;
			case "10":
				sys = "通用信息";
				break;
			case "11":
				sys = "机械磁铁";
				break;
			case "12":
				sys = "RFQ";
				break;
			case "13":
				sys = "PPS";
				break;
			case "14":
				sys = "漏水检测";
				break;
			case "15":
				sys = "服务器与网络";
				break;
			case "16":
				sys = "环真空";
				break;
			case "17":
				sys = "RTBT真空";
				break;
			case "18":
				sys = "环电源";
				break;
			case "19":
				sys = "RTBT电源";
				break;
			default:
				break;

			}

			answer = sys + "\n" + answer;
		} else
			answer = str;
		return answer;

	}

	private String timestampToLDT(long timestamp) {
		String time = Long.toString(timestamp);
		String answer;
		if (time.length() == 10) {
			answer = Instant.ofEpochSecond(timestamp).atZone(ZoneId.systemDefault())
					.format(DateTimeFormatter.ofPattern("yyyy/MM/d HH:mm:ss"));
		} else if (time.length() == 13) {
			answer = Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault())
					.format(DateTimeFormatter.ofPattern("yyyy/MM/d HH:mm:ss.SSS"));
		} else
			answer = null;
		return answer;
	}

	public List<String> activeConsumerByTopic(String topicName) {
		List<String> lists = new ArrayList<>();
		PreferencesReader prefs = AnnotatedPreferences.initialize(AlarmSystem.class, "/alarm_preferences.properties");
		Properties props = new Properties();
		props.put("bootstrap.servers", prefs.get("server"));
		AdminClient client = AdminClient.create(props);
		try {
			// get all consumer groupId
			List<String> groupIds = client.listConsumerGroups().all().get().stream().map(s -> s.groupId())
					.collect(Collectors.toList());
			// Here you get all the descriptions for the groups
			Map<String, ConsumerGroupDescription> groups = client.describeConsumerGroups(groupIds).all().get();
			for (final String groupId : groupIds) {
				ConsumerGroupDescription descr = groups.get(groupId);
				// find if any description is connected to the topic with topicName
				Optional<TopicPartition> tp = descr.members().stream().map(s -> s.assignment().topicPartitions())
						.flatMap(coll -> coll.stream()).filter(s -> s.topic().equals(topicName)).findAny();
				if (tp.isPresent()) {
					// you found the consumer, so collect the group id somewhere
					lists.add(descr.groupId());
				}
			}
		} catch (Exception e) {
			throw new IllegalStateException();
		}

		return lists;
	}

}
