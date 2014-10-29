package com.example.synclists.synclists;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by SirChickenHair on 10/26/14.
 */
public class SyncListsApi {

    protected final static String USER_CONTEXT = "USER-CONTEXT";

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

    protected static void createList(SyncListsRequestAsyncTaskCallback callback, String name) {
        Map<String, Object> json = new HashMap<String, Object>();
        json.put("name", name);

        SyncListsRequest request = new SyncListsRequest(
                SyncListsRequest.SyncListsRequestMethod.POST, "lists", json);

        new SyncListsCreateListAsyncTask(callback).execute(request);
    }

    protected static void createTask(SyncListsRequestAsyncTaskCallback callback, int listId, String name) {
        Map<String, Object> json = new HashMap<String, Object>();
        json.put("name", name);

        Log.d("SL", "Making createTask request");
        SyncListsRequest request = new SyncListsRequest(
                SyncListsRequest.SyncListsRequestMethod.POST, "lists/" + listId + "/tasks", json);

        new SyncListsCreateTaskAsyncTask(callback).execute(request);
    }

    protected static void getLists(SyncListsRequestAsyncTaskCallback callback) {
        SyncListsRequest request = new SyncListsRequest(
                SyncListsRequest.SyncListsRequestMethod.GET, "user/lists");

        new SyncListsGetListsAsyncTask(callback).execute(request);
    }

    protected static void deleteList(SyncListsRequestAsyncTaskCallback callback, int listId) {
        SyncListsRequest request = new SyncListsRequest(
                SyncListsRequest.SyncListsRequestMethod.DELETE, "lists/" + listId);

        new SyncListsDeleteListAsyncTask(callback).execute(request);
    }

    protected static ArrayList<SyncListsList> parseLists(String json) throws Exception {
        ArrayList<SyncListsList> lists = new ArrayList<SyncListsList>();
        JSONArray jsonArray = new JSONArray(json);

        for(int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            JSONObject fields = jsonObject.getJSONObject("fields");

            //should handle Integer.parseInt in case error
            SyncListsList list = new SyncListsList(Integer.parseInt(jsonObject.get("pk").toString()), fields.get("name").toString());
            lists.add(list);
        }

        return lists;
    }

    protected static void logout() {
        SharedPreferences.Editor editor = SyncListsLogin.getPreferencesEditor();
        editor.remove(USER_CONTEXT);
        editor.commit();
    }
}
