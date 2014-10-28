package com.example.synclists.synclists;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by ethan on 10/25/14.
 */
public class ListsActivity extends Activity {

    private final String TAG = "SyncLists";

    private List<SyncListsList> mLists;
    private ListArrayAdapter mAdapter;
    private boolean mCanAddList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_lists_lists);


        mCanAddList = true;

        final Context context = this;
        SyncListsApi.getLists(new SyncListsRequestAsyncTaskCallback() {
            @Override
            public void onTaskComplete(SyncListsResponse syncListsResponse) {
                if (syncListsResponse == null) {
                    Toast.makeText(context, "Error retrieving lists",
                            Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        mLists = SyncListsApi.parseLists(syncListsResponse.getBody());

                        mAdapter = new ListArrayAdapter(context, R.layout.lists_list_view, mLists);

                        ListView lv = (ListView) findViewById(R.id.listsListView);
                        lv.setAdapter(mAdapter);
                    } catch (Exception e) {
                        Toast.makeText(context, "Error retrieving lists",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public void addList(MenuItem item) {
//        setCanAddList(false);
        mLists.add(new SyncListsList(-1, "", true));
        mAdapter.notifyDataSetChanged();
        showKeyboard();

//        // TODO: Move to getView()
//        newList.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                Log.d(TAG, "Focus Changed");
//
//                if (hasFocus) {
//                    Log.d(TAG, "newList has focus");
//                    showKeyboard(v);
//                }
//                else {
//                    Log.d(TAG, "NO FOCUS");
//                    //if the new list has not already been added
//                    if (!mCanAddList)
//                        validateOnCreateList(newList, layout);
//
//                    hideKeyboard(v);
//                }
//            }
//        });
    }

    public void listSettings(View v) {
        SyncListsList list = (SyncListsList) v.getTag();
        Toast.makeText(this, "You clicked " + list.getName() + " with id " + list.getId(),
                Toast.LENGTH_SHORT).show();
    }

    public void onListClick(View v) {
        SyncListsList list = (SyncListsList) v.getTag();
        Intent tasksIntent = new Intent(this, ListsActivity.class);
        tasksIntent.putExtra("listId", list.getId());
        startActivity(tasksIntent);
    }

    private void setCanAddList(boolean canAddList) {
        mCanAddList = canAddList;
        invalidateOptionsMenu();
    }

    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
        menu.getItem(0).setEnabled(mCanAddList);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sync_lists_lists, menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.settings:
                Intent settings = new Intent(this, SettingsActivity.class);
                startActivity(settings);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void showKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        if(imm != null){
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
        }
    }

    private void hideKeyboard(View v) {
        Log.d(TAG, "IN HIDE KEYBOARD");
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        if(imm != null){
            imm.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }
}
