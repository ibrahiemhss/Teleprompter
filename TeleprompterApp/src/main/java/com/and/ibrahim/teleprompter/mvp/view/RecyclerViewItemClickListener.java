package com.and.ibrahim.teleprompter.mvp.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class RecyclerViewItemClickListener implements RecyclerView.OnItemTouchListener {

    //GestureDetector to detect touch event.
    private final GestureDetector gestureDetector;
    private final RecylerViewClickListener recylerViewClickListener;

    public RecyclerViewItemClickListener(Context context, final RecyclerView recyclerView, final RecylerViewClickListener recylerViewClickListener) {
        this.recylerViewClickListener = recylerViewClickListener;
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                //Find child on x and y position relative to screen
                View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (child != null && recylerViewClickListener != null) {
                    recylerViewClickListener.onLongClick(child, recyclerView.getChildLayoutPosition(child));
                }
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
        //On Touch event
        View child = rv.findChildViewUnder(e.getX(), e.getY());
        if (child != null && recylerViewClickListener != null && gestureDetector.onTouchEvent(e)) {
            recylerViewClickListener.onClick(child, rv.getChildLayoutPosition(child));
        }
        return false;
    }

    @Override
    public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
}