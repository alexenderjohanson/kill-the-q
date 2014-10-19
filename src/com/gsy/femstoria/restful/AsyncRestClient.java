package com.gsy.femstoria.restful;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import java.util.ArrayList;

public final class AsyncRestClient extends IntentService {

    private final String LOG_TAG = "AsyncRestClient";

    public AsyncRestClient() {
        super("executeAsyncRequest");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ArrayList<ParcelableNameValuePair> params = intent.getParcelableArrayListExtra("params");
        ArrayList<ParcelableNameValuePair> headers = intent.getParcelableArrayListExtra("headers");
        String url = intent.getStringExtra("url");
        ResultReceiver receiver = (ResultReceiver) intent.getParcelableExtra("receiver");
        RequestMethod requestMethod = RequestMethod.fromValue(intent.getIntExtra("method", RequestMethod.GET.getValue()));
        String entity = intent.getStringExtra("entity");
        RestClient client = new RestClient(url, headers, params, entity);

        try {
            client.setReceiver(receiver);
            client.execute(requestMethod);
        } catch (Exception e) {
//        	Logger.w(LOG_TAG, Log.getStackTraceString(e));
            receiver.send(requestMethod.getValue(), Bundle.EMPTY);
        }
    }
}
