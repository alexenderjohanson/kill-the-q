package com.gsy.femstoria.restful;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gsy.femstoria.restful.Formatter.UpperCamelCaseNamingStrategy;

public class JsonHelper {

	public final static ObjectMapper	ObjMapper	= new ObjectMapper();

	public static <T> T fromJson(Class<T> classType, String jsonResponse) throws IOException {

		ObjMapper.setPropertyNamingStrategy(new UpperCamelCaseNamingStrategy());
		
		T jsonResult = ObjMapper.readValue(jsonResponse, classType);
		return jsonResult;
	}

	/**
	 * Deserialize a single response type
	 * 
	 * @param type
	 * @param jsonResponse
	 * @throws IOException
	 *             if we failed to deserialize the response
	 */
	public static <T> T fromJson(JavaType type, String jsonResponse) throws IOException {

		if (jsonResponse == null || jsonResponse.length() < 1) {
			return null;
		}

		ObjMapper.setPropertyNamingStrategy(new UpperCamelCaseNamingStrategy());
		
		T jsonResult = ObjMapper.readValue(jsonResponse, type);

		return jsonResult;

	}

	/**
	 * Deserialize the response that is of collection type.
	 * 
	 * @param classType
	 * @param jsonResponse
	 * @throws IOException
	 *             if we failed to deserialize the response
	 */
	public static <T> List<T> fromJsonCollection(Class<T> classType, String jsonResponse) throws IOException {

		if (jsonResponse == null || jsonResponse.length() < 1) {
			return null;
		}

		ObjMapper.setPropertyNamingStrategy(new UpperCamelCaseNamingStrategy());

		JavaType type = ObjMapper.getTypeFactory().constructParametricType(List.class, classType);
		List<T> jsonResult = ObjMapper.readValue(jsonResponse, type);

		return jsonResult;
	}

	public static <T> String toJson(T object) throws JsonProcessingException {
		ObjMapper.setPropertyNamingStrategy(new UpperCamelCaseNamingStrategy());
		String jsonData = ObjMapper.writeValueAsString(object);
		return jsonData;
	}
	
	public static <T> String toJsonWithNoNamingStrategy(T object) throws JsonProcessingException {
		ObjMapper.setPropertyNamingStrategy(null);
		String jsonData = ObjMapper.writeValueAsString(object);
		return jsonData;
	}

	public static JavaType constructParametricType(Class<?> parent, Class<?>... child) {

		return ObjMapper.getTypeFactory().constructParametricType(parent, child);
	}

	public static JavaType constructParametricType(Class<?> parent, JavaType... child) {

		return ObjMapper.getTypeFactory().constructParametricType(parent, child);
	}
}
