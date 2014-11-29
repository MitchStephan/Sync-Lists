package com.example.synclists.synclists;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.OnDismissCallback;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.undo.TimedUndoAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ethan on 10/25/14.
 */
public class ListsActivity extends Activity {

    private List<SyncListsList> mLists;
    private ListArrayAdapter mAdapter;
    public boolean mCanAddList;
    private DynamicListView mDynamicListView;
    private final Activity CONTEXT = this;
    private SyncListsSync mSyncer;
    private boolean mFirstOnResume = true;

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

        mSyncer = new SyncListsSync(this, syncListsTask);
    }

    protected void onResume() {
        super.onResume();

        if(mFirstOnResume) {
            mSyncer.startSync();
            mFirstOnResume = false;
        }
        else
            mSyncer.startSync(false);
    }

    protected void onPause() {
        super.onPause();
        mSyncer.stopSync();
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

    public void onClickEditList(View v) {

        final SyncListsList list = (SyncListsList) v.getTag();
        final String currentListName = list.getName();

        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(this);

        View promptView = layoutInflater.inflate(R.layout.popup_edit_list, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set prompts.xml to be the layout file of the alertdialog builder
        alertDialogBuilder.setView(promptView);

        final EditText input = (EditText) promptView.findViewById(R.id.editListUserInput);
        input.setText(currentListName);

        //set cursor to after last letter
        input.setSelection(currentListName.length());

        // setup a dialog window
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        final String successMessage = "List name successfully updated";
                        final String newListName = input.getText().toString();

                        //only make API call if list name changed
                        if (currentListName.equals(newListName)) {
                            Toast.makeText(CONTEXT, successMessage, Toast.LENGTH_SHORT).show();
                        }
                        else {
                            list.setName(newListName);

                            SyncListsApi.updateList(new SyncListsRequestAsyncTaskCallback() {
                                @Override
                                public void onTaskComplete(SyncListsResponse syncListsResponse) {
                                    if (syncListsResponse == null) {
                                        Toast.makeText(CONTEXT, "Error renaming list " + list.getName(),
                                                Toast.LENGTH_SHORT).show();
                                        list.setName(currentListName);
                                    } else {
                                        Toast.makeText(CONTEXT, successMessage,
                                                Toast.LENGTH_SHORT).show();

                                        int position = mAdapter.getPosition(list);
                                        View view = mDynamicListView.getChildAt(position);
                                        TextView editText = (TextView) view.findViewById(R.id.listsListViewText);
                                        editText.setText(newListName);
                                    }
                                }
                            }, list, CONTEXT);
                        }
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        AlertDialog alertD = alertDialogBuilder.create();

        alertD.show();
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

    private SyncListsSyncTask syncListsTask = new SyncListsSyncTask() {
        @Override
        public void onPerformSync() {
            Log.d(Constants.TAG, "In onPerformSync in ListsActivity");

            SyncListsApi.getLists(new SyncListsRequestAsyncTaskCallback() {
                @Override
                public void onTaskComplete(SyncListsResponse syncListsResponse) {
                    if (syncListsResponse != null) {
                        try {
                            Map<Integer, SyncListsList> lists = SyncListsApi.parseListsAsMap(syncListsResponse.getBody());
                            Log.d(Constants.TAG, "lists parsed as map");

                            int i = 0;
                            while(i < mAdapter.getCount()) {
                                SyncListsList list = mAdapter.getItem(i);
                                i++;

                                if(list.getId() < 0) {
                                    continue;
                                }

                                if(lists.containsKey(list.getId())) {
                                    SyncListsList updatedList = lists.get(list.getId());

                                    if(!list.getName().equals(updatedList.getName())) {

                                        Toast.makeText(CONTEXT, "List " + list.getName() + " renamed to " + updatedList.getName(),
                                                Toast.LENGTH_SHORT).show();

                                        mAdapter.insert(updatedList, i);
                                        mAdapter.remove(list);
                                    }

                                    lists.remove(list.getId());
                                }
                                else {
                                    Log.d(Constants.TAG, "Deleting list " + list.getName() + " with id " + list.getId());
                                    mAdapter.remove(list);

                                    Toast.makeText(CONTEXT, "List " + list.getName() + " deleted",
                                            Toast.LENGTH_SHORT).show();

                                    i--;
                                }
                            }

                            //any remaining lists are new and need to be added
                            for(int newListId : lists.keySet()) {
                                SyncListsList list = lists.get(newListId);
                                Log.d(Constants.TAG, "Adding new list " + list.getName() + " with id " + list.getId());

                                Toast.makeText(CONTEXT, "New list " + list.getName() + " added",
                                        Toast.LENGTH_SHORT).show();

                                mAdapter.add(lists.get(newListId));
                            }
                        }
                        catch (Exception e) {
                            Log.d(Constants.TAG, "exception parsing lists: " + e.getMessage());
                        }
                    }
                }
            }, CONTEXT);
        }
    };
}
