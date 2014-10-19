package com.backyard.killtheq;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class QueueCell extends RelativeLayout{

	public QueueCell(Context context, final int queueNumber, final DequeueListener dequeueListener) {
		super(context);
		
		View queueCell = View.inflate(context, R.layout.queue_cell, null);
		TextView queueNumberText = (TextView)queueCell.findViewById(R.id.tvQueueNumber);
		ImageButton dequeue = (ImageButton)queueCell.findViewById(R.id.btnDequeue);
		
		queueNumberText.setText(Integer.toString(queueNumber));
		
		dequeue.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(dequeueListener != null)
					dequeueListener.dequeue(queueNumber);
			}
			
		});
		
		addView(queueCell);
	}

}
