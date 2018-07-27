package com.and.ibrahim.teleprompter.modules.display;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.AppCompatTextView;
import android.text.Layout;
import android.text.Selection;
import android.text.SpannableString;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.and.ibrahim.teleprompter.R;
import com.and.ibrahim.teleprompter.data.Contract;
import com.and.ibrahim.teleprompter.data.SharedPrefManager;
import com.and.ibrahim.teleprompter.modules.CustomView.AutoScrollingTextView;
import com.and.ibrahim.teleprompter.modules.CustomView.AutomaticScrollTextView;
import com.and.ibrahim.teleprompter.modules.CustomView.SlideShowScrollView;
import com.and.ibrahim.teleprompter.modules.ScrollingTextView;
import com.and.ibrahim.teleprompter.util.LinedEditText;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

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
    @BindView(R.id.vertical_outer_id)
    protected LinearLayout  verticalOuterLayout;
///////////////////////////////////////////
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    Dialog dialog;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    CustomDrawerAdapter adapter;

    List<DrawerItem> dataList;
    private View navHeader;
    private NavigationView navigationView;
    protected SeekBar mSeekScrollSpeed;
    protected SeekBar mSeekTextSize;



    //////////////////////////////////////////////////////////////////////////////
    private Runnable count;
    private Timer scrollTimer		=	null;
    private int scrollPos ;
    private int verticalScrollMax=	0;
    private TimerTask clickSchedule;
    private TimerTask scrollerSchedule;

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
    SeekBar.OnSeekBarChangeListener mSpeedUpSeekBarListener;
    SeekBar.OnSeekBarChangeListener mResizeTextSeekBarListener;
    private int timeSpeed=30;

    private int[] mScrollongPosition;


/////////////////////////////////////////////////////////////////////////////
public void startAutoScrolling(int time){

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

        scrollTimer.schedule(scrollerSchedule, time, time);
        Log.d("speedScrollViw","\n delay ="+String.valueOf(time)+"\n period ="+String.valueOf(time));

    }
}


public void moveScrollView(){
    scrollPos							= 	(int) (mSlideShowScroll.getScrollY() + 1.0);
    if(scrollPos >= verticalScrollMax){
        scrollPos						=	0;
    }
    mSlideShowScroll.scrollTo(0,scrollPos);

    Log.e("moveScrollView","moveScrollView");
}

    public void stopAutoScrolling(){
        if (scrollTimer != null) {
            scrollTimer.cancel();
            scrollTimer	=	null;
        }
    }
    public void getScrollMaxAmount(){
        verticalScrollMax   = (verticalOuterLayout.getMeasuredHeight()-(256*3));
    }



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


        dataList = new ArrayList<DrawerItem>();
        final int seekProgress=SharedPrefManager.getInstance(getActivity()).getPrefIndex();
        setSpeed(seekProgress);
        int tim=timeSpeed;
        mDrawerLayout = (DrawerLayout) view.findViewById(R.id.drawer_layout);
        navigationView = (NavigationView)view. findViewById(R.id.nav_view);

        // Navigation view header

        // ScrollingTextView.mirror = prefs.getBoolean("pref_mirror", true);
        // mScrollText.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/" + prefs.getString("pref_font", "Roboto") + ".ttf"), 1);
        // mScrollText.setTextSize((float) (25));


        ViewTreeObserver vto 		=	verticalOuterLayout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                verticalOuterLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                getScrollMaxAmount();
               // startAutoScrolling();
            }
        });

        mScrollText.setText(mScroollString);

        mSlideShowScroll.getChildAt(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //tap();

                mPlay.setVisibility(View.VISIBLE);

                if(paused) {
                    mPlay.setBackground(getActivity().getDrawable(R.drawable.ic_pause_circle_filled));
                    paused =false;
                    startAutoScrolling(timeSpeed);
                    launchDismissDlg();
                    dialog.show();

                }
                else {
                    mPlay.setBackground(getActivity().getDrawable(R.drawable.ic_play_circle_filled));
                    paused =true;
                    stopAutoScrolling();
                }

                mPlay.postDelayed(new Runnable() {
                    public void run() {
                        mPlay.setVisibility(View.INVISIBLE);
                    }
                }, 2000);


            }

        });


        return view;

    }

   private void    setSpeed(int progress){
       if(progress<=10){
           timeSpeed=200;


       }else if(progress>=11||progress<=20){
           timeSpeed=150;


       }
       else if(progress>=21||progress<=30){
           timeSpeed=100;




       }
       else if(progress>=31||progress<=40){
           timeSpeed=50;

       }
       else  if(progress>=46||progress<=50){
           timeSpeed=45;

       }else if(progress>=51||progress<=55){
           timeSpeed=30;

       }
       else if(progress>=56||progress<=60){
           timeSpeed=25;

       }
       else if(progress>=61||progress<=65){
           timeSpeed=20;

       }
       else  if(progress>=66||progress<=70){
           timeSpeed=15;

       }
       else  if(progress>=71||progress<75){
           timeSpeed=10;

       }else if(progress>=76||progress<=80){
           timeSpeed=8;

       }

       else if(progress>81&&progress<85){
           timeSpeed=5;

       }
       else  if(progress>86&&progress<90){
           timeSpeed=4;

       }else  if(progress>=91||progress<100){
           timeSpeed=3;

       }

   }
    @Override
    public void onPause() {
        super.onPause();

    }


    private void launchDismissDlg() {

    dialog = new Dialog(getActivity());
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        lp.windowAnimations = R.style.SlideDialogAnimation;
        dialog.getWindow().setAttributes(lp);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.custom_drawer_item);
        dialog.setCanceledOnTouchOutside(true);
        mSeekScrollSpeed = (SeekBar) dialog.findViewById(R.id.seek_speed_up);
        mSeekScrollSpeed.setProgress(SharedPrefManager.getInstance(getActivity()).getPrefIndex());
        mSeekTextSize = (SeekBar) dialog.findViewById(R.id.seek_text_size);
        final LinedEditText mEditContent = dialog.findViewById(R.id.linededit_text_content);
        final EditText mEditTitle = dialog.findViewById(R.id.edit_title);

        mSeekTextSize.setOnSeekBarChangeListener(mResizeTextSeekBarListener);
        mSeekScrollSpeed.setOnSeekBarChangeListener(mSpeedUpSeekBarListener);
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

        mSpeedUpSeekBarListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                startAutoScrolling(mSeekScrollSpeed.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                stopAutoScrolling();
            }

            @Override
            public void onProgressChanged(SeekBar seekBark, int progress, boolean fromUser) {
                timeSpeed=progress;
                SharedPrefManager.getInstance(getActivity()).setPrefIndex(progress);
            }
        };


        mResizeTextSeekBarListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBark, int progress, boolean fromUser) {
                mScrollText.setTextSize(progress);
            }
        };
    /*      mSeekScrollSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

                stopAutoScrolling();
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {

                // t1.setTextSize(progress);
               // time = 100*-progress;
                mScrollText.setTextSize(progress);

               if(progress<10){
                    delay=200;
                    period=200;


                }else if(progress>11&&progress<20){
                   delay=150;
                   period=150;


               }
                else if(progress>21&&progress<30){
                   delay=100;
                   period=100;




               }
                else if(progress>31&&progress<40){
                   delay=50;
                   period=50;

                }
               else  if(progress>46&&progress<50){
                   delay=45;
                   period=45;

               }else if(progress>51&&progress<55){
                   delay=30;
                   period=30;

               }
               else if(progress>56&&progress<60){
                   delay=25;
                   period=25;

               }
               else if(progress>61&&progress<65){
                   delay=20;
                   period=20;

               }
               else  if(progress>66&&progress<70){
                   delay=15;
                   period=15;

               }
                else  if(progress>71&&progress<75){
                   delay=10;
                   period=10;

                }else if(progress>76&&progress<80){
                   delay=8;
                   period=8;

               }

               else if(progress>81&&progress<85){
                   delay=5;
                   period=5;

               }
               else  if(progress>86&&progress<90){
                   delay=4;
                   period=4;

               }else  if(progress>91&&progress<100){
                   delay=3;
                   period=3;

               }




                Log.d("seekBarProgress",String.valueOf(progress));

            }
        });
*/
    }

    public void onDestroy(){
        clearTimerTaks(clickSchedule);
        clearTimerTaks(scrollerSchedule);
        clearTimers(scrollTimer);
        clickSchedule         = null;
        scrollerSchedule      = null;
        scrollTimer           = null;
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



    private float setSlideShowFontSize(int fontSize) {

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

        return size;
    }



}
