package com.and.ibrahim.teleprompter.util;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
public class ScrollingTextView extends AppCompatTextView {
    @SuppressWarnings("unused")
    private static boolean mirror;

    public ScrollingTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void onDraw(Canvas canvas) {
        if (mirror) {
            canvas.translate((float) getWidth(), 0.0f);
            canvas.scale(-1.0f, 1.0f);
        }
        super.onDraw(canvas);
    }
}
