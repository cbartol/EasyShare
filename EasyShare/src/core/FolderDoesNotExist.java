package core;

public class FolderDoesNotExist extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public FolderDoesNotExist(){
		super();
	}
	public FolderDoesNotExist(String message){
		super(message);
	}

}
