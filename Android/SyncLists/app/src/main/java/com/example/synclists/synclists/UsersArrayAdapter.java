package com.example.synclists.synclists;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

/**
 * Created by ethan on 11/29/14.
 */
public class UsersArrayAdapter extends ArrayAdapter<SyncListsUser>  {
    private List<SyncListsUser> mItems;
    private int mLayoutResourceId;
    private Context mContext;

    public UsersArrayAdapter(Context context, int layoutResourceId, List<SyncListsUser> items) {
        super(context, layoutResourceId, items);
        mLayoutResourceId = layoutResourceId;
        mContext = context;
        mItems = items;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        SyncListsUser list = getItem(position);

        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();

//        if (list.getIsListEdit()){
//            return getEditView(position, parent, list, inflater);
//        }
//        else {
            return getListView(parent, list, inflater, convertView);
//        }
    }

    private View getEditView(final int position, ViewGroup parent, final SyncListsUser user, LayoutInflater inflater) {
        View row = inflater.inflate(R.layout.lists_edit_list_view, parent, false);

        //set typeface for button
        Button button = (Button)row.findViewById( R.id.listEditDeleteButton );
        button.setTypeface(Typefaces.get(mContext));
        button.setTag(user);

        final EditText edit = (EditText)row.findViewById(R.id.listsListEditText);
        edit.setText(user.getEmail());
        edit.setTag(user);
        edit.requestFocus();

        edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                user.setEmail(edit.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

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

    private void validateOnCreate(String newUserEmail, final int position) {
        hideKeyboard();
        remove(getItem(position));
        final int positionToAddTo = getCount();

        if (validEmail(newUserEmail)) {
            final SyncListsUser user = new SyncListsUser(-1, newUserEmail, true);
            insert(user, positionToAddTo);

//            SyncListsApi.createList(new SyncListsRequestAsyncTaskCallback() {
//                @Override
//                public void onTaskComplete(SyncListsResponse syncListsResponse) {
//                    if (syncListsResponse == null) {
//                        Toast.makeText(mContext, "Error creating user",
//                                Toast.LENGTH_SHORT).show();
//                        remove(getItem(positionToAddTo));
//                    }
//                    else {
//                        try {
//                            JSONObject jsonObject = new JSONObject(syncListsResponse.getBody());
//                            user.setId(jsonObject.getInt("pk"));
//                        }
//                        catch(Exception e) {
//
//                        }
//                    }
//                }
//            }, newUserEmail, mContext);
        }
    }

    private View getListView(ViewGroup parent, SyncListsUser user, LayoutInflater inflater, View convertView) {
        View row = convertView;
        ListRowHolder holder;

        row = inflater.inflate(R.layout.user_view, parent, false);
        holder = new ListRowHolder();

        holder.userButtonDelete = (Button) row.findViewById(R.id.userDeleteButton);
        holder.userButtonDelete.setTypeface(Typefaces.get(mContext));

        holder.userViewText = (TextView) row.findViewById(R.id.usersListViewText);

        row.setTag(holder);

        holder.user = user;
        holder.userButtonDelete.setTag(user);
        holder.userViewText.setText(user.getEmail());
        holder.userViewText.setTag(holder.user);
        return row;
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

    private boolean validEmail(String newListName) {
        return newListName != null && !newListName.equals("") && !newListName.matches("^\\s*$");
    }

    public static class ListRowHolder {
        SyncListsUser user;
        TextView userViewText;
        Button userButtonDelete;
    }
}
