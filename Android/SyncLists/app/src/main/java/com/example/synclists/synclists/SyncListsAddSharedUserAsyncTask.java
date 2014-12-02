package com.example.synclists.synclists;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpStatus;

/**
 * Created by ethan on 12/1/14.
 */
public class SyncListsAddSharedUserAsyncTask extends SyncListsRequestAsyncTask {
    private ProgressDialog mProgressDialog;

    public SyncListsAddSharedUserAsyncTask(SyncListsRequestAsyncTaskCallback callback, Context context) {
        super(callback);
        mProgressDialog = new ProgressDialog(context);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mProgressDialog.setMessage("Adding User...");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();
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
        mProgressDialog.dismiss();
    }
}
