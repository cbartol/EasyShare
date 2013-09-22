package core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class DownloadThread extends Thread {
	private final static int BUFFER_SIZE = UdpMessages.MAX_DATA_SIZE;
	private final static int TIME_FOR_TIMEOUT = ConstantsConfig.TIME_FOR_TIMEOUT;
	private final ServerSocket tcpSocket;
	private String downloadFolder;
	private int nthread = 0;
	private AcceptDownloadUI acceptUI = new DefaultAcceptDownload();
	private TransferProgressUI progressUI = new NullTransferProgress();

	public DownloadThread(String defaultFolder, int tcpPort) throws IOException {
		this.tcpSocket = new ServerSocket(tcpPort);
		this.setDownloadFolder(defaultFolder);
	}

	public DownloadThread(String defaultDownloadFolder, int tcpPort,
			TransferProgressUI progressUI, AcceptDownloadUI acceptanceUI) throws IOException {
		this(defaultDownloadFolder, tcpPort);
		this.acceptUI = acceptanceUI;
		this.progressUI = progressUI;
	}

	@Override
	public void run(){
		try{
			while(true){
				final Socket connectionSocket = tcpSocket.accept();
				try{
					connectionSocket.setSoTimeout(TIME_FOR_TIMEOUT);
					final ObjectInputStream inFromClient = new ObjectInputStream(connectionSocket.getInputStream());
					final FileInfo metadata = (FileInfo) inFromClient.readObject();
					//TODO prompt user to accept or reject the file
					if(!acceptUI.acceptDownload(metadata)){
						connectionSocket.close();
						continue;
					}
					Thread downloadThread = new Thread(){
						@Override
						public void run(){
							FileOutputStream file = null;
							try{
								(new File(downloadFolder)).mkdirs();
								System.out.println("File name: "+ metadata.getFileName());
								System.out.println("Download folder: "+downloadFolder);
								file = new FileOutputStream(new File(downloadFolder, metadata.getFileName()));
								byte[] buffer = new byte[BUFFER_SIZE];
								int nread = -1;
								System.out.println("Download Started.");
								progressUI.startDownload(metadata);
								while((nread = inFromClient.read(buffer)) != -1){
									file.write(buffer, 0, nread);
									progressUI.increment(metadata, nread);
								}
								file.flush();
								progressUI.transferFinished(metadata, true);
								System.out.println("Download Complete.");
							}catch (IOException e){
								progressUI.transferFinished(metadata, false);
								e.printStackTrace();
							} finally {
								try {
									connectionSocket.close();
								} catch (IOException e) {
									e.printStackTrace();
								}
								if(file != null){
									try {
										file.close();
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
							}
						}
					};
					downloadThread.start();
					downloadThread.setName("Sub Download Thread #"+nthread++);
				}catch (IOException e){
					e.printStackTrace();
					try {
						connectionSocket.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}catch (ClassNotFoundException e){
					e.printStackTrace();
					try {
						connectionSocket.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
			try {
				tcpSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void interrupt(){
		try {
			tcpSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("DownloadThread stopped...");		
		super.interrupt();
	}

	public String getDownloadFolder() {
		return downloadFolder;
	}

	public void setDownloadFolder(String downloadFolder) {
		this.downloadFolder = downloadFolder;
	}
}
