package com.backyard.killtheq.boradcastReceiver;

public interface BroadcastReceiverListener<T> {

	void onReceive(T result);
}
