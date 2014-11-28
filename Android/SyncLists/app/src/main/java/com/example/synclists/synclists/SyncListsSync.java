package com.example.synclists.synclists;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by SirChickenHair on 11/26/14.
 */
public class SyncListsSync {
    private Activity mContext;
    private Timer mTimer;
    private SyncListsSyncTask mSyncListsSyncTask;
    private SharedPreferences mPrefs;

    public SyncListsSync(Activity context, SyncListsSyncTask syncListsSyncTask) {
        mContext = context;
        mSyncListsSyncTask = syncListsSyncTask;
        mPrefs = mContext.getSharedPreferences(Constants.PREF_FILE_NAME, mContext.MODE_PRIVATE);
    }

    public void stopSync() {
        Log.d(Constants.TAG, "Syncing stopped for " + mContext.getLocalClassName());
        mTimer.cancel();
    }

    public void startSync() {
        startSync(true);
    }

    public void startSync(boolean delay) {
        Log.d(Constants.TAG, "Sync Started");

        final String logMessage = "Syncing for " + mContext.getLocalClassName() + "...";
        int syncEvery =  mPrefs.getInt(Constants.PREF_SYNC_EVERY, Constants.DEFAULT_SYNC_EVERY);
        int delayTime = delay ? syncEvery : 0;

        mTimer = new Timer();
        mTimer.schedule( new TimerTask() {
            public void run() {
                Log.d(Constants.TAG, logMessage);
                mSyncListsSyncTask.onPerformSync();
            }
        }, delayTime, syncEvery);
    }
}
