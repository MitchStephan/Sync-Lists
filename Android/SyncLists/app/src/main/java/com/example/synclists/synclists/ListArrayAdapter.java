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

import org.json.JSONObject;

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

        //set typeface for button
        Typeface font = Typeface.createFromAsset( mContext.getAssets(), "fontawesome-webfont.ttf" );
        Button button = (Button)row.findViewById( R.id.listDeleteButton );
        button.setTypeface(font);
        holder.listsListDeleteButton = button;
        button.setTag(holder.list);

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
                    validateOnCreate(newListName, position);
                    return true;
                }
                return false;
            }
        });

//        edit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
////                Log.d("Sync Lists", "Focus Changed");
//
//                if (hasFocus) {
//                    Log.d("Sync Lists", "newList has focus");
////                    showKeyboard();
//                }
//                else {
//                    Log.d("Sync Lists", "NO FOCUS");
////                        hideKeyboard();
//                        String newListName = edit.getText().toString();
//                        validateOnCreate(newListName, position);
//                }
//            }
//        });

        return row;
    }


    private void validateOnCreate(String newListName, final int position) {
        hideKeyboard();
        mItems.remove(position);
        if (validName(newListName)) {
            final SyncListsList list = new SyncListsList(-1, newListName);
            mItems.add(position, list);
            notifyDataSetChanged();

            SyncListsApi.createList(new SyncListsRequestAsyncTaskCallback() {
                @Override
                public void onTaskComplete(SyncListsResponse syncListsResponse) {
                    if (syncListsResponse == null) {
                        Toast.makeText(mContext, "Error creating list",
                                Toast.LENGTH_SHORT).show();
                        mItems.remove(position);
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
            }, newListName);
        }
        notifyDataSetChanged();
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

    private void setEditTextFocus(EditText editText, boolean isFocused){
        editText.setCursorVisible(isFocused);
        editText.setFocusable(isFocused);
        editText.setFocusableInTouchMode(isFocused);

        if (isFocused){
            editText.requestFocus();
        }
    }

    public static class ListRowHolder {
        SyncListsList list;
        TextView listsListViewText;
        Button listsListDeleteButton;
        ImageButton listsListViewSettingsButton;
    }
}
