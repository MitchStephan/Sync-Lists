package com.example.synclists.synclists;

/**
 * Created by SirChickenHair on 11/26/14.
 */
public final class Constants {

    /* App TAG */
    protected static final String TAG = "SyncLists";

    /* USER-CONTEXT header */
    protected final static String USER_CONTEXT_HEADER = "USER-CONTEXT";

    /* resources */
    protected static final String PREF_FILE_NAME = "SyncListsPrefs";

    /* path to fontawesome assets */
    protected static final String FONTAWESOME_ASSETS = "fontawesome-webfont.ttf";

    /* defaults */

    // in milliseconds
    protected static final int DEFAULT_SYNC_EVERY = 30000;

    /* Prefs */
    protected static final String PREF_SYNC_EVERY = "SYNC_EVERY";
    protected final static String PREF_USER_CONTEXT = "USER-CONTEXT";

    private Constants() throws Exception {
        throw new Exception("Cannot instantiate Constants class");
    }
}
