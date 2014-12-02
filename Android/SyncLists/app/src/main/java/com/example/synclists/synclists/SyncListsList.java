package com.example.synclists.synclists;

import java.util.ArrayList;

/**
 * Created by SirChickenHair on 10/27/14.
 */
public class SyncListsList {
    private String mName;
    private int mId;
    private boolean mIsListEdit;
    private String mListOwner;
    private ArrayList<String> mSharedUsersList;

    public SyncListsList(int id, String name, String listOwner, ArrayList<String> sharedUsers) {
        this(id, name, false, listOwner, sharedUsers);
    }

    public SyncListsList(int id, String name, boolean isListEdit, String listOwner, ArrayList<String> sharedUsersList){
        mId = id;
        mName = name;
        mIsListEdit = isListEdit;
        mListOwner = listOwner;
        mSharedUsersList = sharedUsersList;
    }

    public ArrayList<String> getSharedUsersList() { return mSharedUsersList; }

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
