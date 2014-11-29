package com.example.synclists.synclists;

/**
 * Created by ethan on 11/29/14.
 */
public class SyncListsUser {
    private String mEmail;
    private int mId;
    private boolean mIsListEdit;

    public SyncListsUser(int id, String email) {
        this(id, email, false);
    }

    public SyncListsUser(int id, String email, boolean isListEdit){
        mId = id;
        mEmail = email;
        mIsListEdit = isListEdit;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String name) {
        mEmail = name;
    }

    public boolean getIsListEdit() { return mIsListEdit; }
}
