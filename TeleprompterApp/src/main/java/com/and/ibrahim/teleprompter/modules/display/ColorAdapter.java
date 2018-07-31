package com.and.ibrahim.teleprompter.modules.display;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.and.ibrahim.teleprompter.R;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

public class ColorAdapter extends BaseAdapter implements View.OnClickListener{

    Context mContext;
    int[] colorNumberarray;
    // array of color names
    String[] colorNameArray;

    // View lookup cache
    private static class ViewHolder {
        private TextView mTxtColorName;
        private RoundedImageView mImgColorValue;
    }

    public ColorAdapter( Context context) {
        this.mContext=context;
        colorNumberarray = context.getResources().getIntArray(R.array.colors);
        //fill  colorNameArray with values from the colorNameArray Array in strings.xml
        colorNameArray = context.getResources().getStringArray(R.array.colorNames);

    }

    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();
        Object object= getItem(position);
        Colors dataModel=(Colors)object;


    }

    private int lastPosition = -1;

    @Override
    public int getCount() {
        return colorNumberarray.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();


            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.list_item_colors, parent, false);
            viewHolder.mImgColorValue = convertView.findViewById(R.id.image_color);
           // viewHolder.mTxtColorName = (TextView) convertView.findViewById(R.id.text_color);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        //viewHolder.mTxtColorName.setText(colorNameArray[position]);
        viewHolder.mImgColorValue.setBackgroundColor(colorNumberarray[position]);
        // Return the completed view to render on screen
        return convertView;
    }
}
