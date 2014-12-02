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
public class SharedUsersArrayAdapter extends ArrayAdapter<SyncListsUser>  {
    private List<SyncListsUser> mItems;
    private int mLayoutResourceId;
    private Context mContext;
    private SyncListsList mList;

    public SharedUsersArrayAdapter(Context context, int layoutResourceId, List<SyncListsUser> items, SyncListsList list) {
        super(context, layoutResourceId, items);
        mLayoutResourceId = layoutResourceId;
        mContext = context;
        mItems = items;
        mList = list;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        SyncListsUser user = getItem(position);
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();

        View row = inflater.inflate(mLayoutResourceId, parent, false);

        TextView sharedUserViewText = (TextView) row.findViewById(R.id.sharedUsersListViewText);
        sharedUserViewText.setText(user.getEmail());

        if(mLayoutResourceId == R.layout.shared_user_row) {
            SharedUserRowHolder holder = new SharedUserRowHolder();
            holder.list = mList;

            holder.userUnshareButton = (Button) row.findViewById(R.id.userUnshareButton);
            holder.userUnshareButton.setTypeface(Typefaces.get(mContext));

            holder.sharedUserViewText = sharedUserViewText;

            row.setTag(holder);

            holder.user = user;
            holder.userUnshareButton.setTag(holder);
            holder.sharedUserViewText.setTag(holder.user);
        }

        return row;
    }

    public static class SharedUserRowHolder {
        SyncListsUser user;
        TextView sharedUserViewText;
        Button userUnshareButton;
        SyncListsList list;
    }
}
