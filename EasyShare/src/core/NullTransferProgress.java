package core;

public class NullTransferProgress implements TransferProgressUI {

	
	@Override
	public void startDownload(FileInfo file){
	}
	
	@Override
	public void startUpload(FileInfo file){
	}

	@Override
	public void increment(FileInfo file, long ammount){
	}

	@Override
	public void transferFinished(FileInfo file, boolean success) {
	}

}
