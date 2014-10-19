package com.backyard.killtheq.asynctask;

public interface AsyncTaskListener<T> {
	
	void onPostExecuteCallBack(T result);
	
	void onPreExecuteCallBack();
}
