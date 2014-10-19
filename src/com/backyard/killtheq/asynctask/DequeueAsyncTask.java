package com.backyard.killtheq.asynctask;

import android.os.AsyncTask;

import com.backyard.killtheq.model.Enqueue;
import com.backyard.killtheq.provider.QueueProvider;
import com.backyard.killtheq.service.QueueService;

public class DequeueAsyncTask extends AsyncTask<Integer, Void, Boolean>{

private AsyncTaskListener<Boolean> listener;
	
	public DequeueAsyncTask(AsyncTaskListener<Boolean> listener){
		this.listener = listener;
	}
	
	@Override
	protected Boolean doInBackground(Integer... params) {
		QueueProvider provider = new QueueProvider();
		try {
			return provider.Dequeue(params[0]);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		
		if(listener != null){
			listener.onPreExecuteCallBack();
		}
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		
		if(listener != null){
			listener.onPostExecuteCallBack(result);
		}
	}
}
