package com.gsy.femstoria.restful;

public class RestClientResponse {
	
	public int responseCode;
	public String response;
	
	public RestClientResponse(int responseCode, String response){
		this.responseCode = responseCode;
		this.response = response;
	}

}
