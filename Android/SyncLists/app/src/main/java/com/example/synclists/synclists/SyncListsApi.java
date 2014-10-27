package com.example.synclists.synclists;

import android.app.Activity;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;
import android.preference.PreferenceManager;
import android.content.SharedPreferences;

/**
 * Created by SirChickenHair on 10/26/14.
 */
public class SyncListsApi {

    protected final static String USER_CONTEXT = "USER_CONTEXT";

    protected static void login(Activity activity, String email, String password) {
        Map<String, Object> json = new HashMap<String, Object>();
        json.put("email", email);
        json.put("password", password);

        SyncListsRequest request = new SyncListsRequest(
                        SyncListsRequest.SyncListsRequestMethod.POST, "user/login", json);

        new SyncListsLoginAsyncTask(activity).execute(request);
    }

    protected static void createUser(Activity activity, String email, String password) {
        Map<String, Object> json = new HashMap<String, Object>();
        json.put("email", email);
        json.put("password", password);

        SyncListsRequest request = new SyncListsRequest(
                SyncListsRequest.SyncListsRequestMethod.POST, "user", json);

        new SyncListsCreateUserAsyncTask(activity).execute(request);
    }
}
