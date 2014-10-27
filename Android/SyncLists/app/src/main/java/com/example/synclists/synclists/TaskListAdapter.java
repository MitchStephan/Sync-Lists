package com.example.synclists.synclists;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

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
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        TaskHolder holder = null;

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        row = inflater.inflate(layoutResourceID,parent, false);

        holder = new TaskHolder();
        holder.task = taskList.get(position);
        holder.settingsButton = (Button)row.findViewById(R.id.task_settings);
        holder.settingsButton.setTag(holder.task);

        row.setTag(holder);

        holder.name.setText(holder.task.getName());
        return row;
    }

    public static class TaskHolder {
        Task task;
        TextView name;
        Button settingsButton;

    }

}
