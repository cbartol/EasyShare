package easyshare.androidui.application.notifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import core.AcceptDownloadUI;
import core.FileInfo;
import easyshare.androidui.R;

public class RequestDownloadNotification implements AcceptDownloadUI {
	private int notificationId = 10000;
	private Context context;
	
	public RequestDownloadNotification(Context context) {
		this.context = context;
	}
	
	private int generateId(){
		return notificationId++;
	}
	
	@Override
	public boolean acceptDownload(FileInfo arg0) {
		NotificationManager mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, new Intent(), 0);
		NotificationCompat.Builder mBuilder =
			    new NotificationCompat.Builder(context)
			    .setSmallIcon(R.drawable.download_icon)
			    .setContentTitle("EasyShare")
			    .setContentText("You have a new file for download.")
			    .setContentIntent(resultPendingIntent);
		mNotifyManager.notify(generateId(), mBuilder.build());
		
		
		
		return true;
	}

}
