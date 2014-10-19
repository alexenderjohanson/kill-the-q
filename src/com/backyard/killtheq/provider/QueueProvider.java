package com.backyard.killtheq.provider;

import java.util.List;

import android.util.Log;

import com.backyard.killtheq.model.Enqueue;
import com.gsy.femstoria.restful.JsonHelper;
import com.gsy.femstoria.restful.RequestMethod;
import com.gsy.femstoria.restful.RestClientResponse;
import com.gsy.femstoria.restful.RestService;
import com.gsy.femstoria.restful.RestServiceFactory;
import com.gsy.femstoria.restful.ServiceRoute;

public class QueueProvider {
	
private String LOG_TAG = "post_provider";
	
	public Enqueue Enqueue() throws Exception{
		
		try {
			RestService service = new RestService(RestServiceFactory.createPath(ServiceRoute.ENQUEUE), RequestMethod.GET);
			RestClientResponse response = service.executeWithResponseCode();

			if(response.responseCode != 200){
				return null;
			}
			
			Enqueue result = JsonHelper.fromJson(Enqueue.class, response.response);

			return result;

		}
		catch (Exception ex) {
			Log.e(LOG_TAG, Log.getStackTraceString(ex));
			throw ex;
		}
	}
	
	public boolean Dequeue(int queueNumber) throws Exception{
		try{
			RestService service = new RestService(RestServiceFactory.createPath(ServiceRoute.DEQUEUE), RequestMethod.POST);
			service.setEntity(Integer.toString(queueNumber));
			RestClientResponse response = service.executeWithResponseCode();
			
			if(response.responseCode != 200){
				return false;
			}
			
			if(response.response.toLowerCase().contains("success")){
				return true;
			}
			
			return false;
			
		} catch(Exception ex){
			Log.e(LOG_TAG, Log.getStackTraceString(ex));
			throw ex;
		}
	}
	
	public int getQueue() throws Exception{
		
		try {
			RestService service = new RestService(RestServiceFactory.createPath(ServiceRoute.GET_QUEUE), RequestMethod.GET);
			RestClientResponse response = service.executeWithResponseCode();
			

			if(response.responseCode != 200){
				return 0;
			}
			
			return Integer.parseInt(response.response.replace("\n", ""));
		}
		catch (Exception ex) {
			Log.e(LOG_TAG, Log.getStackTraceString(ex));
			throw ex;
		}
	}
	
	public List<Integer> getQueueList() throws Exception{
		
		try {
			RestService service = new RestService(RestServiceFactory.createPath(ServiceRoute.LIST_QUEUE), RequestMethod.GET);
			RestClientResponse response = service.executeWithResponseCode();

			if(response.responseCode != 200){
				return null;
			}
			
			List<Integer> result = JsonHelper.fromJsonCollection(Integer.class, response.response);

			return result;

		}
		catch (Exception ex) {
			Log.e(LOG_TAG, Log.getStackTraceString(ex));
			throw ex;
		}
	}
}
