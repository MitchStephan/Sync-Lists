package com.example.synclists.synclists;

import android.app.Activity;
import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

/**
 * Created by SirChickenHair on 10/27/14.
 *
 * Code from https://looksok.wordpress.com/tag/listview-item-with-button/
 *
 */
public class ListArrayAdapter extends ArrayAdapter<SyncListsList> {

    private List<SyncListsList> mItems;
    private int mLayoutResourceId;
    private Context mContext;

    public ListArrayAdapter(Context context, int layoutResourceId, List<SyncListsList> items) {
        super(context, layoutResourceId, items);
        mLayoutResourceId = layoutResourceId;
        mContext = context;
        mItems = items;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        SyncListsList list;
        list = mItems.get(position);

        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();

        if (list.getIsListEdit()){
            return getEditView(position, parent, list, inflater);
        }
        else {
            return getListView(parent, list, inflater);
        }
    }

    private View getListView(ViewGroup parent, SyncListsList list, LayoutInflater inflater) {
        View row = inflater.inflate(R.layout.lists_list_view, parent, false);
        ListRowHolder holder = new ListRowHolder();
        holder.list = list;
        holder.listsListViewSettingsButton = (ImageButton)row.findViewById(R.id.listsListViewSettingsButton);
        holder.listsListViewSettingsButton.setTag(holder.list);

        holder.listsListViewText = (TextView)row.findViewById(R.id.listsListViewText);
        holder.listsListViewText.setText(holder.list.getName());
        holder.listsListViewText.setTag(holder.list);

        row.setTag(holder);
        return row;
    }

    private View getEditView(final int position, ViewGroup parent, SyncListsList list, LayoutInflater inflater) {
        View row = inflater.inflate(R.layout.lists_edit_list_view, parent, false);
        final EditText edit = (EditText)row.findViewById(R.id.listsListEditText);
        edit.setText(list.getName());
        edit.requestFocus();

        edit.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {

                    String newListName = edit.getText().toString();
                    mItems.remove(position);

                    validateOnCreate(newListName, position);
                    return true;
                }
                return false;
            }
        });
        return row;
    }

    private void validateOnCreate(String newListName, int position) {
        if (validName(newListName)) {
            mItems.add(position, new SyncListsList(-1, newListName));
            notifyDataSetChanged();
        }
    }

    private boolean validName(String newListName) {
        return newListName != null && !newListName.equals("") && !newListName.matches("^\\s*$");
    }

    public static class ListRowHolder {
        SyncListsList list;
        TextView listsListViewText;
        ImageButton listsListViewSettingsButton;
    }
}
