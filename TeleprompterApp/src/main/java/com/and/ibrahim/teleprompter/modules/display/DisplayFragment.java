package com.and.ibrahim.teleprompter.modules.fragments;

import android.animation.AnimatorSet;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.text.Selection;
import android.text.SpannableString;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.and.ibrahim.teleprompter.R;
import com.and.ibrahim.teleprompter.data.Contract;
import com.and.ibrahim.teleprompter.modules.CustomView.AutoScrollingTextView;
import com.and.ibrahim.teleprompter.modules.CustomView.AutomaticScrollTextView;
import com.and.ibrahim.teleprompter.modules.CustomView.SlideShowScrollView;
import com.and.ibrahim.teleprompter.modules.ScrollingTextView;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.OVER_SCROLL_IF_CONTENT_SCROLLS;

public class DisplayFragment extends Fragment implements View.OnClickListener {


    private static final int SCROLL_START_DELAY_MILLIS = 6000;


    @BindView(R.id.text_sroolling)
    protected ScrollingTextView mScrollText;
    @BindView(R.id.slide_show_scroll)
    protected ScrollView mSlideShowScroll;
    @BindView(R.id.play_scroll)
    protected ImageView mPlay;
    @BindView(R.id.lin_edit_container)
    protected LinearLayout mLinearLayoutContainer;
    @BindView(R.id.seek_speed_up)
    protected SeekBar mSeekToSpeed;
    @BindView(R.id.img_font_size)
    protected ImageView mChangFontSize;


    //////////////////////////////////////////////////////////////////////////////
    private Runnable count;
    private int countdown;
    private Handler customHandler;
    private boolean delayDone;
    private boolean killToast;
    private Runnable scroll;
    private int scrollSpeed;
    private boolean scrollText;
    private boolean showCountdown;
    private int time;
    private int timer;
    private Toast toast;
    private String mScroollString;
    private boolean paused = true;
    private int animationDelayMillis;
    private int scrollOffset;

    private int[] mScrollongPosition;

    public DisplayFragment() {
        scroll = new Runnable() {
            @Override
            public void run() {
                if (scrollText) {
                    if (timer > 0) {
                        timer = timer - 1;
                    } else {
                        mSlideShowScroll.scrollTo(0, mSlideShowScroll.getScrollY() + 1);
                        timer = time;
                    }
                    customHandler.post(scroll);
                }
            }
        };
        count = new Runnable() {
            @Override
            public void run() {
                if (showCountdown) {
                    if (killToast) {
                        toast.cancel();
                    }
                    if (countdown > 0) {
                        toast = Toast.makeText(getActivity(), String.valueOf(countdown) + "...", Toast.LENGTH_SHORT);
                        toast.show();
                        customHandler.postDelayed(this, 1000);
                        countdown = countdown - 1;
                    } else {
                        if (killToast) {
                            toast.cancel();
                        }
                        delayDone = true;
                        scrollText = true;
                        customHandler.post(scroll);
                    }
                    killToast = true;
                } else if (countdown > 0) {
                    customHandler.postDelayed(this, 1000);
                    countdown = countdown - 1;
                } else {
                    delayDone = true;
                    scrollText = true;
                    customHandler.post(scroll);
                }
            }
        };
    }
/////////////////////////////////////////////////////////////////////////////

    private void readBundle(Bundle bundle) {

        if (bundle != null && bundle.containsKey(Contract.EXTRA_TEXT)) {
            mScroollString = bundle.getString(Contract.EXTRA_TEXT);
        }

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.display_fragment, container, false);

        ButterKnife.bind(this, view);
        Bundle extras = this.getArguments();
        if (extras != null) {
            readBundle(extras);
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        scrollSpeed = 10;


        // ScrollingTextView.mirror = prefs.getBoolean("pref_mirror", true);
        // mScrollText.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/" + prefs.getString("pref_font", "Roboto") + ".ttf"), 1);
        // mScrollText.setTextSize((float) (25));

        setSlideShowFontSize(1);
        mScrollText.setText(mScroollString);
        time = this.scrollSpeed * 100;
        scrollText = false;
        killToast = false;
        delayDone = true;


        mSlideShowScroll.getChildAt(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tap();
                mPlay.setVisibility(View.VISIBLE);

                if(paused) {
                    mPlay.setBackground(getActivity().getDrawable(R.drawable.ic_pause_circle_filled));
                    paused =false;
                }
                else {
                    mPlay.setBackground(getActivity().getDrawable(R.drawable.ic_play_circle_filled));
                    paused =true;
                }

                mPlay.postDelayed(new Runnable() {
                    public void run() {
                        mPlay.setVisibility(View.INVISIBLE);
                    }
                }, 2000);


            }

        });
        customHandler = new Handler();

        customHandler.postDelayed(this.scroll, SCROLL_START_DELAY_MILLIS);

        return view;

    }


    private void tap() {
        if (this.delayDone) {
            scrollText = !scrollText;
            customHandler.post(scroll);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (toast != null) {
            toast.cancel();
        }
        if (customHandler != null) {
            customHandler.removeCallbacksAndMessages(null);
        }
        scrollText = false;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            mScrollongPosition = savedInstanceState.getIntArray(Contract.EXTRA_SCROLL_POSITION);
            if (mScrollongPosition != null)
                mSlideShowScroll.post(new Runnable() {
                    public void run() {
                        mSlideShowScroll.scrollTo(mScrollongPosition[0], mScrollongPosition[1]);
                    }
                });
        }
        mSeekToSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {

                // t1.setTextSize(progress);
                time = 100*-progress;

                if(progress<20){
                    time = 130;

                }else if(progress>20&&progress<40){
                    time = 100;

                }
                else if(progress>40&&progress<60){
                    time = 70;

                }
                else if(progress>60&&progress<80){
                    time = 50;

                }
                else  if(progress>80&&progress<100){
                    time = 30;

                }

                Log.d("seekBarProgress",String.valueOf(progress));

            }
        });

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntArray(Contract.EXTRA_SCROLL_POSITION,
                new int[]{mSlideShowScroll.getScrollX(), mSlideShowScroll.getScrollY()});
    }

    @Override
    public void onClick(View view) {

        int id=view.getId();
        if (id == R.id.play_scroll) {


        }
    }

    private void setAnimationSpeed(int scrollSpeed){

        switch (scrollSpeed){
            case 0:
                animationDelayMillis = 25;
                scrollOffset = 1;
                break;
            case 1:
                animationDelayMillis = 30;
                scrollOffset = 2;
                break;

            case 2:
                animationDelayMillis = 30;
                scrollOffset = 3;
                break;

            case 3:
                animationDelayMillis = 25;
                scrollOffset = 3;
                break;

            case 4:
                animationDelayMillis = 30;
                scrollOffset = 4;
                break;
        }

    }

    private void setSlideShowFontSize(int fontSize) {

        int size = 16;
        switch (fontSize) {


            case 0:
                size = R.integer.font_size_small_sp;
                break;

            case 1:
                size = R.integer.font_size_medium_sp;
                break;

            case 2:
                size = R.integer.font_size_large_sp;
                break;

            case 3:
                size = R.integer.font_size_xlarge_sp;
                break;

        }

        mScrollText.setTextSize(TypedValue.COMPLEX_UNIT_SP, getResources().getInteger(size));

    }



}
