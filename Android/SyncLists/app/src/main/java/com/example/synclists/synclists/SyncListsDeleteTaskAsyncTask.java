package com.example.synclists.synclists;

import org.apache.http.HttpStatus;

/**
 * Created by SirChickenHair on 11/29/14.
 */
public class SyncListsDeleteTaskAsyncTask extends SyncListsRequestAsyncTask {
    public SyncListsDeleteTaskAsyncTask(SyncListsRequestAsyncTaskCallback callback) {
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
