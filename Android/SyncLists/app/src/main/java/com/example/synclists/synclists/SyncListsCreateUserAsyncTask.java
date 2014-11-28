package com.example.synclists.synclists;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import org.apache.http.HttpStatus;
import org.json.JSONObject;

/**
 * Created by SirChickenHair on 10/26/14.
 */
public class SyncListsCreateUserAsyncTask extends SyncListsRequestAsyncTask  {
    public SyncListsCreateUserAsyncTask(Activity activity) {
        super(activity);
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
                // Save email
                editor.putString("Email", jsonObject.getJSONObject("fields").getString("email"));
                // Save preferences
                editor.putBoolean("sharingEnabled", jsonObject.getJSONObject("fields").getBoolean("sharing_enabled"));
                editor.apply();

                Intent lists = new Intent(mActivity, ListsActivity.class);
                mActivity.startActivity(lists);
                return;
            }
            catch(Exception e) {
                // if exception parsing json, then error logging in
            }
        }
        else {
            Toast.makeText(mActivity, "There was an error signing up",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
