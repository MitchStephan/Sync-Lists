package com.example.synclists.synclists;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.undo.UndoAdapter;
import com.nhaarman.listviewanimations.util.Insertable;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by SirChickenHair on 10/27/14.
 *
 * Code from https://looksok.wordpress.com/tag/listview-item-with-button/
 *
 */
public class ListArrayAdapter extends ArrayAdapter<SyncListsList> implements UndoAdapter {

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
        SyncListsList list = getItem(position);

        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();

        if (list.getIsListEdit()){
            return getEditView(position, parent, list, inflater);
        }
        else {
            return getListView(parent, list, inflater, convertView);
        }
    }

    private View getListView(ViewGroup parent, SyncListsList list, LayoutInflater inflater, View convertView) {
        View row = convertView;
        ListRowHolder holder;

        if(row == null) {
            row = inflater.inflate(R.layout.lists_list_view, parent, false);
            holder = new ListRowHolder();

            holder.listsListViewEditButton = (Button) row.findViewById(R.id.editList);
            holder.listsListViewEditButton.setTypeface(Typefaces.get(mContext));

            holder.listsListViewSettingsButton = (ImageButton) row.findViewById(R.id.listsListViewSettingsButton);
            holder.listsListViewText = (TextView) row.findViewById(R.id.listsListViewText);

            row.setTag(holder);
        }
        else {
            holder = (ListRowHolder) row.getTag();
        }

        holder.list = list;
        holder.listsListViewEditButton.setTag(list);
        holder.listsListViewSettingsButton.setTag(list);
        holder.listsListViewText.setText(list.getName());
        holder.listsListViewText.setTag(holder.list);
        return row;
    }

    private View getEditView(final int position, ViewGroup parent, SyncListsList list, LayoutInflater inflater) {
        View row = inflater.inflate(R.layout.lists_edit_list_view, parent, false);

        //set typeface for button
        Button button = (Button)row.findViewById( R.id.listEditDeleteButton );
        button.setTypeface(Typefaces.get(mContext));
        button.setTag(list);

        final EditText edit = (EditText)row.findViewById(R.id.listsListEditText);
        edit.setText(list.getName());
        edit.requestFocus();

        edit.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_ENDCALL ||
                        keyCode == KeyEvent.ACTION_DOWN) {
                    String newListName = edit.getText().toString();
                    validateOnCreate(newListName, position);
                    return true;
                }
                return false;
            }
        });

        return row;
    }

    private void validateOnCreate(String newListName, final int position) {
        hideKeyboard();
        remove(getItem(position));

        if (validName(newListName)) {
            final SyncListsList list = new SyncListsList(-1, newListName);
            insert(list, position);

            SyncListsApi.createList(new SyncListsRequestAsyncTaskCallback() {
                @Override
                public void onTaskComplete(SyncListsResponse syncListsResponse) {
                    if (syncListsResponse == null) {
                        Toast.makeText(mContext, "Error creating list",
                                Toast.LENGTH_SHORT).show();
                        remove(getItem(position));
                    }
                    else {
                        try {
                            JSONObject jsonObject = new JSONObject(syncListsResponse.getBody());
                            list.setId(jsonObject.getInt("pk"));
                        }
                        catch(Exception e) {

                        }
                    }
                }
            }, newListName, mContext);
        }
    }

    private boolean validName(String newListName) {
        return newListName != null && !newListName.equals("") && !newListName.matches("^\\s*$");
    }

    private void showKeyboard() {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);

        if(imm != null){
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);

        if(imm != null){
            imm.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }

    @Override
    public View getUndoView(final int position, final View convertView, final ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.undo_row, parent, false);
        }
        return view;
    }

    @Override
    public View getUndoClickView(final View view) {
        return view.findViewById(R.id.undo_row_undobutton);
    }

    public static class ListRowHolder {
        SyncListsList list;
        TextView listsListViewText;
        Button listsListViewEditButton;
        ImageButton listsListViewSettingsButton;
    }
}
