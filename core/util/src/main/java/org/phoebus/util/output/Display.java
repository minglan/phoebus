package org.phoebus.util.output;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Display {
	static LocalDateTime ldt;
	static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss");

	private static String getTraceInfo() {
		StringBuffer sb = new StringBuffer();
		StackTraceElement[] stacks = Thread.currentThread().getStackTrace();

		
		int n=stacks.length>12?12:stacks.length;
		if(stacks.length>4)for(int i=4;i<n;i++){
			StackTraceElement fatherElement = stacks[i];
		String field=getLastField(fatherElement.getClassName());
		sb.append(field+" "+fatherElement.getLineNumber()+" ");
		}
		
		sb.append("\n");
		sb.append(ldt.now().format(formatter)+"	");
		StackTraceElement element = stacks[3];
		String fullClassName = element.getClassName();
		int begin=fullClassName.length()<40?0:fullClassName.length()-40;
		String calssName = fullClassName.substring(begin);
		sb.append(" ").append(calssName).append("	").append(element.getMethodName()).append(" ")
				.append(element.getLineNumber());
		return sb.toString();
	}

	private static String getLastField(String s) {
		String[] fields=s.split("\\.");
		String field;
		
		if(fields.length>1)field=fields[fields.length-1];
		else field=s;
		return field;
	}

	
	public static void output(String s) {

		System.out.println(getTraceInfo() + "	" + s);
	}
	public static void output(long s) {

		System.out.println(getTraceInfo() + "	" + s);
	}

	public static void output() {

		System.out.println(getTraceInfo());
	}

	public static void output(String... s) {
		System.out.print(getTraceInfo() + "	");
		for (int i = 0; i < s.length; i++) {
			System.out.print(s[i] + "	");
		}
		System.out.println();
	}
	public static void output(Object... s) {
		System.out.print(getTraceInfo() + "	");
		for (int i = 0; i < s.length; i++) {
			System.out.print(s[i] + "	");
		}
		System.out.println();
	}
	public static void output(long... s) {
		System.out.print(getTraceInfo() + "	");
		for (int i = 0; i < s.length; i++) {
			System.out.print(s[i] + "	");
		}
		System.out.println();
	}

}
