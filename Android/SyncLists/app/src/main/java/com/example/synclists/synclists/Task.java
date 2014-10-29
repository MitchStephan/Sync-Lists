package com.example.synclists.synclists;

/**
 * Created by ethan on 10/27/14.
 */
public class Task {
    private String mName = "";
    private int mId;
    private boolean mIsTaskEdit;

    public Task(String name, int id)
    {
        this(name, id, false);
    }

    public Task(String name, int id, boolean isTaskEdit) {
        mName = name;
        mId = id;
        mIsTaskEdit = isTaskEdit;
    }

    public String getName() { return mName; }

    public int getId() { return mId; }

    public boolean getIsTaskEdit() { return mIsTaskEdit; }
}
