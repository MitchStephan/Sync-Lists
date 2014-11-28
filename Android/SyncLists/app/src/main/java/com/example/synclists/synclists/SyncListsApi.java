package com.example.synclists.synclists;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by SirChickenHair on 10/26/14.
 */
public class SyncListsApi {

    protected static void login(Activity activity, String email, String password, Context context) {
        Map<String, Object> json = new HashMap<String, Object>();
        json.put("email", email);
        json.put("password", password);

        if (isNetworkAvailable(context)) {
            SyncListsRequest request = new SyncListsRequest(
                    SyncListsRequest.SyncListsRequestMethod.POST, "user/login", json);

            new SyncListsLoginAsyncTask(activity).execute(request);
        }
    }

    protected static void changePassword(Activity activity, String newPassword, String confirmNewPassword, Context context, String email) {
        Map<String, Object> json = new HashMap<String, Object>();
        json.put("password", newPassword);
        json.put("email", email);

        if (isNetworkAvailable(context) && newPassword.equals(confirmNewPassword)) {
            Log.d(Constants.TAG, "Email: " + email);
            SyncListsRequest request = new SyncListsRequest(
                    SyncListsRequest.SyncListsRequestMethod.PUT, "user", json);

            new SyncListsChangePasswordAsyncTask(activity).execute(request);
        }
    }

    protected static void createUser(Activity activity, String email, String password, Context context) {
        Map<String, Object> json = new HashMap<String, Object>();
        json.put("email", email);
        json.put("password", password);

        if (isNetworkAvailable(context)) {
            SyncListsRequest request = new SyncListsRequest(
                    SyncListsRequest.SyncListsRequestMethod.POST, "user", json);

            new SyncListsCreateUserAsyncTask(activity).execute(request);
        }
    }

    protected static void createList(SyncListsRequestAsyncTaskCallback callback, String name, Context context) {
        Map<String, Object> json = new HashMap<String, Object>();
        json.put("name", name);

        if (isNetworkAvailable(context)) {
            SyncListsRequest request = new SyncListsRequest(
                    SyncListsRequest.SyncListsRequestMethod.POST, "lists", json);

            new SyncListsCreateListAsyncTask(callback).execute(request);
        }
    }

    protected static void updateList(SyncListsRequestAsyncTaskCallback callback, SyncListsList list, Context context) {
        Map<String, Object> json = new HashMap<String, Object>();
        json.put("name", list.getName());

        if (isNetworkAvailable(context)) {
            SyncListsRequest request = new SyncListsRequest(
                    SyncListsRequest.SyncListsRequestMethod.PUT, "lists/" + list.getId(), json);

            new SyncListsUpdateListAsyncTask(callback).execute(request);
        }
    }

    protected static void createTask(SyncListsRequestAsyncTaskCallback callback, int listId, String name, Context context) {
        Map<String, Object> json = new HashMap<String, Object>();
        json.put("name", name);

        if (isNetworkAvailable(context)) {
            Log.d(Constants.TAG, "Making createTask request");
            SyncListsRequest request = new SyncListsRequest(
                    SyncListsRequest.SyncListsRequestMethod.POST, "lists/" + listId + "/tasks", json);

            new SyncListsCreateTaskAsyncTask(callback).execute(request);
        }
    }

    protected static void updateTask(SyncListsRequestAsyncTaskCallback callback, int listId, SyncListsTask task, Context context) {
        Map<String, Object> json = new HashMap<String, Object>();
        json.put("name", task.getName());
        json.put("completed", task.getCompleted() ? 1 : 0);
        json.put("visible", 0);

        if (isNetworkAvailable(context)) {
            Log.d(Constants.TAG, "Making updateTask request");
            SyncListsRequest request = new SyncListsRequest(
                    SyncListsRequest.SyncListsRequestMethod.PUT, "lists/" + listId + "/tasks/" + task.getId(), json);

            new SyncListsUpdateTaskAsyncTask(callback).execute(request);
        }
    }

    protected static void getLists(SyncListsRequestAsyncTaskCallback callback, Context context) {
        if (isNetworkAvailable(context)) {
            SyncListsRequest request = new SyncListsRequest(
                    SyncListsRequest.SyncListsRequestMethod.GET, "user/lists");

            new SyncListsGetListsAsyncTask(callback).execute(request);
        }
    }

    protected static void getTasks(SyncListsRequestAsyncTaskCallback callback, int listId, Context context) {
        if (isNetworkAvailable(context)) {
            SyncListsRequest request = new SyncListsRequest(
                    SyncListsRequest.SyncListsRequestMethod.GET, "lists/" + listId + "/tasks");

            new SyncListsGetTasksAsyncTask(callback).execute(request);
        }
    }

    protected static void deleteList(SyncListsRequestAsyncTaskCallback callback, int listId, Context context) {
        if (isNetworkAvailable(context)) {
            SyncListsRequest request = new SyncListsRequest(
                    SyncListsRequest.SyncListsRequestMethod.DELETE, "lists/" + listId);

            new SyncListsDeleteListAsyncTask(callback).execute(request);
        }
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

    protected static ArrayList<SyncListsTask> parseTasks(String json) throws Exception {
        ArrayList<SyncListsTask> tasks = new ArrayList<SyncListsTask>();
        JSONArray jsonArray = new JSONArray(json);

        for(int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            JSONObject fields = jsonObject.getJSONObject("fields");

            //should handle Integer.parseInt in case error
            SyncListsTask task = new SyncListsTask(jsonObject.getInt("pk"), fields.getString("name"), false, fields.getBoolean("completed"));
            tasks.add(task);
        }

        return tasks;
    }

    protected static void logout() {
        SharedPreferences.Editor editor = SyncListsLogin.getPreferencesEditor();
        editor.remove(Constants.PREF_USER_CONTEXT);
        editor.apply();
    }

    private static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo == null) {
            Toast.makeText(context,
                    "No Network Connection!", Toast.LENGTH_SHORT).show();
        }
        return activeNetworkInfo != null;
    }
}
