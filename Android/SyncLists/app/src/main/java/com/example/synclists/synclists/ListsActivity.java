package com.example.synclists.synclists;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.OnDismissCallback;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.undo.TimedUndoAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ethan on 10/25/14.
 */
public class ListsActivity extends Activity {

    private List<SyncListsList> mLists;
    private ListArrayAdapter mAdapter;
    public boolean mCanAddList;
    private DynamicListView mDynamicListView;
    private final Activity CONTEXT = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_lists_lists);

        mCanAddList = true;
        mLists = new ArrayList<SyncListsList>();
        mAdapter = new ListArrayAdapter(this, R.layout.lists_list_view, mLists);

        mDynamicListView = (DynamicListView) findViewById(R.id.list_lists_view);
        final Context context = this;

        TimedUndoAdapter timedUndoAdapter = new TimedUndoAdapter(mAdapter, this, mOnDeleteListCallback);
        timedUndoAdapter.setAbsListView(mDynamicListView);
        mDynamicListView.setAdapter(timedUndoAdapter);
        mDynamicListView.enableSimpleSwipeUndo();

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

                        mAdapter.clear();
                        for(SyncListsList list : mLists)
                            mAdapter.add(list);
                    }
                    catch (Exception e) {
                        Toast.makeText(context, "Error retrieving lists",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, this);
    }

    public void addList(MenuItem item) {

        if (!isListEdit(mAdapter.getCount() - 1))
        {
            showKeyboard();
            mAdapter.add(new SyncListsList(-1, "", true));
            mDynamicListView.smoothScrollToPosition(mAdapter.getCount());
        }
    }

    private boolean isListEdit(int position) {
        return position > -1 && mAdapter.getItem(position).getIsListEdit();
    }

    public void listSettings(View v) {
        SyncListsList list = (SyncListsList) v.getTag();
        Toast.makeText(this, "You clicked " + list.getName() + " with id " + list.getId(),
                Toast.LENGTH_SHORT).show();
    }

    public void onListClick(View v) {
        ListArrayAdapter.ListRowHolder listRowHolder = (ListArrayAdapter.ListRowHolder) v.getTag();
        SyncListsList list = listRowHolder.list;
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

    public void onClickDeleteEditList(View v) {
        final SyncListsList list = (SyncListsList) v.getTag();

        mAdapter.remove(list);
        hideKeyboard();
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        if(imm != null){
            imm.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY);
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

    private OnDismissCallback mOnDeleteListCallback = new OnDismissCallback() {
        @Override
        public void onDismiss(final ViewGroup listView, final int[] reverseSortedPositions) {
            for (int position : reverseSortedPositions) {

                final SyncListsList list = mAdapter.getItem(position);
                mAdapter.remove(list);

                SyncListsApi.deleteList(new SyncListsRequestAsyncTaskCallback() {
                    @Override
                    public void onTaskComplete(SyncListsResponse syncListsResponse) {
                        if (syncListsResponse == null) {
                            Toast.makeText(CONTEXT, "Error deleting list " + list.getName(),
                                    Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(CONTEXT, "List " + list.getName() + " successfully deleted",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }, list.getId(), CONTEXT);
            }
        }
    };

}
