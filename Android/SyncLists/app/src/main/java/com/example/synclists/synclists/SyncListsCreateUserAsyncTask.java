package com.example.synclists.synclists;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpStatus;

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

            //Write out user_context
            SharedPreferences.Editor editor = SyncListsMain.getPreferencesEditor();
            editor.putInt(SyncListsApi.USER_CONTEXT, 1);
            editor.commit();

            Intent lists = new Intent(mActivity, ListsActivity.class);
            mActivity.startActivity(lists);
        }
        else {
            Toast.makeText(mActivity, "There was an error signing up",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
