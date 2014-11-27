package com.example.synclists.synclists;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpStatus;
import org.json.JSONObject;

/**
 * Created by SirChickenHair on 11/18/14.
 */
public class SyncListsUpdateTaskAsyncTask extends SyncListsRequestAsyncTask  {
    public SyncListsUpdateTaskAsyncTask(SyncListsRequestAsyncTaskCallback callback) {
        super(callback);
    }

    protected void onPostExecute(SyncListsResponse result) {
        Log.d(Constants.TAG, "Result " + result.toString());
        Log.d(Constants.TAG, "Response " + result.getHttpResponse());
        Log.d(Constants.TAG, "Status Line " + result.getHttpResponse().getStatusLine());
        Log.d(Constants.TAG, "Status code " + result.getHttpResponse().getStatusLine().getStatusCode());

        if(result.getHttpResponse().getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            mSyncListsRequestAsyncTaskCallback.onTaskComplete(result);
        }
        else {
            mSyncListsRequestAsyncTaskCallback.onTaskComplete(null);
        }
    }
}