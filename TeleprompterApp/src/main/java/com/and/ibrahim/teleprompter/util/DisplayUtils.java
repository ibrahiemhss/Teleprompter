package com.and.ibrahim.teleprompter.util;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Build;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.annotation.RequiresApi;

import com.and.ibrahim.teleprompter.R;
import com.and.ibrahim.teleprompter.data.SharedPrefManager;
import com.and.ibrahim.teleprompter.mvp.view.OnScrollViewActions;
import com.google.android.material.appbar.AppBarLayout;

import java.util.Timer;
import java.util.TimerTask;

import static com.and.ibrahim.teleprompter.data.Contract.INTERSTITAI_ADS;


public class DisplayUtils {
    private static final String TAG = "DisplayActivity";
    private final OnScrollViewActions mOnScrollViewActionsListener;
    private long lastPause = 0;
    private boolean paused = true;
    private final Context mContext;
    private boolean isUp=true;
    private final boolean isCameraEnable;
    private Timer scrollTimer = null;
    private TimerTask scrollerSchedule;
    private TimerTask clickSchedule;

    int melliSeconds;
    private int mScrollPos;
    private int verticalScrollMax = 0;

    private int mTimeSpeed = 30;

    private final ScrollView mSlideShowScroll;
    private final Chronometer mChronometer;
    private final ImageView mPlayStatus;
    private final AdsUtils mAdUtils;
    private final AppBarLayout mAppBarLayout;

    public void setOnScrollViewActionsListner(int timeSpeed){
            mOnScrollViewActionsListener.action(timeSpeed);

    }
    public DisplayUtils(OnScrollViewActions onScrollViewActionsListener,Context context,
                        ScrollView scrollView, Chronometer chronometer, ImageView imageView, AdsUtils adsUtils, AppBarLayout appBarLayout, int mScrollPos,boolean isCameraEnable) {
        this.mContext=context;
        this.mSlideShowScroll=scrollView;
        this.mChronometer=chronometer;
        this.mPlayStatus=imageView;
        this.mAdUtils=adsUtils;
        this.mAppBarLayout=appBarLayout;
        this.mScrollPos=mScrollPos;
        this.mOnScrollViewActionsListener=onScrollViewActionsListener;
        this.isCameraEnable=isCameraEnable;
    }


    public void scrollViewConfig(){
        Configuration configuration = mContext.getResources().getConfiguration();
        int screenWidthDp = configuration.screenWidthDp; //The current width of the available screen space, in dp units, corresponding to screen width resource qualifier.
        int screenHeightDp = configuration.screenHeightDp; //The smallest screen size an application will see in normal operation, corresponding to smallest screen width resource qualifier.

        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) mContext
                .getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        mSlideShowScroll.getViewTreeObserver()
                .addOnScrollChangedListener(() -> {
                    if (mSlideShowScroll.getChildAt(0).getBottom()
                            <= (mSlideShowScroll.getHeight() + mSlideShowScroll.getScrollY())) {

                        stopAutoScrolling();
                    }
                });
    }
    public void getScrollMaxAmount( LinearLayout verticalOuterLayout) {

        ViewTreeObserver vto = verticalOuterLayout.getViewTreeObserver();


        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                verticalOuterLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                verticalScrollMax = (verticalOuterLayout.getMeasuredHeight() - (256 * 3));
            }
        });

    }


    @SuppressLint("UseCompatLoadingForDrawables")
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void clickToScrolling() {
        //if(!isCameraEnable){
            mSlideShowScroll.getChildAt(0).setOnClickListener(v -> {

                if (paused) {


                    startPlayStatus();
                    paused = false;
                    if (lastPause > 0) {
                        mChronometer.setBase(mChronometer.getBase() + SystemClock.elapsedRealtime() - lastPause);

                        mChronometer.start();


                    } else {
                        mChronometer.setBase(SystemClock.elapsedRealtime());
                        mChronometer.start();
                    }

                    startAutoScrolling(mTimeSpeed);



                } else {
                    if(SharedPrefManager.getInstance(mContext).isCameraEnabled()){
                        mPlayStatus.setVisibility(View.INVISIBLE);

                    }else{
                        mPlayStatus.setVisibility(View.VISIBLE);
                        mPlayStatus.setBackground(mContext.getDrawable(R.drawable.ic_pause_circle_filled));

                        mPlayStatus.setBackground(mContext.getDrawable(R.drawable.ic_play_circle_filled));
                        paused = true;
                    }

                    stopAutoScrolling();


                }


                onSlideUbDowView(mAppBarLayout);


            });
       // }

    }
    public void stopAutoScrolling() {

        if (scrollTimer != null) {
            scrollTimer.cancel();
            scrollTimer = null;

        }
        lastPause = SystemClock.elapsedRealtime();
        if (mChronometer != null) {
            mChronometer.stop();
        }
    }


    public void startAutoScrolling(int time) {


        if (mSlideShowScroll != null) {
            if (scrollTimer == null) {
                scrollTimer = new Timer();
                final Runnable start = () -> {

                    moveScrollView();
                    melliSeconds += 1;
                };
                if (scrollerSchedule != null) {
                    scrollerSchedule.cancel();
                    scrollerSchedule = null;
                }
                scrollerSchedule = new TimerTask() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void run() {
                        ((Activity)mContext).runOnUiThread(start);
                    }
                };

                scrollTimer.schedule(scrollerSchedule, time, time);
                Log.d("speedScrollViw", "\n delay =" + time + "\n period =" + time);

            }
        }

    }

    public void startPlayStatus() {
        Log.d(TAG, " isFullScreenAdShown ="+ SharedPrefManager.getInstance(mContext).isFullScreenAdShown());

      /* if(SharedPrefManager.getInstance(mContext).isCameraEnabled()){
            mPlayStatus.postDelayed(() -> mPlayStatus.setVisibility(View.INVISIBLE), 800);

        }else{*/
            if(!SharedPrefManager.getInstance(mContext).isFullScreenAdShown()){
                SharedPrefManager.getInstance(mContext).setFullScreenAdShown(true);
                mAdUtils.showAdd(INTERSTITAI_ADS);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mPlayStatus.setBackground(mContext.getDrawable(R.drawable.ic_pause_circle_filled));
            }
            mPlayStatus.postDelayed(() -> mPlayStatus.setVisibility(View.INVISIBLE), 800);

      //  }

    }

    public void slideUp(View view) {// slide the view from below itself to the current position
        view.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).start();

    }
    public void slideDown(View view) {    // slide the view from its current position to below itself
        view.animate().translationY(-view.getBottom()).setInterpolator(new AccelerateInterpolator()).start();

    }

    public void onSlideUbDowView(View view) {
        if (isUp) {
            slideDown(view);

        } else {
            slideUp(view);
        }
        isUp = !isUp;
    }

    private void moveScrollView() {

        if (mSlideShowScroll == null) {
            stopAutoScrolling();

        } else {
            if(isCameraEnable){
                mSlideShowScroll.setScrollY(600);
            }

                mScrollPos = (int) (mSlideShowScroll.getScrollY() + 1.0);


            if (mScrollPos >= verticalScrollMax) {
                mScrollPos = 0;
            }

                Log.d(TAG, "moveScrollView to 1 ============ : "+String.valueOf(mScrollPos));


            mSlideShowScroll.scrollTo(0, mScrollPos);

        }

    }


    public void setSpeed(int progress) {//generate suitable value of scrolling by parsing seekbar progress value
        if (progress > 1 && progress <= 10) {
            setOnScrollViewActionsListner(200);
        } else if (progress > 11 && progress <= 20) {
            mTimeSpeed = 150;
            setOnScrollViewActionsListner(150);

        } else if (progress >= 21 && progress <= 30) {
            mTimeSpeed = 100;
            setOnScrollViewActionsListner(100);
        } else if (progress >= 31 && progress <= 40) {
            mTimeSpeed = 50;
            setOnScrollViewActionsListner(50);
        } else if (progress >= 46 && progress <= 50) {
            mTimeSpeed = 40;
            setOnScrollViewActionsListner(40);
        } else if (progress >= 51 && progress <= 55) {
            mTimeSpeed = 30;
            setOnScrollViewActionsListner(30);
        } else if (progress >= 56 && progress <= 60) {
            mTimeSpeed = 20;
            setOnScrollViewActionsListner(20);
        } else if (progress >= 61 && progress <= 65) {
            mTimeSpeed = 10;
            setOnScrollViewActionsListner(10);
        } else if (progress >= 66 && progress <= 69) {
            mTimeSpeed = 15;
            setOnScrollViewActionsListner(10);
        } else if (progress >= 72 && progress < 75) {
            mTimeSpeed = 10;
            setOnScrollViewActionsListner(10);
        } else if (progress >= 78 && progress <= 81) {
            mTimeSpeed = 8;
            setOnScrollViewActionsListner(8);
        } else if (progress > 84 && progress < 87) {
            mTimeSpeed = 5;
            setOnScrollViewActionsListner(5);
        } else if (progress > 90 && progress < 93) {
            mTimeSpeed = 3;
            setOnScrollViewActionsListner(30);
        } else if (progress >= 96 && progress < 99) {
            mTimeSpeed = 1;
            setOnScrollViewActionsListner(1);
        }
    }
    public void dispose(String lifeStatus){
        Log.d(TAG, "display utils dispose = "+lifeStatus+" is shown ="+SharedPrefManager.getInstance(mContext).isFullScreenAdShown());
        clearTimerTaks(clickSchedule);
        clearTimerTaks(scrollerSchedule);
        clearTimers(scrollTimer);
        clickSchedule = null;
        scrollerSchedule = null;
        scrollTimer = null;
    }

    private void clearTimerTaks(TimerTask timerTask) {
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
    }
    private void clearTimers(Timer timer) {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}
