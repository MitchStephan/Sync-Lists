package com.example.synclists.synclists;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by SirChickenHair on 10/27/14.
 */
public class SyncListsGetListsAsyncTask extends SyncListsRequestAsyncTask {

    public SyncListsGetListsAsyncTask(Activity activity) {
        super(activity);
    }

    protected void onPostExecute(SyncListsResponse result) {
        //handle valid login

        Log.d("SyncLists", "Result " + result.toString());
        Log.d("SyncLists", "Response " + result.getHttpResponse());
        Log.d("SyncLists", "Status Line " + result.getHttpResponse().getStatusLine());
        Log.d("SyncLists", "Status code " + result.getHttpResponse().getStatusLine().getStatusCode());

        if(true || result.getHttpResponse().getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            Toast.makeText(mActivity, "You did it",
                    Toast.LENGTH_SHORT).show();

            ArrayList<SyncListsList> lists = new ArrayList<SyncListsList>();
            try {
                JSONArray jsonArray = new JSONArray(result.getBody());

                for(int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    JSONObject fields = jsonObject.getJSONObject("fields");

                    //should handle Integer.parseInt in case error
                    SyncListsList list = new SyncListsList(Integer.parseInt(jsonObject.get("pk").toString()), fields.get("name").toString());
                    lists.add(list);
                }

                ((ListsActivity) mActivity).populateLists(lists);
                return;
            }
            catch(Exception e) {
                //if exception parsing json, then error loggin in
            }
        }

        Toast.makeText(mActivity, "Error retrieving lists",
                Toast.LENGTH_SHORT).show();
    }
}
