package easyshare.androidui.service;

interface IEasyShareService{
	oneway void setUsername(in String name);
	oneway void setDownloadPath(in String path);
	List<String> refresh();
	oneway void sendFile(in String owner, in String path, in String name, in String host, in int port);
}