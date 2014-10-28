package com.example.synclists.synclists;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ethan on 10/27/14.
 */
public class TaskListActivity extends Activity {
    private List<Task> mTaskList;
    private TaskListAdapter mTaskAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_lists_tasks);

        mTaskList = new ArrayList<Task>();

        populateExampleTasks();

        mTaskAdapter = new TaskListAdapter(this, R.layout.tasks_view, mTaskList);

        ListView lv = (ListView) findViewById(R.id.tasks_view_wrapper);
        lv.setAdapter(mTaskAdapter);
    }

    public void populateExampleTasks() {

        mTaskList.add(new Task("Task 1", 1));
        mTaskList.add(new Task("Task 2", 2));
    }

}
