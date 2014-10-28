package com.example.synclists.synclists;

import android.app.Activity;
import android.widget.Toast;

import org.apache.http.HttpStatus;
import org.json.JSONObject;

/**
 * Created by Mitch on 10/27/14.
 */
public class SyncListsCreateListAsyncTask extends SyncListsRequestAsyncTask  {
    public SyncListsCreateListAsyncTask(Activity activity) {
        super(activity);
    }

    protected void onPostExecute(SyncListsResponse result) {
        if(result.getHttpResponse().getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            Toast.makeText(mActivity, "List created!",
                    Toast.LENGTH_SHORT).show();

            try {
                JSONObject jsonObject = new JSONObject(result.getBody());

                Toast.makeText(mActivity, jsonObject.toString(),
                        Toast.LENGTH_LONG).show();
                return;
            }
            catch(Exception e) {
                //if exception parsing json, then error loggin in
            }
        }
        else {
            Toast.makeText(mActivity, "There was an error creating your list",
                    Toast.LENGTH_SHORT).show();
        }
    }
}