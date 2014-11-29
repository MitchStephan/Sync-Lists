package com.example.synclists.synclists;

/**
 * Created by SirChickenHair on 11/28/14.
 */

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

/**
 * Created by SirChickenHair on 10/27/14.
 *
 * Code from https://looksok.wordpress.com/tag/listview-item-with-button/
 *
 */
public class SettingsArrayAdapter extends ArrayAdapter<String> {

    private List<String> mItems;
    private int mLayoutResourceId;
    private Context mContext;
    private String mSharing;
    private boolean mSharingDefault;

    public SettingsArrayAdapter(Context context, int layoutResourceId, List<String> items, String sharing, boolean sharingDefault) {
        super(context, layoutResourceId, items);
        mLayoutResourceId = layoutResourceId;
        mContext = context;
        mItems = items;
        mSharing = sharing;
        mSharingDefault = sharingDefault;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        View view;

        String setting = getItem(position);

        if(setting.equals(mSharing)) {
            view = inflater.inflate(R.layout.settings_sharing_toggle, parent, false);

            Switch sharingSwitch = (Switch) view.findViewById(R.id.sharing_toggle_button);
            sharingSwitch.setChecked(mSharingDefault);
        }
        else {
            view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            ((TextView) view).setText(getItem(position));
        }

        return view;
    }
}

