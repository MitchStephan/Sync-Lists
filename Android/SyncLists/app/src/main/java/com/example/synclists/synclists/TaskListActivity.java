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

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ethan on 10/27/14.
 */
public class TaskListActivity extends Activity {
    private List<Task> mTaskList;
    private TaskListAdapter mTaskAdapter;
    private int mListId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_lists_tasks);

        mTaskList = new ArrayList<Task>();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mListId = extras.getInt("listId");
        }
        else {
            finish(); // we can't run Task activity without a list id!
        }

        populateExampleTasks();

        mTaskAdapter = new TaskListAdapter(this, R.layout.tasks_view, mTaskList, mListId);

        ListView lv = (ListView) findViewById(R.id.tasks_view_wrapper);
        lv.setAdapter(mTaskAdapter);
    }

    public void populateExampleTasks() {

//        mTaskList.add(new Task("Task 1", 1, true));
//        mTaskList.add(new Task("Task 2", 2, true));
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

    private void showKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        if(imm != null){
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
        }
    }

    public void taskSettings(View v) {
        Task task = (Task) v.getTag();
        Toast.makeText(this, "You clicked " + task.getName() + " with id " + task.getId(),
                Toast.LENGTH_SHORT).show();
    }

    public void addTask(MenuItem item) {
        if (!isLastElementEdit())
        {
            showKeyboard();
            final Task task = new Task(-1, "", true);
            mTaskList.add(task);
            mTaskAdapter.notifyDataSetChanged();
        }
    }

    private boolean isLastElementEdit() {
        int last = mTaskList.size()-1;
        return last > -1 && mTaskList.get(last).getIsTaskEdit();

    }
}
