package core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;

public class FileInfo implements Serializable{
	private static final long serialVersionUID = 1L;

	private final String owner;
	private final String fullFileName;
	private final String fileName;
	private final long size;
	
	public FileInfo(String owner, String fullFileName) throws IOException{
		this.fullFileName = fullFileName;
		FileInputStream testFile = new FileInputStream(fullFileName);
		testFile.close();
		File file = new File(fullFileName);
		size = file.length();
		this.fileName = file.getName();
		this.owner = owner;
	}
	
	public String getFullName() {
		return fullFileName;
	}
	
	public String getFileName() {
		return fileName;
	}

	public long getFileSize() {
		return size;
	}

	public String getFileOwner() {
		return owner;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof FileInfo){
			FileInfo f = (FileInfo) obj;
			return f.owner == this.owner && f.fullFileName == this.fullFileName && f.fileName == this.fileName && f.size == this.size;
		}
		return false;
	}

}
