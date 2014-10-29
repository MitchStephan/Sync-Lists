package com.example.synclists.synclists;

/**
 * Created by ethan on 10/27/14.
 */
public class Task {
    private String mName = "";
    private int mId;
    private boolean mIsTaskEdit;

    public Task(int id, String name)
    {
        this(id, name, false);
    }

    public Task(int id, String name, boolean isTaskEdit) {
        mName = name;
        mId = id;
        mIsTaskEdit = isTaskEdit;
    }

    public String getName() { return mName; }

    public int getId() { return mId; }

    public void setId(int id) {
        mId = id;
    }

    public boolean getIsTaskEdit() { return mIsTaskEdit; }
}
