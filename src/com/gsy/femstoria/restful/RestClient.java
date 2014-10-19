package com.gsy.femstoria.restful;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.net.ssl.SSLException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

final class RestClient {

	private static final String			LOG_TAG	= "RestClient";

	int									responseCode;
	RequestMethod						requestMethod;
	String								message, response, entity;
	ArrayList<ParcelableNameValuePair>	params;
	ArrayList<ParcelableNameValuePair>	headers;
	HttpRequestBase						request;
	ResultReceiver						receiver;

	private String						url;

	public RestClient(String url, ArrayList<ParcelableNameValuePair> headers, ArrayList<ParcelableNameValuePair> params, String entity) {
		this.url = url;
		this.headers = headers;
		this.params = params;
		this.entity = entity;
		this.receiver = null;
	}

	public String getResponse() {
		return this.response;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public void setReceiver(ResultReceiver receiver) {
		this.receiver = receiver;
	}

	public void executeDelete(String deletePath) throws Exception {
		request = new HttpDelete(url + deletePath);

		// add headers
		for (NameValuePair h : headers) {
			request.addHeader(h.getName(), h.getValue());
		}
		commit();
	}

	public void execute(RequestMethod requestMethod) throws Exception {
		this.requestMethod = requestMethod;

		switch (requestMethod) {
			case GET: {
				String combinedParams = "";
				if (!params.isEmpty()) {
					combinedParams += "?";
					for (NameValuePair p : params) {
						String paramString = p.getName() + "=" + URLEncoder.encode(p.getValue(), "UTF-8");
						if (combinedParams.length() > 1) {
							combinedParams += "&" + paramString;
						}else {
							combinedParams += paramString;
						}
					}
				}

				request = new HttpGet(url + combinedParams);

				// add headers
				for (NameValuePair h : headers) {
					request.addHeader(h.getName(), h.getValue());
				}

				commit();
				break;
			}
			case POST: {
				request = new HttpPost(url);

				// add headers
				for (NameValuePair h : headers) {
					request.addHeader(h.getName(), h.getValue());
				}

				if (!params.isEmpty()) {
					((HttpPost) request).setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
				}
				if (entity != null && entity != "") {
					StringEntity se = new StringEntity(entity);
					((HttpPost) request).setEntity(se);
				}
				commit();
				break;
			}
			case PUT: {
				request = new HttpPut(url);

				// add headers
				for (NameValuePair h : headers) {
					request.addHeader(h.getName(), h.getValue());
				}

				if (!params.isEmpty()) {
					((HttpPut) request).setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
				}
				
				if (entity != null && entity != "") {
					StringEntity se = new StringEntity(entity);
					((HttpPost) request).setEntity(se);
				}
				commit();
				break;
			}
			case DELETE: {
				request = new HttpDelete(url);

				// add headers
				for (NameValuePair h : headers) {
					request.addHeader(h.getName(), h.getValue());
				}
				commit();
			}
		}
	}

	private void commit() throws SSLException {
		HttpClient client = CustomHttpClient.get();
		HttpResponse httpResponse;

		try {
			Log.i(LOG_TAG, request.getURI().toURL().toString());

			httpResponse = client.execute(request);
			responseCode = httpResponse.getStatusLine().getStatusCode();
			message = httpResponse.getStatusLine().getReasonPhrase();

			HttpEntity entity = httpResponse.getEntity();

			// If the response does not enclose an entity, there is no need
			// to bother about connection release
			if (entity != null) {
				InputStream instream = entity.getContent();
				try {
					response = convertStreamToString(instream);

					if (receiver != null) {
						Bundle responseBundle = new Bundle();
						responseBundle.putString("result", response);
						receiver.send(requestMethod.getValue(), responseBundle);
					}
				}catch (IOException ex) {
					// In case of an IOException the connection will be released
					// back to the connection manager automatically
					throw ex;
				}catch (RuntimeException ex) {
					// In case of an unexpected exception you may want to abort
					// the HTTP request in order to shut down the underlying
					// connection immediately.
					request.abort();
					throw ex;
				}finally {
					// Closing the input stream will trigger connection release
					try {
						instream.close();
					}catch (Exception ignore) {
					}
				}
			}

		}catch (ClientProtocolException e) {
			Log.e(LOG_TAG, Log.getStackTraceString(e));
			if (receiver != null) {
				receiver.send(requestMethod.getValue(), Bundle.EMPTY);
			}
		}catch (SSLException e) {
			// we want to show user this error
			throw e;
		}catch (IOException e) {

			Log.e(LOG_TAG, Log.getStackTraceString(e));
			if (receiver != null) {
				receiver.send(requestMethod.getValue(), Bundle.EMPTY);
			}
		}
	}

	private String convertStreamToString(InputStream is) throws IOException {

		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		}catch (IOException ex) {
			Log.e(LOG_TAG, Log.getStackTraceString(ex));
		}finally {
			try {
				is.close();
			}catch (IOException ex) {
				Log.e(LOG_TAG, Log.getStackTraceString(ex));
			}
		}

		return sb.toString();
	}
}