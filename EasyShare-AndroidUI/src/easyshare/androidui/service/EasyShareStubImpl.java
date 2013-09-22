package easyshare.androidui.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.os.RemoteException;
import android.util.Log;
import core.EasyShare;
import core.FileInfo;
import core.FolderDoesNotExist;
import core.InvalidPath;
import core.PC;

public class EasyShareStubImpl extends IEasyShareService.Stub{
	private final EasyShareService service;

	public EasyShareStubImpl(EasyShareService easyShareService) {
		service = easyShareService;
	}

	@Override
	public void setUsername(String name) throws RemoteException {
		Log.i("EasyShareStub", "changing name ["+name+"]");
		EasyShare server = service.getServer();
		if(server == null){
			return;
		}
		server.changeUsername(name);
	}

	@Override
	public void setDownloadPath(String path) throws RemoteException {
		Log.i("EasyShareStub", "changing path ["+path+"]");
		EasyShare server = service.getServer();
		if(server == null){
			return;
		}
		try {
			server.changeDownloadFolder(path);
		} catch (FolderDoesNotExist e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidPath e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}

	@Override
	public List<String> refresh() throws RemoteException {
		List<String> lst = new ArrayList<String>();
		EasyShare server = service.getServer();
		Log.i("EasyShareStub", "REFRESHING ["+server+"]");
		if(server == null){
			return lst;
		}
		List<PC> result = server.refreshLocalPCs();
		for(PC pc : result){
			lst.add(pc.getName()+"\n"+pc.getHost()+"\n"+pc.getPort());
		}
		return lst;
	}

	@Override
	public void sendFile(String owner, String path, String name, String host, int port) throws RemoteException {
		Log.i("EasyShareStub", "sending file ["+name+" "+host+":"+port+"] ["+path+"]");
		EasyShare server = service.getServer();
		if(server == null){
			return;
		}
		PC targetPC = new PC(name, host, port);
		FileInfo sendFile;
		try {
			sendFile = new FileInfo(owner, path);
		} catch (IOException e) {
			e.printStackTrace();
			//TODO send alert
			return;
		}
		try {
			server.sendFile(sendFile, targetPC);
		} catch (IOException e) {
			e.printStackTrace();
			//TODO send alert
		}
	}

}
