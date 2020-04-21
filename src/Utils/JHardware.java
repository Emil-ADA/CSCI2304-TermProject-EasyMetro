package Utils;


import java.awt.Dimension;
import java.awt.Toolkit;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Set;

import Dependencies.Kernel32;



public class JHardware {
    public static void main(String[] args) {
	System.out.println(getBatteryStatus());
    }
    public static Dimension getScreenSize() {
	return Toolkit.getDefaultToolkit().getScreenSize();
    }

    public static String getBatteryStatus() {
	Kernel32.SYSTEM_POWER_STATUS batteryStatus = new Kernel32.SYSTEM_POWER_STATUS();
	Kernel32.INSTANCE.GetSystemPowerStatus(batteryStatus);
	return batteryStatus.toString();
    }

    public static String getFullDeviceInfo() {
	RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();

	Map<String, String> systemProperties = runtimeBean.getSystemProperties();
	Set<String> keys = systemProperties.keySet();

	String result = "";
	for (String key : keys) {
	    String value = systemProperties.get(key);
	    String Line = String.format("[%s] = %s\n", key, value);
	    result += Line;
	}
	return result;
    }

    public static long getFreeMemoryAvailable2JVM() {
	/* Total amount of free memory available to the JVM */
	return Runtime.getRuntime().freeMemory();
    }

    public static long getTotalMemoryAvailable2JVM() {
	/* Total memory currently available to the JVM */
	return Runtime.getRuntime().totalMemory();
    }

    public static long getMemoryLimitAvailable2JVM() {
	/* This will return Long.MAX_VALUE if there is no preset limit */
	long maxMemory = Runtime.getRuntime().maxMemory();
	return maxMemory;
    }

    public static int getCoresAmount() {
	return Runtime.getRuntime().availableProcessors();
    }

    public static String getProcessorInfo() {
	return getCoresAmount() + " Cores: Core i" + (getCoresAmount() * 2 - 1);
    }
    /**
     * Method that gives a mac address of the current device
     * 
     * @return a mac address of the current device in String format
     */
    public static String getMacAdress() {
	InetAddress ip;
	try {

	    ip = InetAddress.getLocalHost();
	    // System.out.println("Current IP address : " + ip.getHostAddress());

	    NetworkInterface network = NetworkInterface.getByInetAddress(ip);

	    byte[] mac = network.getHardwareAddress();

	    // System.out.print("Current MAC address : ");

	    StringBuilder sb = new StringBuilder();
	    for (int i = 0; i < mac.length; i++) {
		sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
	    }
	    return sb.toString();

	} catch (UnknownHostException e) {
	} catch (SocketException e) {
	}
	return null;
    }

}
