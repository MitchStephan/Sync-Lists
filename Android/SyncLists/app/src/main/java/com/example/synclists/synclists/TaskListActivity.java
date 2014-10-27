package com.example.synclists.synclists;

import android.app.Activity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ethan on 10/27/14.
 */
public class TaskListActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_lists_tasks);

        ArrayList<Task> x = new ArrayList<Task>();

        TaskListAdapter g = new TaskListAdapter(this, R.layout.task_layout, x);
        Task testTask = new Task("New Task", 1);
        g.add(testTask);

    }

}
