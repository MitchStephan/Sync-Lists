package com.example.synclists.synclists;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * Created by SirChickenHair on 10/26/14.
 */
public abstract class SyncListsRequestAsyncTask extends AsyncTask<SyncListsRequest, Integer, SyncListsResponse> {

    protected Activity mActivity;
    protected SyncListsRequestAsyncTaskCallback mSyncListsRequestAsyncTaskCallback;

    public SyncListsRequestAsyncTask() {
    }

    public SyncListsRequestAsyncTask(Activity activity) {
        mActivity = activity;
    }

    public SyncListsRequestAsyncTask(SyncListsRequestAsyncTaskCallback syncListsRequestAsyncTaskCallback) {
        mSyncListsRequestAsyncTaskCallback = syncListsRequestAsyncTaskCallback;
    }

    protected SyncListsResponse doInBackground(SyncListsRequest... syncListsRequests) {

        if(syncListsRequests.length != 1)
            return null;

        SyncListsRequest syncListsRequest = syncListsRequests[0];
        HttpRequest httpRequest;

        try {
            httpRequest = syncListsRequest.getHttpRequest();
        }
        catch(Exception e) {
            return null;
        }

        HttpClient client = new DefaultHttpClient();

        HttpResponse response;

        try {
            switch (syncListsRequest.getMethod()) {
                case GET:
                    response = client.execute((HttpGet) httpRequest);
                    break;
                case POST:
                    response = client.execute((HttpPost) httpRequest);
                    break;
                case PUT:
                    response = client.execute((HttpPut) httpRequest);
                    break;
                case DELETE:
                    response = client.execute((HttpDelete) httpRequest);
                    break;
                default:
                    throw new SyncListsRequestException("Method for SyncListRequest must be GET, POST, PUT, or DELETE");
            }
        }
        catch(Exception e) {
            return null;
        }

        return new SyncListsResponse(response);
    }

    protected abstract void onPostExecute(SyncListsResponse result);
}
