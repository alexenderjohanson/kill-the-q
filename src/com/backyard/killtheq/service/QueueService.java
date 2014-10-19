package com.backyard.killtheq.service;

import com.backyard.killtheq.provider.QueueProvider;

public class QueueService {

	public int getRemainingPerson(int queueNumber) throws Exception{
		QueueProvider provider = new QueueProvider();
		int currentQueue = provider.getQueue();
		
		if(currentQueue < 1){
			return -1;
		}
		
		return queueNumber - currentQueue;
	}
}
