package com.and.ibrahim.teleprompter.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;

import com.and.ibrahim.teleprompter.R;

public class LinedEditText extends android.support.v7.widget.AppCompatEditText {
    private final Rect mRect;
    private final Paint mPaint;

    // we need this constructor for LayoutInflater
    public LinedEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

        mRect = new Rect();
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setColor(context.getResources().getColor(R.color.colorPrimaryLight));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //int count = getLineCount();

        int height = getHeight();
        int line_height = getLineHeight();

        int count = height / line_height;

        if (getLineCount() > count)
            count = getLineCount();//for long text with scrolling

        Rect r = mRect;
        int baseline = getLineBounds(0, r);//first line

        for (int i = 0; i < count; i++) {

            canvas.drawLine(r.left, baseline + 1, r.right, baseline + 1, mPaint);
            baseline += getLineHeight();//next line
        }

        super.onDraw(canvas);
    }
}
