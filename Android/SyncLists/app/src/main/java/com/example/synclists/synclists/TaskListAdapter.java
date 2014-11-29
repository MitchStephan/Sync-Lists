package com.example.synclists.synclists;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.undo.UndoAdapter;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by ethan on 10/27/14.
 */
public class TaskListAdapter extends ArrayAdapter<SyncListsTask> implements UndoAdapter {

    private List<SyncListsTask> mTaskList;
    private int mLayoutResourceID;
    private Context mContext;
    private int mListId;

    public TaskListAdapter(Context context, int layoutResourceID, List<SyncListsTask> taskList, int listId) {
        super(context, layoutResourceID, taskList);
        this.mLayoutResourceID = layoutResourceID;
        this.mContext = context;
        this.mTaskList = taskList;
        mListId = listId;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        SyncListsTask task;
        task = getItem(position);

        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();

        if (task.getIsTaskEdit()){
            return getEditView(position, parent, task, inflater);
        }
        else {
            return getTaskView(parent, task, convertView, inflater);
        }
    }

    public View getTaskView(ViewGroup parent, SyncListsTask task, View convertView, LayoutInflater inflater) {
        View row;
        TaskHolder holder;

        row = inflater.inflate(R.layout.tasks_view, parent, false);
        holder = new TaskHolder();

        holder.taskEditButton = (Button) row.findViewById(R.id.editTask);
        holder.taskEditButton.setTypeface(Typefaces.get(mContext));

        holder.taskSettingsButton = (ImageButton)row.findViewById(R.id.task_settings);
        holder.taskCheckBox = (CheckBox)row.findViewById(R.id.task_name);

        int strikethroughFlags;
        if(task.getCompleted())
            strikethroughFlags = holder.taskCheckBox.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG;
        else
            strikethroughFlags = holder.taskCheckBox.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG);

        holder.taskCheckBox.setPaintFlags(strikethroughFlags);
        holder.taskCheckBox.setChecked(task.getCompleted());

        row.setTag(holder);

        holder.task = task;
        holder.taskEditButton.setTag(holder.task);
        holder.taskSettingsButton.setTag(holder.task);
        holder.taskCheckBox.setTag(task);
        holder.taskCheckBox.setText(holder.task.getName());
        return row;
    }

    private View getEditView(final int position, ViewGroup parent, final SyncListsTask task, LayoutInflater inflater) {
        View row = inflater.inflate(R.layout.tasks_edit_view, parent, false);

        //set typeface for button
        Button button = (Button)row.findViewById( R.id.taskEditDeleteButton );
        button.setTypeface(Typefaces.get(mContext));
        button.setTag(task);

        final EditText edit = (EditText)row.findViewById(R.id.tasksEditText);
        edit.setText(task.getName());
        edit.requestFocus();

        edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                task.setName(edit.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edit.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_ENDCALL ||
                        keyCode == KeyEvent.ACTION_DOWN) {
                    String newTaskName = edit.getText().toString();
                    validateOnCreate(newTaskName, position);
                    return true;
                }
                return false;
            }
        });

        return row;
    }

    private void validateOnCreate(String newTaskName, final int position) {
        remove(getItem(position));

        if (validName(newTaskName)) {
            final SyncListsTask task = new SyncListsTask(-1, newTaskName);
            insert(task, position);

            //create task with api
            SyncListsApi.createTask(new SyncListsRequestAsyncTaskCallback() {
                @Override
                public void onTaskComplete(SyncListsResponse syncListsResponse) {

                    Log.d(Constants.TAG, "Returned from creating task");
                    if (syncListsResponse == null) {
                        Log.d(Constants.TAG, "Error creating task");
                        Toast.makeText(mContext, "Error creating task",
                                Toast.LENGTH_SHORT).show();

                        if(getCount() > 0)
                            remove(getItem(position));
                    }
                    else {
                        try {
                            JSONObject jsonObject = new JSONObject(syncListsResponse.getBody());
                            task.setId(jsonObject.getInt("pk"));
                            Log.d(Constants.TAG, "Success creating task " + task.getId());
                        }
                        catch (Exception e) {

                        }
                    }
                }
            }, mListId, newTaskName, mContext);
            ((TaskListActivity)mContext).addTask(null);
        }
        else {
            hideKeyboard();
        }
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

    @Override
    public View getUndoView(final int position, final View convertView, final ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.task_undo_row, parent, false);
        }

        return view;
    }

    @Override
    public View getUndoClickView(final View view) {
        TextView textView = (TextView) view.findViewById(R.id.task_undo_row_texttv);
        textView.setTypeface(Typefaces.get(mContext));

        return view.findViewById(R.id.task_undo_row_undobutton);
    }

    public static class TaskHolder {
        SyncListsTask task;
        CheckBox taskCheckBox;
        Button taskEditButton;
        ImageButton taskSettingsButton;
    }
}
