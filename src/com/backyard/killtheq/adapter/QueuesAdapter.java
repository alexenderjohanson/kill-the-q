package com.backyard.killtheq.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.backyard.killtheq.DequeueListener;
import com.backyard.killtheq.QueueCell;

public class QueuesAdapter extends BaseAdapter{
	
	private List<Integer> queues;
	private Context context;
	private DequeueListener listener;
	
	public QueuesAdapter(Context context, DequeueListener listener){
		this.context = context;
		this.queues = new ArrayList<Integer>();
		this.listener = listener;
	}

	@Override
	public int getCount() {
		return queues.size();
	}

	@Override
	public Object getItem(int position) {
		return queues.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		int queueNumber = queues.get(position);
		View cell = new QueueCell(context, queueNumber, listener);
		return cell;
	}

	public void setList(List<Integer> queueNumbers){
		queues = queueNumbers;
	}
	
	public void removeFromQueues(int queueNumber){
		for(int i = 0; i < queues.size(); i++){
			if(queues.get(i) == queueNumber){
				queues.remove(i);
				break;
			}
		}
	}
}
