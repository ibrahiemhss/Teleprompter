package com.and.ibrahim.teleprompter.modules.CustomView;

import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.OverScroller;

public class FixedSpeedScroller extends OverScroller {

    private int mDuration = 2000;

    public FixedSpeedScroller(Context context) {
        super(context);
    }

    public FixedSpeedScroller(Context context, Interpolator interpolator) {
        super(context, interpolator);
    }


    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        // Ignore received duration, use fixed one instead
        super.startScroll(startX, startY, dx, dy, mDuration);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy) {
        // Ignore received duration, use fixed one instead
        super.startScroll(startX, startY, dx, dy, mDuration);
    }

    public void setFixedDuration(int duration) {
        this.mDuration = duration;
    }
}