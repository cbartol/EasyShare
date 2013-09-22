package easyshare.androidui.application;

import java.io.File;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import android.widget.ToggleButton;
import easyshare.androidui.R;
import easyshare.androidui.service.EasyShareService;

public class SettingsActivity extends Activity {

	private boolean isBound;
	private EasyServiceConnection refreshConnection = new EasyServiceConnection();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		// Show the Up button in the action bar.
		setupActionBar();

		setupRadioButtons();
		setupInserUsername();
		setupToggleButton();
	}

	private void setupInserUsername() {
		SharedPreferences sharedPref = getSharedPreferences(getString(R.string.user_properties), Context.MODE_PRIVATE);
		String username = sharedPref.getString(getString(R.string.username_key), getString(R.string.default_username));
		Log.i("SettingsActivity", "username: "+username);
		EditText edit = (EditText) findViewById(R.id.insert_username);
		edit.setText(username);
		edit.setImeActionLabel("save", EditorInfo.IME_ACTION_DONE);
		edit.setImeActionLabel("save", KeyEvent.KEYCODE_ENTER);
		edit.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				String username = ((EditText) findViewById(R.id.insert_username)).getText().toString();
				if(username.equals("")){
					String invalidUsername = getString(R.string.invalid_username); 
					Toast.makeText(SettingsActivity.this, invalidUsername, Toast.LENGTH_LONG).show();
					return true;
				}
				if(actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_UNSPECIFIED){
					SharedPreferences sharedPref = getSharedPreferences(getString(R.string.user_properties), Context.MODE_PRIVATE);
					SharedPreferences.Editor editor = sharedPref.edit();
					editor.putString(getString(R.string.username_key), username);
					editor.commit();
					Log.i("SettingsActivity", "Saving Username..");
					String saved = getString(R.string.saved);
					if(isBound){
						refreshConnection.setUsername(username);
					}
					Toast.makeText(SettingsActivity.this, saved, Toast.LENGTH_LONG).show();
				}
				return false;
			}
		});
		
	}

	private void setupRadioButtons() {
		SharedPreferences sharedPref = getSharedPreferences(getString(R.string.user_properties), Context.MODE_PRIVATE);
		String path = sharedPref.getString(getString(R.string.path_key), getDir(getString(R.string.download_folder), Context.MODE_PRIVATE).getAbsolutePath());
		if(!isExternalStorageAvailable()){
			View radioButton = findViewById(R.id.external_folder);
			radioButton.setEnabled(false);
		} else if(!path.equals(getDir(getString(R.string.download_folder), Context.MODE_PRIVATE).getAbsolutePath())){
			Log.i("SettingsActivity", "change radio");
			RadioButton radioButton1 = (RadioButton) findViewById(R.id.internal_folder);
			radioButton1.setChecked(false);
			RadioButton radioButton2 = (RadioButton) findViewById(R.id.external_folder);
			radioButton2.setChecked(true);
		}
	}

	private void setupToggleButton() {
		SharedPreferences sharedPref = getSharedPreferences(getString(R.string.user_properties), Context.MODE_PRIVATE);
		boolean on = sharedPref.getBoolean(getString(R.string.service_status_key), true);
		Log.i("SettingsActivity", "on["+on+"]");
		ToggleButton toggle = (ToggleButton) findViewById(R.id.service_status_button);
		toggle.setChecked(on);

		final Intent intent = new Intent(this, EasyShareService.class);
		toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					startService(intent);
				} else {
					stopService(intent);
				}
				SharedPreferences sharedPref = getSharedPreferences(getString(R.string.user_properties), Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = sharedPref.edit();
				editor.putBoolean(getString(R.string.service_status_key), isChecked);
				editor.commit();
			}
		});		
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {
		Intent intent = new Intent(this, EasyShareService.class);
		isBound = bindService(intent, refreshConnection, 0);
		super.onResume();
	}

	@Override
	protected void onPause() {
		if(isBound){
			unbindService(refreshConnection);
		}
		super.onPause();
	}

	private static boolean isExternalStorageAvailable() {
		String extStorageState = Environment.getExternalStorageState();  
		if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {  
			return true;  
		}  
		return false;  
	}


	public void setInternal(View view){
		String path = getDir(getString(R.string.download_folder), Context.MODE_PRIVATE).getAbsolutePath();
		SharedPreferences sharedPref = getSharedPreferences(getString(R.string.user_properties), Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString(getString(R.string.path_key), path);
		editor.commit();
		if(isBound){
			refreshConnection.setDownloadFolder(path);
		}
		Toast.makeText(SettingsActivity.this, "Internal Mode", Toast.LENGTH_LONG).show();
		
	}
	
	public void setExternal(View view){
		File file = Environment.getExternalStorageDirectory();
		if(file == null){
			return;
		}
		String path = file.getAbsolutePath()+"/"+getString(R.string.download_folder);
		SharedPreferences sharedPref = getSharedPreferences(getString(R.string.user_properties), Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString(getString(R.string.path_key), path);
		editor.commit();
		if(isBound){
			refreshConnection.setDownloadFolder(path);
		}
		Toast.makeText(SettingsActivity.this, "External Mode", Toast.LENGTH_LONG).show();
		
	}
}
