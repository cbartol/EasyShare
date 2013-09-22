package textui;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import core.ConstantsConfig;
import core.EasyShare;
import core.FileInfo;
import core.PC;

public class AppTest {
	private static Scanner in = new Scanner(System.in);
	private static String name = "default";
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		int tcpPort = 20000;
		int udpPort = ConstantsConfig.DEFAULT_UDP_PORT;
		System.out.println("Welcome to the best program ever!");
		System.out.println(" ");
		System.out.println("Enter your name: ");
		name = in.nextLine();
		EasyShare client = new EasyShare(name, "", tcpPort, udpPort);
		System.out.println("Searching for pc's....");
		List<PC> pcs = client.getLocalPCs();

		while(true){
			int option = -1;
			do{
				System.out.println("Choose the destination:");
				int i = 1;
				for(PC pc : pcs){
					System.out.println(i++ + " - " + pc.getHost() +":"+pc.getPort() + "  " + pc.getName());
				}
				System.out.println((pcs.size() + 1) + " - Refresh List");
				System.out.println("0 - Exit");
				System.out.println(" ");
				System.out.print("option: ");
				option = in.nextInt();
				in.nextLine();
				System.out.println(" ");
			} while(option < 0 || option > pcs.size()+1);
			if(option == 0){
				client.stop();
				return;
			} else if(option > 0 && option <= pcs.size()){
				FileInfo file = chooseFile();
				client.sendFile(file, pcs.get(option-1));
			} else {
				System.out.println("Searching for pc's....");
				pcs = client.refreshLocalPCs();
			}
		}
	}

	private static FileInfo chooseFile() throws IOException {
		System.out.println("Choose file to send:");
		String file = in.nextLine();
		return new FileInfo(name, file);
	}

}
