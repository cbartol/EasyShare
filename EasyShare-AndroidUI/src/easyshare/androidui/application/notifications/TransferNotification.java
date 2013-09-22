package easyshare.androidui.application.notifications;

import java.util.HashMap;
import java.util.Map;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import core.FileInfo;
import core.TransferProgressUI;
import easyshare.androidui.R;

public class TransferNotification implements TransferProgressUI {
	private static final int PROGRESS_MAX = 100;
	private Context context;
	private NotificationManager mNotifyManager;
	private int id = 1;
	private Map<FileInfo,ProgressFile> downloading = new HashMap<FileInfo,ProgressFile>();
	private Map<FileInfo,ProgressFile> uploading = new HashMap<FileInfo,ProgressFile>();
	
	public TransferNotification(Context context) {
		this.context = context;
		mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
	}
	
	private int generateId(){
		return id++;
	}
	
	@Override
	public void increment(FileInfo arg0, long arg1) {
		ProgressFile progressUP = uploading.get(arg0);
		ProgressFile progressDOWN = downloading.get(arg0);
		incrementProgress(progressUP, arg1);
		incrementProgress(progressDOWN, arg1);
	}
	
	private void incrementProgress(ProgressFile p, long incr){
		if(p != null){
			int progr = p.increment(incr);
			int progressId = p.getId();
			NotificationCompat.Builder mBuilder = p.getNotificationBuilder();
			if(p.progressChanged()){
				mBuilder.setProgress(PROGRESS_MAX, progr, false);
				mNotifyManager.notify(progressId, mBuilder.build());
			}
		}
	}

	@Override
	public void startDownload(FileInfo arg0) {
		int progressId = generateId();
		PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, new Intent(), 0);
		NotificationCompat.Builder mBuilder =
			    new NotificationCompat.Builder(context)
			    .setSmallIcon(R.drawable.download_icon)
			    .setContentTitle("File: "+arg0.getFileName())
			    .setContentText("Downloading...")
			    .setContentIntent(resultPendingIntent)
			    .setOngoing(true)
			    .setProgress(PROGRESS_MAX, 0, false);
		downloading.put(arg0, new ProgressFile(progressId, arg0, mBuilder, PROGRESS_MAX));
		mNotifyManager.notify(progressId, mBuilder.build());
	}

	@Override
	public void startUpload(FileInfo arg0) {
		int progressId = generateId();
		PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, new Intent(), 0);
		NotificationCompat.Builder mBuilder =
				new NotificationCompat.Builder(context)
		.setSmallIcon(R.drawable.upload_icon)
		.setContentTitle("File: "+arg0.getFileName())
		.setContentText("Uploading...")
		.setContentIntent(resultPendingIntent)
		.setOngoing(true)
		.setProgress(PROGRESS_MAX, 0, false);
		uploading.put(arg0, new ProgressFile(progressId, arg0, mBuilder, PROGRESS_MAX));
		mNotifyManager.notify(progressId, mBuilder.build());
	}

	@Override
	public void transferFinished(FileInfo arg0, boolean arg1) {
		ProgressFile progressUP = uploading.get(arg0);
		ProgressFile progressDOWN = downloading.get(arg0);
		finishProgress(progressUP, "Upload complete.");
		finishProgress(progressDOWN, "Download complete.");
	}
	
	private void finishProgress(ProgressFile p, String text){
		if(p != null){
			int progressId = p.getId();
			NotificationCompat.Builder mBuilder = p.getNotificationBuilder();
			mBuilder.setContentText(text);
			mBuilder.setOngoing(false);
			mBuilder.setProgress(0,0, false);
			mNotifyManager.notify(progressId, mBuilder.build());
		}
	}

}
