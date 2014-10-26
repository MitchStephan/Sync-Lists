package com.example.synclists.synclists;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
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
import android.widget.SimpleExpandableListAdapter;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ethan on 10/25/14.
 */
public class ListsActivity extends Activity{

    private final String TAG = "SyncLists";

    private ExpandableListAdapter mListAdapter;
    private ExpandableListView mExpandableListView;
    private List<String> mListDataHeader;
    private HashMap<String, List<String>> mListDataChild;
    private boolean mCanAddList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_lists_lists);

        mExpandableListView = (ExpandableListView) findViewById(R.id.lists);
        mListDataHeader = new ArrayList<String>();
        mListDataChild = new HashMap<String, List<String>>();
        mCanAddList = true;

        populateLists();

        mListAdapter = new ExpandableListAdapter(this, mListDataHeader, mListDataChild);

        // setting list adapter
        mExpandableListView.setAdapter(mListAdapter);
    }

    private void createList(String name, List<String> tasks) {
        mListDataHeader.add(name);
        mListDataChild.put(name, tasks);
        mListAdapter.notifyDataSetChanged();
    }

    private void populateLists() {
        // get list from API class?

        mListDataHeader.add("List 1");
        mListDataHeader.add("List 2");

        List<String> list1 = new ArrayList<String>();
        list1.add("Task 1");
        list1.add("Task 2");

        List<String> list2 = new ArrayList<String>();
        list2.add("Task 1");
        list2.add("Task 2");

        //
        mListDataChild.put(mListDataHeader.get(0), list1);
        mListDataChild.put(mListDataHeader.get(1), list2);
    }

    public void addList(MenuItem item) {
        setCanAddList(false);

        final LinearLayout layout = (LinearLayout) findViewById(R.id.lists_layout);
        final EditText newList = new EditText(this);
        newList.setLayoutParams(new ListView.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, ListView.LayoutParams.WRAP_CONTENT));

        // set focus on newList
        setEditTextFocus(newList, true);

        layout.addView(newList);
        showKeyboard(newList);

        newList.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press

                    String newListName = newList.getText().toString();
                    layout.removeView(newList);

                    if(validateListName(newListName))
                        createList(newListName, new ArrayList<String>());

                    setCanAddList(true);
                    return true;
                }
                return false;
            }
        });

        newList.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.d(TAG, "Focus Changed");

                if(hasFocus) {
                    Log.d(TAG, "newList has focus");
                    showKeyboard(v);
                }
                else {
                    hideKeyboard(v);
                }
            }
        });
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
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    private void hideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }
}