package com.gsy.femstoria.restful.Formatter;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class CustomJsonDateDeserializer extends JsonDeserializer<Date> {
	/*
	 * Parses a Microsoft .NET style JSON timestamp and returns a Java Date
	 * 
	 * (non-Javadoc)
	 * @see com.fasterxml.jackson.databind.JsonDeserializer#deserialize(com.fasterxml.jackson.core.JsonParser, com.fasterxml.jackson.databind.DeserializationContext)
	 */
	
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
	
    @Override
    public Date deserialize(JsonParser jsonparser, DeserializationContext deserializationcontext) throws IOException {

        try {
            String rawDate = jsonparser.getText();
            
            int dotIndex = rawDate.indexOf(".");
            
            StringBuilder builder = new StringBuilder();
            
            if(dotIndex != -1){
            	builder.append(rawDate.substring(0, rawDate.indexOf(".")));
            	builder.append(rawDate.substring(rawDate.indexOf("+")));
            } else {
            	builder.append(rawDate);
            }
            
            return dateFormat.parse(builder.toString());         
            
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
