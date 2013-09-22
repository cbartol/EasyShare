package easyshare.androidui.application;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import core.PC;
import easyshare.androidui.R;
import easyshare.androidui.application.filesearch.SampleFolderActivity;
import easyshare.androidui.service.EasyShareService;

public class MainActivity extends Activity {
	public static boolean serviceAlreadyStarted = false;
	private EasyServiceConnection refreshConnection = new EasyServiceConnection();
	private boolean isBound = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		
		SharedPreferences sharedPref = getSharedPreferences(getString(R.string.user_properties), Context.MODE_PRIVATE);
		boolean on = sharedPref.getBoolean(getString(R.string.service_status_key), true);
		Log.i("MainActivity", "on["+on+"] alreadyStarted["+serviceAlreadyStarted+"]");
		if(on && !serviceAlreadyStarted){
			Intent intent = new Intent(this, EasyShareService.class);
			startService(intent);
			serviceAlreadyStarted = true;
		}
	}

	@Override
	protected void onResume() {
		Intent intent = new Intent(this, EasyShareService.class);
		isBound = bindService(intent, refreshConnection, 0);
		super.onResume();
		
		//the following is needed because when we try to make refresh after binding to service sometimes it doesn't work.
		//the solution is to delay the call to refresh() by 500 milliseconds.
		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
			@Override
			protected Void doInBackground(Void... params) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				refresh(null);
				return null;
			}
		};
		task.execute();
	}

	@Override
	protected void onPause() {
		if(isBound()){
			unbindService(refreshConnection);
		}
		super.onPause();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.action_settings:
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	/** Called when the user clicks the Send button */
	public void refresh(View view) {
		Log.i("MainActvity", "refresh ["+isBound()+"]");
		final MainActivity activity = this;
		AsyncTask<Void, Void, List<PC>> task = new AsyncTask<Void, Void, List<PC> >(){
			
			@Override
			protected List<PC> doInBackground(Void... params) {
				List<PC> pc_details = refreshConnection.refresh();
				return pc_details;
			}
			
			@Override
			protected void onPostExecute(List<PC> result) {
				final ListView lv1 = (ListView) activity.findViewById(R.id.devices_list);
				lv1.setAdapter(new ComputersListAdapter(activity, result));
				lv1.setOnItemClickListener(new OnItemClickListener() {
					
					@Override
					public void onItemClick(AdapterView<?> a, View v, int position, long id) {
						Object o = lv1.getItemAtPosition(position);
						PC newsData = (PC) o;
						Toast.makeText(activity, "Selected : " + newsData.getName(), Toast.LENGTH_SHORT).show();
						Intent intent = new Intent(activity, SampleFolderActivity.class);
						intent.putExtra("NAME", newsData.getName());
						intent.putExtra("HOST", newsData.getHost());
						intent.putExtra("PORT", newsData.getPort());
						startActivity(intent);
					}
					
				});
				super.onPostExecute(result);
			}
			
		};
		task.execute();
	}

	private boolean isBound() {
		return isBound;
	}	
}
