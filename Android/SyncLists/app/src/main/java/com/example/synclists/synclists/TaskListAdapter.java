package com.example.synclists.synclists;

import android.app.Activity;
import android.content.Context;
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

import java.util.List;

/**
 * Created by ethan on 10/27/14.
 */
public class TaskListAdapter extends ArrayAdapter<Task> {

    private List<Task> taskList;
    private int layoutResourceID;
    private Context context;

    public TaskListAdapter(Context context, int layoutResourceID, List<Task> taskList) {
        super(context, layoutResourceID, taskList);
        this.layoutResourceID = layoutResourceID;
        this.context = context;
        this.taskList = taskList;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Task task;
        task = taskList.get(position);

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();

        if (task.getIsTaskEdit()){
            return getEditView(position, parent, task, inflater);
        }
        else {
            return getTaskView(parent, task, inflater);
        }
    }

    public View getTaskView(ViewGroup parent, Task task, LayoutInflater inflater) {

        View row = inflater.inflate(layoutResourceID, parent, false);
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
        final EditText edit = (EditText)row.findViewById(R.id.tasksEditText);
        edit.setText(task.getName());
        edit.requestFocus();

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
        if (validName(newTaskName)) {
            taskList.remove(position);
            taskList.add(position, new Task(newTaskName, -1));
            notifyDataSetChanged();
            hideKeyboard();
        }
    }

    private boolean validName(String newTaskName) {
        return newTaskName != null && !newTaskName.equals("") && !newTaskName.matches("^\\s*$");
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

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
