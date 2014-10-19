package com.backyard.killtheq;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class DataStore {
	
	private final static String SHARED_PREFERENCE_KEY = "watershortage";
	private Editor editor;
	private SharedPreferences pref;
	
	private static final String KEY_EMAIL = "email"; 
	private static final String KEY_PASSWORD = "password";
	private static final String KEY_USERID = "userId";
	
	public DataStore(Context context){
		pref = context.getSharedPreferences(SHARED_PREFERENCE_KEY, context.MODE_PRIVATE);
		editor = pref.edit(); 
	}
	
	public String getUserName(){
		return pref.getString(KEY_EMAIL, "");
	}
	
	public void setUserName(String userName){
		editor.putString(KEY_EMAIL, userName);
		editor.commit();
	}
	
	public String getPassword(){
		return pref.getString(KEY_PASSWORD, "");
	}
	
	public void setPassword(String password){
		editor.putString(KEY_PASSWORD, password);
		editor.commit();
	}
	
	public String getUserId(){
		return pref.getString(KEY_USERID, "");
	}
	
	public void setUserId(String userId){
		editor.putString(KEY_USERID, userId);
		editor.commit();
	}
}
