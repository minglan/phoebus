package org.phoebus.alarm.poster.httpclient;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

//import org.apache.log4j.PropertyConfigurator;

public class Post {

	public static void main(String[] args) throws IOException {
		Properties properties = new Properties();
		FileInputStream fileInputStream = null;
		try {
			fileInputStream = new FileInputStream("src/main/resources/log4j.properties");
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        try {
			properties.load(fileInputStream);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
//        PropertyConfigurator.configure(properties);
        fileInputStream.close();
		Map<String, Object> paramsMap	=new HashMap<String, Object>();
		Random random=new Random();
		for(int i=0;i<100;i++) {
		Double x=random.nextGaussian();
		String info=90+""+x;
		paramsMap.put("para1", info);
		try {
			MessagePost.say(paramsMap);
		
			Thread.sleep(1000);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		
	;
	}

}
