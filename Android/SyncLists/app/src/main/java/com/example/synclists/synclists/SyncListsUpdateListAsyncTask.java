package com.example.synclists.synclists;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpStatus;
import org.json.JSONObject;

/**
 * Created by SirChickenHair on 11/18/14.
 */
public class SyncListsUpdateListAsyncTask extends SyncListsRequestAsyncTask  {
    public SyncListsUpdateListAsyncTask(SyncListsRequestAsyncTaskCallback callback) {
        super(callback);
    }

    protected void onPostExecute(SyncListsResponse result) {
        Log.d("SyncLists", "Result " + result.toString());
        Log.d("SyncLists", "Response " + result.getHttpResponse());
        Log.d("SyncLists", "Status Line " + result.getHttpResponse().getStatusLine());
        Log.d("SyncLists", "Status code " + result.getHttpResponse().getStatusLine().getStatusCode());

        if(result.getHttpResponse().getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            mSyncListsRequestAsyncTaskCallback.onTaskComplete(result);
        }
        else {
            mSyncListsRequestAsyncTaskCallback.onTaskComplete(null);
        }
    }
}