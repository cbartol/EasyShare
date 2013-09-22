package core;

public class DefaultAcceptDownload implements AcceptDownloadUI {

	@Override
	public boolean acceptDownload(FileInfo file) {
		return true;
	}

}
