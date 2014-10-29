package com.example.synclists.synclists;

/**
 * Created by ethan on 10/27/14.
 */
public class Task {
    private String name = "";
    private int id;
    private boolean mIsTaskEdit;

    public Task(String name, int id)
    {
        this(name, id, false);
    }

    public Task(String name, int id, boolean isTaskEdit) {
        this.name = name;
        this.id = id;
        mIsTaskEdit = isTaskEdit;
    }

    public String getName() { return name; }

    public int getId() { return id; }

    public boolean getIsTaskEdit() { return mIsTaskEdit; }
}
