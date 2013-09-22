package core;

public class PC {
	private final String name;
	private final String host;
	private final int port;
	
	
	public PC(String svname, String svhost, int svport) {
		this.name = svname;
		this.host = svhost;
		this.port = svport;
	}


	public String getName() {
		return name;
	}


	public String getHost() {
		return host;
	}


	public int getPort() {
		return port;
	}

}
