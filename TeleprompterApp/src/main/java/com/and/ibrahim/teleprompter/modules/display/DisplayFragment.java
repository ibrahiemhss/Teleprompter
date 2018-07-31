package com.and.ibrahim.teleprompter.modules.display;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.and.ibrahim.teleprompter.R;
import com.and.ibrahim.teleprompter.data.Contract;
import com.and.ibrahim.teleprompter.data.SharedPrefManager;
import com.and.ibrahim.teleprompter.modules.ScrollingTextView;
import com.and.ibrahim.teleprompter.modules.main.HomeActivity;
import com.and.ibrahim.teleprompter.mvp.view.RecyclerViewItemClickListener;
import com.and.ibrahim.teleprompter.mvp.view.RecylerViewClickListener;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.Gravity.BOTTOM;
import static android.view.Gravity.LEFT;
import static android.view.Gravity.START;

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
    @BindView(R.id.show_setting)
    protected ImageView mImgSetting;
    @BindView(R.id.back_id)
    protected ImageView mBackImg;
    @BindView(R.id.r_toolbar_container)
    protected RelativeLayout mToolbarContainer;
    @BindView(R.id.show_play)
    protected ImageView mPlayStatus;
    private TextView mTextSpeed;
    private Dialog mDialog;
    private Dialog mDialogTextColors;
    private Dialog mDialogBackgroundtColors;
    private int[] mTextColorrArray;
    private int[] mBackGroundColorrArray;


    private Timer scrollTimer = null;
    private int verticalScrollMax = 0;
    private TimerTask clickSchedule;
    private TimerTask scrollerSchedule;
    private boolean isOpen = false;
    private int textSpeedValue;
    private boolean isUp;
    private int mTextColor;
    private int mBackgroundColor;
    private boolean isFirstOpen = false;

    private RecyclerView mColorsRV;
    private ListView mBackgroundColorListView;

    private ColorsAdapter mColorAdapter;
    private Colors mColors;


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
        isUp = true;
        textSpeedValue = 1;
        mTextColorrArray = getActivity().getResources().getIntArray(R.array.text_colors);
        mBackGroundColorrArray = getActivity().getResources().getIntArray(R.array.background_colors);


        isFirstOpen = SharedPrefManager.getInstance(getActivity()).isFirstOpen();

        mToolbarContainer.setVisibility(View.VISIBLE);
        mPlay.setBackground(Objects.requireNonNull(getActivity()).getDrawable(R.drawable.ic_arrow));
        mPlayStatus.setBackground(Objects.requireNonNull(getActivity()).getDrawable(R.drawable.ic_arrow));
        final int seekProgress = SharedPrefManager.getInstance(getActivity()).getPrefSpeed();

        if (!isFirstOpen) {
            mTextColor = getActivity().getResources().getColor(R.color.White);
            mBackgroundColor = getActivity().getResources().getColor(R.color.Black);
            SharedPrefManager.getInstance(getActivity()).setPrefFirstOpen(true);
        } else {

            mTextColor = SharedPrefManager.getInstance(getActivity()).getPrefTextColor();
            mBackgroundColor = SharedPrefManager.getInstance(getActivity()).getPrefBackgroundColr();

        }

        mScrollText.setTextColor(mTextColor);
        mSlideShowScroll.setBackgroundColor(mBackgroundColor);
        mScrollText.setText(mScrollString);

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
            }
        });


        clickToScrolling();


        return view;

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

        mImgSetting.setOnClickListener(this);
        mPlay.setOnClickListener(this);
        mBackImg.setOnClickListener(this);

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
                mTextSpeed.setText(String.valueOf(progress));
                SharedPrefManager.getInstance(getActivity()).setPrefSpeed(progress);
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
                SharedPrefManager.getInstance(getActivity()).setPrefTextSize(progress);

            }
        };

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void clickToScrolling() {
        mSlideShowScroll.getChildAt(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mPlayStatus.setVisibility(View.VISIBLE);

                if (paused) {
                    mPlay.setBackground(Objects.requireNonNull(getActivity()).getDrawable(R.drawable.ic_pause));
                    mPlayStatus.setBackground(Objects.requireNonNull(getActivity()).getDrawable(R.drawable.ic_pause));

                    paused = false;
                    startAutoScrolling(timeSpeed);

                } else {
                    mPlay.setBackground(Objects.requireNonNull(getActivity()).getDrawable(R.drawable.ic_arrow));
                    mPlayStatus.setBackground(Objects.requireNonNull(getActivity()).getDrawable(R.drawable.ic_arrow));
                    paused = true;
                    stopAutoScrolling();


                }

                mPlayStatus.postDelayed(new Runnable() {
                    public void run() {
                        mPlayStatus.setVisibility(View.INVISIBLE);
                    }
                }, 800);

                onSlideView(mToolbarContainer);
            }

        });
    }

    // slide the view from below itself to the current position
    public void slideUp(View view) {
        view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                view.getHeight(),  // fromYDelta
                0);                // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    // slide the view from its current position to below itself
    public void slideDown(View view) {
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                0,                 // fromYDelta
                view.getHeight()); // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    public void onSlideView(View view) {
        if (isUp) {
            slideDown(view);
        } else {
            slideUp(view);
        }
        isUp = !isUp;
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
    private void launchEditDlg() {
        isOpen = false;

        mDialog = new Dialog(Objects.requireNonNull(getActivity()));
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(mDialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = LEFT | BOTTOM;
        lp.windowAnimations = R.style.ToLiftAnimation;
        mDialog.getWindow().setAttributes(lp);

        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        mDialog.setContentView(R.layout.custom_dialog_edit_scroll);
        SeekBar mSeekScrollSpeed = mDialog.findViewById(R.id.seek_speed_up);
        SeekBar mSeekTextSize = mDialog.findViewById(R.id.seek_text_size);
        final LinearLayout onClickDialogTextColor = mDialog.findViewById(R.id.ln_launch_text_color);
        TextView cancelBtn = mDialog.findViewById(R.id.cancel_edit_dialog);

        mSeekScrollSpeed.setProgress(SharedPrefManager.getInstance(getActivity()).getPrefSpeed());
        mSeekTextSize.setProgress(SharedPrefManager.getInstance(getActivity()).getPrefTextSize());
        mTextSpeed = mDialog.findViewById(R.id.text_font);

        mDialog.setCancelable(true);
        mSeekTextSize.setOnSeekBarChangeListener(mResizeTextSeekBarListener);
        mSeekScrollSpeed.setOnSeekBarChangeListener(mSpeedUpSeekBarListener);


        textSpeedValue = SharedPrefManager.getInstance(getActivity()).getPrefSpeed();
        if (textSpeedValue > 0) {
            mTextSpeed.setText(String.valueOf(textSpeedValue));

        } else {
            mTextSpeed.setText("");
        }

        onClickDialogTextColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchDlgTextColors();
                if (!isOpen) {
                    isOpen = true;
                    launchDlgTextColors();
                }
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void launchDlgTextColors() {

        mDialogTextColors = new Dialog(Objects.requireNonNull(getActivity()));
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(mDialogTextColors.getWindow()).getAttributes());
        lp.width = 48;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = BOTTOM | START;
        lp.windowAnimations = R.style.ToRightAnimation;
        mDialogTextColors.getWindow().setAttributes(lp);

        mDialogTextColors.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialogTextColors.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialogTextColors.setContentView(R.layout.dialog_colors);

        FloatingActionButton fab = mDialogTextColors.findViewById(R.id.cancel_text_color_dialog);

        mColorsRV = mDialogTextColors.findViewById(R.id.rv_colors);

        mColors = new Colors();
        mColorsRV.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = null;
        gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        mColorsRV.setLayoutManager(gridLayoutManager);

        mColorAdapter = new ColorsAdapter(getActivity(), getActivity().getLayoutInflater());

        mColorsRV.setAdapter(mColorAdapter);

        mColorsRV.addOnItemTouchListener(new RecyclerViewItemClickListener(getActivity(), mColorsRV, new RecylerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                mScrollText.setTextColor(mTextColorrArray[position]);
                SharedPrefManager.getInstance(getActivity()).setPrefTextColor(mTextColorrArray[position]);
                mSlideShowScroll.setBackgroundColor(mBackGroundColorrArray[position]);
                SharedPrefManager.getInstance(getActivity()).setPrefBackgroundColor(mBackGroundColorrArray[position]);
            }

            @Override
            public void onLongClick(View view, final int position) {

            }
        }));
       /* mColorsRV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });*/
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialogTextColors.dismiss();
            }
        });
        mDialogTextColors.show();

        isOpen = false;
    }
   /* @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void launchDlgBackgrounColors() {

        mDialogBackgroundtColors = new Dialog(Objects.requireNonNull(getActivity()));
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(mDialogBackgroundtColors.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = BOTTOM|END;
        lp.windowAnimations = R.style.SlideDialogAnimation;
        mDialogBackgroundtColors.getWindow().setAttributes(lp);

        mDialogBackgroundtColors.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialogBackgroundtColors.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        mDialogBackgroundtColors.setContentView(R.layout.dialog_background_color);

        mBackgroundColorListView=mDialogBackgroundtColors.findViewById(R.id.list_background_color);
        FloatingActionButton fab =mDialogBackgroundtColors.findViewById(R.id.cancel_bacckground_color_dialog);

        mColors =new Colors();

        mColorAdapter= new ColorAdapter(getActivity());

        mBackgroundColorListView.setAdapter(mColorAdapter);

        mBackgroundColorListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mSlideShowScroll.setBackgroundColor(colorNumberarray[i]);
                SharedPrefManager.getInstance(getActivity()).setPrefBackgroundColor(colorNumberarray[i]);
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialogBackgroundtColors.dismiss();
            }
        });
        isOpen=false;
        mDialogBackgroundtColors.show();

    }
*/

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

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View view) {
        int getView = view.getId();
        switch (getView) {
            case R.id.show_setting:
                if (!isOpen) {
                    isOpen = true;
                    launchEditDlg();
                    mDialog.show();


                }

                break;
            case R.id.play_scroll:

                break;

            case R.id.back_id:
                Intent intent = new Intent(getActivity(), HomeActivity.class);
                startActivity(intent);
                AnimatedVectorDrawable drawable = (AnimatedVectorDrawable) getResources().getDrawable(R.drawable.ic_menu_animatable);
                mBackImg.setImageDrawable(drawable);
                drawable.start();
                break;


        }


    }


}
