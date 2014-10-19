package com.backyard.killtheq.asynctask;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;

import com.backyard.killtheq.provider.QueueProvider;

public class GetQueueListAsyncTask extends AsyncTask<Void, Void, List<Integer>>{
	
	private AsyncTaskListener<List<Integer>> listener;
	
	public GetQueueListAsyncTask(AsyncTaskListener<List<Integer>> listener){
		this.listener = listener;
	}

	@Override
	protected List<Integer> doInBackground(Void... params) {
		QueueProvider provider = new QueueProvider();
		try {
			return provider.getQueueList();
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<Integer>();
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
	protected void onPostExecute(List<Integer> result) {
		super.onPostExecute(result);
		
		if(listener != null){
			listener.onPostExecuteCallBack(result);
		}
	}

}
