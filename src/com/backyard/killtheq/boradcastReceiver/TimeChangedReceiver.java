package com.backyard.killtheq.boradcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class TimeChangedReceiver extends BroadcastReceiver{
	
	private BroadcastReceiverListener<Void> listener;
	
	public TimeChangedReceiver(BroadcastReceiverListener<Void> listener){
		this.listener = listener;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		
		final String action = intent.getAction();

        if (action.equals(Intent.ACTION_TIME_TICK) && listener != null){
        	Bundle bundle = intent.getExtras();
        	listener.onReceive(null);
        }
	}

}
