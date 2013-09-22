package core;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class EasyShare {
	private final static int MAX_DATA_SIZE = UdpMessages.MAX_DATA_SIZE;
	private List<PC> availableServers;
	private String clientName;
	private final DatagramSocket communicationSocket;
	private final int SCAN_TIMEOUT = 1000; //time in milliseconds
	private int threadNumber = 0;
	private final EasyShareServer server;
	private TransferProgressUI progressUI = new NullTransferProgress();

	public EasyShare(String name, String defaultDownloadFolder, int tcpPort, int udpPort) throws IOException{
		this.server = new EasyShareServer(name, defaultDownloadFolder, tcpPort, udpPort);
		this.communicationSocket = new DatagramSocket();
		this.clientName = name;
		availableServers = Collections.synchronizedList(new LinkedList<PC>());
	}
	
	public EasyShare(String name, String defaultDownloadFolder, int tcpPort, int udpPort, TransferProgressUI progressUI, AcceptDownloadUI acceptanceUI) throws IOException{
		this.server = new EasyShareServer(name, defaultDownloadFolder, tcpPort, udpPort, progressUI, acceptanceUI);
		this.communicationSocket = new DatagramSocket();
		this.clientName = name;
		availableServers = Collections.synchronizedList(new LinkedList<PC>());
		this.progressUI = progressUI;
	}
	
	public void changeDownloadFolder(String path) throws FolderDoesNotExist, InvalidPath{
		server.changeDownloadFolder(path);
	}

	public void sendFile(final FileInfo sendFile, final PC targetPC) throws IOException{
		Thread sendingThread = new Thread(){
			@Override
			public void run(){
				Socket clientSocket = null;
				FileInputStream file = null;
				System.out.println("Start Upload!!");
				try{
					byte[] buffer = new byte[MAX_DATA_SIZE];
					clientSocket = new Socket(targetPC.getHost(), targetPC.getPort());
					clientSocket.setSoTimeout(ConstantsConfig.TIME_FOR_TIMEOUT);
					ObjectOutputStream outToClient = new ObjectOutputStream(clientSocket.getOutputStream());
					file = new FileInputStream(sendFile.getFullName());
					outToClient.writeObject(sendFile);
					progressUI.startUpload(sendFile);
					int nread = -1;
					while((nread = file.read(buffer)) != -1){
						outToClient.write(buffer, 0, nread);
						outToClient.flush();
						progressUI.increment(sendFile, nread);
					}
					progressUI.transferFinished(sendFile, true);
				} catch(IOException e){
					progressUI.transferFinished(sendFile, false);
					e.printStackTrace();
				} finally {
					if(clientSocket != null){
						try {
							clientSocket.close();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
					if(file != null){
						try {
							file.close();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				}
				System.out.println("End Upload!!");

			}
		};
		sendingThread.setName("Uploading Thread #"+ threadNumber++);
		sendingThread.start();
	}

	public List<PC> getLocalPCs(){
		if(availableServers.size() == 0){
			return refreshLocalPCs();
		}
		return availableServers;
	}

	public void changeUsername(String name){
		clientName = name;
		server.setName(name);
	}
	
	public List<PC> refreshLocalPCs(){
		//TODO improve this function

		availableServers.clear();

		byte[] sendData = new byte[MAX_DATA_SIZE];
		String sentence = UdpMessages.searchMessage(clientName);
		sendData = sentence.getBytes();
		DatagramPacket sendPacket;
		try {
			sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(ConstantsConfig.BROADCAST), ConstantsConfig.DEFAULT_UDP_PORT);
			communicationSocket.send(sendPacket);
		}catch(IOException io){
			System.out.println(UdpMessages.ERROR_1(io.getMessage()));
			io.printStackTrace();
		}

		DatagramPacket receivePacket;
		try {
			communicationSocket.setSoTimeout(SCAN_TIMEOUT);
			do{
				byte[] receiveData = new byte[UdpMessages.MAX_DATA_SIZE];
				receivePacket = new DatagramPacket(receiveData, receiveData.length);
				communicationSocket.receive(receivePacket);
				String svMessage = new String(receivePacket.getData());
				String[] msgtoken = svMessage.split("\n");
				if(msgtoken.length < 3){
					System.out.println("Erro a obeter um servidor: " + svMessage);
					continue;
				}
				if(msgtoken[0].contains(UdpMessages.FOUND_MESSAGE)){
					availableServers.add(new PC(msgtoken[1], receivePacket.getAddress().getHostAddress(), Integer.parseInt(msgtoken[2])));
				}
			}while(true);
		}catch(SocketTimeoutException se){
			System.out.println("Search done!");
		}catch(IOException io){
			System.out.println(UdpMessages.ERROR_0(io.getMessage()));
			io.printStackTrace();
		}
		return availableServers;
	}
	
	public void stop(){
		server.stop();
	}


}
