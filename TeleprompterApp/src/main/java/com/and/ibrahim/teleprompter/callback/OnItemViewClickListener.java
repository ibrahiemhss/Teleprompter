package com.and.ibrahim.teleprompter.callback;

import android.view.View;

@SuppressWarnings("unused")
public interface OnItemViewClickListener {

    void onEditImgClickListener(int position, View v);

    void onTextClickListener(int position, View v);

    void onImageClickListener(int position, View v);

    void onViewGroupClickListener(int adapterPosition, View view);

    void onItemCheckListener(int pos, View item);

    void onItemUncheckListener(int pos, View item);
}
