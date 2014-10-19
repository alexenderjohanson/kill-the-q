package com.backyard.killtheq.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Enqueue implements Serializable {

	private static final long serialVersionUID = -3162653634570740095L;
	@JsonProperty("currentQueueNum")
	public int currentQueueNumber;
	@JsonProperty("queueNum")
	public int queueNumber;
	public String status;
	
	public Enqueue(){
		
	}
}
