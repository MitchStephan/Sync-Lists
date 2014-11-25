package com.example.synclists.synclists;

/**
 * Created by SirChickenHair on 10/27/14.
 */
public class SyncListsList {
    private String mName;
    private int mId;
    private boolean mIsListEdit;

    public SyncListsList(int id, String name) {
        this(id, name, false);
    }

    public SyncListsList(int id, String name, boolean isListEdit){
        mId = id;
        mName = name;
        mIsListEdit = isListEdit;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public boolean getIsListEdit() { return mIsListEdit; }
}
