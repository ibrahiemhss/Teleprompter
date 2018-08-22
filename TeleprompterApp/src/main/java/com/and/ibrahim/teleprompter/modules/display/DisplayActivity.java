package com.and.ibrahim.teleprompter.modules.display;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.and.ibrahim.teleprompter.R;
import com.and.ibrahim.teleprompter.base.BaseActivity;
import com.and.ibrahim.teleprompter.callback.FragmentEditListRefreshListener;
import com.and.ibrahim.teleprompter.callback.OnDataPassListener;
import com.and.ibrahim.teleprompter.data.Contract;
import com.and.ibrahim.teleprompter.data.SharedPrefManager;
import com.and.ibrahim.teleprompter.modules.listContents.ListContentsFragment;
import com.and.ibrahim.teleprompter.modules.setting.SettingsActivity;
import com.and.ibrahim.teleprompter.mvp.view.RecyclerViewItemClickListener;
import com.and.ibrahim.teleprompter.util.ScrollingTextView;
import com.google.firebase.crash.FirebaseCrash;

import java.util.Locale;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;

import static android.view.Gravity.BOTTOM;
import static android.view.Gravity.END;

@SuppressWarnings("WeakerAccess")
public class DisplayActivity extends BaseActivity implements View.OnClickListener, OnDataPassListener, SharedPreferences.OnSharedPreferenceChangeListener {

    @BindView(R.id.display_toolbar)
    protected Toolbar mToolbar;
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
    @BindView(R.id.show_play)
    protected ImageView mPlayStatus;
    @BindView(R.id.nav_view)
    protected NavigationView mNavView;
    @BindView(R.id.drawer_layout)
    protected DrawerLayout mDrawerLayout;
    @BindView(R.id.chronometer)
    protected Chronometer mChronometer;
    @BindView(R.id.up_view)
    protected View mUpView;
    @BindView(R.id.down_view)
    protected View mDownView;
    @BindView(R.id.toggle_marker)
    protected ImageView mToggleMarker;

    int melliSeconds;
    private TextView mEmptyTextShow;
    private FrameLayout mScrollContainer;
    private Fragment mContentListFragment;
    private TextView mTextSpeed;
    private Dialog mDialogTextColors;
    private int[] mTextColorArray;
    private int[] mBackGroundColorArray;
    private Timer scrollTimer = null;
    private int verticalScrollMax = 0;
    private TimerTask clickSchedule;
    private TimerTask scrollerSchedule;
    private boolean isOpen = false;
    private int textSpeedValue;
    private boolean isUp;
    private boolean mOpenDrawer;
    private String mScrollString;
    private boolean paused = true;
    private int timeSpeed = 30;
    private boolean isDialogShow;
    private int mScrollPos;
    private boolean isTablet;


    private FragmentEditListRefreshListener fragmentEditListRefreshListener;

    @Override
    public int getResourceLayout() {
        return R.layout.activity_display;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onViewReady(Bundle savedInstanceState, Intent intent) {
        super.onViewReady(savedInstanceState, intent);
        boolean isFirstEntry = SharedPrefManager.getInstance(this).isFirstEntry();
        isTablet = getResources().getBoolean(R.bool.isTablet);
        if (savedInstanceState != null) {//save state case

            stopAutoScrolling();
            if (isTablet) {
                mContentListFragment = getSupportFragmentManager().getFragment(savedInstanceState, Contract.EXTRA_FRAGMENT);

            }
            mScrollString = savedInstanceState.getString(Contract.EXTRA_SCROLL_STRING);
            isDialogShow = savedInstanceState.getBoolean(Contract.EXTRA_SHOW_COLOR_DIALOG);
            mScrollPos = savedInstanceState.getInt(Contract.EXTRA_SCROLL_POS);
            mChronometer.setBase(savedInstanceState.getLong(Contract.EXTRA_CHRONOTIME));
            if (isDialogShow) {
                launchDlgTextColors();
            }

        }
        //noinspection deprecation
        FirebaseCrash.report(new Exception("My first Android non-fatal error"));
        //noinspection deprecation
        FirebaseCrash.log("DisplayActivity started");

        if (!isFirstEntry) {
            addDemo();

        }
        if (Locale.getDefault().getLanguage().equals("ar")) {

            RelativeLayout.LayoutParams myImageLayout = (RelativeLayout.LayoutParams) mToggleMarker.getLayoutParams();
            myImageLayout.addRule(RelativeLayout.ALIGN_PARENT_END);
            mToggleMarker.setLayoutParams(myImageLayout);

        } else {
            RelativeLayout.LayoutParams myImageLayout = (RelativeLayout.LayoutParams) mToggleMarker.getLayoutParams();
            myImageLayout.addRule(RelativeLayout.ALIGN_PARENT_START);
            mToggleMarker.setLayoutParams(myImageLayout);

        }
        mContentListFragment = new ListContentsFragment();

        //print all Screens sizes wanted to set suitable width for scrollView//
        Configuration configuration = getResources().getConfiguration();
        int screenWidthDp = configuration.screenWidthDp; //The current width of the available screen space, in dp units, corresponding to screen width resource qualifier.
        int screenHeightDp = configuration.screenHeightDp; //The smallest screen size an application will see in normal operation, corresponding to smallest screen width resource qualifier.
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        Log.d("screenSize", "\nWidthDp=" + String.valueOf(screenWidthDp)
                + "\nHeightDp=" + String.valueOf(screenHeightDp)
                + "\nwidthPx=" + String.valueOf(width)
                + "\nheightPx=" + String.valueOf(height));


        setupToolbar();//in tablet screen size make main toolbar to one screen for all views

        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) mCollapsingToolbarLayout.getLayoutParams();
        params.setScrollFlags(0);  // clear all scroll flags
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mScrollString = extras.getString(Contract.EXTRA_TEXT);
        }

        if (isTablet) {//so scroll view will hide if mScrollText have null value
            mEmptyTextShow = findViewById(R.id.text_empty_show);

            mScrollContainer = findViewById(R.id.scroll_container);

        }
        if (isTablet) {//custom view in tablet devices
            if (mScrollString == null) { //in tablet DisplayActivity is first activity and text will be empty
                mEmptyTextShow.setVisibility(View.VISIBLE);
                mScrollContainer.setVisibility(View.GONE);

            } else {
                mScrollContainer.setVisibility(View.VISIBLE);
                mEmptyTextShow.setVisibility(View.GONE);

            }
        }
        mPlayStatus.setVisibility(View.VISIBLE);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void init() {
        isUp = true;
        textSpeedValue = 1;//default value generator of speed text in first open
        mTextColorArray = getResources().getIntArray(R.array.text_colors);//initialize text colors
        mBackGroundColorArray = getResources().getIntArray(R.array.background_colors);//initialize background colors
        boolean isColorSet = SharedPrefManager.getInstance(this).isColorPref();

        final int seekProgress = //get recorded seekbar value
                SharedPrefManager.getInstance(this).getPrefSpeed();

        mPlayStatus.//set play status of image that show playing playing status
                setBackground(Objects.requireNonNull(this).getDrawable(R.drawable.ic_play_circle_filled));

        if (isTablet) {//listContentFragment with displayActivity in tablet will be in one screen :)
            initFragment();
        }

        int mBackgroundColor;
        int mTextColor;
        if (!isColorSet) {//setting default colors of scrolling text in first opening of app
            mTextColor = getResources().getColor(R.color.White);
            mBackgroundColor = getResources().getColor(R.color.Black);
        } else {//get recorded value of colors from SharedPrefManager class
            mTextColor = SharedPrefManager.getInstance(this).getPrefTextColor();
            mBackgroundColor = SharedPrefManager.getInstance(this).getPrefBackgroundColor();
        }

        mScrollText.setTextColor(mTextColor);
        mSlideShowScroll.setBackgroundColor(mBackgroundColor);
        mScrollText.setText(mScrollString);

        setSpeed(seekProgress);//get suitable value of speed by bas seekbar progress

        clickToScrolling();//perform scrolling
        initNavigationDrawer();//initialize navigation drawer
        setupSharedPreferences();
    }

    @Override
    public void setListener() {

        mPlayStatus.setOnClickListener(this);


        getScrollMaxAmount();//get all scrollView amount after text length inside

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {//this menu will appear just in tablets
        MenuInflater inflater = getMenuInflater();
        if (isTablet) {//inflate main menu toolbar in tablet
            inflater.inflate(R.menu.content_list_menu, menu);

        }
        if (!isTablet) {//inflate phone menu toolbar
            inflater.inflate(R.menu.phone_menu, menu);
        }


        super.onCreateOptionsMenu(menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (isTablet) {
            switch (item.getItemId()) {

                case R.id.action_delete:
                    @SuppressWarnings("unused") boolean ischecked = true;
                    if (getFragmentEditListRefreshListener() != null) {
                        getFragmentEditListRefreshListener().onRefresh();
                    }
                    break;

                case R.id.action_setting:
                    Intent intent = (new Intent(DisplayActivity.this, SettingsActivity.class));
                    startActivity(intent);

                default:
                    break;
            }

        } else {
            if (item.getItemId() == R.id.action_setting) {

                if (mOpenDrawer) {
                    mDrawerLayout.closeDrawer(Gravity.START);
                    mOpenDrawer = false;
                } else {
                    mDrawerLayout.openDrawer(Gravity.START);
                    mOpenDrawer = true;
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(Contract.EXTRA_SCROLL_STRING, mScrollString);
        outState.putBoolean(Contract.EXTRA_SHOW_COLOR_DIALOG, isDialogShow);
        outState.putInt(Contract.EXTRA_SCROLL_POS, mScrollPos);
        outState.putLong(Contract.EXTRA_CHRONOTIME, mChronometer.getBase());

        if (isTablet) {//save fragment
            getSupportFragmentManager().putFragment(outState, Contract.EXTRA_FRAGMENT, mContentListFragment);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        final int[] position = savedInstanceState.getIntArray(Contract.EXTRA_SCROLL_POSITION);
        if (position != null)
            mSlideShowScroll.post(() -> mSlideShowScroll.scrollTo(position[0], position[1]));
    }

    private void initFragment() {
        Bundle bundle = new Bundle();
        mContentListFragment.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.contents_container, mContentListFragment)
                .commit();
    }

    //////////all methods responsible for for auto scrolling for scrolling text//////////////////////////////////////////////
    public void startAutoScrolling(int time) {
        Log.d("lifCycle", "startAutoScrolling1");

        if (mSlideShowScroll != null) {
            if (scrollTimer == null) {
                scrollTimer = new Timer();
                final Runnable Timer_Tick = () -> {
                    Log.d("lifCycle", "startAutoScrolling2");


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

                        runOnUiThread(Timer_Tick);
                    }
                };

                scrollTimer.schedule(scrollerSchedule, time, time);
                Log.d("speedScrollViw", "\n delay =" + String.valueOf(time) + "\n period =" + String.valueOf(time));

            }
        }

    }

    private void moveScrollView() {
        Log.d("lifCycle", "moveScrollView1");

        if (mSlideShowScroll == null) {
            stopAutoScrolling();
        } else {
            mScrollPos = (int) (mSlideShowScroll.getScrollY() + 1.0);
            if (mScrollPos >= verticalScrollMax) {
                mScrollPos = 0;
            }
            mSlideShowScroll.scrollTo(0, mScrollPos);

            Log.e("moveScrollView", "moveScrollView");
            Log.d("lifCycle", "moveScrollView2");

        }

    }

    public void stopAutoScrolling() {
        if (scrollTimer != null) {
            scrollTimer.cancel();
            scrollTimer = null;
        }
    }

    private void getScrollMaxAmount() {

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

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void clickToScrolling() {
        mSlideShowScroll.getChildAt(0).setOnClickListener(v -> {


            if (paused) {

                startPlayStatus();
                paused = false;
                mChronometer.setBase(mChronometer.getBase());
                mChronometer.start();
                startAutoScrolling(timeSpeed);


            } else {
                mPlayStatus.setVisibility(View.VISIBLE);
                mPlayStatus.setBackground(getDrawable(R.drawable.ic_pause_circle_filled));

                mPlayStatus.setBackground(getDrawable(R.drawable.ic_play_circle_filled));
                paused = true;
                mChronometer.stop();
                mChronometer.setActivated(false);

                stopAutoScrolling();


            }


            onSlideUbDowView(mAppBarLayout);


        });
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void startPlayStatus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mPlayStatus.setBackground(getDrawable(R.drawable.ic_pause_circle_filled));
        }
        mPlayStatus.postDelayed(() -> mPlayStatus.setVisibility(View.INVISIBLE), 800);

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


    private void setSpeed(int progress) {//generate suitable value of scrolling by parsing seekbar progress value
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

    private void initNavigationDrawer() {
        SeekBar mSeekScrollSpeed = mNavView.findViewById(R.id.seek_speed_up);
        SeekBar mSeekTextSize = mNavView.findViewById(R.id.seek_text_size);
        TextView OtherSetting = mNavView.findViewById(R.id.other_setting);

        final LinearLayout onClickDialogTextColor = mNavView.findViewById(R.id.ln_launch_text_color);
        final TextView defaultText = mNavView.findViewById(R.id.default_text);
        final TextView undoText = mNavView.findViewById(R.id.undo_text);

        mSeekScrollSpeed.setProgress(SharedPrefManager.getInstance(this).getPrefSpeed());
        mSeekTextSize.setProgress(SharedPrefManager.getInstance(this).getPrefTextSize());
        mTextSpeed = mNavView.findViewById(R.id.text_font);


        if (!SharedPrefManager.getInstance(DisplayActivity.this).isFirstSetText()) {
            mSeekTextSize.setProgress(20);
            mTextSpeed.setText(String.valueOf(20));

        }

        if (!SharedPrefManager.getInstance(DisplayActivity.this).isFirstSetSpeed()) {
            setSpeed(40);
            mSeekScrollSpeed.setProgress(40);

        }

        mSeekScrollSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                startPlayStatus();
                startAutoScrolling(timeSpeed);
                SharedPrefManager.getInstance(DisplayActivity.this).setFirstSetSpeed(true);
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
                SharedPrefManager.getInstance(DisplayActivity.this).setFirstSetText(true);
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

        onClickDialogTextColor.setOnClickListener(view -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                launchDlgTextColors();
            }

            if (!isOpen) {
                isOpen = true;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    launchDlgTextColors();
                }
                if (!mDialogTextColors.isShowing()) {
                    mDialogTextColors.show();
                }

            }
        });
        defaultText.setOnClickListener(view -> {
            mScrollText.setTextColor(getResources().getColor(R.color.White));
            mSlideShowScroll.setBackgroundColor(getResources().getColor(R.color.Black));
            SharedPrefManager.getInstance(DisplayActivity.this).setColorPref(false);

        });
        undoText.setOnClickListener(view -> {
            if (!SharedPrefManager.getInstance(DisplayActivity.this).isFirstSetColor()) {
                Toast.makeText(DisplayActivity.this, getResources().getString(R.string.first_set_color), Toast.LENGTH_LONG).show();

            } else {
                mScrollText.setTextColor(SharedPrefManager.getInstance(DisplayActivity.this)
                        .getPrefUndoTextSize());
                mSlideShowScroll.setBackgroundColor(SharedPrefManager.getInstance(DisplayActivity.this)
                        .getPrefUndoBackgroundColor());

            }

        });
        OtherSetting.setOnClickListener(view -> {
            Intent intent = new Intent(DisplayActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void launchDlgTextColors() {
        isDialogShow = true;
        mDialogTextColors = new Dialog(Objects.requireNonNull(this));
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(mDialogTextColors.getWindow()).getAttributes());
        lp.width = 48;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = BOTTOM | END;
        lp.windowAnimations = R.style.ToUptAnimation;
        mDialogTextColors.getWindow().setAttributes(lp);

        mDialogTextColors.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialogTextColors.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialogTextColors.setContentView(R.layout.dialog_colors);

        FloatingActionButton fab = mDialogTextColors.findViewById(R.id.cancel_text_color_dialog);
        RecyclerView mColorsRV = mDialogTextColors.findViewById(R.id.rv_colors);
        mColorsRV.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager;
        gridLayoutManager = new GridLayoutManager(this, 1);
        mColorsRV.setLayoutManager(gridLayoutManager);

        ColorsAdapter mColorAdapter = new ColorsAdapter(this, getLayoutInflater());

        mColorsRV.setAdapter(mColorAdapter);

        mColorsRV.addOnItemTouchListener(new RecyclerViewItemClickListener(this, (view, position) -> {
            SharedPrefManager.getInstance(DisplayActivity.this).setPrefUndoTextSize(
                    SharedPrefManager.getInstance(DisplayActivity.this).getPrefTextColor());
            SharedPrefManager.getInstance(DisplayActivity.this).setPrefUndoBackgroundColor(
                    SharedPrefManager.getInstance(DisplayActivity.this).getPrefBackgroundColor());

            mScrollText.setTextColor(mTextColorArray[position]);
            SharedPrefManager.getInstance(DisplayActivity.this).setPrefTextColor(mTextColorArray[position]);
            mSlideShowScroll.setBackgroundColor(mBackGroundColorArray[position]);
            SharedPrefManager.getInstance(DisplayActivity.this).setPrefBackgroundColor(mBackGroundColorArray[position]);
            SharedPrefManager.getInstance(DisplayActivity.this).setColorPref(true);
            SharedPrefManager.getInstance(DisplayActivity.this).setFirstSetColor(true);

            if (mOpenDrawer) {
                mDrawerLayout.closeDrawer(Gravity.START);
                mOpenDrawer = false;
            }
            mDialogTextColors.dismiss();

        }));

        fab.setOnClickListener(view -> {
            mDialogTextColors.dismiss();
            isDialogShow = false;
        });


        isOpen = false;
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mDialogTextColors != null) {
            if (!mDialogTextColors.isShowing()) {
                isDialogShow = false;
            }

        }
        Log.d("lifCycle", "onPause");
    }


    public void onDestroy() {
        super.onDestroy();

        clearTimerTaks(clickSchedule);
        clearTimerTaks(scrollerSchedule);
        clearTimers(scrollTimer);
        clickSchedule = null;
        scrollerSchedule = null;
        scrollTimer = null;
        mChronometer = null;

        Log.d("lifCycle", "onDestroy");

        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("lifCycle", "onResume");

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("lifCycle", "onRestart");


    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("lifCycle", "onStart");

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


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.show_play) {
            getScrollMaxAmount();
            startPlayStatus();
            startAutoScrolling(timeSpeed);
        }

    }

    @Override
    public void onDataPass(String data) {
        if (isTablet) {
            mScrollString = data;

            if (mScrollString != null) {
                mScrollText.setText(mScrollString);
                mScrollContainer.setVisibility(View.VISIBLE);
                mEmptyTextShow.setVisibility(View.GONE);

                getScrollMaxAmount();

            }
        }
    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            mCollapsingToolbarLayout.setTitleEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            mToolbar.setTitle(getResources().getString(R.string.app_name));
            if (!isTablet) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }

        }
    }


    public FragmentEditListRefreshListener getFragmentEditListRefreshListener() {
        return fragmentEditListRefreshListener;
    }

    public void setFragmentEditListRefreshListener(FragmentEditListRefreshListener fragmentEditListRefreshListener) {
        this.fragmentEditListRefreshListener = fragmentEditListRefreshListener;
    }

    public void addDemo() {


        ContentValues values = new ContentValues();
        values.put(Contract.Entry.COL_TITLE, getResources().getString(R.string.demo_title));
        values.put(Contract.Entry.COL_CONTENTS, getResources().getString(R.string.demo_text));
        values.put(Contract.Entry.COL_UNIQUE_ID, 1);

        final Uri uriInsert = getContentResolver().insert(Contract.Entry.PATH_TELEPROMPTER_URI, values);
        SharedPrefManager.getInstance(this).setFirstEntry(true);

        if (uriInsert != null) {
            Log.d("contentResolver insert", "first added success");

            values.clear();
        }
    }

    private void setupSharedPreferences() {
        // Get all of the values from shared preferences to set it up
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        setHorizontalMode(sharedPreferences.getBoolean(getString(R.string.pref_horizontal_mode_key),
                getResources().getBoolean(R.bool.pref_horizontal_mode_default)));
        seTimerShow(sharedPreferences.getBoolean(getString(R.string.pref_timer_key),
                getResources().getBoolean(R.bool.pref_timer_default)));
        setToggleMarker(sharedPreferences.getBoolean(getString(R.string.pref_toggle_marker_key),
                getResources().getBoolean(R.bool.pref_toggle_marker_default)));


        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_horizontal_mode_key))) {
            setHorizontalMode(sharedPreferences.getBoolean(key, getResources().getBoolean(R.bool.pref_horizontal_mode_default)));
        } else if (key.equals(getString(R.string.pref_timer_key))) {
            seTimerShow(sharedPreferences.getBoolean(key, getResources().getBoolean(R.bool.pref_timer_default)));
        } else if (key.equals(getString(R.string.pref_toggle_marker_key))) {
            setToggleMarker(sharedPreferences.getBoolean(key, getResources().getBoolean(R.bool.pref_toggle_marker_default)));
        }
    }


    public void seTimerShow(boolean is) {
        if (is) {
            mChronometer.setVisibility(View.VISIBLE);
        } else {
            mChronometer.setVisibility(View.GONE);

        }

    }

    private void setToggleMarker(boolean is) {

        if (is) {
            mToggleMarker.setVisibility(View.VISIBLE);
            mUpView.setVisibility(View.VISIBLE);
            mDownView.setVisibility(View.VISIBLE);
        } else {
            mToggleMarker.setVisibility(View.INVISIBLE);
            mUpView.setVisibility(View.INVISIBLE);
            mDownView.setVisibility(View.INVISIBLE);
        }

    }

    private void setHorizontalMode(boolean is) {
        if (is) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        }
    }

}
