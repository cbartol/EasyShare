package core;

/**
 * The Class UdpMessages.
 * This class contains the default messages used for server and client to communicate.
 */
public final class UdpMessages {
	
	/** The Constant MAX_DATA_SIZE defines the size of a UDP message. */
	public static final int MAX_DATA_SIZE = 1024;
	
	/** The Constant ERROR0. */
	public static final String ERROR0 = "[Error 0] Algo marado aconteceu a receber... -> ";
	
	/** The Constant ERROR1. */
	public static final String ERROR1 = "[Error 1] Algo marado aconteceu a enviar... -> ";
	
	/** The Constant SEARCH_MESSAGE. */
	public static final String SEARCH_MESSAGE = "SEARCHING SERVERS";
	
	/** The Constant FOUND_MESSAGE. */
	public static final String FOUND_MESSAGE = "FOUND SERVER";
	
	/** The Constant CONNECT_MESSAGE. */
	public static final String CONNECT_MESSAGE = "REQ TUNNELING";
	
	/** The Constant ACCEPT_MESSAGE. */
	public static final String ACCEPT_MESSAGE = "TUNNELING ACCEPTED";
	
	/** The Constant REFRESH_MESSAGE. */
	public static final String REFRESH_MESSAGE = "REFRESH CLIENT";
	
	/** The Constant PAUSE_MESSAGE. */
	public static final String PAUSE_MESSAGE = "PAUSE SERVER";
	
	/** The Constant STOP_S_MESSAGE. */
	public static final String STOP_S_MESSAGE = "STOP SERVER";
	
	/** The Constant STOP_C_MESSAGE. */
	public static final String STOP_C_MESSAGE = "STOP CLIENT";
	
	/** The Constant REJECT_MESSAGE. */
	public static final String REJECT_MESSAGE = "REJECT CLIENT";
	
	/**
	 * Instantiates a new UDP messages.
	 */
	private UdpMessages(){
	}
	
	/**
	 * ERRO r_0.
	 *
	 * @param m the m
	 * @return the string
	 */
	public final static String ERROR_0(String m){
		return ERROR0 + m;
	}
	
	/**
	 * ERRO r_1.
	 *
	 * @param m the m
	 * @return the string
	 */
	public final static String ERROR_1(String m){
		return ERROR1 + m;
	}
	
	/**
	 * Search message.
	 *
	 * @param clientName the client name
	 * @return the string
	 */
	public final static String searchMessage(String clientName){
		return SEARCH_MESSAGE + "\n" + clientName;
	}
	
	/**
	 * Found server message.
	 *
	 * @param serverName the server name
	 * @param svPort TODO
	 * @return the string
	 */
	public final static String foundServerMessage(String serverName, int svPort){
		return FOUND_MESSAGE + "\n" + serverName + "\n" + svPort +"\n";
	}
}
