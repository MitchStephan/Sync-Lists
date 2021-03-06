package com.example.synclists.synclists;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.example.synclists.synclists.SyncListsRequestAsyncTask;
import com.example.synclists.synclists.SyncListsResponse;

import org.apache.http.HttpStatus;
import org.json.JSONObject;

/**
 * Created by ethan on 11/20/14.
 */
public class SyncListsChangePasswordAsyncTask extends SyncListsRequestAsyncTask {
    private ProgressDialog mProgressDialog;

    public SyncListsChangePasswordAsyncTask(Activity activity) {
        super(activity);
        mProgressDialog = new ProgressDialog(activity);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mProgressDialog.setMessage("Changing Password...");
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

            Toast.makeText(mActivity, "Your password has changed", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(mActivity, "Something went wrong, please try again later", Toast.LENGTH_SHORT).show();
        }
        mProgressDialog.dismiss();
    }
}
