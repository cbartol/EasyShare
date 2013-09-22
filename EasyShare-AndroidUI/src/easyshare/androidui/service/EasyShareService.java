package easyshare.androidui.service;

import java.io.IOException;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;
import core.ConstantsConfig;
import core.EasyShare;
import easyshare.androidui.R;
import easyshare.androidui.application.MainActivity;
import easyshare.androidui.application.notifications.RequestDownloadNotification;
import easyshare.androidui.application.notifications.TransferNotification;

public class EasyShareService extends Service {
	private EasyShare server = null;
	private IEasyShareService.Stub mBinder = new EasyShareStubImpl(this);

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		MainActivity.serviceAlreadyStarted = true;
		
		if(server == null){
			SharedPreferences sharedPref = getSharedPreferences(getString(R.string.user_properties), Context.MODE_PRIVATE);
			String username = sharedPref.getString(getString(R.string.username_key), getString(R.string.default_username));
			String path = sharedPref.getString(getString(R.string.path_key), getDir(getString(R.string.download_folder), Context.MODE_PRIVATE).getAbsolutePath());
			Log.i("EasyShareService", "Starting service.");
			try {
				server = new EasyShare(username, path, ConstantsConfig.DEFAULT_TCP_PORT, ConstantsConfig.DEFAULT_UDP_PORT, new TransferNotification(this), new RequestDownloadNotification(this));
			} catch (IOException e) {
				e.printStackTrace();
				stopSelf();
				return START_NOT_STICKY;
			}
			Log.i("EasyShareService", "Service started.");
		} else {
			Log.i("EasyShareService", "Service already started.");
		}
		return START_STICKY;
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		return mBinder;
	}
	
	@Override
	public void onDestroy() {
		Log.i("EasyShareService", "Stopping service ["+server+"].");
		if(server != null){
			server.stop();
			server = null;
		}
		Log.i("EasyShareService", "Service stopped.");
		super.onDestroy();
	}
	
	EasyShare getServer(){
		return server;
	}
	
}
