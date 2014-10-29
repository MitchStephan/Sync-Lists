package com.example.synclists.synclists;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by ethan on 10/27/14.
 */
public class TaskListAdapter extends ArrayAdapter<Task> {

    private List<Task> mTaskList;
    private int mLayoutResourceID;
    private Context mContext;
    private int mListId;

    public TaskListAdapter(Context context, int layoutResourceID, List<Task> taskList, int listId) {
        super(context, layoutResourceID, taskList);
        this.mLayoutResourceID = layoutResourceID;
        this.mContext = context;
        this.mTaskList = taskList;
        mListId = listId;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Task task;
        task = mTaskList.get(position);

        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();

        if (task.getIsTaskEdit()){
            return getEditView(position, parent, task, inflater);
        }
        else {
            return getTaskView(parent, task, inflater);
        }
    }

    public View getTaskView(ViewGroup parent, Task task, LayoutInflater inflater) {

        View row = inflater.inflate(mLayoutResourceID, parent, false);
        TaskHolder holder = new TaskHolder();

        holder.task = task;
        holder.taskSettingsButton = (ImageButton)row.findViewById(R.id.task_settings);
        holder.taskSettingsButton.setTag(holder.task);

        holder.taskText = (TextView)row.findViewById(R.id.task_name);
        holder.taskText.setText(holder.task.getName());
        row.setTag(holder);

        return row;
    }

    private View getEditView(final int position, ViewGroup parent, final Task task, LayoutInflater inflater) {
        View row = inflater.inflate(R.layout.tasks_edit_view, parent, false);

        //set typeface for button
        Typeface font = Typeface.createFromAsset( mContext.getAssets(), "fontawesome-webfont.ttf" );
        Button button = (Button)row.findViewById( R.id.taskEditDeleteButton );
        button.setTypeface(font);
        button.setTag(task);


        final EditText edit = (EditText)row.findViewById(R.id.tasksEditText);
        edit.setText(task.getName());
        edit.requestFocus();

        edit.setImeActionLabel("Add", KeyEvent.KEYCODE_ENTER);
        edit.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    String newTaskName = edit.getText().toString();
                    validateOnCreate(newTaskName, position);
                    return true;
                }
                return false;
            }
        });

        edit.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {

                    String newTaskName = edit.getText().toString();
                    validateOnCreate(newTaskName, position);
                    return true;
                }
                return false;
            }
        });

        return row;
    }

    private void validateOnCreate(String newTaskName, int position) {
        mTaskList.remove(position);
        if (validName(newTaskName)) {
            final Task task = new Task(-1, newTaskName);
            mTaskList.add(position, task);

            //create task with api
            SyncListsApi.createTask(new SyncListsRequestAsyncTaskCallback() {
                @Override
                public void onTaskComplete(SyncListsResponse syncListsResponse) {

                    Log.d("SL", "Returned from creating task");
                    if (syncListsResponse == null) {
                        Log.d("SL", "Error creating task");
                        Toast.makeText(mContext, "Error creating task",
                                Toast.LENGTH_SHORT).show();

                        if(mTaskList.size() > 0)
                            mTaskList.remove(mTaskList.size() - 1);
                    }
                    else {
                        try {
                            JSONObject jsonObject = new JSONObject(syncListsResponse.getBody());
                            task.setId(jsonObject.getInt("pk"));
                            Log.d("SL", "Success creating task " + task.getId());
                        } catch (Exception e) {

                        }
                    }
                }
            }, mListId, newTaskName);
            ((TaskListActivity)mContext).addTask(null);
        }
        else {
            hideKeyboard();
        }
        notifyDataSetChanged();

    }

    private boolean validName(String newTaskName) {
        return newTaskName != null && !newTaskName.equals("") && !newTaskName.matches("^\\s*$");
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);

        if(imm != null){
            imm.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }

    public static class TaskHolder {
        Task task;
        TextView taskText;
        ImageButton taskSettingsButton;
    }
}
