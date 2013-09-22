package core;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class PingThread extends Thread {
	private final DatagramSocket udpSocket;
	private String name;
	private final int tcpServerPort;
	
	public PingThread(String name, int udpPort, int tcpPort) throws SocketException {
		this.name = name;
		this.tcpServerPort = tcpPort;
		this.udpSocket = new DatagramSocket(udpPort);
	}

	@Override
	public void run(){
		while(true){
			byte[] receiveData = new byte[UdpMessages.MAX_DATA_SIZE];
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			try{
				udpSocket.receive(receivePacket);
			}catch(IOException io){
				System.out.println(UdpMessages.ERROR_0(io.getMessage()));
				io.printStackTrace();
				return;
			}
            InetAddress ipAddress = receivePacket.getAddress();
            int port = receivePacket.getPort();
			String sentence = new String(receivePacket.getData());
			
			if(sentence.contains(UdpMessages.SEARCH_MESSAGE)){
				sendFoundResponse(ipAddress, port);
			}

           // System.out.println("RECEIVED: " + sentence);
           // System.out.printf("\tFrom: %s:%d\n\n", ipAddress.getHostAddress(), port);
		}
		
	}
	
	@Override
	public void interrupt(){
		udpSocket.close();
		System.out.println("PingThread stopped...");		
		super.interrupt();
	}
	
	private void sendFoundResponse(InetAddress ip, int port){
		byte[] sendData = new byte[UdpMessages.MAX_DATA_SIZE];
		sendData = UdpMessages.foundServerMessage(name, tcpServerPort).getBytes();
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ip, port);
		try{
			udpSocket.send(sendPacket);
		}catch(IOException io){
			System.out.println(UdpMessages.ERROR_1(io.getMessage()));
			io.printStackTrace();
		}
	}

	public void setUsername(String name2) {
		name = name2;		
	}
}
