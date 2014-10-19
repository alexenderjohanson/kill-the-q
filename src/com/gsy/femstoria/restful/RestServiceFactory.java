package com.gsy.femstoria.restful;

import java.util.ArrayList;

public class RestServiceFactory {

	private static final String	URL_FORMAT				= "http://%s/%s";
	private static final String	LOCAL_WEBSERVICE_DOMAIN	= "killlaqueue.apphb.com";

	public static ArrayList<ParcelableNameValuePair> getDefaultHeaders(boolean includeGatewayHeaders) {
		ArrayList<ParcelableNameValuePair> headers = new ArrayList<ParcelableNameValuePair>();
		headers.add(new ParcelableNameValuePair("Content-Type", "application/json"));

		// MobileServices headers
//		headers.add(new ParcelableNameValuePair("x-domain", WebServiceConfig.getChannelURI()));
//		headers.add(new ParcelableNameValuePair("x-brand", WebServiceConfig.getApplication().getBrand().toString()));
//		headers.add(new ParcelableNameValuePair("x-device", String.valueOf(MobileDeviceTypeId.Android.getValue())));

		// Gateway headers
//		if (includeGatewayHeaders) {
//			headers.add(new ParcelableNameValuePair("x-auth-claim-token", WebServiceConfig.getClaimToken()));
//			headers.add(new ParcelableNameValuePair("x-auth-issuer", WebServiceConfig.getTokenIssuer()));

//			if (WebServiceConfig.getIssueInstant() != null) {
//				TimeZone utc = TimeZone.getTimeZone("UTC");
//				SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sszzz");
//				dateFormatter.setTimeZone(utc);
//				GregorianCalendar cal = new GregorianCalendar(utc);
//				try {
//					cal.setTime(dateFormatter.parse(WebServiceConfig.getIssueInstant()));
//					String issueInstantDate = String.valueOf(cal.getTime());
//					headers.add(new ParcelableNameValuePair("x-auth-issue-instant", issueInstantDate));
//				}catch (ParseException e) {
//					// ignore
//				}
//			}

			// TODO: this is used for local testing only, please remove
//			headers.add(new ParcelableNameValuePair("x-userid", Integer.toString(156692863)));
//		}

		return headers;
	}

	public static String createPath(String endpoint) {
		return String.format(URL_FORMAT, getWebserviceDomain(), endpoint);
	}

	public static String createPath(String endpoint, Object... param) {
		String servicePath = String.format(endpoint, param);
		return String.format(URL_FORMAT, getWebserviceDomain(), servicePath);
	}

	private static String getWebserviceDomain() {
//		return WebServiceConfig.getTargetURI();
		return LOCAL_WEBSERVICE_DOMAIN;
	}
}
