package com.and.ibrahim.teleprompter.modules.fragments;

import android.animation.AnimatorSet;
import android.app.ActionBar;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.text.Selection;
import android.text.SpannableString;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.TypedValue;
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
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.and.ibrahim.teleprompter.R;
import com.and.ibrahim.teleprompter.modules.CustomView.AutoScrollingTextView;
import com.and.ibrahim.teleprompter.modules.CustomView.AutomaticScrollTextView;
import com.and.ibrahim.teleprompter.modules.CustomView.SlideShowScrollView;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.OVER_SCROLL_IF_CONTENT_SCROLLS;

public class DisplayFragment extends Fragment implements View.OnClickListener , SlideShowScrollView.ScrollViewListener{


    private static final String TAG = "DisplayFragment";

    private Handler animationHandler;
    private AnimationRunnable animationRunnable;
    boolean isFirstOpen;

    @BindView(R.id.scroll_txt)
    protected TextView mScrollText;
    @BindView(R.id.slide_show_scroll)
    SlideShowScrollView mScrollViewScrollTextView;
    @BindView(R.id.slide_show_pause)
    Button pauseButton;
    @BindView(R.id.ui_control_container)
    LinearLayout scrollContainer;

    @BindView(R.id.slide_show_play)
    Button playButton;
    @BindView(R.id.countdown_text)
    TextView countDownText;
    @BindView(R.id.countdown_view)
    FrameLayout countDownView;

    private int animationDelayMillis;
    private int scrollOffset;
    private AnimatorSet animators;

    private boolean isPlaying;
    String testString;

//////////////////////////////////////////////////////////////////////////////

    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
    private static final int SCROLL_START_DELAY_MILLIS = 6000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;

    public static final String INTENT_PARCELABLE_EXTRA_KEY = "parcel-data-key";

    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
          /*  mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);*/
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
          /*  ActionBar actionBar =getActivity(). getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }*/
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
//                view.performClick();
            }
            return false;
        }
    };


    private final View.OnTouchListener mDelayHideTouchListener2 = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
                animators.start();
//                view.performClick();

            }
            return false;
        }
    };

///////////////////////////////////////////////////////////////////////////////////


    private void readBundle(Bundle bundle) {


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.display_fragment, container, false);

        ButterKnife.bind(this, view);
        Bundle extras = this.getArguments();
        if (extras != null) {
        }

        if(savedInstanceState == null){
            isFirstOpen = true;
        }
        //TeleSpec teleSpec=null;
        testString = getActivity().getResources().getString(R.string.mytest);


        mControlsView = view.findViewById(R.id.fullscreen_content_controls);


        // Set up the user interaction to manually show or hide the system UI.
        mScrollText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show();
            }
        });


        scrollContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                show();
                return true;
            }
        });

        view.findViewById(R.id.slide_show_play).setOnTouchListener(mDelayHideTouchListener);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startScrollAnimation();
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopScrollAnimation();
            }
        });

        mScrollViewScrollTextView.setScrollViewListener(this);
        setAnimationSpeed(10000);



        mScrollText.setTextSize((float) 33.00);
        mScrollText.setText(testString);
       // startCountdown();
        return view;

        }





    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {


            }
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startScrollAnimation();
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopScrollAnimation();
            }
        });

        mScrollViewScrollTextView.setScrollViewListener(this);
    }



    @Override
    public void onClick(View view) {

    }


    @Override
    public void onScrollChanged(SlideShowScrollView scrollView, int x, int y, int oldx, int oldy) {
        View view = (View) scrollView.getChildAt(scrollView.getChildCount() - 1);
        int diff = (view.getBottom() - (scrollView.getHeight() + scrollView.getScrollY()));
        if(diff <=40) {
//            Log.d("blah","scroll bottom reached");
            if(animationHandler!=null) {
                stopScrollAnimation();
            }
        }
    }

    private void hide() {
        // Hide UI first
        /*ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }*/
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
       mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }


    private void show() {
        // Show the system bar
      //  mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
       //         | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
    private void startCountdown(){
        showCountDownView();
        new Handler().postDelayed(new CountdownRunnable(),2000);
    }


    private void startScrollAnimation(){
//        scrollView.fullScroll(ScrollView.FOCUS_UP);
        int y = mScrollViewScrollTextView.getScrollY();
        mScrollViewScrollTextView.setScrollable(false);
        animationHandler = new Handler();
        animationRunnable = new AnimationRunnable(y);
        animationHandler.postDelayed(animationRunnable,SCROLL_START_DELAY_MILLIS);
        showPauseButton();



    }

    private void stopScrollAnimation(){
        animationHandler.removeCallbacks(animationRunnable);
        mScrollViewScrollTextView.setScrollable(true);
        showPlayButton();
    }
    private class AnimationRunnable implements Runnable {
        private int scrollTo;

        AnimationRunnable(int to){
            scrollTo = to;
        }

        @Override
        public void run() {
            mScrollViewScrollTextView.smoothScrollTo(0,scrollTo);
            animationHandler = new Handler();
            animationRunnable = new AnimationRunnable(scrollTo+scrollOffset);
            animationHandler.postDelayed(animationRunnable,animationDelayMillis);
        }
    }


    private class CountdownRunnable implements Runnable{


        @Override
        public void run() {
            int count = Integer.parseInt(countDownText.getText().toString());
            if(count>1){
                count--;
                countDownText.setText(Integer.toString(count));
                new Handler().postDelayed(new CountdownRunnable(),1000);
            }else {
            //    countDownText.setText(getString(R.string.mytest));
                hideCountDownView();
                startScrollAnimation();
            }


        }
    }

    private void showCountDownView(){
        countDownView.setVisibility(View.VISIBLE);
    }

    private void hideCountDownView(){
        countDownView.setVisibility(View.GONE);

    }

    private void showPauseButton(){
        playButton.setVisibility(View.GONE);
        pauseButton.setVisibility(View.VISIBLE);
        isPlaying = true;
    }

    private void showPlayButton(){
        pauseButton.setVisibility(View.GONE);
        playButton.setVisibility(View.VISIBLE);
        isPlaying = false;
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



    private void setSlideShowFontSize(int fontSize){

        int size = 16;
        switch (fontSize){


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

        mScrollText.setTextSize(TypedValue.COMPLEX_UNIT_SP,getResources().getInteger(size));

    }
}
