package com.and.ibrahim.teleprompter.modules.CustomView;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

public class AutoScrollingTextView extends AppCompatTextView {
    public AutoScrollingTextView(Context context, AttributeSet attrs,
                                 int defStyle) {
        super(context, attrs, defStyle);
    }

    public AutoScrollingTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoScrollingTextView(Context context) {
        super(context);
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction,
                                  Rect previouslyFocusedRect) {
        if (focused) {
            super.onFocusChanged(true, direction, previouslyFocusedRect);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean focused) {
        if (focused) {
            super.onWindowFocusChanged(true);
        }
    }

    @Override
    public boolean isFocused() {
        return true;
    }
}