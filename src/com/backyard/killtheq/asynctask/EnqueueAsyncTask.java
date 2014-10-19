package com.backyard.killtheq.asynctask;

import android.os.AsyncTask;

import com.backyard.killtheq.model.Enqueue;
import com.backyard.killtheq.provider.QueueProvider;
import com.backyard.killtheq.service.QueueService;

public class EnqueueAsyncTask extends AsyncTask<Void, Void, Enqueue>{

	private AsyncTaskListener<Enqueue> listener;
	
	public EnqueueAsyncTask(AsyncTaskListener<Enqueue> listener){
		this.listener = listener;
	}
	
	@Override
	protected Enqueue doInBackground(Void... params) {
		QueueProvider provider = new QueueProvider();
		try {
			return provider.Enqueue();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
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
	protected void onPostExecute(Enqueue result) {
		super.onPostExecute(result);
		
		if(listener != null){
			listener.onPostExecuteCallBack(result);
		}
	}
}
