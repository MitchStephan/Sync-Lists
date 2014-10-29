package com.example.synclists.synclists;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ethan on 10/25/14.
 */
public class ListsActivity extends Activity {

    private List<SyncListsList> mLists;
    private ListArrayAdapter mAdapter;
    public boolean mCanAddList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_lists_lists);

        mCanAddList = true;
        mLists = new ArrayList<SyncListsList>();
        mAdapter = new ListArrayAdapter(this, R.layout.lists_list_view, mLists);

        final Context context = this;
        SyncListsApi.getLists(new SyncListsRequestAsyncTaskCallback() {
            @Override
            public void onTaskComplete(SyncListsResponse syncListsResponse) {
                if (syncListsResponse == null) {
                    Toast.makeText(context, "Error retrieving lists",
                            Toast.LENGTH_SHORT).show();
                }
                else {
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

        if (!isListEdit(mLists.size() - 1))
        {
            showKeyboard();
            mLists.add(new SyncListsList(-1, "", true));
            mAdapter.notifyDataSetChanged();
        }
    }

    private boolean isListEdit(int position) {
        return position > -1 && mLists.get(position).getIsListEdit();
    }

    public void listSettings(View v) {
        SyncListsList list = (SyncListsList) v.getTag();
        Toast.makeText(this, "You clicked " + list.getName() + " with id " + list.getId(),
                Toast.LENGTH_SHORT).show();
    }

    public void onListClick(View v) {
        SyncListsList list = (SyncListsList) v.getTag();
        Intent tasksIntent = new Intent(this, TaskListActivity.class);
        tasksIntent.putExtra("listId", list.getId());
        tasksIntent.putExtra("listName", list.getName());
        startActivity(tasksIntent);
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

    public void onClickDeleteList(View v) {
        final SyncListsList list = (SyncListsList) v.getTag();
        final Context context = this;

        SyncListsApi.deleteList(new SyncListsRequestAsyncTaskCallback() {
            @Override
            public void onTaskComplete(SyncListsResponse syncListsResponse) {
                if(syncListsResponse == null) {
                    Toast.makeText(context, "Error deleting list " + list.getName(),
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(context, "List " + list.getName() + " successfully deleted",
                            Toast.LENGTH_SHORT).show();
                    deleteListFromView(list);
                }
            }
        }, list.getId());
    }

    public void onClickDeleteEditList(View v) {
        final SyncListsList list = (SyncListsList) v.getTag();

        int position = mAdapter.getPosition(list);
        mLists.remove(position);
        mAdapter.notifyDataSetChanged();
        hideKeyboard();
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        if(imm != null){
            imm.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }

    private void deleteListFromView(SyncListsList list) {
        int i = 0;
        for(; i < mLists.size(); i++) {
            if(mLists.get(i).getId() == list.getId())
                break;
        }

        if(i < mLists.size()) {
            mLists.remove(i);
            mAdapter.notifyDataSetChanged();
        }
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

}
