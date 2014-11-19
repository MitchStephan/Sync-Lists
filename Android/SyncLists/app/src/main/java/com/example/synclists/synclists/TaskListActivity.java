package com.example.synclists.synclists;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.Toast;

import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ethan on 10/27/14.
 */
public class TaskListActivity extends Activity {
    private List<SyncListTask> mTaskList;
    private TaskListAdapter mTaskAdapter;
    private int mListId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_lists_tasks);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mListId = extras.getInt("listId");
        }
        else {
            finish(); // we can't run Task activity without a list id!
        }

        // Set title to list name
        setTitle(getIntent().getStringExtra("listName"));

        mTaskList = new ArrayList<SyncListTask>();
        mTaskAdapter = new TaskListAdapter(this, R.layout.tasks_view, mTaskList, mListId);

        final Context context = this;
        SyncListsApi.getTasks(new SyncListsRequestAsyncTaskCallback() {
            @Override
            public void onTaskComplete(SyncListsResponse syncListsResponse) {
                if (syncListsResponse == null) {
                    Toast.makeText(context, "Error retrieving tasks",
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    try {
                        mTaskList = SyncListsApi.parseTasks(syncListsResponse.getBody());

                        mTaskAdapter = new TaskListAdapter(context, R.layout.tasks_view, mTaskList, mListId);

                        ListView lv = (ListView) findViewById(R.id.tasks_view_wrapper);
                        lv.setAdapter(mTaskAdapter);
                    } catch (Exception e) {
                        Toast.makeText(context, "Error retrieving tasks",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, mListId);

        ListView lv = (ListView) findViewById(R.id.tasks_view_wrapper);
        lv.setAdapter(mTaskAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.tasks_menu, menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.settings:
                Intent settings = new Intent(this, SettingsActivity.class);
                startActivity(settings);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onClickDeleteEditTask(View v) {
        final SyncListTask task = (SyncListTask) v.getTag();

        int position = mTaskAdapter.getPosition(task);
        mTaskList.remove(position);
        mTaskAdapter.notifyDataSetChanged();
        hideKeyboard();
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        if(imm != null){
            imm.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }


    private void showKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        if(imm != null){
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
        }
    }

    public void taskSettings(View v) {
        SyncListTask task = (SyncListTask) v.getTag();
        Toast.makeText(this, "You clicked " + task.getName() + " with id " + task.getId(),
                Toast.LENGTH_SHORT).show();
    }

    public void addTask(MenuItem item) {
        if (!isElementEdit(mTaskList.size()-1))
        {
            final SyncListTask task = new SyncListTask(-1, "", true);
            mTaskList.add(task);
            mTaskAdapter.notifyDataSetChanged();
            if (item != null) {
                showKeyboard();
            }
        }
    }

    private boolean isElementEdit(int position) {
        return position > -1 && mTaskList.get(position).getIsTaskEdit();
    }
}
