package com.and.ibrahim.teleprompter.modules.CustomView;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.and.ibrahim.teleprompter.modules.display.DisplayActivity;

public class AutomaticScrollTextView extends LinearLayout {

    // Context of application
    Context context;
    // TextView
    private TextView mTextField1;

    // Horizontal scroll
    private ScrollView mScrollView1;

    // Animation on start
    private Animation mMoveTextOnStart = null;
    // Out animation
    private Animation mMoveText1TextOut = null;

    // Duration of animation on start
    private int durationStart;
    // Duration of animation
    private int duration;

    // Pain for drawing text
    private Paint mPaint;

    // Text current width
    private float mText1TextWidth;

    /**
     * Control the speed. The lower this value, the faster it will scroll.
     */
    public static final int MS_PER_PX = 80;

    /**
     * Control the pause between the animations. Also, after starting this
     * activity.
     */
    public static final int PAUSE_BETWEEN_ANIMATIONS = 0;
    private boolean mCancelled = false;

    // Layout width
    private int mWidth;
    // Animation thread
    private Runnable mAnimation1StartRunnable;

    public AutomaticScrollTextView(Context context) {
        super(context);
        init(context);
        this.context = context;
    }

    public AutomaticScrollTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        this.context = context;
    }

    private void init(Context context) {
        initView(context);

        // init helper
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(1);
        mPaint.setStrokeCap(Paint.Cap.ROUND);

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        mWidth = getMeasuredWidth();

        // Calculate
        prepare();

        // Setup
        setupText1Marquee();

    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        super.setOnClickListener(l);

        mTextField1.setOnClickListener(l);
    }

    // Method to finally start the marquee.
    public void startMarquee() {
        prepare();
        prepareTextFields();

        startTextField1Animation();

        mCancelled = false;
    }

    private void startTextField1Animation() {
        mAnimation1StartRunnable = new Runnable() {
            public void run() {
                mTextField1.setVisibility(View.VISIBLE);
                mTextField1.startAnimation(mMoveTextOnStart);
            }
        };
        postDelayed(mAnimation1StartRunnable, PAUSE_BETWEEN_ANIMATIONS);
    }

    public void reset() {

        mCancelled = true;

        if (mAnimation1StartRunnable != null) {
            removeCallbacks(mAnimation1StartRunnable);
        }

        mTextField1.clearAnimation();

        prepareTextFields();

        mMoveTextOnStart.reset();
        mMoveText1TextOut.reset();

        mScrollView1.removeView(mTextField1);
        mScrollView1.addView(mTextField1);

        mTextField1.setEllipsize(TextUtils.TruncateAt.END);

        invalidate();
    }

    public void prepareTextFields() {
        mTextField1.setEllipsize(TextUtils.TruncateAt.END);
        mTextField1.setVisibility(View.INVISIBLE);
        expandTextView(mTextField1);
    }

    private void setupText1Marquee() {

        // Calculate duration of animations
        durationStart = (int) ((mWidth + mText1TextWidth) * MS_PER_PX);
        duration = (int) (2 * mWidth * MS_PER_PX);

        // On start animation
        mMoveTextOnStart = new TranslateAnimation(0, -mWidth - mText1TextWidth,
                0, 0);

        mMoveTextOnStart.setDuration(durationStart);
        mMoveTextOnStart.setInterpolator(new LinearInterpolator());
        mMoveTextOnStart.setFillAfter(true);

        // Main scrolling animation
        mMoveText1TextOut = new TranslateAnimation(mWidth, -mWidth
                - mText1TextWidth, 0, 0);

        mMoveText1TextOut.setDuration(duration);
        mMoveText1TextOut.setInterpolator(new LinearInterpolator());
        mMoveText1TextOut.setFillAfter(true);
        mMoveText1TextOut.setRepeatCount(Animation.INFINITE);

        // Animation listeners
        mMoveTextOnStart
                .setAnimationListener(new Animation.AnimationListener() {
                    public void onAnimationStart(Animation animation) {
                        invalidate();
                        mTextField1.invalidate();

                    }

                    public void onAnimationEnd(Animation animation) {

                        if (mCancelled) {
                            return;
                        }

                        mTextField1.startAnimation(mMoveText1TextOut);

                    }

                    public void onAnimationRepeat(Animation animation) {
                        invalidate();
                        mTextField1.invalidate();
                    }
                });

        mMoveText1TextOut
                .setAnimationListener(new Animation.AnimationListener() {
                    public void onAnimationStart(Animation animation) {
                        invalidate();
                        mTextField1.invalidate();

                    }

                    public void onAnimationEnd(Animation animation) {

                        if (mCancelled) {
                            return;
                        }

                    }

                    public void onAnimationRepeat(Animation animation) {
                        invalidate();
                        mTextField1.invalidate();
                    }
                });

    }

    private void prepare() {

        // Measure
        mPaint.setTextSize(mTextField1.getTextSize());
        mPaint.setTypeface(mTextField1.getTypeface());
        mText1TextWidth = mPaint.measureText(mTextField1.getText().toString());

        setupText1Marquee();

    }

    private void initView(Context context) {
        setOrientation(LinearLayout.VERTICAL);
        setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
                LayoutParams.FILL_PARENT, Gravity.LEFT));
        setPadding(0, 0, 0, 0);

        // Scroll View 1
        LayoutParams sv1lp = new LayoutParams(LayoutParams.FILL_PARENT,
                LayoutParams.WRAP_CONTENT);
        sv1lp.gravity = Gravity.CENTER_HORIZONTAL;
        mScrollView1 = new ScrollView(context);

        // Scroll View 1 - Text Field
        mTextField1 = new TextView(context);
        mTextField1.setSingleLine(true);
        mTextField1.setEllipsize(TextUtils.TruncateAt.END);
        mTextField1.setTypeface(null, Typeface.BOLD);

        mScrollView1.addView(mTextField1, new ScrollView.LayoutParams(
                mTextField1.getWidth(), LayoutParams.WRAP_CONTENT));

        addView(mScrollView1, sv1lp);
    }

    public void setText1(String text) {

        String temp = "";
        if (text.length() < 10) {
            temp = "         " + text + "         ";
        } else {
            temp = text;
        }
        mTextField1.setText(temp);

    }

    public void setTextSize1(int textSize) {
        mTextField1.setTextSize(textSize);
    }

    public void setTextColor1(int textColor) {

        mTextField1.setTextColor(textColor);
    }

    private void expandTextView(TextView textView) {
        ViewGroup.LayoutParams lp = textView.getLayoutParams();
       // lp.width = DisplayActivity.getScreenWidth();
        textView.setLayoutParams(lp);
    }
}