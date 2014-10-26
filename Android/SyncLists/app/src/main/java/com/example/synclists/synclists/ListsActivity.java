package com.example.synclists.synclists;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
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

    private ExpandableListAdapter mListAdapter;
    private ExpandableListView mExpandableListView;
    private List<String> mListDataHeader;
    private HashMap<String, List<String>> mListDataChild;
    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_lists_lists);

        mExpandableListView = (ExpandableListView) findViewById(R.id.lists);
        mButton = (Button) findViewById(R.id.addListButton);
        mListDataHeader = new ArrayList<String>();
        mListDataChild = new HashMap<String, List<String>>();


        populateLists();

        mListAdapter = new ExpandableListAdapter(this, mListDataHeader, mListDataChild);

        // setting list adapter
        mExpandableListView.setAdapter(mListAdapter);
    }

    private void createList(String name, List<String> tasks) {
        mListDataHeader.add(name);
        mListDataChild.put(name, tasks);
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

    public void addList(View view) {
        mButton.setEnabled(false);

        final LinearLayout layout = (LinearLayout) findViewById(R.id.lists_layout);
        final EditText newList = new EditText(this);
        newList.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        // set focus on newList
        setEditTextFocus(newList, true);

        layout.addView(newList);

        newList.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    String newListName = newList.getText().toString();
                    layout.removeView(newList);
                    createList(newListName, new ArrayList<String>());
                    mButton.setEnabled(true);
                    return true;
                }
                return false;
            }
        });


    }

    private void setEditTextFocus(EditText et, boolean isFocused){
        et.setCursorVisible(isFocused);
        et.setFocusable(isFocused);
        et.setFocusableInTouchMode(isFocused);

        if (isFocused){
            et.requestFocus();
        }
    }
}