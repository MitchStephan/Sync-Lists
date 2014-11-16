package com.example.synclists.synclists;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpStatus;
import org.json.JSONObject;

/**
 * Created by SirChickenHair on 10/26/14.
 */
public class SyncListsLoginAsyncTask extends SyncListsRequestAsyncTask {
    private ProgressDialog mProgressDialog;

    public SyncListsLoginAsyncTask(Activity activity) {
        super(activity);
        mProgressDialog = new ProgressDialog(activity);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();
    }

    protected void onPostExecute(SyncListsResponse result) {
        //handle valid login

        Log.d("SyncLists", "Result " + result.toString());
        Log.d("SyncLists", "Response " + result.getHttpResponse());
        Log.d("SyncLists", "Status Line " + result.getHttpResponse().getStatusLine());
        Log.d("SyncLists", "Status code " + result.getHttpResponse().getStatusLine().getStatusCode());

        if(result.getHttpResponse().getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            try {
                JSONObject jsonObject = new JSONObject(result.getBody());

                //Write out user_context
                SharedPreferences.Editor editor = SyncListsLogin.getPreferencesEditor();
                editor.putInt(SyncListsApi.USER_CONTEXT, jsonObject.getInt("pk"));
                editor.commit();

                Intent lists = new Intent(mActivity, ListsActivity.class);
                mActivity.startActivity(lists);
                return;
            }
            catch(Exception e) {
                //if exception parsing json, then error loggin in
            }
        }

        //only dismiss dialog on failure, because otherwise activity is exited and not saved in
        //history
        mProgressDialog.dismiss();
        Toast.makeText(mActivity, "Invalid login",
                Toast.LENGTH_SHORT).show();
    }
}
