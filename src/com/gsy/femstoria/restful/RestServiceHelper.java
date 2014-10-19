package com.gsy.femstoria.restful;

import java.util.ArrayList;

public class RestServiceHelper {
    // gateway
    private static final String AUTHENTICATE_FACEBOOK_ENDPOINT = "/authentication/facebook";
    private static final String ACCOUNT_MERGE_ENDPOINT = "/seeker/mobile/me/defaultprofile/%s";

    // local
//    private static final String AUTHENTICATE_FACEBOOK_ENDPOINT = "/login/facebook";
//    private static final String ACCOUNT_MERGE_ENDPOINT = "/account/defaultprofile/%s";

    public static ArrayList<ParcelableNameValuePair> getDefaultHeaders()
    {
        ArrayList<ParcelableNameValuePair> headers = new ArrayList<ParcelableNameValuePair>();
        headers.add(new ParcelableNameValuePair("Content-Type", "application/json"));

        return headers;
    }

}
