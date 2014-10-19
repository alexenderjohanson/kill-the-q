package com.backyard.killtheq.asynctask;

import android.os.AsyncTask;

import com.backyard.killtheq.service.QueueService;

public class GetQueueAsyncTask extends AsyncTask<Integer, Void, Integer>{
	
	private AsyncTaskListener<Integer> listener;
	
	public GetQueueAsyncTask(AsyncTaskListener<Integer> listener){
		this.listener = listener;
	}

	@Override
	protected Integer doInBackground(Integer... params) {
		QueueService service = new QueueService();
		try {
			return service.getRemainingPerson(params[0]);
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	@Override
	protected void onPostExecute(Integer result) {
		super.onPostExecute(result);
		
		if(listener != null){
			listener.onPostExecuteCallBack(result);
		}
	}

}
