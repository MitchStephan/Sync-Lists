package com.example.synclists.synclists;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.OnDismissCallback;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.undo.TimedUndoAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ethan on 10/27/14.
 */
public class TaskListActivity extends Activity {
    private List<SyncListsTask> mTaskList;
    private TaskListAdapter mAdapter;
    private int mListId;
    private DynamicListView mDynamicListView;
    private final Activity CONTEXT = this;
    private final int SHOW_TASK_COMPLETED_TIME = 1000; // in milliseconds

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

        mTaskList = new ArrayList<SyncListsTask>();
        mAdapter = new TaskListAdapter(this, R.layout.tasks_view, mTaskList, mListId);

        mDynamicListView = (DynamicListView) findViewById(R.id.tasks_view_wrapper);
        final Context context = this;

        TimedUndoAdapter timedUndoAdapter = new TimedUndoAdapter(mAdapter, this, mOnCompleteTaskCallback);
        timedUndoAdapter.setAbsListView(mDynamicListView);
        timedUndoAdapter.setTimeoutMs(SHOW_TASK_COMPLETED_TIME);
        mDynamicListView.setAdapter(timedUndoAdapter);
        mDynamicListView.enableSimpleSwipeUndo();

        // Set title to list name
        setTitle(getIntent().getStringExtra("listName"));

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

                        mAdapter.clear();

                        for(SyncListsTask task : mTaskList)
                            if(!task.getCompleted())
                                mAdapter.add(task);
                    } catch (Exception e) {
                        Toast.makeText(context, "Error retrieving tasks",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, mListId, CONTEXT);
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
        final SyncListsTask task = (SyncListsTask) v.getTag();

        mAdapter.remove(task);
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
        SyncListsTask task = (SyncListsTask) v.getTag();
        Toast.makeText(this, "You clicked " + task.getName() + " with id " + task.getId(),
                Toast.LENGTH_SHORT).show();
    }

    public void addTask(MenuItem item) {
        if (!isElementEdit(mAdapter.getCount() - 1))
        {
            final SyncListsTask task = new SyncListsTask(-1, "", true);
            mAdapter.add(task);
            mDynamicListView.smoothScrollToPosition(mAdapter.getCount());
            if (item != null) {
                showKeyboard();
            }
        }
    }

    public void onClickEditTask(View v) {

        final SyncListsTask task = (SyncListsTask) v.getTag();
        final String currentTaskName = task.getName();

        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(this);

        View promptView = layoutInflater.inflate(R.layout.popup_edit_task, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set prompts.xml to be the layout file of the alertdialog builder
        alertDialogBuilder.setView(promptView);

        final EditText input = (EditText) promptView.findViewById(R.id.editTaskUserInput);
        input.setText(currentTaskName);

        //set cursor to after last letter
        input.setSelection(currentTaskName.length());

        // setup a dialog window
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        final String successMessage = "Task name successfully updated";
                        final String newTaskName = input.getText().toString();

                        //only make API call if list name changed
                        if (currentTaskName.equals(newTaskName)) {
                            Toast.makeText(CONTEXT, successMessage, Toast.LENGTH_SHORT).show();
                        }
                        else {
                            task.setName(newTaskName);

                            SyncListsApi.updateTask(new SyncListsRequestAsyncTaskCallback() {
                                @Override
                                public void onTaskComplete(SyncListsResponse syncListsResponse) {
                                    if (syncListsResponse == null) {
                                        Toast.makeText(CONTEXT, "Error renaming task " + task.getName(),
                                                Toast.LENGTH_SHORT).show();
                                        task.setName(currentTaskName);
                                    } else {
                                        Toast.makeText(CONTEXT, successMessage,
                                                Toast.LENGTH_SHORT).show();

                                        int position = mAdapter.getPosition(task);
                                        View view = mDynamicListView.getChildAt(position);
                                        TextView editText = (TextView) view.findViewById(R.id.task_name);
                                        editText.setText(newTaskName);
                                    }
                                }
                            }, mListId, task, CONTEXT);
                        }
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        AlertDialog alertD = alertDialogBuilder.create();

        alertD.show();
    }

    private boolean isElementEdit(int position) {
        return position > -1 && mAdapter.getItem(position).getIsTaskEdit();
    }

    private OnDismissCallback mOnCompleteTaskCallback = new OnDismissCallback() {
        @Override
        public void onDismiss(final ViewGroup listView, final int[] reverseSortedPositions) {
            for (int position : reverseSortedPositions) {

                final SyncListsTask task = mAdapter.getItem(position);
                mAdapter.remove(task);
                task.setCompleted(true);

                SyncListsApi.updateTask(new SyncListsRequestAsyncTaskCallback() {
                    @Override
                    public void onTaskComplete(SyncListsResponse syncListsResponse) {
                        if (syncListsResponse == null) {
                            Toast.makeText(CONTEXT, "Error completing task " + task.getName(),
                                    Toast.LENGTH_SHORT).show();

                            //error updating task, so set it not completed
                            task.setCompleted(false);
                        }
                        else {
                            Toast.makeText(CONTEXT, "Task " + task.getName() + " successfully completed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }, mListId, task, CONTEXT);
            }
        }
    };
}
