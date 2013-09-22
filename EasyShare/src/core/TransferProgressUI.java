package core;

public interface TransferProgressUI {
	public void startDownload(FileInfo file);
	public void startUpload(FileInfo file);
	public void increment(FileInfo file, long ammount);
	public void transferFinished(FileInfo file, boolean success);
	
}
