package org.phoebus.alarm.poster.httpclient;


import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class MessagePost {
private static String URL="http://accalarm.csns.ihep.ac.cn/alarm-post.php";
static HttpClientPoolUtil httpClientPool = new HttpClientPoolUtil();
	

	public static void say(Map<String, Object> paramsMap) throws Exception {
		
		//http response string format is errcount=count
		String respErrorCntStr = "errcount=0";
		int respErrorCnt = 0;
		respErrorCntStr = httpClientPool.post(URL, paramsMap);
		//System.out.println("http resp:" + respErrorCntStr);
		//System.out.println("http resp len=" + respErrorCntStr.length());		
		respErrorCnt = Integer.valueOf(respErrorCntStr.split("=")[1]);
		
		//如果收到的http请求返回的errorno大于0，只重传一次
		if ( respErrorCnt> 0) {
			httpClientPool.post(URL, paramsMap);
		}
	}

}
