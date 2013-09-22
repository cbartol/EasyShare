package core;

import java.io.IOException;

public class EasyShareServer {
	private String name;
	private final DownloadThread downloadThread;
	private final PingThread pingThread;
	
	public EasyShareServer(String serverName, String defaultDownloadFolder, int tcpPort, int udpPort) throws IOException{
		this.name = serverName;
		this.downloadThread = new DownloadThread(defaultDownloadFolder, tcpPort);
		this.pingThread = new PingThread(name, udpPort, tcpPort);
		this.downloadThread.setName("Download Thread");
		this.pingThread.setName("Ping Thread");
		
		this.downloadThread.start();
		this.pingThread.start();
	}
	
	public EasyShareServer(String serverName, String defaultDownloadFolder, int tcpPort, int udpPort, TransferProgressUI progressUI,
			AcceptDownloadUI acceptanceUI) throws IOException {
		this.name = serverName;
		this.downloadThread = new DownloadThread(defaultDownloadFolder, tcpPort, progressUI, acceptanceUI);
		this.pingThread = new PingThread(name, udpPort, tcpPort);
		this.downloadThread.setName("Download Thread");
		this.pingThread.setName("Ping Thread");
		
		this.downloadThread.start();
		this.pingThread.start();
	}

	public void changeDownloadFolder(String path){
		downloadThread.setDownloadFolder(path);
	}

	public void stop() {
		downloadThread.interrupt();
		pingThread.interrupt();
	}

	public void setName(String name2) {
		this.name = name2;
		this.pingThread.setUsername(name);		
	}
}
