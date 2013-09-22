package easyshare.androidui.application.filesearch;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import easyshare.androidui.R;
import easyshare.androidui.application.EasyServiceConnection;
import easyshare.androidui.service.EasyShareService;

public class SampleFolderActivity extends Activity implements IFolderItemListener {

	FolderLayout localFolders;
	private String name = null;
	private String host = null;
	private int port = 0;
	private boolean isBound = false;
	private EasyServiceConnection sendFileConnection = new EasyServiceConnection();

	/** Called when the activity is first created. */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.folders);
		name = getIntent().getStringExtra("NAME");
		host = getIntent().getStringExtra("HOST");
		port = getIntent().getIntExtra("PORT", 0);
		localFolders = (FolderLayout)findViewById(R.id.localfolders);
		localFolders.setIFolderItemListener(this);
		localFolders.setDir("/");//change directory if u want,default is root   
	}

	@Override
	protected void onResume() {
		Intent intent = new Intent(this, EasyShareService.class);
		isBound = bindService(intent, sendFileConnection, 0);
		super.onResume();
	}

	@Override
	protected void onPause() {
		if(isBound){
			unbindService(sendFileConnection);
		}
		super.onPause();
	}

	//Your stuff here for Cannot open Folder
	public void OnCannotFileRead(File file) {
		// TODO Auto-generated method stub
		new AlertDialog.Builder(this)
		.setTitle(
				"[" + file.getName() + "] folder can't be read!")
				.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog,
							int which) {


					}
				}).show();

	}


	//Your stuff here for file Click
	public void OnFileClicked(final File file) {
		final Activity activity = this;
		new AlertDialog.Builder(this)
		.setTitle("["+ file.getName() +"] Send this file?")
		.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog,
					int which) {
				sendFileConnection.sendFile("", file.getAbsolutePath(), name, host, port);
				activity.finish();
			}

		})
		.setNegativeButton("No", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		}).show();
	}

}
