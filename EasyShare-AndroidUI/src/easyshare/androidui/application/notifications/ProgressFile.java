package easyshare.androidui.application.notifications;

import android.support.v4.app.NotificationCompat;
import core.FileInfo;

public class ProgressFile {
	private int id;
	private FileInfo file;
	private NotificationCompat.Builder mBuilder;
	private long progress = 0;
	private int convertedProgress = 0;
	private boolean changed = false;
	private int progressSize;
	
	public ProgressFile(int id, FileInfo file, NotificationCompat.Builder notificationBuilder, int barSize){
		this.id = id;
		this.file = file;
		this.mBuilder = notificationBuilder;
		this.progressSize = barSize;
	}

	public int getId() {
		return id;
	}

	public FileInfo getFile() {
		return file;
	}

	public NotificationCompat.Builder getNotificationBuilder() {
		return mBuilder;
	}

	public int increment(long incr){
		progress += incr;
		int result = (int) ((progress*progressSize)/file.getFileSize());
		changed = result != convertedProgress;
		convertedProgress = result;
		return result;
	}
	
	
	public boolean progressChanged() {
		return changed;
	}
}
