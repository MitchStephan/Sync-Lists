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
public class SyncListsLoginAsyncTask extends SyncListsRequestAsyncTask {

    public SyncListsLoginAsyncTask(Activity activity) {
        super(activity);
    }

    protected void onPostExecute(SyncListsResponse result) {
        //handle valid login

        Log.d("SyncLists", "Result " + result.toString());
        Log.d("SyncLists", "Response " + result.getHttpResponse());
        Log.d("SyncLists", "Status Line " + result.getHttpResponse().getStatusLine());
        Log.d("SyncLists", "Status code " + result.getHttpResponse().getStatusLine().getStatusCode());

        if(result.getHttpResponse().getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            Toast.makeText(mActivity, "You did it",
                    Toast.LENGTH_SHORT).show();

            //Write out user_context
            SharedPreferences.Editor editor = SyncListsMain.getPreferencesEditor();
            editor.putInt(SyncListsApi.USER_CONTEXT, 1);
            editor.commit();

            Intent lists = new Intent(mActivity, ListsActivity.class);
            mActivity.startActivity(lists);
            return;
        }



        Toast.makeText(mActivity, "Invalid login",
                Toast.LENGTH_SHORT).show();
    }
}
