package org.phoebus.alamr.poster.oshi;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.phoebus.util.output.Display;

import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.Sensors;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;

public class oshi {


public static void main(String[] Args) {
	 Display.output(OperateSystemUtil.getMemoryInfo().get("usageRate")); 
	 Display.output(OperateSystemUtil.getCpuInfo().get("cpu当前使用率"));
}

}
