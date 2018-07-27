package com.and.ibrahim.teleprompter.modules.display;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.and.ibrahim.teleprompter.R;

import java.util.List;

public class CustomDrawerAdapter extends ArrayAdapter<DrawerItem> {

    Context context;
    List<DrawerItem> drawerItemList;
    int layoutResID;

    public CustomDrawerAdapter(Context context, int layoutResourceID, List<DrawerItem> listItems) {
        super(context, layoutResourceID, listItems);
        this.context = context;
        this.drawerItemList = listItems;
        this.layoutResID = layoutResourceID;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        DrawerItemHolder drawerHolder;
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            drawerHolder = new DrawerItemHolder();

            view = inflater.inflate(layoutResID, parent, false);
            drawerHolder.mSetTextSize = view.findViewById(R.id.seek_text_size);
            drawerHolder.mSetSpeedScrolling = view.findViewById(R.id.seek_speed_up);
            drawerHolder.mTextFont = view.findViewById(R.id.text_font);


            view.setTag(drawerHolder);

        } else {
            drawerHolder = (DrawerItemHolder) view.getTag();
        }

        DrawerItem dItem =  this.drawerItemList.get(position);
        drawerHolder.mTextFont.setText(dItem.getTextFont());
        return view;
    }

    private static class DrawerItemHolder {
        private SeekBar mSetTextSize;
        private SeekBar mSetSpeedScrolling;
        private TextView mTextFont;
    }
}