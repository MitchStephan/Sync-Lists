package com.example.synclists.synclists;

import android.app.Activity;
import android.os.Bundle;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_lists_lists);

        mExpandableListView = (ExpandableListView) findViewById(R.id.lists);
        mListDataHeader = new ArrayList<String>();
        mListDataChild = new HashMap<String, List<String>>();

        populateLists();

        mListAdapter = new ExpandableListAdapter(this, mListDataHeader, mListDataChild);

        // setting list adapter
        mExpandableListView.setAdapter(mListAdapter);
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

        mListDataChild.put(mListDataHeader.get(0), list1);
        mListDataChild.put(mListDataHeader.get(1), list2);
    }
}
