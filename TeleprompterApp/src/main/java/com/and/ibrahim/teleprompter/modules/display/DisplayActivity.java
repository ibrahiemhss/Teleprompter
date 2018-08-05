package com.and.ibrahim.teleprompter.modules.display;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.and.ibrahim.teleprompter.R;
import com.and.ibrahim.teleprompter.base.BaseActivity;
import com.and.ibrahim.teleprompter.callback.FragmentEditListRefreshListener;
import com.and.ibrahim.teleprompter.data.Contract;
import com.and.ibrahim.teleprompter.data.SharedPrefManager;
import com.and.ibrahim.teleprompter.callback.OnDataPass;
import com.and.ibrahim.teleprompter.util.ScrollingTextView;
import com.and.ibrahim.teleprompter.modules.listContents.ListContentsActivity;
import com.and.ibrahim.teleprompter.modules.listContents.ListContentsFragment;
import com.and.ibrahim.teleprompter.mvp.view.RecyclerViewItemClickListener;
import com.and.ibrahim.teleprompter.mvp.view.RecylerViewClickListener;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;

import static android.view.Gravity.BOTTOM;
import static android.view.Gravity.START;

public class DisplayActivity extends BaseActivity implements View.OnClickListener ,OnDataPass {


    private Fragment mContentListFragment;

    //@BindView(R.id.display_toolbar)
  //  protected Toolbar mToolbar;
    @BindView(R.id.display_collapsing_toolbar)
    protected CollapsingToolbarLayout mCollapsingToolbarLayout;
    @BindView(R.id.display_app_bar_layout)
    protected AppBarLayout mAppBarLayout;
    @BindView(R.id.text_scrolling)
    protected ScrollingTextView mScrollText;
    @BindView(R.id.slide_show_scroll)
    protected ScrollView mSlideShowScroll;
    @BindView(R.id.vertical_outer_id)
    protected LinearLayout verticalOuterLayout;
    @BindView(R.id.frame_show_container)
    protected FrameLayout mFramShowContainer;
    @BindView(R.id.text_empty_show)
    protected TextView mEmptyTextShow;
    @BindView(R.id.show_setting)
    protected ImageView mImgSetting;
    @BindView(R.id.back_id)
    protected ImageView mBackImg;
    @BindView(R.id.show_play)
    protected ImageView mPlayStatus;
    @BindView(R.id.nav_view)
    protected NavigationView mNavView;
    @BindView(R.id.drawer_layout)
    protected DrawerLayout mDrawerLayout;

    private ActionBarDrawerToggle mBarDrawerToggle;
    private SeekBar mSeekScrollSpeed;
    private SeekBar mSeekTextSize;
    private TextView mTextSpeed;
    private Dialog mDialog;
    private Dialog mDialogTextColors;
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
    private boolean isTablet;

    private boolean mOpenDrawer;
    private RecyclerView mColorsRV;
    private ListView mBackgroundColorListView;

    private ColorsAdapter mColorAdapter;
    AppBarLayout.LayoutParams params;



    private String mScrollString;
    private boolean paused = true;
    private SeekBar.OnSeekBarChangeListener mSpeedUpSeekBarListener;
    private SeekBar.OnSeekBarChangeListener mResizeTextSeekBarListener;
    private int timeSpeed = 30;

    private int[] mScrollPosition;
    private FragmentEditListRefreshListener fragmentEditListRefreshListener;

    public FragmentEditListRefreshListener getFragmentEditListRefreshListener() {
        return fragmentEditListRefreshListener;
    }
    public void setFragmentEditListRefreshListener(FragmentEditListRefreshListener fragmentEditListRefreshListener) {
        this.fragmentEditListRefreshListener = fragmentEditListRefreshListener;
    }
    @Override
    public int getResourceLayout() {
        return R.layout.activity_display;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onViewReady(Bundle savedInstanceState, Intent intent) {
        super.onViewReady(savedInstanceState, intent);
        mContentListFragment = new ListContentsFragment();

        View headerView= LayoutInflater.from(this).inflate(R.layout.nav_header, null);
        mNavView.addHeaderView(headerView);
        mNavView.getHeaderView(0).setVisibility(View.GONE);

        ViewTreeObserver vto = verticalOuterLayout.getViewTreeObserver();


        if (savedInstanceState != null) {
            mContentListFragment = (ListContentsFragment) getSupportFragmentManager().getFragment(savedInstanceState, Contract.EXTRA_FRAGMENT);

        }

        params = (AppBarLayout.LayoutParams) mCollapsingToolbarLayout.getLayoutParams();
        params.setScrollFlags(0);  // clear all scroll flags
        Bundle extras = intent.getExtras();
        if(extras!=null){
            mScrollString = extras.getString(Contract.EXTRA_TEXT);
        }

        mNavView.setBackgroundColor(getResources().getColor(R.color.colorTransparent));
        if (isTablet()) {
           // mToolbar.setVisibility(View.GONE);
            mFramShowContainer.setVisibility(View.GONE);

            if(mScrollString==null){
                mEmptyTextShow.setVisibility(View.VISIBLE);
            }else {
                mFramShowContainer.setVisibility(View.VISIBLE);

            }

        }else {
            mEmptyTextShow.setVisibility(View.GONE);
            //mToolbar.setVisibility(View.VISIBLE);
            mFramShowContainer.setVisibility(View.VISIBLE);


        }

        mPlayStatus.setVisibility(View.VISIBLE);


    }

    @Override
    public void setListener() {
        mImgSetting.setOnClickListener(this);
        mBackImg.setOnClickListener(this);
       // mNavView.setDrawerIndicatorEnabled(false);

        ViewTreeObserver vto = verticalOuterLayout.getViewTreeObserver();


        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                verticalOuterLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                getScrollMaxAmount();
            }
        });


        mBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,R.string.Open, R.string.Close);
        mDrawerLayout.addDrawerListener(mBarDrawerToggle);
        mBarDrawerToggle.syncState();

    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void init() {
        isUp = true;
        textSpeedValue = 1;
        mTextColorrArray = getResources().getIntArray(R.array.text_colors);
        mBackGroundColorrArray = getResources().getIntArray(R.array.background_colors);


        isFirstOpen = SharedPrefManager.getInstance(this).isFirstOpen();

        if(!isTablet()){
            mPlayStatus.setBackground(Objects.requireNonNull(this).getDrawable(R.drawable.ic_play_circle_filled));

        }
        final int seekProgress = SharedPrefManager.getInstance(this).getPrefSpeed();

        if (!isFirstOpen) {
            mTextColor = getResources().getColor(R.color.White);
            mBackgroundColor = getResources().getColor(R.color.Black);
            SharedPrefManager.getInstance(this).setPrefFirstOpen(true);
        } else {

            mTextColor = SharedPrefManager.getInstance(this).getPrefTextColor();
            mBackgroundColor = SharedPrefManager.getInstance(this).getPrefBackgroundColr();

        }

        mScrollText.setTextColor(mTextColor);
        mSlideShowScroll.setBackgroundColor(mBackgroundColor);
        mScrollText.setText(mScrollString);

        setSpeed(seekProgress);



        clickToScrolling();

        initNavigationDrawer();
        if (isTablet()) {
            initFragment();

        }


    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putIntArray(Contract.EXTRA_SCROLL_POSITION,
                new int[]{mSlideShowScroll.getScrollX(), mSlideShowScroll.getScrollY()});
        if (isTablet()) {
            getSupportFragmentManager().putFragment(outState, Contract.EXTRA_FRAGMENT, mContentListFragment);

        }
        super.onSaveInstanceState(outState);
    }
    private void initFragment(){
        Bundle bundle = new Bundle();

        bundle.putString(Contract.EXTRA_TEXT, getResources().getString(R.string.mytest));
        mContentListFragment.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.contents_container, mContentListFragment)
                .commit();
    }

    public boolean isTablet() {
        return (DisplayActivity.this.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }


    /////////////////////////////////////////////////////////////////////////////
    public void startAutoScrolling(int time) {

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

                    runOnUiThread(Timer_Tick);
                }
            };

            scrollTimer.schedule(scrollerSchedule, time, time);
            Log.d("speedScrollViw", "\n delay =" + String.valueOf(time) + "\n period =" + String.valueOf(time));

        }
    }
//////////////////////////////////////////////////////////////////////////////////////

    private void moveScrollView() {
        int scrollPos = (int) (mSlideShowScroll.getScrollY() + 1.0);
        if (scrollPos >= verticalScrollMax) {
            scrollPos = 0;
        }
        mSlideShowScroll.scrollTo(0, scrollPos);

        Log.e("moveScrollView", "moveScrollView");
    }

    public void stopAutoScrolling() {
        if (scrollTimer != null) {
            scrollTimer.cancel();
            scrollTimer = null;
        }
    }

    private void getScrollMaxAmount() {
        verticalScrollMax = (verticalOuterLayout.getMeasuredHeight() - (256 * 3));
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View view) {
        int getView = view.getId();
        switch (getView) {
            case R.id.show_setting:
                if (mOpenDrawer) {
                    mDrawerLayout.closeDrawer(Gravity.START);
                    mOpenDrawer=false;
                }else {
                    mDrawerLayout.openDrawer(Gravity.START);
                    mOpenDrawer=true;

                }

                break;

            case R.id.back_id:
                Intent intent = new Intent(this, ListContentsActivity.class);
                startActivity(intent);
                AnimatedVectorDrawable drawable = (AnimatedVectorDrawable) getResources().getDrawable(R.drawable.ic_menu_animatable);
                // mBackImg.setImageDrawable(drawable);
                drawable.start();
                break;

           /* case R.id.tablet_setting:
                if (!isOpen) {
                    isOpen = true;
                    launchEditDlg();
                    mDialog.show();
                }
                break;*/


        }


    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void clickToScrolling() {
        mSlideShowScroll.getChildAt(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (paused) {
                    mPlayStatus.setBackground(getDrawable(R.drawable.ic_pause_circle_filled));

                    paused = false;
                    startAutoScrolling(timeSpeed);
                    mPlayStatus.postDelayed(new Runnable() {
                        public void run() {
                            mPlayStatus.setVisibility(View.INVISIBLE);
                        }
                    }, 800);


                } else {
                    mPlayStatus.setVisibility(View.VISIBLE);

                    //  mPlay.setBackground(Objects.requireNonNull(getActivity()).getDrawable(R.drawable.ic_arrow));
                    mPlayStatus.setBackground(getDrawable(R.drawable.ic_play_circle_filled));
                    paused = true;
                    stopAutoScrolling();


                }


                if (!isTablet()) {
                   onSlideView(mAppBarLayout);


                }
            }

        });
    }
    // slide the view from below itself to the current position
    public void slideUp(View view) {
        view.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).start();


    }

    // slide the view from its current position to below itself
    public void slideDown(View view) {

        view.animate().translationY(-view.getBottom()).setInterpolator(new AccelerateInterpolator()).start();


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


    private void initNavigationDrawer(){
        SeekBar mSeekScrollSpeed = mNavView.findViewById(R.id.seek_speed_up);
        SeekBar mSeekTextSize = mNavView.findViewById(R.id.seek_text_size);
        final LinearLayout onClickDialogTextColor = mNavView.findViewById(R.id.ln_launch_text_color);
        TextView cancelBtn = mNavView.findViewById(R.id.cancel_edit_dialog);


        mSeekScrollSpeed.setProgress(SharedPrefManager.getInstance(this).getPrefSpeed());
        mSeekTextSize.setProgress(SharedPrefManager.getInstance(this).getPrefTextSize());
        mTextSpeed = mNavView.findViewById(R.id.text_font);
        mSeekScrollSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
                 SharedPrefManager.getInstance(DisplayActivity.this).setPrefSpeed(progress);
             }
         });


        mSeekTextSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
           @Override
           public void onStopTrackingTouch(SeekBar seekBar) {
           }

           @Override
           public void onStartTrackingTouch(SeekBar seekBar) {
           }

           @Override
           public void onProgressChanged(SeekBar seekBark, int progress, boolean fromUser) {
               mScrollText.setTextSize(progress);
               SharedPrefManager.getInstance(DisplayActivity.this).setPrefTextSize(progress);

           }
       });


        textSpeedValue = SharedPrefManager.getInstance(this).getPrefSpeed();
        if (textSpeedValue > 0) {
            mTextSpeed.setText(String.valueOf(textSpeedValue));

        } else {
            mTextSpeed.setText("");
        }

        onClickDialogTextColor.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
                launchDlgTextColors();
                if (!isOpen) {
                    isOpen = true;
                    launchDlgTextColors();
                }
            }
        });



    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void launchDlgTextColors() {

        mDialogTextColors = new Dialog(Objects.requireNonNull(this));
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

        mColorsRV.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = null;
        gridLayoutManager = new GridLayoutManager(this, 3);
        mColorsRV.setLayoutManager(gridLayoutManager);

        mColorAdapter = new ColorsAdapter(this, getLayoutInflater());

        mColorsRV.setAdapter(mColorAdapter);

        mColorsRV.addOnItemTouchListener(new RecyclerViewItemClickListener(this, mColorsRV, new RecylerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                mScrollText.setTextColor(mTextColorrArray[position]);
                SharedPrefManager.getInstance(DisplayActivity.this).setPrefTextColor(mTextColorrArray[position]);
                mSlideShowScroll.setBackgroundColor(mBackGroundColorrArray[position]);
                SharedPrefManager.getInstance(DisplayActivity.this).setPrefBackgroundColor(mBackGroundColorrArray[position]);
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
    public void onDataPass(String data) {
        if (isTablet()) {
            mScrollString=data;
            Log.d("LOG","hellooooo " + data);

            if(mScrollString!=null){
                mScrollText.setText(mScrollString);
                mFramShowContainer.setVisibility(View.VISIBLE);
                mEmptyTextShow.setVisibility(View.GONE);
            }
        }
    }
}
