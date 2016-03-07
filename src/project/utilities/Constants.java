package project.utilities;

import java.util.HashMap;
import java.util.Map;

public interface Constants {
	
	/******************* Ping Commands *************************************/
	public final static String PING_WINDOWS = "ping";
	public final static String PING_MAC = "ping -c 3";
	public final static String SPACE = " ";
	public final static int NUMCONNECTHOST = 5;

	/****************** Configuring VM ************************************/
	public final static String VIRTUALCENTER_URL = "VirtualCenter_URL";
	public final static String VIRTUALCENTER_USERNAME = "VirtualCenter_Username";
	public final static String VIRTUALCENTER_PASSWORD = "VirtualCenter_Password";
	public final static String CONFIGURATION_FILE = "config.properties";
	
	public final static String ME_NAME = "";
	public final static String VHOST_UID = "vHost_username";
	public final static String VHOST_PASSWORD = "vHost_password";
	public final static String ADMIN_URL = "vAdmin_url";
	public final static String ADMIN_UID = "vAdmin_username";
	public final static String ADMIN_PASSWORD = "vAdmin_password";
	public final static String RESOURCE_POOL = "ResourcePool";
	
	//Stats Interval
	public final static String STAT_TIME="Stat_time_interval";
	public final static String SNAPSHOT_TIME = "Snapshot_time_interval";
	public final static String RECOVERY_TIME = "recovery_time_interval";

	// Ping constants
	public final static String PING_ATTEMPTS = "ping_attempts";
	public final static String PING_INTERVAL = "ping_interval";

	public final static Map<String, String> HOST_MAP = new HashMap<String, String>() {

		private static final long serialVersionUID = 1L;
		{
			put("192.168.200.128", "Host01");
		}
	};
	
	
}