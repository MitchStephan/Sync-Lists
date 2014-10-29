package com.example.synclists.synclists;

import java.io.Serializable;

/**
 * Created by ethan on 10/27/14.
 */
public class Task implements Serializable {
    private String name = "";
    private int id;
    private boolean mIsTaskEdit;
    private boolean completed = false;

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
