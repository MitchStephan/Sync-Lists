package com.example.synclists.synclists;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import org.apache.http.HttpStatus;
import org.json.JSONObject;

/**
 * Created by SirChickenHair on 10/26/14.
 */
public class SyncListsCreateUserAsyncTask extends SyncListsRequestAsyncTask  {
    private ProgressDialog mProgressDialog;

    public SyncListsCreateUserAsyncTask(Activity activity, Context context) {
        super(activity);
        mProgressDialog = new ProgressDialog(context);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mProgressDialog.setMessage("Creating account...");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();
    }

    protected void onPostExecute(SyncListsResponse result) {
        if(result.getHttpResponse().getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            Toast.makeText(mActivity, "Account created!",
                    Toast.LENGTH_SHORT).show();

            try {
                JSONObject jsonObject = new JSONObject(result.getBody());

                // Write out user-context
                SharedPreferences.Editor editor = SyncListsLogin.getPreferencesEditor();
                editor.putInt(Constants.USER_CONTEXT_HEADER, jsonObject.getInt("pk"));
                editor.apply();
                // Save email
                editor.putString(Constants.PREF_EMAIL, jsonObject.getJSONObject("fields").getString("email"));
                editor.apply();
                // Save preferences
                editor.putBoolean(Constants.PREF_SHARING, jsonObject.getJSONObject("fields").getBoolean("sharing_enabled"));
                editor.apply();

                Intent lists = new Intent(mActivity, ListsActivity.class);
                mActivity.startActivity(lists);
                return;
            }
            catch(Exception e) {
                // if exception parsing json, then error logging in
            }
            finally {
                mProgressDialog.dismiss();
            }
        }
        else {
            Toast.makeText(mActivity, "There was an error signing up",
                    Toast.LENGTH_SHORT).show();
            mProgressDialog.dismiss();
        }
    }
}
