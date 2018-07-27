package com.and.ibrahim.teleprompter.modules.display;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;

import com.and.ibrahim.teleprompter.R;
import com.and.ibrahim.teleprompter.data.Contract;
import com.and.ibrahim.teleprompter.data.SharedPrefManager;
import com.and.ibrahim.teleprompter.modules.ScrollingTextView;
import com.and.ibrahim.teleprompter.util.LinedEditText;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressWarnings("WeakerAccess")
public class DisplayFragment extends Fragment implements View.OnClickListener {


    @BindView(R.id.text_scrolling)
    protected ScrollingTextView mScrollText;
    @BindView(R.id.slide_show_scroll)
    protected ScrollView mSlideShowScroll;
    @BindView(R.id.play_scroll)
    protected ImageView mPlay;
    @BindView(R.id.vertical_outer_id)
    protected LinearLayout verticalOuterLayout;
    @BindView(R.id.drawer_layout)
    protected DrawerLayout mDrawerLayout;
    @BindView(R.id.nav_view)
    protected NavigationView navigationView;
    private Dialog dialog;
    private Timer scrollTimer = null;
    private int verticalScrollMax = 0;
    private TimerTask clickSchedule;
    private TimerTask scrollerSchedule;

    private String mScrollString;
    private boolean paused = true;
    private SeekBar.OnSeekBarChangeListener mSpeedUpSeekBarListener;
    private SeekBar.OnSeekBarChangeListener mResizeTextSeekBarListener;
    private int timeSpeed = 30;

    private int[] mScrollPosition;


    /////////////////////////////////////////////////////////////////////////////
    private void startAutoScrolling(int time) {

        if (scrollTimer == null) {
            scrollTimer = new Timer();
            final Runnable Timer_Tick = new Runnable() {
                public void run() {
                    moveScrollView();
                }
            };

            if (scrollerSchedule != null) {
                scrollerSchedule.cancel();
                scrollerSchedule = null;
            }
            scrollerSchedule = new TimerTask() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void run() {
                    Objects.requireNonNull(getActivity()).runOnUiThread(Timer_Tick);
                }
            };

            scrollTimer.schedule(scrollerSchedule, time, time);
            Log.d("speedScrollViw", "\n delay =" + String.valueOf(time) + "\n period =" + String.valueOf(time));

        }
    }


    private void moveScrollView() {
        int scrollPos = (int) (mSlideShowScroll.getScrollY() + 1.0);
        if (scrollPos >= verticalScrollMax) {
            scrollPos = 0;
        }
        mSlideShowScroll.scrollTo(0, scrollPos);

        Log.e("moveScrollView", "moveScrollView");
    }

    private void stopAutoScrolling() {
        if (scrollTimer != null) {
            scrollTimer.cancel();
            scrollTimer = null;
        }
    }

    private void getScrollMaxAmount() {
        verticalScrollMax = (verticalOuterLayout.getMeasuredHeight() - (256 * 3));
    }


    private void readBundle(Bundle bundle) {

        if (bundle != null && bundle.containsKey(Contract.EXTRA_TEXT)) {
            mScrollString = bundle.getString(Contract.EXTRA_TEXT);
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


        final int seekProgress = SharedPrefManager.getInstance(getActivity()).getPrefIndex();
        setSpeed(seekProgress);

        // Navigation view header

        // ScrollingTextView.mirror = prefs.getBoolean("pref_mirror", true);
        // mScrollText.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/" + prefs.getString("pref_font", "Roboto") + ".ttf"), 1);
        // mScrollText.setTextSize((float) (25));


        ViewTreeObserver vto = verticalOuterLayout.getViewTreeObserver();


        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                verticalOuterLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                getScrollMaxAmount();
                // startAutoScrolling();
            }
        });

        mScrollText.setText(mScrollString);

        mSlideShowScroll.getChildAt(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //tap();

                mPlay.setVisibility(View.VISIBLE);

                if (paused) {
                    mPlay.setBackground(Objects.requireNonNull(getActivity()).getDrawable(R.drawable.ic_pause_circle_filled));
                    paused = false;
                    startAutoScrolling(timeSpeed);
                    launchDismissDlg();
                    dialog.show();

                } else {
                    mPlay.setBackground(Objects.requireNonNull(getActivity()).getDrawable(R.drawable.ic_play_circle_filled));
                    paused = true;
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

    private void setSpeed(int progress) {
        if (progress > 1 && progress <= 10) {
            timeSpeed = 200;


        } else if (progress > 11 && progress <= 20) {
            timeSpeed = 150;


        } else if (progress >= 21 && progress <= 30) {
            timeSpeed = 100;
        } else if (progress >= 31 && progress <= 40) {
            timeSpeed = 50;

        } else if (progress >= 46 && progress <= 50) {
            timeSpeed = 40;

        } else if (progress >= 51 && progress <= 55) {
            timeSpeed = 30;

        } else if (progress >= 56 && progress <= 60) {
            timeSpeed = 20;

        } else if (progress >= 61 && progress <= 65) {
            timeSpeed = 10;

        } else if (progress >= 66 && progress <= 69) {
            timeSpeed = 15;

        } else if (progress >= 72 && progress < 75) {
            timeSpeed = 10;

        } else if (progress >= 78 && progress <= 81) {
            timeSpeed = 8;

        } else if (progress > 84 && progress < 87) {
            timeSpeed = 5;

        } else if (progress > 90 && progress < 93) {
            timeSpeed = 3;

        } else if (progress >= 96 && progress < 99) {
            timeSpeed = 1;

        }


    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void launchDismissDlg() {

        dialog = new Dialog(Objects.requireNonNull(getActivity()));
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        lp.windowAnimations = R.style.SlideDialogAnimation;
        dialog.getWindow().setAttributes(lp);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.custom_drawer_item);
        dialog.setCanceledOnTouchOutside(true);
        SeekBar mSeekScrollSpeed = dialog.findViewById(R.id.seek_speed_up);
        mSeekScrollSpeed.setProgress(SharedPrefManager.getInstance(getActivity()).getPrefIndex());
        SeekBar mSeekTextSize = dialog.findViewById(R.id.seek_text_size);
        final LinedEditText mEditContent = dialog.findViewById(R.id.linededit_text_content);
        final EditText mEditTitle = dialog.findViewById(R.id.edit_title);

        mSeekTextSize.setOnSeekBarChangeListener(mResizeTextSeekBarListener);
        mSeekScrollSpeed.setOnSeekBarChangeListener(mSpeedUpSeekBarListener);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            mScrollPosition = savedInstanceState.getIntArray(Contract.EXTRA_SCROLL_POSITION);
            if (mScrollPosition != null)
                mSlideShowScroll.post(new Runnable() {
                    public void run() {
                        mSlideShowScroll.scrollTo(mScrollPosition[0], mScrollPosition[1]);
                    }
                });
        }

        mSpeedUpSeekBarListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                startAutoScrolling(timeSpeed);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                stopAutoScrolling();
            }

            @Override
            public void onProgressChanged(SeekBar seekBark, int progress, boolean fromUser) {
                setSpeed(progress);
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

    public void onDestroy() {
        clearTimerTaks(clickSchedule);
        clearTimerTaks(scrollerSchedule);
        clearTimers(scrollTimer);
        clickSchedule = null;
        scrollerSchedule = null;
        scrollTimer = null;
        super.onDestroy();
    }

    @SuppressWarnings("UnusedAssignment")
    private void clearTimers(Timer timer) {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @SuppressWarnings("UnusedAssignment")
    private void clearTimerTaks(TimerTask timerTask) {
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntArray(Contract.EXTRA_SCROLL_POSITION,
                new int[]{mSlideShowScroll.getScrollX(), mSlideShowScroll.getScrollY()});
    }

    @Override
    public void onClick(View view) {


    }


}
