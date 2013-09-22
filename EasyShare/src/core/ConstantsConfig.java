package core;

/**
 * The Class ConstantsConfig.
 * This class is used to set default constants used for other classes.
 */
public abstract class ConstantsConfig {
	
	public static final int DEFAULT_TCP_PORT = 20000;

	/** The Constant DEFAULT_SERVER_COMMUNICATION_PORT defines the default communication port for servers. */
	public static final int DEFAULT_UDP_PORT = 30000;
	
	public static final int TIME_FOR_TIMEOUT = 10000; // time in milliseconds
	
	/** The Constant BROADCAST is the default IP when sending messages through broadcast. */
	public static final String BROADCAST = "255.255.255.255";

}
