package com.example.notificationspush;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BroadcastReceiverNotifications extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent intentService = new Intent(context, NotificationService.class);
		context.startService(intentService);
	}

}
