package com.example.notificationspush;

import java.util.HashMap;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class NotificationActivity extends Activity {

	public static HashMap<String, Integer> dataToSend = new HashMap<String, Integer>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notification);
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
			dataToSend.put("Id", 484848);
			dataToSend.put("IdGerente", 4455);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.notification, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		private Button btTryMe;
		private Button btDisconnect;

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_notification, container, false);

			btTryMe = (Button) rootView.findViewById(R.id.btTryMe);
			btDisconnect = (Button) rootView.findViewById(R.id.btDisconnet);

			btTryMe.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {

					getActivity().startService(new Intent(getActivity(), NotificationService.class));

				}
			});

			btDisconnect.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Toast.makeText(getActivity(), "Adios", Toast.LENGTH_SHORT).show();
				}
			});

			return rootView;
		}

	}
}
