package com.example.synclists.synclists;

import android.app.Activity;
import android.widget.Toast;

import org.apache.http.HttpStatus;
import org.json.JSONObject;

/**
 * Created by Mitch on 10/27/14.
 */
public class SyncListsCreateListAsyncTask extends SyncListsRequestAsyncTask  {
    public SyncListsCreateListAsyncTask(SyncListsRequestAsyncTaskCallback callback) {
        super(callback);
    }

    protected void onPostExecute(SyncListsResponse result) {
        if(result.getHttpResponse().getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            mSyncListsRequestAsyncTaskCallback.onTaskComplete(result);
        }
        else {
            mSyncListsRequestAsyncTaskCallback.onTaskComplete(null);
        }
    }
}