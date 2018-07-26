package com.and.ibrahim.teleprompter.modules.display;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.and.ibrahim.teleprompter.R;
import com.and.ibrahim.teleprompter.data.Contract;
import com.and.ibrahim.teleprompter.modules.ScrollingTextView;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TestFragmet extends Fragment implements View.OnClickListener {

    private LinearLayout  verticalOuterLayout;
    private ScrollView verticalScrollview;
    private TextView verticalTextView;
    private int verticalScrollMax;
    private Timer scrollTimer		=	null;
    private TimerTask clickSchedule;
    private TimerTask scrollerSchedule;
    private TimerTask faceAnimationSchedule;
    private int scrollPos =	0;
    private Boolean isFaceDown      =   true;
    private Timer clickTimer		=	null;
    private Timer faceTimer         =   null;
    private Button clickedButton	=	null;
    private String[] nameArray = {"Apple", "Banana", "Grapes", "Orange", "Strawberry","Apple", "Banana","Grapes"};
    private String[] imageNameArray = {"apple", "banana", "grapes", "orange", "strawberry","apple", "banana","grapes"};
/////////////////////////////////////////////////////////////////////////////

    private void readBundle(Bundle bundle) {

        if (bundle != null && bundle.containsKey(Contract.EXTRA_TEXT)) {
          //  mScroollString = bundle.getString(Contract.EXTRA_TEXT);
        }

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.test, container, false);

        Bundle extras = this.getArguments();
        if (extras != null) {
            readBundle(extras);
        }
        verticalScrollview  =   (ScrollView) view.findViewById(R.id.vertical_scrollview_id);
        verticalOuterLayout =	(LinearLayout)view.findViewById(R.id.vertical_outer_layout_id);
        verticalTextView    = (TextView)view.findViewById(R.id.vertical_textview_id);
        addImagesToView();

        ViewTreeObserver vto 		=	verticalOuterLayout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                verticalOuterLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                getScrollMaxAmount();
                startAutoScrolling();
            }
        });

        return view;

    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
         //   mScrollongPosition = savedInstanceState.getIntArray(Contract.EXTRA_SCROLL_POSITION);
          //  if (mScrollongPosition != null)

        }

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
       // outState.putIntArray(Contract.EXTRA_SCROLL_POSITION,
               // new int[]{mSlideShowScroll.getScrollX(), mSlideShowScroll.getScrollY()});
    }

    @Override
    public void onClick(View view) {

    }

    public void addImagesToView(){
        for (int i=0;i<imageNameArray.length;i++){
            final Button imageButton =	new Button(getActivity());
           // int imageResourceId		 =	getResources().getIdentifier(imageNameArray[i], "drawable",getActivity().getPackageName());
          //  Drawable image 			 =	this.getResources().getDrawable(imageResourceId);
           // imageButton.setBackgroundDrawable(image);
            imageButton.setTag(i);
            imageButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View arg0) {
                    if(isFaceDown){
                        if(clickTimer!= null){
                            clickTimer.cancel();
                            clickTimer			=	null;
                        }
                        clickedButton			=	(Button)arg0;
                        stopAutoScrolling();
                        clickedButton.startAnimation(scaleFaceUpAnimation());
                        clickedButton.setSelected(true);
                        clickTimer				=	new Timer();

                        if(clickSchedule != null) {
                            clickSchedule.cancel();
                            clickSchedule = null;
                        }

                        clickSchedule = new TimerTask(){
                            public void run() {
                                startAutoScrolling();
                            }
                        };

                        clickTimer.schedule( clickSchedule, 1500);
                    }
                }
            });

            LinearLayout.LayoutParams params 	=	new LinearLayout.LayoutParams(256,256);
            imageButton.setLayoutParams(params);
            verticalOuterLayout.addView(imageButton);
        }
    }

    public void getScrollMaxAmount(){
        int actualWidth = (verticalOuterLayout.getMeasuredHeight()-(256*3));
        verticalScrollMax   = actualWidth;
    }

    public void startAutoScrolling(){
        if (scrollTimer == null) {
            scrollTimer					=	new Timer();
            final Runnable Timer_Tick 	= 	new Runnable() {
                public void run() {
                    moveScrollView();
                }
            };

            if(scrollerSchedule != null){
                scrollerSchedule.cancel();
                scrollerSchedule = null;
            }
            scrollerSchedule = new TimerTask(){
                @Override
                public void run(){
                    getActivity().runOnUiThread(Timer_Tick);
                }
            };

            scrollTimer.schedule(scrollerSchedule, 30, 30);
        }
    }

    public void moveScrollView(){
        scrollPos							= 	(int) (verticalScrollview.getScrollY() + 1.0);
        if(scrollPos >= verticalScrollMax){
            scrollPos						=	0;
        }
        verticalScrollview.scrollTo(0,scrollPos);
        Log.e("moveScrollView","moveScrollView");
    }

    public void stopAutoScrolling(){
        if (scrollTimer != null) {
            scrollTimer.cancel();
            scrollTimer	=	null;
        }
    }

    public Animation scaleFaceUpAnimation(){
        Animation scaleFace = new ScaleAnimation(1.0f, 1.2f, 1.0f, 1.2f, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        scaleFace.setDuration(500);
        scaleFace.setFillAfter(true);
        scaleFace.setInterpolator(new AccelerateInterpolator());
        Animation.AnimationListener	scaleFaceAnimationListener = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation arg0) {
                verticalTextView.setText(nameArray[(Integer) clickedButton.getTag()]);
                isFaceDown = false;
            }
            @Override
            public void onAnimationRepeat(Animation arg0) {}
            @Override
            public void onAnimationEnd(Animation arg0) {
                if(faceTimer != null){
                    faceTimer.cancel();
                    faceTimer = null;
                }

                faceTimer = new Timer();
                if(faceAnimationSchedule != null){
                    faceAnimationSchedule.cancel();
                    faceAnimationSchedule = null;
                }
                faceAnimationSchedule = new TimerTask() {
                    @Override
                    public void run() {
                        faceScaleHandler.sendEmptyMessage(0);
                    }
                };

                faceTimer.schedule(faceAnimationSchedule, 750);
            }
        };
        scaleFace.setAnimationListener(scaleFaceAnimationListener);
        return scaleFace;
    }

    private Handler faceScaleHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(clickedButton.isSelected() == true)
                clickedButton.startAnimation(scaleFaceDownAnimation(2000));
        }
    };

    public Animation scaleFaceDownAnimation(int duration){
        Animation scaleFace = new ScaleAnimation(1.2f, 1.0f, 1.2f, 1.0f, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        scaleFace.setDuration(duration);
        scaleFace.setFillAfter(true);
        scaleFace.setInterpolator(new AccelerateInterpolator());
        Animation.AnimationListener	scaleFaceAnimationListener = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation arg0) {}
            @Override
            public void onAnimationRepeat(Animation arg0) {}
            @Override
            public void onAnimationEnd(Animation arg0) {
                verticalTextView.setText("");
                isFaceDown = true;
            }
        };
        scaleFace.setAnimationListener(scaleFaceAnimationListener);
        return scaleFace;
    }



    public void onDestroy(){
        clearTimerTaks(clickSchedule);
        clearTimerTaks(scrollerSchedule);
        clearTimerTaks(faceAnimationSchedule);
        clearTimers(scrollTimer);
        clearTimers(clickTimer);
        clearTimers(faceTimer);

        clickSchedule         = null;
        scrollerSchedule      = null;
        faceAnimationSchedule = null;
        scrollTimer           = null;
        clickTimer            = null;
        faceTimer             = null;

        super.onDestroy();
    }

    private void clearTimers(Timer timer){
        if(timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void clearTimerTaks(TimerTask timerTask){
        if(timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
    }


}
