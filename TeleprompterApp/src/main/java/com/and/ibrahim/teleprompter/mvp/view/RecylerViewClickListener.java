package com.and.ibrahim.teleprompter.mvp.view;

import android.view.View;

public interface RecylerViewClickListener {
    void onClick(View view, int position);

    void onLongClick(View view, int position);
}

