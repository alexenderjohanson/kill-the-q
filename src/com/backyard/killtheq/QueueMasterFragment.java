package com.backyard.killtheq;

import java.util.List;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.backyard.killtheq.adapter.QueuesAdapter;
import com.backyard.killtheq.asynctask.AsyncTaskListener;
import com.backyard.killtheq.asynctask.DequeueAsyncTask;
import com.backyard.killtheq.asynctask.GetQueueListAsyncTask;

public class QueueMasterFragment extends Fragment implements DequeueListener{
	
	private ListView queues;
	private QueuesAdapter adapter;
	private AsyncTaskListener<List<Integer>> getQueueListListener;
	private AsyncTaskListener<Boolean> dequeueListListener;
	private ProgressBar progress;
	private int dequeueNumber;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_queue_master, container, false);
        queues = (ListView)rootView.findViewById(R.id.lvQueues);
        progress = (ProgressBar)rootView.findViewById(R.id.pbLoading);
        
        adapter = new QueuesAdapter(getActivity(), this);
        queues.setAdapter(adapter);
        
        getQueueListListener = new AsyncTaskListener<List<Integer>>(){

			@Override
			public void onPostExecuteCallBack(List<Integer> result) {
				progress.setVisibility(View.GONE);
				adapter.setList(result);
				adapter.notifyDataSetChanged();
				
			}

			@Override
			public void onPreExecuteCallBack() {
				progress.setVisibility(View.VISIBLE);
				
			}
        	
        };
        
        dequeueListListener = new AsyncTaskListener<Boolean>(){

			@Override
			public void onPostExecuteCallBack(Boolean success) {
				progress.setVisibility(View.GONE);
				
				if(!success){
					return;
				}
				
				adapter.removeFromQueues(dequeueNumber);
				adapter.notifyDataSetChanged();
				
			}

			@Override
			public void onPreExecuteCallBack() {
				progress.setVisibility(View.VISIBLE);
				
			}
        	
        };
        return rootView;
    }

	@Override
	public void dequeue(int queueNumber) {
		dequeueNumber = queueNumber;
		new DequeueAsyncTask(dequeueListListener).execute(queueNumber);
		
	}

	@Override
	public void onResume() {
		super.onResume();
		
		new GetQueueListAsyncTask(getQueueListListener).execute();
	}

}
