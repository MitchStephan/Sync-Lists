package com.example.synclists.synclists;

/**
 * Created by ethan on 10/27/14.
 */
public class SyncListsTask {
    private String mName;
    private int mId;
    private boolean mIsTaskEdit;
    private boolean mCompleted;

    public SyncListsTask(int id, String name)
    {
        this(id, name, false);
    }

    public SyncListsTask(int id, String name, boolean isTaskEdit) {
        this(id, name, isTaskEdit, false);
    }

    public SyncListsTask(int id, String name, boolean isTaskEdit, boolean completed) {
        mName = name;
        mId = id;
        mIsTaskEdit = isTaskEdit;
        mCompleted = completed;
    }

    public String getName() { return mName; }

    public void setName(String name) {
        mName = name;
    }

    public int getId() { return mId; }

    public void setId(int id) {
        mId = id;
    }

    public boolean getIsTaskEdit() { return mIsTaskEdit; }

    public boolean getCompleted() {
        return mCompleted;
    }

    public void setCompleted(boolean completed) {
        mCompleted = completed;
    }
}
