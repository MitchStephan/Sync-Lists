package com.example.synclists.synclists;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.OnDismissCallback;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.undo.TimedUndoAdapter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by ethan on 10/27/14.
 */
public class TaskListActivity extends Activity {
    private List<SyncListsTask> mTaskList;
    private TaskListAdapter mAdapter;
    private int mListId;
    private DynamicListView mDynamicListView;
    private final Activity CONTEXT = this;
    private SyncListsSync mSyncer;
    private boolean mFirstOnResume = true;
    private boolean mRefreshing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_lists_tasks);
        mRefreshing = false;

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
                            mAdapter.add(task);

                    } catch (Exception e) {
                        Toast.makeText(context, "Error retrieving tasks",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, mListId, CONTEXT);

        mSyncer = new SyncListsSync(this, syncTasksTask);
    }

    protected void onResume() {
        super.onResume();

        if(mFirstOnResume) {
            mSyncer.startSync();
            mFirstOnResume = false;
        }
        else
            mSyncer.startSync(false);
    }

    protected void onPause() {
        super.onPause();
        mSyncer.stopSync();
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
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.refresh:
                refresh();
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

    public void taskLastUpdated(View v) {
        SyncListsTask task = (SyncListsTask) v.getTag();

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        df.setTimeZone(TimeZone.getTimeZone("CST"));
        Date date;
        try {
            String dateString = task.getDateUpdated().replaceAll("\\..*Z", "");

            date = df.parse(dateString);
            String newDateString = df.format(date);
            System.out.println(newDateString);

            Toast.makeText(this, "Last update: " + date.toString() + " by " +  task.getLastEditor(),
                    Toast.LENGTH_SHORT).show();
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
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

    public void onCheckTask(View v) {
        final CheckBox checkBox = (CheckBox) v;
        final SyncListsTask task = (SyncListsTask) checkBox.getTag();
        final boolean isChecked = checkBox.isChecked();
        int strikethroughFlags;

        if(isChecked)
            strikethroughFlags = checkBox.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG;
        else
            strikethroughFlags = checkBox.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG);

        task.setCompleted(isChecked);

        SyncListsApi.updateTask(new SyncListsRequestAsyncTaskCallback() {
            @Override
            public void onTaskComplete(SyncListsResponse syncListsResponse) {
                if (syncListsResponse == null) {
                    Toast.makeText(CONTEXT, "Error updating task " + task.getName(),
                            Toast.LENGTH_SHORT).show();

                    //error updating task, so set it not completed
                    task.setCompleted(!isChecked);

                    int strikeThroughFlags;
                    if(isChecked)
                        strikeThroughFlags = checkBox.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG);
                    else
                        strikeThroughFlags = checkBox.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG;

                    checkBox.setPaintFlags(strikeThroughFlags);
                }
            }
        }, mListId, task, CONTEXT);

        checkBox.setPaintFlags(strikethroughFlags);
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

    public void refresh() {
        if(mRefreshing) {
            return;
        }

        mRefreshing = true;
        SyncListsApi.getTasks(new SyncListsRequestAsyncTaskCallback() {
            @Override
            public void onTaskComplete(SyncListsResponse syncListsResponse) {
                if (syncListsResponse != null) {
                    try {
                        Map<Integer, SyncListsTask> tasks = SyncListsApi.parseTasksAsMap(syncListsResponse.getBody());
                        Log.d(Constants.TAG, "tasks parsed as map");

                        int i = 0;
                        while(i < mAdapter.getCount()) {
                            SyncListsTask task = mAdapter.getItem(i);
                            i++;

                            if(task.getId() < 0) {
                                continue;
                            }

                            if(tasks.containsKey(task.getId())) {
                                SyncListsTask updatedTask = tasks.get(task.getId());
                                boolean updated = false;

                                if(!task.getName().equals(updatedTask.getName())) {

                                    Toast.makeText(CONTEXT, "Task " + task.getName() + " renamed to " + updatedTask.getName(),
                                            Toast.LENGTH_SHORT).show();

                                    updated = true;
                                }
                                else if(task.getCompleted() != updatedTask.getCompleted()) {

                                    String completedMessage = "Task " + task.getName() + " ";

                                    if(updatedTask.getCompleted())
                                        completedMessage += "completed";
                                    else
                                        completedMessage += "marked not completed";

                                    Toast.makeText(CONTEXT, completedMessage,
                                            Toast.LENGTH_SHORT).show();

                                    updated = true;
                                }

                                if(updated) {
                                    mAdapter.insert(updatedTask, i);
                                    mAdapter.remove(task);
                                }

                                tasks.remove(task.getId());
                            }
                            else {
                                Log.d(Constants.TAG, "Deleting task " + task.getName() + " with id " + task.getId());
                                mAdapter.remove(task);

                                Toast.makeText(CONTEXT, "Task " + task.getName() + " deleted",
                                        Toast.LENGTH_SHORT).show();

                                i--;
                            }
                        }

                        //any remaining lists are new and need to be added
                        for(int newTaskId : tasks.keySet()) {
                            SyncListsTask task = tasks.get(newTaskId);
                            Log.d(Constants.TAG, "Adding new task " + task.getName() + " with id " + task.getId());

                            Toast.makeText(CONTEXT, "New task " + task.getName() + " added",
                                    Toast.LENGTH_SHORT).show();

                            mAdapter.add(tasks.get(newTaskId));
                        }
                    }
                    catch (Exception e) {
                        Log.d(Constants.TAG, "exception parsing tasks: " + e.getMessage());
                    }
                }

                mRefreshing = false;
            }
        }, mListId, CONTEXT);
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

                SyncListsApi.deleteTask(new SyncListsRequestAsyncTaskCallback() {
                    @Override
                    public void onTaskComplete(SyncListsResponse syncListsResponse) {
                        if (syncListsResponse == null) {
                            Toast.makeText(CONTEXT, "Error deleting task " + task.getName(),
                                    Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(CONTEXT, "Task " + task.getName() + " successfully deleted",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }, mListId, task, CONTEXT);
            }
        }
    };

    private SyncListsSyncTask syncTasksTask = new SyncListsSyncTask() {
        @Override
        public void onPerformSync() {
            Log.d(Constants.TAG, "In onPerformSync in TasksActivity");
            refresh();
        }
    };
}
