package com.example.synclists.synclists;

import java.io.Serializable;

/**
 * Created by SirChickenHair on 10/27/14.
 */
public class SyncListsList {
    private String mName;
    private int mId;

    public SyncListsList(int id, String name) {
        mId = id;
        mName = name;
    }

    public int getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

}
