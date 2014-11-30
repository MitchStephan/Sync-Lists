package com.example.synclists.synclists;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by ethan on 10/27/14.
 */
public class SyncListsTask {
    private String mName;
    private int mId;
    private boolean mIsTaskEdit;
    private boolean mCompleted;
    private String mDateUpdated;
    private String mLastEditor;

    public SyncListsTask(int id, String name)
    {
        this(id, name, false);
    }

    public SyncListsTask(int id, String name, boolean isTaskEdit) {
        this(id, name, isTaskEdit, false, "just now", "you");
    }

    public SyncListsTask(int id, String name, boolean isTaskEdit, boolean completed, String dateUpdated, String lastEditor) {
        mName = name;
        mId = id;
        mIsTaskEdit = isTaskEdit;
        mCompleted = completed;
        mDateUpdated = dateUpdated;
        mLastEditor = lastEditor;
    }

    public String getName() { return mName; }

    public void setName(String name) { mName = name; }

    public int getId() { return mId; }

    public void setId(int id) { mId = id; }

    public boolean getIsTaskEdit() { return mIsTaskEdit; }

    public boolean getCompleted() { return mCompleted; }

    public void setCompleted(boolean completed) { mCompleted = completed; }

    public String getDateUpdated() { return mDateUpdated; }

    public String getLastEditor() { return mLastEditor; }
}
