package com.example.synclists.synclists;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        mLists = new ArrayList<SyncListsList>();
        mCanAddList = true;

        populateLists();

        mAdapter = new ListArrayAdapter(this, R.layout.lists_list_view, mLists);

        ListView lv = (ListView) findViewById(R.id.listsListView);
        lv.setAdapter(mAdapter);
    }


//    private void createList(String name, List<String> tasks) {
//        mListDataHeader.add(name);
//        mListDataChild.put(name, tasks);
//    }


    private void populateLists() {
        // get list from API class?

        mLists.add(new SyncListsList(1, "List 1"));
        mLists.add(new SyncListsList(2, "List 2"));
    }

    public void addList(MenuItem item) {
//        setCanAddList(false);
//
//        final LinearLayout layout = (LinearLayout) findViewById(R.id.lists_layout);
//        final EditText newList = new EditText(this);
//        newList.setLayoutParams(new ListView.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, ListView.LayoutParams.WRAP_CONTENT));
//
//        // set focus on newList
//        setEditTextFocus(newList, true);
//
//        layout.addView(newList);
//        showKeyboard(newList);
//
//        newList.setOnKeyListener(new View.OnKeyListener() {
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                // If the event is a key-down event on the "enter" button
//                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
//                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
//
//                    Log.d(TAG, "ENTER PRESSED");
//
//                    // create new list
//                    if (!mCanAddList)
//                        validateOnCreateList(newList, layout);
//
//                    return true;
//                }
//                return false;
//            }
//        });
//
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

    // handles list creation and validation so that different listeners can use it
    public void validateOnCreateList(EditText newList, LinearLayout layout) {
        setCanAddList(true);

        String newListName = newList.getText().toString();
        layout.removeView(newList);

        if (validateListName(newListName)) {
            //createList(newListName, new ArrayList<String>());
        }
    }

    private boolean validateListName(String newListName) {
        return newListName != null && !newListName.equals("") && !newListName.matches("^\\s*$");
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

    private void setEditTextFocus(EditText editText, boolean isFocused){
        editText.setCursorVisible(isFocused);
        editText.setFocusable(isFocused);
        editText.setFocusableInTouchMode(isFocused);

        if (isFocused){
            editText.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editText, InputMethodManager.SHOW_FORCED);
        }
    }

    private void showKeyboard(View v) {
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
