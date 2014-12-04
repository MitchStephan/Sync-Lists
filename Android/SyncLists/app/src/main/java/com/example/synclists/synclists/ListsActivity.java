package com.example.synclists.synclists;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.OnDismissCallback;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.undo.TimedUndoAdapter;

import org.apache.http.HttpStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ethan on 10/25/14.
 */
public class ListsActivity extends Activity {

    private List<SyncListsList> mLists;
    private Map<Integer, Boolean> mIgnoreInSync;
    private ListArrayAdapter mAdapter;
    public boolean mCanAddList;
    private DynamicListView mDynamicListView;
    private final Activity CONTEXT = this;
    private SyncListsSync mSyncer;
    private boolean mFirstOnResume = true;
    private SharedUsersArrayAdapter mSharedUsersAdapter;
    private SharedPreferences mPrefs;
    private String mEmail;
    private boolean mRefreshing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_lists_lists);
        mRefreshing = false;

        mIgnoreInSync = new HashMap<Integer, Boolean>();

        mPrefs = getSharedPreferences(Constants.PREF_FILE_NAME, MODE_PRIVATE);
        mEmail = mPrefs.getString(Constants.PREF_EMAIL, Constants.DEFAULT_EMAIL);
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
            mAdapter.add(new SyncListsList(-1, "", true, mEmail, new ArrayList<String>()));
            mDynamicListView.smoothScrollToPosition(mAdapter.getCount());
        }
    }

    private boolean isListEdit(int position) {
        return position > -1 && mAdapter.getItem(position).getIsListEdit();
    }

    public void onListClick(View v) {
        ListArrayAdapter.ListRowHolder listRowHolder = (ListArrayAdapter.ListRowHolder) v.getTag();
        SyncListsList list = listRowHolder.list;
        Intent tasksIntent = new Intent(this, TaskListActivity.class);
        tasksIntent.putExtra("listId", list.getId());
        tasksIntent.putExtra("listName", list.getName());
        startActivity(tasksIntent);
    }

    public void onClickedEditSharedUsers(View v) {
        final SyncListsList list = (SyncListsList) v.getTag();

        SyncListsApi.getList(new SyncListsRequestAsyncTaskCallback() {
            @Override
            public void onTaskComplete(SyncListsResponse syncListsResponse) {
                String failureMessage = "Error loading shared users";
                if(syncListsResponse == null) {
                    Toast.makeText(CONTEXT, failureMessage, Toast.LENGTH_SHORT).show();
                }
                else {
                    try {
                        SyncListsList list = SyncListsApi.parseList(syncListsResponse.getBody());
                        showSharedUsersDialog(list);
                    }
                    catch(Exception e) {
                        Toast.makeText(CONTEXT, failureMessage, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, list.getId(), this);
    }

    public void showSharedUsersDialog(final SyncListsList list) {
        ArrayList<SyncListsUser> users = new ArrayList<SyncListsUser>();
        int rowLayoutId;
        int dialogLayoutId;
        int sharedUsersListId;

        String email = mPrefs.getString(Constants.PREF_EMAIL, Constants.DEFAULT_EMAIL);
        boolean isListOwner = list.getListOwner().equals(email);
        if(isListOwner) {
            rowLayoutId = R.layout.shared_user_row;
            dialogLayoutId = R.layout.shared_users_dialog;
            sharedUsersListId = R.id.sharedUsersList;
        }
        else {
            rowLayoutId = R.layout.shared_user_row_non_owner;
            dialogLayoutId = R.layout.shared_users_dialog_not_owner;
            sharedUsersListId = R.id.sharedUsersListNonOwner;
        }

        mSharedUsersAdapter = new SharedUsersArrayAdapter(this, rowLayoutId, users, list);

        Dialog sharedUsersDialog = new Dialog(this);
        sharedUsersDialog.setContentView(dialogLayoutId);
        sharedUsersDialog.setTitle(getString(R.string.shared_users_title) + " for " + list.getName());

        for(int i = 0; i < list.getSharedUsersList().size(); i++) {
            mSharedUsersAdapter.add(new SyncListsUser(-1, list.getSharedUsersList().get(i)));
        }


        final ListView listView = (ListView) sharedUsersDialog.findViewById(sharedUsersListId);
        listView.setAdapter(mSharedUsersAdapter);

        if(isListOwner) {
            final EditText shareEditText = (EditText) sharedUsersDialog.findViewById(R.id.sharedUsersEditText);
            shareEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_ENDCALL ||
                            keyCode == KeyEvent.ACTION_DOWN) {
                        String emailToShareWith = shareEditText.getText().toString();

                        if (Validate.email(emailToShareWith)) {
                            final SyncListsUser user = new SyncListsUser(-1, emailToShareWith);
                            mSharedUsersAdapter.add(user);
                            listView.smoothScrollToPosition(mSharedUsersAdapter.getCount());
                            shareEditText.setText("");

                            SyncListsApi.addSharedUserToList(new SyncListsRequestAsyncTaskCallback() {
                                @Override
                                public void onTaskComplete(SyncListsResponse syncListsResponse) {
                                    if (syncListsResponse == null) {
                                        Toast.makeText(CONTEXT, "Something went wrong, please try again later", Toast.LENGTH_SHORT).show();
                                        mSharedUsersAdapter.remove(user);
                                    } else {
                                        Toast.makeText(CONTEXT, "New shared user has been added", Toast.LENGTH_SHORT).show();
                                        list.getSharedUsersList().add(user.getEmail());
                                    }
                                }
                            }, list.getId(), emailToShareWith, CONTEXT);
                        } else {
                            Toast.makeText(CONTEXT, "Invalid email",
                                    Toast.LENGTH_SHORT).show();
                        }

                        return true;
                    }

                    return false;
                }
            });
        }
        else {
            TextView textView = (TextView) sharedUsersDialog.findViewById(R.id.sharedUsersOwner);
            textView.setText("Owner: " + list.getListOwner());
        }

        sharedUsersDialog.show();
    }

    public void onClickUnshareUser(View v) {
        SharedUsersArrayAdapter.SharedUserRowHolder sharedUserRowHolder = (SharedUsersArrayAdapter.SharedUserRowHolder) v.getTag();
        final SyncListsUser user = sharedUserRowHolder.user;
        final SyncListsList list = sharedUserRowHolder.list;

        mSharedUsersAdapter.remove(user);

        SyncListsApi.deleteSharedUserFromList(
                new SyncListsRequestAsyncTaskCallback() {
                    @Override
                    public void onTaskComplete(SyncListsResponse syncListsResponse) {
                        if (syncListsResponse == null) {
                            Toast.makeText(CONTEXT, "Error unsharing list with " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(CONTEXT, "List " + list.getName() + " successfully unshared with " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }, list.getId(), user.getEmail(), CONTEXT);
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
            case R.id.refresh:
                refresh();
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

    private void refresh() {
        if(mRefreshing) {
            return;
        }

        mRefreshing = true;
        SyncListsApi.getLists(new SyncListsRequestAsyncTaskCallback() {
            @Override
            public void onTaskComplete(SyncListsResponse syncListsResponse) {
                if (syncListsResponse != null) {
                    try {
                        Map<Integer, SyncListsList> lists = SyncListsApi.parseListsAsMap(syncListsResponse.getBody());
                        Log.d(Constants.TAG, "lists parsed as map");
                        String email = mPrefs.getString(Constants.PREF_EMAIL, Constants.DEFAULT_EMAIL);

                        int i = 0;
                        while(i < mAdapter.getCount()) {
                            SyncListsList list = mAdapter.getItem(i);
                            i++;

                            if(list.getId() < 0 || mIgnoreInSync.containsKey(list.getId())) {
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
                                String listDeletedMessage;

                                if(!list.getListOwner().equals(email)) {
                                    listDeletedMessage = "List " + list.getName() + " unshared by " + list.getListOwner();
                                }
                                else {
                                    listDeletedMessage = "List " + list.getName() + " deleted";
                                }

                                Toast.makeText(CONTEXT, listDeletedMessage,
                                        Toast.LENGTH_SHORT).show();

                                i--;
                            }
                        }

                        //any remaining lists are new and need to be added
                        for(int newListId : lists.keySet()) {
                            SyncListsList list = lists.get(newListId);
                            Log.d(Constants.TAG, "Adding new list " + list.getName() + " with id " + list.getId());

                            String newListMessage;

                            if(!list.getListOwner().equals(email)) {
                                newListMessage = "List " + list.getName() + " shared by " + list.getListOwner();
                            }
                            else {
                                newListMessage = "New list " + list.getName() + " added";
                            }

                            Toast.makeText(CONTEXT, newListMessage,
                                    Toast.LENGTH_SHORT).show();

                            mAdapter.add(lists.get(newListId));
                        }
                    }
                    catch (Exception e) {
                        Log.d(Constants.TAG, "exception parsing lists: " + e.getMessage());
                    }
                }

                mRefreshing = false;
            }
        }, CONTEXT);
    }

    private OnDismissCallback mOnDeleteListCallback = new OnDismissCallback() {
        @Override
        public void onDismiss(final ViewGroup listView, final int[] reverseSortedPositions) {
            for (int position : reverseSortedPositions) {

                final SyncListsList list = mAdapter.getItem(position);
                mIgnoreInSync.put(list.getId(), true);
                mAdapter.remove(list);

                SyncListsApi.deleteList(new SyncListsRequestAsyncTaskCallback() {
                    @Override
                    public void onTaskComplete(SyncListsResponse syncListsResponse) {
                        if (syncListsResponse == null) {
                            Toast.makeText(CONTEXT, "Error deleting list " + list.getName(),
                                    Toast.LENGTH_SHORT).show();
                            mIgnoreInSync.remove(list.getId());
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
            refresh();
        }
    };
}
