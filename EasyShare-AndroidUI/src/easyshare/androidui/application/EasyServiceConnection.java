package easyshare.androidui.application;

import java.util.ArrayList;
import java.util.List;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import core.PC;
import easyshare.androidui.service.IEasyShareService;

public class EasyServiceConnection implements ServiceConnection{
	private IEasyShareService service = null;
	
	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		Log.i("RefreshListServiceConnection", "onServiceConnected");
		this.service = IEasyShareService.Stub.asInterface(service);
	}
	
	@Override
	public void onServiceDisconnected(ComponentName name) {
		Log.i("RefreshListServiceConnection", "onServiceDisconnected");
		this.service = null;
	}

//	public IEasyShareService getService(){
//		return service;
//	}
	
	public List<PC> refresh(){
		List<PC> result = new ArrayList<PC>();
		Log.i("MainActivity", "refreshConnection.getService() ["+service+"]");
		if(service == null){
			return result;
		}
		try {
			List<String> lst = service.refresh();
			for(String s : lst){
				String[] fields = s.split("\n");
				if(fields.length < 3){
					continue;
				}
				result.add(new PC(fields[0], fields[1], Integer.parseInt(fields[2])));
			}
		} catch (RemoteException e) {
			service = null;
		}
		return result;
	}
	
	public void setUsername(String name){
		if(service == null){
			return;
		}
		try {
			service.setUsername(name);
		} catch (RemoteException e) {
			service = null;
		}
	}
	
	public void setDownloadFolder(String path){
		if(service == null){
			return;
		}
		try {
			service.setDownloadPath(path);
		} catch (RemoteException e) {
			service = null;
		}
	}
	
	public void sendFile(String owner, String path, String destName, String host, int port){
		if(service == null){
			return;
		}
		try {
			service.sendFile(owner, path, destName, host, port);
		} catch (RemoteException e) {
			service = null;
		}
	}
}
