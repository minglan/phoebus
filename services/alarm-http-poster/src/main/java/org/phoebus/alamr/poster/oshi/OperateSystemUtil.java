package org.phoebus.alamr.poster.oshi;
 

import oshi.SystemInfo;
import oshi.hardware.*;
import oshi.software.os.*;
import oshi.util.FormatUtil;
 
import java.net.NetworkInterface;
import java.text.DecimalFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
 
/**
 * @author OnCloud9
 * @description
 * @project tt-server
 * @date 2022年12月31日 下午 07:32
 */

public class OperateSystemUtil {
    private static final SystemInfo systemInfo = new SystemInfo();
    private static final OperatingSystem operatingSystem = systemInfo.getOperatingSystem();
    private static final HardwareAbstractionLayer hal = systemInfo.getHardware();
    private static final DecimalFormat df=  new DecimalFormat("#.##%");
    /**
     * 读取显示器信息
     * @return List<Map<String, Object>>
     */
    public static List<Map<String, Object>> getDisplayInfo() {
        final List<Display> displays = hal.getDisplays();
        final List<Map<String, Object>> displayInfos = new ArrayList<>(displays.size());
        for (int i = 0; i < displays.size(); i++) {
            Map<String, Object> displayInfo = new ConcurrentHashMap<>();
            displayInfo.put("index", String.valueOf(i));
            final Display display = displays.get(i);
            displayInfo.put("instance", display);
            displayInfo.put("toString", String.valueOf(display));
            displayInfos.add(displayInfo);
        }
        return displayInfos;
    }
 
    /**
     * 读取声卡信息
     * @return List<Map<String, Object>>
     */
    public static List<Map<String, Object>> getSoundCardInfo() {
        final List<SoundCard> soundCards = hal.getSoundCards();
        final List<Map<String, Object>> soundCardInfos = new ArrayList<>(soundCards.size());
        for (int i = 0; i < soundCards.size(); i++) {
            Map<String, Object> soundCard = new ConcurrentHashMap<>();
            soundCard.put("index", String.valueOf(i));
            final SoundCard soundCardInst = soundCards.get(i);
            soundCard.put("toString", String.valueOf(soundCardInst));
            soundCard.put("codec", soundCardInst.getCodec());
            soundCard.put("name", soundCardInst.getName());
            soundCard.put("driverVersion", soundCardInst.getDriverVersion());
            soundCardInfos.add(soundCard);
        }
        return soundCardInfos;
    }
 
    /**
     * 读取显卡信息
     * @return List<Map<String, Object>>
     */
    public static List<Map<String, Object>> getGraphicsCardsInfo() {
        final List<GraphicsCard> graphicsCards = hal.getGraphicsCards();
        final List<Map<String, Object>> graphicsCardsInfos = new ArrayList<>(graphicsCards.size());
        for (int i = 0; i < graphicsCards.size(); i++) {
            Map<String, Object> gcInfo = new ConcurrentHashMap<>();
            gcInfo.put("index", String.valueOf(i));
            final GraphicsCard graphicsCard = graphicsCards.get(i);
            gcInfo.put("toString", String.valueOf(graphicsCard));
            gcInfo.put("name", graphicsCard.getName());
            gcInfo.put("deviceId", graphicsCard.getDeviceId());
            gcInfo.put("versionInfo", graphicsCard.getVersionInfo());
            gcInfo.put("vendor", graphicsCard.getVendor());
            gcInfo.put("vRam", graphicsCard.getVRam());
            gcInfo.put("vRamFormat", FormatUtil.formatBytes(graphicsCard.getVRam()));
            graphicsCardsInfos.add(gcInfo);
        }
        return graphicsCardsInfos;
    }
 
    /**
     * usb设备信息
     * @return List<Map<String, Object>>
     */
    public static List<Map<String, Object>> getUsbDevicesInfo() {
        final List<UsbDevice> usbDevices = hal.getUsbDevices(true);
        final List<Map<String, Object>> usbDevicesInfos = new ArrayList<>(usbDevices.size());
        for (int i = 0; i < usbDevices.size(); i++) {
            Map<String, Object> usbDeviceInfo = new ConcurrentHashMap<>();
            usbDeviceInfo.put("index", String.valueOf(i));
            final UsbDevice usbDevice = usbDevices.get(i);
            usbDeviceInfo.put("toString", String.valueOf(usbDevice));
            usbDeviceInfo.put("name", usbDevice.getName());
            usbDeviceInfo.put("uniqueDeviceId", usbDevice.getUniqueDeviceId());
            usbDeviceInfo.put("vendor", usbDevice.getVendor());
            usbDeviceInfo.put("productId", usbDevice.getProductId());
            usbDeviceInfo.put("serialNumber", usbDevice.getSerialNumber());
            usbDeviceInfo.put("vendorId", usbDevice.getVendorId());
 
            final List<UsbDevice> connectedDevices = usbDevice.getConnectedDevices();
            usbDeviceInfo.put("connectedDevices", connectedDevices);
            usbDevicesInfos.add(usbDeviceInfo);
        }
        return usbDevicesInfos;
    }
 
    /**
     * 网络参数信息
     * @return Map<String, Object>
     */
    public static Map<String, Object> getNetworkParamsInfo() {
        Map<String, Object> networkParamsInfo = new ConcurrentHashMap<>();
        final NetworkParams networkParams = operatingSystem.getNetworkParams();
        networkParamsInfo.put("toString", networkParams);
        networkParamsInfo.put("hostName", networkParams.getHostName());
        networkParamsInfo.put("dnsServers", Arrays.asList(networkParams.getDnsServers()));
        networkParamsInfo.put("domainName", networkParams.getDomainName());
        networkParamsInfo.put("ipv4Gateway", networkParams.getIpv4DefaultGateway());
        networkParamsInfo.put("ipv6Gateway", networkParams.getIpv6DefaultGateway());
        return networkParamsInfo;
    }
 
    /**
     * ip信息统计
     * @return Map<String, Object>
     */
    public static Map<String, Object> getIpStatistics() {
        final InternetProtocolStats internetProtocolStats = operatingSystem.getInternetProtocolStats();
        final Map<String, Object> ipStatisticsMap = new ConcurrentHashMap<>();
 
        /* tcpV4 */
        Map<String, Object> tcpV4 = new ConcurrentHashMap<>();
        final InternetProtocolStats.TcpStats tcPv4Stats = internetProtocolStats.getTCPv4Stats();
        tcpV4.put("toString", String.valueOf(tcPv4Stats));
        tcpV4.put("connectionFailures", tcPv4Stats.getConnectionFailures());
        tcpV4.put("connectionsActive", tcPv4Stats.getConnectionsActive());
        tcpV4.put("connectionsPassive", tcPv4Stats.getConnectionsPassive());
        tcpV4.put("connectionsReset", tcPv4Stats.getConnectionsReset());
        tcpV4.put("connectionsEstablished", tcPv4Stats.getConnectionsEstablished());
        tcpV4.put("inErrors", tcPv4Stats.getInErrors());
        tcpV4.put("outResets", tcPv4Stats.getOutResets());
        tcpV4.put("segmentsReceived", tcPv4Stats.getSegmentsReceived());
        tcpV4.put("segmentsRetransmitted", tcPv4Stats.getSegmentsRetransmitted());
        tcpV4.put("segmentsSent", tcPv4Stats.getSegmentsSent());
        ipStatisticsMap.put("tcpV4", tcpV4);
 
        /* tcpV6 */
        Map<String, Object> tcpV6 = new ConcurrentHashMap<>();
        final InternetProtocolStats.TcpStats tcPv6Stats = internetProtocolStats.getTCPv6Stats();
        tcpV6.put("toString", String.valueOf(tcPv6Stats));
        tcpV6.put("connectionFailures", tcPv6Stats.getConnectionFailures());
        tcpV6.put("connectionsActive", tcPv6Stats.getConnectionsActive());
        tcpV6.put("connectionsPassive", tcPv6Stats.getConnectionsPassive());
        tcpV6.put("connectionsReset", tcPv6Stats.getConnectionsReset());
        tcpV6.put("connectionsEstablished", tcPv6Stats.getConnectionsEstablished());
        tcpV6.put("inErrors", tcPv6Stats.getInErrors());
        tcpV6.put("outResets", tcPv6Stats.getOutResets());
        tcpV6.put("segmentsReceived", tcPv6Stats.getSegmentsReceived());
        tcpV6.put("segmentsRetransmitted", tcPv6Stats.getSegmentsRetransmitted());
        tcpV6.put("segmentsSent", tcPv6Stats.getSegmentsSent());
        ipStatisticsMap.put("tcpV6", tcpV6);
 
        /* udpV4 */
        Map<String, Object> udpV4 = new ConcurrentHashMap<>();
        final InternetProtocolStats.UdpStats udPv4Stats = internetProtocolStats.getUDPv4Stats();
        udpV4.put("toString", String.valueOf(udPv4Stats));
        udpV4.put("datagramsNoPort", udPv4Stats.getDatagramsNoPort());
        udpV4.put("datagramsReceived", udPv4Stats.getDatagramsReceived());
        udpV4.put("datagramsReceivedErrors", udPv4Stats.getDatagramsReceivedErrors());
        udpV4.put("datagramsSent", udPv4Stats.getDatagramsSent());
        ipStatisticsMap.put("udpV4", udpV4);
 
        /* udpV6 */
        Map<String, Object> udpV6 = new ConcurrentHashMap<>();
        final InternetProtocolStats.UdpStats udPv6Stats = internetProtocolStats.getUDPv6Stats();
        udpV6.put("toString", String.valueOf(udPv6Stats));
        udpV6.put("datagramsNoPort", udPv6Stats.getDatagramsNoPort());
        udpV6.put("datagramsReceived", udPv6Stats.getDatagramsReceived());
        udpV6.put("datagramsReceivedErrors", udPv6Stats.getDatagramsReceivedErrors());
        udpV6.put("datagramsSent", udPv6Stats.getDatagramsSent());
        ipStatisticsMap.put("udpV6", udpV6);
 
        /* connections */
        final List<InternetProtocolStats.IPConnection> connections = internetProtocolStats.getConnections();
        final List<Map<String, Object>> ipConnectionInfoList = new ArrayList<>(connections.size());
        for (int i = 0; i < connections.size(); i++) {
            Map<String, Object> ipConnectionInfo = new ConcurrentHashMap<>();
            ipConnectionInfo.put("connectionIndex", i);
            final InternetProtocolStats.IPConnection ipConnection = connections.get(i);
            ipConnectionInfo.put("toString", String.valueOf(ipConnection));
            ipConnectionInfo.put("foreignAddress", Arrays.toString(ipConnection.getForeignAddress()));
            ipConnectionInfo.put("foreignPort", ipConnection.getForeignPort());
            ipConnectionInfo.put("localAddress", Arrays.toString(ipConnection.getLocalAddress()));
            ipConnectionInfo.put("state", ipConnection.getState());
            ipConnectionInfo.put("type", ipConnection.getType());
            ipConnectionInfo.put("localPort", ipConnection.getLocalPort());
            ipConnectionInfo.put("owningProcessId", ipConnection.getowningProcessId());
            ipConnectionInfo.put("receiveQueue", ipConnection.getReceiveQueue());
            ipConnectionInfo.put("transmitQueue", ipConnection.getTransmitQueue());
            ipConnectionInfoList.add(ipConnectionInfo);
        }
        ipStatisticsMap.put("ipConnections", ipConnectionInfoList);
        return ipStatisticsMap;
    }
 
    /**
     * 网络接口信息读取
     * @return List<Map<String, Object>>
     */
    public static List<Map<String, Object>> getNetWorkInterfaces() {
        final List<NetworkIF> networkIFs = hal.getNetworkIFs();
        final List<Map<String, Object>> networkIFList = new ArrayList<>(networkIFs.size());
 
        for (int i = 0; i < networkIFs.size(); i++) {
            final Map<String, Object> networkIfMap = new ConcurrentHashMap<>();
            networkIfMap.put("index", i);
            final NetworkIF networkIF = networkIFs.get(i);
            networkIfMap.put("toString", String.valueOf(networkIF));
            networkIfMap.put("displayName", networkIF.getDisplayName());
            networkIfMap.put("name", networkIF.getName());
            networkIfMap.put("ifIndex", networkIF.getIndex());
            networkIfMap.put("iPv4addr", networkIF.getIPv4addr());
            networkIfMap.put("iPv6addr", networkIF.getIPv6addr());
            networkIfMap.put("macAddr", networkIF.getMacaddr());
            networkIfMap.put("speed", networkIF.getSpeed());
            networkIfMap.put("subnetMasks", networkIF.getSubnetMasks());
            networkIfMap.put("bytesRecv", networkIF.getBytesRecv());
            networkIfMap.put("bytesSent", networkIF.getBytesSent());
            networkIfMap.put("collisions", networkIF.getCollisions());
            networkIfMap.put("ifAlias", networkIF.getIfAlias());
            networkIfMap.put("ifOperStatus", networkIF.getIfOperStatus().name());
            networkIfMap.put("ifType", networkIF.getIfType());
            networkIfMap.put("inDrops", networkIF.getInDrops());
            networkIfMap.put("inErrors", networkIF.getInErrors());
            networkIfMap.put("mtu", networkIF.getMTU());
            networkIfMap.put("ndisPhysicalMediumType", networkIF.getNdisPhysicalMediumType());
            networkIfMap.put("outErrors", networkIF.getOutErrors());
            networkIfMap.put("packetsRecv", networkIF.getPacketsRecv());
            networkIfMap.put("packetsSent", networkIF.getPacketsSent());
            networkIfMap.put("prefixLengths", networkIF.getPrefixLengths());
            networkIfMap.put("timeStamp", networkIF.getTimeStamp());
            networkIfMap.put("isConnectorPresent",  networkIF.isConnectorPresent());
            networkIfMap.put("isKnownVmMacAddr",  networkIF.isKnownVmMacAddr());
            final NetworkInterface networkInterface = networkIF.queryNetworkInterface();
            networkIfMap.put("networkInterface", String.valueOf(networkInterface));
            networkIFList.add(networkIfMap);
        }
        return networkIFList;
    }
 
    /**
     * 读取文件系统信息
     * @return List<Map<String, Object>>
     */
    public static Map<String, Object> getFileSystemInfo() {
        final Map<String, Object> fsInfo = new ConcurrentHashMap<>();
        final FileSystem fileSystem = operatingSystem.getFileSystem();
        final List<Map<String, Object>> fileSystemInfos = new ArrayList<>();
 
        fsInfo.put("openFileDescriptors", fileSystem.getOpenFileDescriptors());
        fsInfo.put("maxFileDescriptors", fileSystem.getMaxFileDescriptors());
        fsInfo.put("fileDescriptors", String.format("%d/%d", fileSystem.getOpenFileDescriptors(), fileSystem.getMaxFileDescriptors()));
        fsInfo.put("fdUsageRate", (100d * fileSystem.getOpenFileDescriptors() / fileSystem.getMaxFileDescriptors()) + "%");
 
        final List<OSFileStore> fileStores = fileSystem.getFileStores();
        for (int i = 0; i < fileStores.size(); i++) {
            final Map<String, Object> fileStoreInfo = new ConcurrentHashMap<>();
            fileStoreInfo.put("index", i);
            final OSFileStore osFileStore = fileStores.get(i);
            fileStoreInfo.put("toString", String.valueOf(osFileStore));
            fileStoreInfo.put("name", osFileStore.getName());
            fileStoreInfo.put("description", osFileStore.getDescription());
            fileStoreInfo.put("totalSpace", FormatUtil.formatBytes(osFileStore.getTotalSpace()));
            fileStoreInfo.put("usedSpace",  FormatUtil.formatBytes(osFileStore.getTotalSpace() - osFileStore.getUsableSpace()));
            fileStoreInfo.put("usableSpace",  FormatUtil.formatBytes(osFileStore.getUsableSpace()));
            fileStoreInfo.put("usageRate", 100d * (osFileStore.getTotalSpace() - osFileStore.getUsableSpace()) / osFileStore.getTotalSpace());
            fileStoreInfo.put("volume", osFileStore.getVolume());
            fileStoreInfo.put("mount", osFileStore.getMount());
            fileStoreInfo.put("logicalVolume", osFileStore.getLogicalVolume());
            fileStoreInfo.put("totalInodes", FormatUtil.formatValue(osFileStore.getTotalInodes(), ""));
            fileStoreInfo.put("freeInodes", FormatUtil.formatValue(osFileStore.getFreeInodes(), ""));
            fileStoreInfo.put("inodesUsageRate", 100d * osFileStore.getFreeInodes() / osFileStore.getTotalInodes());
            fileSystemInfos.add(fileStoreInfo);
        }
        fsInfo.put("fileStores", fileSystemInfos);
 
        return fsInfo;
    }
 
    /**
     * 逻辑卷组信息
     * @return  List<Map<String, Object>>
     */
    public static List<Map<String, Object>> getLogicalVolumeGroupInfo() {
        final List<LogicalVolumeGroup> logicalVolumeGroups = hal.getLogicalVolumeGroups();
        final List<Map<String, Object>> lvgInfos = new ArrayList<>(logicalVolumeGroups.size());
        for (int i = 0; i < logicalVolumeGroups.size(); i++) {
            final LogicalVolumeGroup lvg = logicalVolumeGroups.get(i);
            final Map<String, Object> lvgInfo = new ConcurrentHashMap<>();
            lvgInfo.put("index", i);
            lvgInfo.put("toString", String.valueOf(lvg));
            lvgInfo.put("name", lvg.getName());
            lvgInfo.put("logicalVolumes", lvg.getLogicalVolumes());
            lvgInfo.put("physicalVolumes", lvg.getPhysicalVolumes());
            lvgInfos.add(lvgInfo);
        }
        return lvgInfos;
    }
 
    /**
     * 磁盘存储信息读取
     * @return List<Map<String, Object>>
     */
    public static List<Map<String, Object>> getDiskStoreInfo() {
        final List<HWDiskStore> diskStores = hal.getDiskStores();
        final List<Map<String, Object>> dsList = new ArrayList<>(diskStores.size());
        for (int i = 0; i < diskStores.size(); i++) {
            final HWDiskStore hwDiskStore = diskStores.get(i);
            final Map<String, Object> hwDsMap = new ConcurrentHashMap<>();
            hwDsMap.put("index", i);
            hwDsMap.put("toString", String.valueOf(hwDiskStore));
            hwDsMap.put("name", hwDiskStore.getName());
            hwDsMap.put("currentQueueLength", hwDiskStore.getCurrentQueueLength());
            hwDsMap.put("model", hwDiskStore.getModel());
            hwDsMap.put("serial", hwDiskStore.getSerial());
            hwDsMap.put("size", FormatUtil.formatBytes(hwDiskStore.getSize()));
            hwDsMap.put("reads", FormatUtil.formatBytes(hwDiskStore.getReads()));
            hwDsMap.put("writes", FormatUtil.formatBytes(hwDiskStore.getWrites()));
            hwDsMap.put("readBytes", hwDiskStore.getReadBytes());
            hwDsMap.put("writeBytes", hwDiskStore.getWriteBytes());
            hwDsMap.put("transferTime", hwDiskStore.getTransferTime());
            hwDsMap.put("timeStamp", hwDiskStore.getTimeStamp());
 
            final List<HWPartition> partitions = hwDiskStore.getPartitions();
            final List<Map<String, Object>> partitionList = new ArrayList<>(partitions.size());
            for (HWPartition partition : partitions) {
                final Map<String, Object> partitionMap = new ConcurrentHashMap<>();
                partitionMap.put("toString", partition);
                partitionMap.put("size", FormatUtil.formatBytes(partition.getSize()));
                partitionMap.put("name", partition.getName());
                partitionMap.put("type", partition.getType());
                partitionMap.put("identification", partition.getIdentification());
                partitionMap.put("major", partition.getMajor());
                partitionMap.put("uuid", partition.getUuid());
                partitionMap.put("mountPoint", partition.getMountPoint());
                partitionMap.put("minor", partition.getMinor());
                partitionList.add(partitionMap);
            }
            hwDsMap.put("partitionList", partitionList);
            dsList.add(hwDsMap);
        }
        return dsList;
    }
 
    /**
     * 电源信息读取
     * @return List<Map<String, Object>
     */
    public static List<Map<String, Object>> getPowerSourceInfo() {
        final List<PowerSource> powerSources = hal.getPowerSources();
        final List<Map<String, Object>> powerSourcesList = new ArrayList<>(powerSources.size());
        for (PowerSource powerSource : powerSources) {
            final Map<String, Object> powerSourceMap = new ConcurrentHashMap<>();
            powerSourceMap.put("toString", String.valueOf(powerSource));
            powerSourceMap.put("amperage", powerSource.getAmperage());
            powerSourceMap.put("name", powerSource.getName());
            powerSourceMap.put("capacityUnits", powerSource.getCapacityUnits());
            powerSourceMap.put("serialNumber", powerSource.getSerialNumber());
            powerSourceMap.put("currentCapacity", powerSource.getCurrentCapacity());
            powerSourceMap.put("deviceName", powerSource.getDeviceName());
            powerSourceMap.put("manufacturer", powerSource.getManufacturer());
            powerSourceMap.put("voltage", powerSource.getVoltage());
            powerSourceMap.put("chemistry", powerSource.getChemistry());
            powerSourceMap.put("cycleCount", powerSource.getCycleCount());
            powerSourceMap.put("powerUsageRate", powerSource.getPowerUsageRate());
            powerSourceMap.put("designCapacity", powerSource.getDesignCapacity());
            powerSourceMap.put("maxCapacity", powerSource.getMaxCapacity());
            // powerSourceMap.put("manufactureDate", powerSource.getManufactureDate());
            powerSourceMap.put("temperature", powerSource.getTemperature());
            powerSourceMap.put("isDischarging", powerSource.isDischarging());
            powerSourceMap.put("isCharging", powerSource.isCharging());
            powerSourceMap.put("isPowerOnLine", powerSource.isPowerOnLine());
            powerSourceMap.put("timeRemainingInstant", powerSource.getTimeRemainingInstant());
            powerSourceMap.put("timeRemainingEstimated", powerSource.getTimeRemainingEstimated());
            powerSourceMap.put("remainingCapacityPercent", powerSource.getRemainingCapacityPercent());
            powerSourcesList.add(powerSourceMap);
        }
        return powerSourcesList;
    }
 
 
    /**
     * 获取系统服务信息，服务基于系统平台决定
     * @return List<Map<String, Object>>
     */
    public static List<Map<String, Object>> getSystemServiceInfo() {
        final List<OSService> services = operatingSystem.getServices();
        final List<Map<String, Object>> systemServiceList = new ArrayList<>(services.size());
 
        for (int i = 0; i < services.size(); i++) {
            final OSService osService = services.get(i);
            final Map<String, Object> osServiceMap = new ConcurrentHashMap<>();
            osServiceMap.put("index", i);
            osServiceMap.put("toString", String.valueOf(osService));
            osServiceMap.put("state", osService.getState().name());
            osServiceMap.put("pid", osService.getProcessID());
            osServiceMap.put("name", osService.getName());
            systemServiceList.add(osServiceMap);
        }
        return systemServiceList;
    }
 
    /**
     * 获取传感器信息
     * @return String
     */
    public static Map<String, Object> getSensorInfo() {
        final Map<String, Object> sensorInfo = new ConcurrentHashMap<>();
        final Sensors sensors = hal.getSensors();
        sensorInfo.put("toString", String.valueOf(sensors));
        sensorInfo.put("instance", sensors);
        return sensorInfo;
    }
 
    /**
     * 获取进程信息
     * @return Map<String, Object>
     */
    public static Map<String, Object> getProcessesInfo() {
        final GlobalMemory globalMemory = hal.getMemory();
        final Map<String, Object> processesInfoMap = new ConcurrentHashMap<>();
        processesInfoMap.put("processCount", operatingSystem.getProcessCount());
        processesInfoMap.put("threadCount", operatingSystem.getThreadCount());
 
        List<OSProcess> osProcessList = operatingSystem.getProcesses(OperatingSystem.ProcessFiltering.ALL_PROCESSES, OperatingSystem.ProcessSorting.CPU_DESC, 100);
        final List<Map<String, Object>> osProcessMapList = new ArrayList<>(osProcessList.size());
        for (int i = 0; i < osProcessList.size(); i++) {
            final OSProcess osProcess = osProcessList.get(i);
            final Map<String, Object> osProcessMap = new ConcurrentHashMap<>();
            osProcessMap.put("index", i);
            osProcessMap.put("toString", String.valueOf(osProcess));
            osProcessMap.put("pid", osProcess.getProcessID());
            osProcessMap.put("kernelTime", osProcess.getKernelTime());
            osProcessMap.put("userTime", osProcess.getUserTime());
            osProcessMap.put("upTime", osProcess.getUpTime());
            osProcessMap.put("startTime", osProcess.getStartTime());
            osProcessMap.put("bytesRead", osProcess.getBytesRead());
            osProcessMap.put("bytesWritten", osProcess.getBytesWritten());
            osProcessMap.put("openFiles", osProcess.getOpenFiles());
            osProcessMap.put("softOpenFileLimit", osProcess.getSoftOpenFileLimit());
            osProcessMap.put("hardOpenFileLimit", osProcess.getHardOpenFileLimit());
            osProcessMap.put("processCpuLoadCumulative", osProcess.getProcessCpuLoadCumulative());
            osProcessMap.put("processCpuLoadBetweenTicks", osProcess.getProcessCpuLoadBetweenTicks(osProcess));
            osProcessMap.put("bitness", osProcess.getBitness());
            osProcessMap.put("affinityMask", osProcess.getAffinityMask());
            osProcessMap.put("minorFaults", osProcess.getMinorFaults());
            osProcessMap.put("majorFaults", osProcess.getMajorFaults());
            osProcessMap.put("priority", osProcess.getPriority());
            osProcessMap.put("threadCount", osProcess.getThreadCount());
            osProcessMap.put("group", osProcess.getGroup());
            osProcessMap.put("groupId", osProcess.getGroupID());
            osProcessMap.put("user", osProcess.getUser());
            osProcessMap.put("userId", osProcess.getUserID());
            osProcessMap.put("currentWorkingDirectory", osProcess.getCurrentWorkingDirectory());
            osProcessMap.put("path", osProcess.getPath());
            osProcessMap.put("arguments", osProcess.getArguments());
            osProcessMap.put("environmentVariables", osProcess.getEnvironmentVariables());
            osProcessMap.put("cpuUsageRate", 100d * (osProcess.getKernelTime() + osProcess.getUserTime()) / osProcess.getUpTime());
            osProcessMap.put("memUsageRate", 100d * osProcess.getResidentSetSize() / globalMemory.getTotal());
            osProcessMap.put("virtualMemSize", FormatUtil.formatBytes(osProcess.getVirtualSize()));
            osProcessMap.put("residentSetSize", FormatUtil.formatBytes(osProcess.getResidentSetSize()));
            osProcessMap.put("processName", osProcess.getName());
            osProcessMapList.add(osProcessMap);
        }
        processesInfoMap.put("osProcessMapList", osProcessMapList);
        return processesInfoMap;
    }
 
    /**
     * 获取内存信息
     * @return Map<String, Object>
     */
    public static Map<String, Object> getMemoryInfo() {
        final GlobalMemory globalMemory = hal.getMemory();
        final Map<String, Object> gmMap = new ConcurrentHashMap<>();
        gmMap.put("total", FormatUtil.formatBytes(globalMemory.getTotal()));
        gmMap.put("available", FormatUtil.formatBytes(globalMemory.getAvailable()));
        gmMap.put("used", FormatUtil.formatBytes(globalMemory.getTotal() - globalMemory.getAvailable()));
        gmMap.put("usageRate", df.format(1.0* (globalMemory.getTotal() - globalMemory.getAvailable()) / globalMemory.getTotal()));
        gmMap.put("pageSize", globalMemory.getPageSize());
 
        final VirtualMemory virtualMemory = globalMemory.getVirtualMemory();
        final Map<String, Object> vmMap = new ConcurrentHashMap<>();
        vmMap.put("toString", virtualMemory);
        vmMap.put("swapTotal",  FormatUtil.formatBytes(virtualMemory.getSwapTotal()));
        vmMap.put("swapUsed",  FormatUtil.formatBytes(virtualMemory.getSwapUsed()));
        vmMap.put("swapUsageRate", 100d * virtualMemory.getSwapUsed() / virtualMemory.getSwapTotal());
        vmMap.put("virtualMax",  FormatUtil.formatBytes(virtualMemory.getVirtualMax()));
        vmMap.put("virtualInUse",  FormatUtil.formatBytes(virtualMemory.getVirtualInUse()));
        vmMap.put("virtualUsageRate",  100d * virtualMemory.getVirtualInUse() / virtualMemory.getVirtualMax());
        gmMap.put("virtualMemory", vmMap);
 
        final List<PhysicalMemory> physicalMemoryList = globalMemory.getPhysicalMemory();
        final List<Map<String, Object>> pmInfoList = new ArrayList<>(physicalMemoryList.size());
        for (PhysicalMemory pm : physicalMemoryList) {
            final Map<String, Object> pmMap = new ConcurrentHashMap<>();
            pmMap.put("toString", String.valueOf(pm));
            pmMap.put("bankLabel", pm.getBankLabel());
            pmMap.put("manufacturer", pm.getManufacturer());
            pmMap.put("capacity",  FormatUtil.formatBytes(pm.getCapacity()));
            pmMap.put("memoryType", pm.getMemoryType());
            pmMap.put("clockSpeed", FormatUtil.formatHertz(pm.getClockSpeed()));
            pmInfoList.add(pmMap);
        }
 
        gmMap.put("physicalMemoryList", pmInfoList);
        return gmMap;
    }
 
    /**
     * 获取操作系统信息
     * @return Map<String, Object>
     */
    public static Map<String, Object> getOperateSystemInfo() {
        final Map<String, Object> osInfo = new ConcurrentHashMap<>();
        osInfo.put("osName", String.valueOf(operatingSystem));
        osInfo.put("booted", Instant.ofEpochSecond(operatingSystem.getSystemBootTime()));
        osInfo.put("sessionList", operatingSystem.getSessions());
 
        final ComputerSystem computerSystem = hal.getComputerSystem();
        osInfo.put("computerSystem", String.valueOf(computerSystem));
        osInfo.put("firmware: ", computerSystem.getFirmware());
        osInfo.put("baseboard: ", computerSystem.getBaseboard());
        return osInfo;
    }
 
    /**
     * 获取CPU信息
     * @return Map<String, Object>
     */
//    public static Map<String, Object> getCpuInfo() {
//        final Map<String, Object> cpuInfo = new ConcurrentHashMap<>();
//        final CentralProcessor processor = hal.getProcessor();
//        cpuInfo.put("toString", String.valueOf(processor));
//        cpuInfo.put("instance", processor);
//        return cpuInfo;
//    }
    
    public static Map<String, Object> getCpuInfo() {
        final Map<String, Object> cpuInfo = new ConcurrentHashMap<>();
        final CentralProcessor processor = hal.getProcessor();
        cpuInfo.put("toString", String.valueOf(processor));
        cpuInfo.put("instance", processor);
        
        long[] prevTicks = processor.getSystemCpuLoadTicks();
        // 睡眠1s
        try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        long[] ticks = processor.getSystemCpuLoadTicks();
        long nice = ticks[CentralProcessor.TickType.NICE.getIndex()] - prevTicks[CentralProcessor.TickType.NICE.getIndex()];
        long irq = ticks[CentralProcessor.TickType.IRQ.getIndex()] - prevTicks[CentralProcessor.TickType.IRQ.getIndex()];
        long softirq = ticks[CentralProcessor.TickType.SOFTIRQ.getIndex()] - prevTicks[CentralProcessor.TickType.SOFTIRQ.getIndex()];
        long steal = ticks[CentralProcessor.TickType.STEAL.getIndex()] - prevTicks[CentralProcessor.TickType.STEAL.getIndex()];
        long cSys = ticks[CentralProcessor.TickType.SYSTEM.getIndex()] - prevTicks[CentralProcessor.TickType.SYSTEM.getIndex()];
        long user = ticks[CentralProcessor.TickType.USER.getIndex()] - prevTicks[CentralProcessor.TickType.USER.getIndex()];
        long iowait = ticks[CentralProcessor.TickType.IOWAIT.getIndex()] - prevTicks[CentralProcessor.TickType.IOWAIT.getIndex()];
        long idle = ticks[CentralProcessor.TickType.IDLE.getIndex()] - prevTicks[CentralProcessor.TickType.IDLE.getIndex()];
        long totalCpu = user + nice + cSys + idle + iowait + irq + softirq + steal;
        cpuInfo.put("cpu核数", processor.getLogicalProcessorCount());
        cpuInfo.put("cpu系统使用率", df.format(cSys * 1.0 / totalCpu));
        cpuInfo.put("cpu用户使用率",  df.format(user * 1.0 / totalCpu));
        cpuInfo.put("cpu当前等待率", df.format(iowait * 1.0 / totalCpu));
        cpuInfo.put("cpu当前使用率", df.format(1.0-(idle * 1.0 / totalCpu)));
//        org.phoebus.util.output.Display.output(ticks);
//        org.phoebus.util.output.Display.output(prevTicks);
        
        return cpuInfo;
    }
}