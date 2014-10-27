package com.example.synclists.synclists;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ListRowHolder holder = null;

        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        row = inflater.inflate(mLayoutResourceId, parent, false);

        holder = new ListRowHolder();
        holder.list = mItems.get(position);
        holder.listsListViewSettingsButton = (ImageButton)row.findViewById(R.id.listsListViewSettingsButton);
        holder.listsListViewSettingsButton.setTag(holder.list);

        holder.listsListViewText = (TextView)row.findViewById(R.id.listsListViewText);
        holder.listsListViewText.setText(holder.list.getName());

        row.setTag(holder);

        return row;
    }

    public static class ListRowHolder {
        SyncListsList list;
        TextView listsListViewText;
        ImageButton listsListViewSettingsButton;
    }
}
