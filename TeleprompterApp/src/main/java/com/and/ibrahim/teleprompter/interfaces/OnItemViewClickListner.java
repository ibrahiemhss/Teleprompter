package com.and.ibrahim.teleprompter.interfaces;

import android.view.View;
import android.widget.CheckBox;

public interface OnItemViewClickListner {

    void onEditImgClickListner(int position,View v);
    void onTextClickListner(int position,View v);
    void onImageClickListner(int position,View v);

    void onViewGroupClickListner(int adapterPosition, View view);
}
