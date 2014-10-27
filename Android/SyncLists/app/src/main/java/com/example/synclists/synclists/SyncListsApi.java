package com.example.synclists.synclists;

import android.app.Activity;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by SirChickenHair on 10/26/14.
 */
public class SyncListsApi {

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
