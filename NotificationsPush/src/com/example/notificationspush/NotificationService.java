package com.example.notificationspush;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Random;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class NotificationService extends Service {

	private NotificationManager notificationManager;
	private int NOTIFICATION = R.string.local_service_started;
	private final BroadcastReceiverNotifications receiver = new BroadcastReceiverNotifications();

	public class LocalBinder extends Binder {
		NotificationService getService() {
			return NotificationService.this;
		}
	}

	@Override
	public void onCreate() {
		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		IntentFilter filter = new IntentFilter();
		filter.addAction("serviceFinished");
		registerReceiver(receiver, filter);

	}

	@Override
	public void onDestroy() {
		unregisterReceiver(receiver);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("NotificationService", "Received start id " + startId + ": " + intent);
		new RunInBackground().execute();

		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	private final IBinder mBinder = new LocalBinder();

	private void showNotification(String titulo, String mensagem) {

		// Escolhendo a Acttivity que a notificação levará
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, NotificationActivity.class),
				0);

		NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
		Notification notification = builder.setContentIntent(contentIntent).setSmallIcon(R.drawable.ic_launcher)
				.setAutoCancel(true).setContentTitle(titulo).setContentText(mensagem).build();

		notificationManager.notify(NOTIFICATION, notification);
	}

	private class RunInBackground extends AsyncTask<String, String, String> {

		private ArrayList<String> urls = new ArrayList<String>();
		private String dadosEnvio;
		private String urlGET;
		private final String urlPOST = "https://api.myjson.com/bins";

		@Override
		protected String doInBackground(String... params) {
			String response = "{\"NO DATA:\"NO DATA\"}";
			Random r = new Random();
			urls.add("https://api.myjson.com/bins/mqlkh");
			urls.add("https://api.myjson.com/bins/wr5i9");
			urls.add("https://api.myjson.com/bins/g30kh");

			try {
				dadosEnvio = preparaDadosEnvio();
				Log.i("Requisicao em background ", dadosEnvio);

				urlGET = urls.get(r.nextInt(urls.size()));
				response = doGet(urlGET);
				buildNotification(new JSONObject(response));
				Log.i("Response ", response);

			} catch (Exception e) {
				response = e.getMessage().toString();
			}

			return response;
		}

		// Monta o json de envio, depois o transforma em String
		private String preparaDadosEnvio() throws JSONException {
			HashMap<String, Integer> dataToSendMap = NotificationActivity.dataToSend;
			JSONObject dataToSendJson = new JSONObject();

			Iterator<Entry<String, Integer>> iterator = dataToSendMap.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<String, Integer> pair = iterator.next();
				dataToSendJson.put(pair.getKey(), pair.getValue());
				iterator.remove();
			}

			return dataToSendJson.toString();
		}

		private void buildNotification(JSONObject json) throws Exception {
			JSONObject resposta = json.getJSONObject("Resposta");
			int erro = resposta.getInt("Erro");
			if (erro == 200) {
				JSONObject notificacaoJson = json.getJSONObject("Notificacao");
				String titulo = notificacaoJson.getString("Titulo");
				String mensagem = notificacaoJson.getString("Mensagem");
				showNotification(titulo, mensagem);
			}
		};

		private String convertInputStreamToString(InputStream inputStream) throws IOException {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			String line = "";
			String result = "";
			while ((line = bufferedReader.readLine()) != null)
				result += line;

			inputStream.close();
			return result;

		}

		public String doGet(String url) throws Exception {
			String result = "";
			InputStream inputStream = null;

			HttpClient httpclient = new DefaultHttpClient();
			HttpResponse httpResponse = httpclient.execute(new HttpGet(url));

			inputStream = httpResponse.getEntity().getContent();
			if (inputStream != null)
				result = convertInputStreamToString(inputStream);

			return result;
		}

		public String doPost(String url, String dataToSend) throws Exception {
			String result = "";
			InputStream inputStream = null;

			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
			httpPost.setEntity(new StringEntity(dataToSend));

			HttpResponse httpResponse = httpClient.execute(httpPost);
			inputStream = httpResponse.getEntity().getContent();
			if (inputStream != null)
				result = convertInputStreamToString(inputStream);

			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			Log.i("Checking for notifications:", result);
			super.onPostExecute(result);
			new Handler().postDelayed(new Runnable() {
				public void run() {
					try {
						new RunInBackground().execute();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}, 10000);
		}

	}

}
