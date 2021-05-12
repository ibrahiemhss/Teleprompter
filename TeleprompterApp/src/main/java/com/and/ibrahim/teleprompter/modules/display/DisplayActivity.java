package com.and.ibrahim.teleprompter.modules.display;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;


import com.and.ibrahim.teleprompter.callback.OnActionAd;
import com.and.ibrahim.teleprompter.callback.OnBrightnessChange;
import com.and.ibrahim.teleprompter.callback.OnPermissionStatusChange;
import com.and.ibrahim.teleprompter.callback.OnRecordVideo;
import com.and.ibrahim.teleprompter.callback.OnRotationChanged;
import com.and.ibrahim.teleprompter.callback.OnStartRecordVideoListener;
import com.and.ibrahim.teleprompter.modules.adapter.ColorsAdapter;
import com.and.ibrahim.teleprompter.mvp.view.OnScrollViewActions;
import com.and.ibrahim.teleprompter.mvp.view.PinchZoomGestureListener;
import com.and.ibrahim.teleprompter.mvp.view.SettingVideoDialogFragment;
import com.and.ibrahim.teleprompter.util.AdsUtils;
import com.and.ibrahim.teleprompter.util.DisplayUtils;
import com.and.ibrahim.teleprompter.util.CameraUtils;
import com.and.ibrahim.teleprompter.util.GLUtil;
import com.and.ibrahim.teleprompter.util.PermissionsUtils;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

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


import java.io.File;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;

import static android.view.Gravity.BOTTOM;
import static android.view.Gravity.END;

@SuppressWarnings("WeakerAccess")
public class DisplayActivity extends BaseActivity implements View.OnClickListener, OnDataPassListener, SharedPreferences.OnSharedPreferenceChangeListener, OnUserEarnedRewardListener, OnActionAd, VideoFragment.PermissionInterface, VideoFragment.SwitchInterface, VideoFragment.LowestThresholdCheckForVideoInterface, OnScrollViewActions, OnStartRecordVideoListener,
        OnPermissionStatusChange {


    private static final String TAG = "DisplayActivity";
    boolean VERBOSE = false;
    boolean allPermissionsGranted = false;
    float rotationAngle = 0f;
    OrientationEventListener orientationEventListener;
    private PermissionsUtils permissionsUtils;

    private OnBrightnessChange onBrightnessChange;


    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);

        if (fragment instanceof OnBrightnessChange) {
            onBrightnessChange = (OnBrightnessChange) fragment;
        }
    }

    public PinchZoomGestureListener getPinchZoomGestureListener() {
        return mCameraUtils.getPinchZoomGestureListener();
    }

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.parentCamera)
    protected FrameLayout mCameraContent;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.container_script)
    protected RelativeLayout containerScript;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.container_toggle)
    protected RelativeLayout mContainerToggle;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.parentScriptView)
    protected FrameLayout parentScriptView;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.display_toolbar)
    protected Toolbar mToolbar;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.display_collapsing_toolbar)
    protected CollapsingToolbarLayout mCollapsingToolbarLayout;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.display_app_bar_layout)
    protected AppBarLayout mAppBarLayout;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.text_scrolling)
    protected ScrollingTextView mScrollText;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.slide_show_scroll)
    protected ScrollView mSlideShowScroll;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.parentAll)
    protected CoordinatorLayout parentAll;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.vertical_outer_id)
    protected LinearLayout verticalOuterLayout;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.show_play)
    protected ImageView mPlayStatus;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.nav_view)
    protected NavigationView mNavView;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.drawer_layout)
    protected DrawerLayout mDrawerLayout;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.chronometer)
    protected Chronometer mChronometer;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.up_view)
    protected View mUpView;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.down_view)
    protected View mDownView;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.toggle_marker)
    protected ImageView mToggleMarker;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.adView2)
    protected AdView mAdView;
    @SuppressLint("NonConstantResourceId")
    @Nullable
    @BindView(R.id.editBrightness)
    protected LinearLayout editBrightness;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.imgBrightness)
    protected ImageView imgBrightness;

    DisplayUtils displayUtils;
    //For camera -------------------------------------------
    VideoFragment mVideoFragment = null;
    //PhotoFragment photoFragment = null;
    View warningMsgRoot;
    Dialog warningMsg;
    Button okButton;
    LayoutInflater layoutInflater;
    SharedPreferences sharedPreferences;
    View settingsRootView;
    Dialog settingsDialog;

    boolean fromGallery = false;
    static boolean isCameraInited = false;


    private TextView mEmptyTextShow;
    private FrameLayout mScrollContainer;
    private Fragment mContentListFragment;
    private TextView mTextSpeed;
    private Dialog mDialogTextColors;
    private int[] mTextColorArray;
    private int[] mBackGroundColorArray;
    private int[] mTransparentTextColorArray;
    private int[] mTransparentBackGroundColorArray;
    private boolean isOpen = false;
    private int textSpeedValue;
    private boolean mOpenDrawer;
    private String mScrollString;
    private int mScrollPos;
    private int mTimeSpeed = 30;
    private boolean isDialogShow;
    private boolean isTablet;
    private FragmentEditListRefreshListener fragmentEditListRefreshListener;
    private AdsUtils mAdUtils;
    CameraUtils mCameraUtils;
    private boolean isCameraEnable = false;
    private boolean isColorSet = false;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PermissionsUtils.ALL_PERMISSIONS) {
            if (permissions != null && permissions.length > 0) {
                permissionsUtils.getPermissionsStatus();
            }
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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

        permissionsUtils = new PermissionsUtils(this, this);

        //  parentScriptView.addView(mSlideShowScroll);

        final int[] orientation = {getResources().getConfiguration().orientation};
       /* if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int height = displayMetrics.heightPixels;
            int width = displayMetrics.widthPixels;
            verticalOuterLayout.setLayoutParams(new FrameLayout.LayoutParams(width/2, WindowManager.LayoutParams.MATCH_PARENT));
            mCameraContent.setLayoutParams(new FrameLayout.LayoutParams(width/2, WindowManager.LayoutParams.MATCH_PARENT));
            containerContents.removeAllViews();
            verticalOuterLayout.removeAllViews();
            containerContents.addView(mCameraContent);
            containerContents.addView(containerScript);
            verticalOuterLayout.addView(mScrollText);
            //mSlideShowScroll.addView(verticalOuterLayout);
            //mSlideShowScroll.addView(containerContents);

        } else {
            verticalOuterLayout.setLayoutParams(new FrameLayout.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT));
            containerContents.setLayoutParams(new CoordinatorLayout.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT));

        }*/
        orientationEventListener = new OrientationEventListener(DisplayActivity.this, SensorManager.SENSOR_DELAY_UI) {
            @Override
            public void onOrientationChanged(int i) {
                if (orientationEventListener.canDetectOrientation()) {
                    determineOrientation(i);
                }
            }
        };
        boolean isFirstEntry = SharedPrefManager.getInstance(this).isFirstEntry();
        mAdUtils = new AdsUtils(this, this);
        mAdUtils.initializeBannerAd(mAdView);
        mAdUtils.initializeInterstitialAd();
        isTablet = getResources().getBoolean(R.bool.isTablet);

        verticalOuterLayout.bringToFront();
        verticalOuterLayout.invalidate();
        mVideoFragment = VideoFragment.newInstance();
        mVideoFragment.setApplicationContext(getApplicationContext());
        displayUtils = new DisplayUtils(this, this,
                mSlideShowScroll, mChronometer, mPlayStatus, mAdUtils, mAppBarLayout, mScrollPos, isCameraEnable);
        mVideoFragment = VideoFragment.newInstance();
        mCameraUtils = new CameraUtils(mVideoFragment, this, editBrightness);

        displayUtils.scrollViewConfig();

        editBrightness.setOnClickListener(v -> {
            openBrightnessPopup();
        });
        Log.d(TAG, "saved instance state == " + savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        warningMsgRoot = layoutInflater.inflate(R.layout.warning_message, null);
        warningMsg = new Dialog(this);
        settingsRootView = layoutInflater.inflate(R.layout.brightness_settings, null);
        settingsDialog = new Dialog(this);
        sharedPreferences = getSharedPreferences(Contract.FC_SETTINGS, Context.MODE_PRIVATE);
        mScrollString = SharedPrefManager.getInstance(this).getCurrentText();
        Log.d(TAG, "get extras mScrollString == " + mScrollString);


        setVerticalOuterLayoutParams(rotationAngle, SharedPrefManager.getInstance(this).isCameraEnabled());

        Bundle extras = intent.getExtras();
       /* if (extras != null) {
            Log.d(TAG, "get extras mScrollString == " + mScrollString);
            mScrollString = extras.getString(Contract.EXTRA_TEXT);
            if(mScrollString==null){
                mScrollString = SharedPrefManager.getInstance(this).getCurrentText();
            }
            fromGallery = extras.getBoolean(Contract.EXTRA_FROM_GALLERY);
        }*/
        mCameraUtils = new CameraUtils(mVideoFragment, this, editBrightness);
        // if (savedInstanceState == null) {
        //Start with video fragment
        mPlayStatus.setVisibility(View.VISIBLE);

        if (SharedPrefManager.getInstance(this).isCameraEnabled()) {
            isCameraEnable = true;
           // mPlayStatus.setVisibility(View.INVISIBLE);


            permissionsUtils.getPermissionsStatus();
            if (!isCameraInited) {
                initCame();
            }
        } else {
           // mPlayStatus.setVisibility(View.VISIBLE);

            isCameraEnable = false;
            setCameraShow(false);

        }
        //}


        if (savedInstanceState != null) {//save state case
            displayUtils.stopAutoScrolling();
            if (isTablet) {
                mContentListFragment = getSupportFragmentManager().getFragment(savedInstanceState, Contract.EXTRA_FRAGMENT);
            }
            mScrollString = savedInstanceState.getString(Contract.EXTRA_SCROLL_STRING);
            Log.d(TAG, "saved instance state quit mScrollString == " + mScrollString);
            isDialogShow = savedInstanceState.getBoolean(Contract.EXTRA_SHOW_COLOR_DIALOG);
            mScrollPos = savedInstanceState.getInt(Contract.EXTRA_SCROLL_POS);
            mChronometer.setBase(savedInstanceState.getLong(Contract.EXTRA_CHRONOTIME));
            fromGallery = savedInstanceState.getBoolean(Contract.EXTRA_FROM_GALLERY);
            if (isDialogShow) {
                launchDlgTextColors();
            }
            if (SharedPrefManager.getInstance(this).isCameraEnabled()) {
                isCameraEnable = true;
                if (savedInstanceState.getBoolean(Contract.EXTRA_RESTART)) {
                    permissionsUtils.setShowMessage(true);
                    permissionsUtils.quitAppCam();

                }
            }
        }
        //   Fabric.with(this, new Crashlytics());

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

        setupToolbar();//in tablet screen size make main toolbar to one screen for all views

        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) mCollapsingToolbarLayout.getLayoutParams();
        params.setScrollFlags(0);  // clear all scroll flags


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
        //toStartCamera();

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void init() {
        textSpeedValue = 1;//default value generator of speed text in first open
        mTextColorArray = getResources().getIntArray(R.array.text_colors);//initialize text colors
        mBackGroundColorArray = getResources().getIntArray(R.array.background_colors);//initialize background colors
        mTransparentTextColorArray = getResources().getIntArray(R.array.transparent_text_colors);//initialize background colors;
        mTransparentBackGroundColorArray = getResources().getIntArray(R.array.transparent_background_colors);//initialize background colors;
        isColorSet = SharedPrefManager.getInstance(this).isColorPref();

        final int seekProgress = //get recorded seekbar value
                SharedPrefManager.getInstance(this).getPrefSpeed();

        mPlayStatus.//set play status of image that show playing playing status
                setBackground(this.getDrawable(R.drawable.ic_play_circle_filled));

        if (isTablet) {//listContentFragment with displayActivity in tablet will be in one screen :)
            initTabletViewWithFragment();
        }

        setViewsColors(isCameraEnable);

        displayUtils.setSpeed(seekProgress);//get suitable value of speed by bas seekbar progress

        displayUtils.clickToScrolling();//perform scrolling
        initNavigationDrawer();//initialize navigation drawer
        setupSharedPreferences();


    }

    int mBackgroundColor;
    int mTextColor;

    private void setViewsColors(boolean isCameraEnabled) {
        if (isCameraEnabled) {
            mBackgroundColor = getResources().getColor(R.color.colorTransparentBackTextBlack);
            mTextColor = getResources().getColor(R.color.colorTransparentTextBlack);
            Log.d(TAG, "onColorSet  isColorSet  isCameraEnable");

        } else if (!isColorSet) {//setting default colors of scrolling text in first opening of app
            Log.d(TAG, "onColorSet isColorSet");

            mTextColor = getResources().getColor(R.color.White);
            mBackgroundColor = getResources().getColor(R.color.Black);
        } else {//get recorded value of colors from SharedPrefManager class
            Log.d(TAG, "onColorSet not isColorSet");
            Log.d(TAG, "onColorSet not isColorSet not isCameraEnable");
            mTextColor = SharedPrefManager.getInstance(this).getPrefTextColor();
            mBackgroundColor = SharedPrefManager.getInstance(this).getPrefBackgroundColor();


        }

        mScrollText.setTextColor(mTextColor);
        verticalOuterLayout.setBackgroundColor(mBackgroundColor);
        mScrollText.setText(mScrollString);
        Log.d(TAG, "onColorSet");

    }

    @Override
    public void setListener() {

        mPlayStatus.setOnClickListener(this);
        displayUtils.getScrollMaxAmount(verticalOuterLayout);//get all scrollView amount after text length inside
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

    @SuppressLint("NonConstantResourceId")
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
                    if (mOpenDrawer) {
                        mDrawerLayout.closeDrawer(GravityCompat.START);
                        mOpenDrawer = false;
                    } else {
                        mDrawerLayout.openDrawer(GravityCompat.START);
                        mOpenDrawer = true;
                    }

                default:
                    break;
            }

        } else {
            if (item.getItemId() == R.id.action_setting) {

                if (mOpenDrawer) {
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                    mOpenDrawer = false;
                } else {
                    mDrawerLayout.openDrawer(GravityCompat.START);
                    mOpenDrawer = true;
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void walkdir(File dir) {
        String txtPattern = ".txt";
        String jpgPattern = ".jpg";
        String mp3Pattern = ".mp3";

        File[] listFile = dir.listFiles();

        if (listFile != null) {
            for (File file : listFile) {

                if (file.isDirectory()) {
                    walkdir(file);
                } else {
                    if (file.getName().endsWith(txtPattern)) {
                        //put in txt folder
                    } else if (file.getName().endsWith(jpgPattern.toLowerCase())) {
                        // put in jpg folder
                    } else if (file.getName().endsWith(mp3Pattern.toLowerCase())) {
                        // put in  mp3 folder
                    }
                }
            }
        }

    }

    boolean calledOnSaveInstanceState = false;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(Contract.EXTRA_SCROLL_STRING, mScrollString);
        outState.putBoolean(Contract.EXTRA_SHOW_COLOR_DIALOG, isDialogShow);
        outState.putInt(Contract.EXTRA_SCROLL_POS, mScrollPos);
        outState.putLong(Contract.EXTRA_CHRONOTIME, mChronometer.getBase());
        calledOnSaveInstanceState = true;
        initCame();
        if (isTablet) {//save fragment
            getSupportFragmentManager().putFragment(outState, Contract.EXTRA_FRAGMENT, mContentListFragment);
        }
        if (SharedPrefManager.getInstance(this).isCameraEnabled()) {
            outState.putAll(permissionsUtils.onRestoreInstanceState(outState));
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (SharedPrefManager.getInstance(this).isCameraEnabled()) {
            savedInstanceState.putAll(permissionsUtils.onRestoreInstanceState(savedInstanceState));

        }
        final int[] position = savedInstanceState.getIntArray(Contract.EXTRA_SCROLL_POSITION);
        if (position != null)
            if(isCameraEnable){
                mSlideShowScroll.post(() -> mSlideShowScroll.scrollTo(position[0], position[10]));

            }else{
                mSlideShowScroll.post(() -> mSlideShowScroll.scrollTo(position[0], position[1]));

            }
        super.onRestoreInstanceState(savedInstanceState);
    }


    private void initTabletViewWithFragment() {
        Bundle bundle = new Bundle();
        mContentListFragment.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.contents_container, mContentListFragment)
                .commit();
    }

    //==================================================================================================
//================================ Navigation Drawer ===============================================
//==================================================================================================
    private void initNavigationDrawer() {
        SeekBar mSeekScrollSpeed = mNavView.findViewById(R.id.seek_speed_up);
        SeekBar mSeekTextSize = mNavView.findViewById(R.id.seek_text_size);
        //SeekBar mSeekTextAlpha = mNavView.findViewById(R.id.seek_text_alpha);
        //SeekBar mSeekBackgroundAlpha = mNavView.findViewById(R.id.seek_background_alpha);

        TextView OtherSetting = mNavView.findViewById(R.id.other_setting);
        AdView adView3 = mNavView.findViewById(R.id.adView3);
        CheckBox checkboxOpenCamera = mNavView.findViewById(R.id.checkboxOpenCamera);
        LinearLayout videoSettingsContainer = mNavView.findViewById(R.id.videoSettingsContainer);
        if (SharedPrefManager.getInstance(this).isCameraEnabled()) {
            if (allPermissionsGranted) {
                videoSettingsContainer.setVisibility(View.VISIBLE);
            }
        } else {
            videoSettingsContainer.setVisibility(View.GONE);
        }
        LinearLayout videoSettings = mNavView.findViewById(R.id.videoSettings);

        final LinearLayout onClickDialogTextColor = mNavView.findViewById(R.id.ln_launch_text_color);
        LinearLayout colorsContainer = mNavView.findViewById(R.id.line4);

        if (isCameraEnable) {
            colorsContainer.setVisibility(View.GONE);
        } else {
            colorsContainer.setVisibility(View.VISIBLE);

        }
        final TextView defaultText = mNavView.findViewById(R.id.default_text);
        final TextView undoText = mNavView.findViewById(R.id.undo_text);
        mTextSpeed = mNavView.findViewById(R.id.text_font);

        checkboxOpenCamera.setChecked(SharedPrefManager.getInstance(this).isCameraEnabled());
        checkboxOpenCamera.setText(SharedPrefManager.getInstance(this).isCameraEnabled() ? getResources().getString(R.string.close_camera) : getResources().getString(R.string.open_camera));
        checkboxOpenCamera.setOnCheckedChangeListener((buttonView, isChecked) -> {
            permissionsUtils.getPermissionsStatus();

            if (isChecked) {
                Log.d(TAG, "on Checked permissions values changed == " + allPermissionsGranted);
                videoSettingsContainer.setVisibility(View.VISIBLE);
                colorsContainer.setVisibility(View.INVISIBLE);

                if (!SharedPrefManager.getInstance(this).isFirstSetPermission()){
                    SharedPrefManager.getInstance(this).setFirstSetPermission(true);
                }else{
                    if (!allPermissionsGranted) {
                        mCameraContent.setVisibility(View.GONE);
                        permissionsUtils.quitAppCam();
                    } else {
                        isLandScapeChanged = false;
                        mCameraUtils.showVideoFragment();
                        checkboxOpenCamera.setText(getResources().getString(R.string.close_camera));
                        mCameraContent.setVisibility(View.VISIBLE);
                    }

                }


            } else {
                checkboxOpenCamera.setText(getResources().getString(R.string.open_camera));
                mCameraContent.setVisibility(View.GONE);
                videoSettingsContainer.setVisibility(View.GONE);
                colorsContainer.setVisibility(View.VISIBLE);

            }
            setCameraShow(isChecked);
            setVerticalOuterLayoutParams(rotationAngle, isChecked);
        });
        videoSettings.setOnClickListener(v -> goToSettings());
        // videoSettings.setOnClickListener(v -> showSettingVideoDialogFragment());


        mAdUtils.initializeBannerAd(adView3);
        if (!SharedPrefManager.getInstance(DisplayActivity.this).isFirstSetText()) {
            mSeekTextSize.setProgress(20);
            mScrollText.setTextSize(20);

        } else {
            mSeekTextSize.setProgress(SharedPrefManager.getInstance(DisplayActivity.this).getPrefTextSize());
            mScrollText.setTextSize(SharedPrefManager.getInstance(DisplayActivity.this).getPrefTextSize());
        }

        if (!SharedPrefManager.getInstance(DisplayActivity.this).isFirstSetSpeed()) {
            displayUtils.setSpeed(40);
            mSeekScrollSpeed.setProgress(40);

        } else {
            displayUtils.setSpeed(SharedPrefManager.getInstance(DisplayActivity.this).getPrefSpeed());
            mSeekScrollSpeed.setProgress(SharedPrefManager.getInstance(DisplayActivity.this).getPrefSpeed());

        }
        /*mSeekTextAlpha.setMax(100);

        mSeekTextAlpha.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                  }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBark, int progress, boolean fromUser) {
                //mScrollText.setAlpha(progress);
                if(isCameraEnable){
                    mScrollText.setTextColor(getColorWithAlpha(SharedPrefManager.getInstance(DisplayActivity.this).getPrefTransparentTextColor(),progress));

                }else{
                    mScrollText.setTextColor(getColorWithAlpha(SharedPrefManager.getInstance(DisplayActivity.this).getPrefTextColor(),progress));

                }

              *//*  Log.d(TAG, "mScrollText.setAlpha progress  = "+progress);
                int color = getResources().getColor(SharedPrefManager.getInstance(DisplayActivity.this).getPrefTransparentTextColor());
                float red   = (color >> 16) & 0xFF;
                float green = (color >> 8)  & 0xFF;
                float blue  = (color)       & 0xFF;
                float alpha = (color >> 24) & 0xFF;*//*
            }
        });

        mSeekBackgroundAlpha.setMax(100);

        mSeekBackgroundAlpha.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                           }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onProgressChanged(SeekBar seekBark, int progress, boolean fromUser) {

                        if(isCameraEnable){
                            mScrollText.setBackgroundColor(getColorWithAlpha(SharedPrefManager.getInstance(DisplayActivity.this).getTransparentPrefBackgroundColor(),progress));

                        }else{
                            mScrollText.setBackgroundColor(getColorWithAlpha(SharedPrefManager.getInstance(DisplayActivity.this).getPrefBackgroundColor(),progress));

                        }
                    }
                });

*/
        mSeekScrollSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                displayUtils.stopAutoScrolling();
                SharedPrefManager.getInstance(DisplayActivity.this).setFirstSetSpeed(true);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                displayUtils.stopAutoScrolling();
            }

            @Override
            public void onProgressChanged(SeekBar seekBark, int progress, boolean fromUser) {
                displayUtils.setSpeed(progress);
                displayUtils.startPlayStatus();
                displayUtils.startAutoScrolling(mTimeSpeed);
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
                if (progress > 10) {
                    mScrollText.setTextSize(progress);
                    SharedPrefManager.getInstance(DisplayActivity.this).setPrefTextSize(progress);

                }

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
            if (isCameraEnable) {
                mBackgroundColor = getResources().getColor(R.color.colorTransparentBlack);
                mTextColor = getResources().getColor(R.color.colorTransparentWhite);

            } else {
                mTextColor = getResources().getColor(R.color.White);
                mBackgroundColor = getResources().getColor(R.color.Black);

            }
            SharedPrefManager.getInstance(DisplayActivity.this).setColorPref(false);
            //setViewsColors(isCameraEnable);
        });
        undoText.setOnClickListener(view -> {
            if (!SharedPrefManager.getInstance(DisplayActivity.this).isFirstSetColor()) {
                //Toast.makeText(DisplayActivity.this, getResources().getString(R.string.first_set_color), Toast.LENGTH_LONG).show();

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

    private int getColorWithAlpha(int color, int ratio) {
        Log.d(TAG, "ratio progress =" + ratio);

        int newColor = 0;
        int alpha = Math.round(Color.alpha(color) * ratio);
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        Log.d(TAG, "alpha =" + alpha);
        Log.d(TAG, "r =" + r);
        Log.d(TAG, "g =" + g);
        Log.d(TAG, "b =" + b);

        newColor = Color.argb(ratio, r, g, b);

        return newColor;
    }

    private float getAlphaFloat(int ratio) {
        // float alpha=0.9f;

        switch (ratio) {
            case 1:
                return 1.9f;
            case 2:
                return 1.9f;
            case 3:
                return 2.9f;
            case 4:
                return 3.9f;
            case 5:
                return 4.9f;
            case 6:
                return 5.9f;
            case 7:
                return 6.9f;
            case 8:
                return 7.9f;
            case 9:
                return 8.9f;
            case 10:
                return 9.9f;
            default:
                return 0.9f;
        }

    }

    public int getBrightnessLevel() {
        return mCameraUtils.getBrightnessLevel();
    }

    @SuppressLint("WrongConstant")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void launchDlgTextColors() {
        isDialogShow = true;
        mDialogTextColors = new Dialog(this);
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
        orientationEventListener.disable();

        if (this.mVideoFragment != null) {
            if (this.mVideoFragment.getZoomBar() != null) {
                this.mVideoFragment.getZoomBar().setProgress(0);
            }
        }

        if (SharedPrefManager.getInstance(this).isCameraEnabled()) {
            if (mCameraUtils != null) {
                mCameraUtils.resetPinchZoomGestureListener();
            }
        }
        if (mAdUtils != null) {
            mAdUtils.dispose("onPause");
        }
        if (mDialogTextColors != null) {
            if (!mDialogTextColors.isShowing()) {
                isDialogShow = false;
            }

        }
    }

    public void onDestroy() {
        super.onDestroy();

        if (displayUtils != null) {
            displayUtils.dispose("onDestroy");
        }
        if (mAdUtils != null) {
            mAdUtils.dispose("onDestroy");
        }
        Log.d("lifCycle", "onDestroy");

        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //if(!isCameraInited){
        Log.d("lifCycle", "onResume = initedCame");
        orientationEventListener.enable();

        initCame();
        //}
        Log.d("lifCycle", "onResume");

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        // if(!isCameraInited){
        Log.d("lifCycle", "onRestart = initedCame");

        initCame();
        //  }
        Log.d("lifCycle", "onRestart");


    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("lifCycle", "onStart");
        if (!isCameraInited) {
            Log.d("lifCycle", "onStart = initedCame");

            initCame();
        }

    }


    void initCame() {
        if (SharedPrefManager.getInstance(this).isCameraEnabled()) {
            if (!calledOnSaveInstanceState) {
                mCameraUtils.showVideoFragment();
            }

            setCameraShow(true);
            isCameraInited = true;
        }
    }

    //==================================================================================================
//================================ onClick All Views ===============================================
//==================================================================================================
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.show_play) {
            displayUtils.getScrollMaxAmount(verticalOuterLayout);
            displayUtils.startPlayStatus();
            displayUtils.startAutoScrolling(mTimeSpeed);

        }

    }

    @Override
    public void onStartRecord() {
        Log.d(TAG, "onStartRecord");

        displayUtils.getScrollMaxAmount(verticalOuterLayout);
        displayUtils.startPlayStatus();
        displayUtils.startAutoScrolling(mTimeSpeed);
    }

    @Override
    public void onStopRecord() {
        Log.d(TAG, "onStopRecord");
        displayUtils.stopAutoScrolling();
    }


    public void goToSettings() {
        Log.d(TAG, "goToSettings clicked");

        Intent settingsIntent = new Intent(this, SettingsCameraActivity.class);
        startActivity(settingsIntent);
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    private void showSettingVideoDialogFragment() {

        FragmentManager fm = getSupportFragmentManager();
        SettingVideoDialogFragment settingVideoDialogFragment = SettingVideoDialogFragment.newInstance("Some Title");
        settingVideoDialogFragment.show(fm, "fragment_edit_name");

    }

    public void openBrightnessPopup() {
        TextView header = (TextView) settingsRootView.findViewById(R.id.timerText);
        header.setText(getResources().getString(R.string.brightnessHeading));
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        settingsDialog.setContentView(settingsRootView);
        settingsDialog.setCancelable(true);
        WindowManager.LayoutParams lp = settingsDialog.getWindow().getAttributes();
        lp.dimAmount = 0.0f;
        lp.width = (int) (size.x * 0.8);
        final SeekBar brightnessBar = (SeekBar) settingsRootView.findViewById(R.id.brightnessBar);
        brightnessBar.setMax(10);
        brightnessBar.setProgress(mCameraUtils.getBrightnessLevel());
        // onBrightnessChange.brightness(mCameraUtils.getBrightnessLevel());
        if (mCameraUtils.getBrightnessLevel() >= 5) {
            imgBrightness.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_brightness_high_24));
        } else {
            imgBrightness.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_brightness_low_24));

        }
        brightnessBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress >= 5) {
                    imgBrightness.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_brightness_high_24));
                } else {
                    imgBrightness.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_brightness_low_24));

                }
                // onBrightnessChange.brightness(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // onBrightnessChange.brightness(mCameraUtils.getBrightnessLevel());
                if (mCameraUtils.getBrightnessLevel() >= 5) {
                    imgBrightness.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_brightness_high_24));
                } else {
                    imgBrightness.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_brightness_low_24));

                }
                seekBar.setProgress(mCameraUtils.getBrightnessLevel());
            }
        });
        Button increaseBrightness = (Button) settingsRootView.findViewById(R.id.increaseBrightness);
        increaseBrightness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (GLUtil.colorVal < 0.25f) {
                    GLUtil.colorVal += 0.05f;
                    brightnessBar.incrementProgressBy(1);
                    mCameraUtils.setBrightnessLevel(brightnessBar.getProgress());
                } else {
                    GLUtil.colorVal = 0.25f;
                }
                mCameraUtils.setBrightnessProgress(GLUtil.colorVal);

            }
        });
        Button decreaseBrightness = (Button) settingsRootView.findViewById(R.id.setTimer);
        decreaseBrightness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (GLUtil.colorVal > -0.25f) {
                    GLUtil.colorVal -= 0.05f;
                    brightnessBar.incrementProgressBy(-1);
                    mCameraUtils.setBrightnessLevel(brightnessBar.getProgress());
                } else {
                    GLUtil.colorVal = -0.25f;
                }
                mCameraUtils.setBrightnessProgress(GLUtil.colorVal);
            }
        });
        //settingsDialog.getWindow().setBackgroundDrawableResource(R.color.backColorSettingPopup);
        settingsDialog.show();
    }

    @Override
    public void onDataPass(String data) {
        if (isTablet) {
            mScrollString = data;

            if (mScrollString != null) {
                mScrollText.setText(mScrollString);
                mScrollContainer.setVisibility(View.VISIBLE);
                mEmptyTextShow.setVisibility(View.GONE);

                displayUtils.getScrollMaxAmount(verticalOuterLayout);

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
                getResources().getBoolean(R.bool.horizontal_mode_default)));
        //setCameraShow(sharedPreferences.getBoolean(getString(R.string.open_camera), getResources().getBoolean(R.bool.open_camera_default)));
        seTimerShow(sharedPreferences.getBoolean(getString(R.string.pref_timer_key),
                getResources().getBoolean(R.bool.timer_default)));
        setToggleMarker(sharedPreferences.getBoolean(getString(R.string.pref_toggle_marker_key),
                getResources().getBoolean(R.bool.toggle_marker_default)));


        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_horizontal_mode_key))) {
            setHorizontalMode(sharedPreferences.getBoolean(key, getResources().getBoolean(R.bool.horizontal_mode_default)));
        } else if (key.equals(getString(R.string.pref_timer_key))) {
            seTimerShow(sharedPreferences.getBoolean(key, getResources().getBoolean(R.bool.timer_default)));
        } else if (key.equals(getString(R.string.pref_toggle_marker_key))) {
            setToggleMarker(sharedPreferences.getBoolean(key, getResources().getBoolean(R.bool.toggle_marker_default)));
        }/*else if (key.equals(getString(R.string.open_camera))) {
            setCameraShow(sharedPreferences.getBoolean(key, getResources().getBoolean(R.bool.open_camera_default)));
        }*/
    }


    public void seTimerShow(boolean is) {
        if (isCameraEnable) {
            mChronometer.setVisibility(View.GONE);

        } else {
            if (is) {
                mChronometer.setVisibility(View.VISIBLE);
            } else {
                mChronometer.setVisibility(View.GONE);

            }
        }

    }

    public void setCameraShow(boolean is) {
        SharedPrefManager.getInstance(this).setCameraEnabled(is);
        if (is) {

            mCameraContent.setVisibility(View.VISIBLE);
        } else {

            mCameraContent.setVisibility(View.GONE);

        }
        setViewsColors(is);
    }

    boolean isLandScapeChanged = false;

    void changeToLandScapeUi() {
        isLandScapeChanged = true;
        initCame();
        if (!isTablet) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int height = displayMetrics.heightPixels;
            int width = displayMetrics.widthPixels;
            CoordinatorLayout.LayoutParams cameraContentParams = (new CoordinatorLayout.LayoutParams((int) (width / 2.2), WindowManager.LayoutParams.MATCH_PARENT));
            cameraContentParams.setMargins(10, width / 9, width / 50, 10);
            mCameraContent.setForegroundGravity(BOTTOM);
            cameraContentParams.setMarginStart(width / 2);
            mCameraContent.setLayoutParams(cameraContentParams);


            FrameLayout.LayoutParams verticalOuterLayoutParams = (new FrameLayout.LayoutParams(width / 2, WindowManager.LayoutParams.MATCH_PARENT));
            verticalOuterLayoutParams.setMargins(width / 50, 0, width / 50, 0);

            verticalOuterLayout.setLayoutParams(verticalOuterLayoutParams);
            //params.setMarginStart(width/2);
            //mCameraContent.setForegroundGravity( Gravity.START );
            verticalOuterLayout.setGravity(END);
            // containerContents.removeAllViews();
            verticalOuterLayout.removeAllViews();

            if (containerScript.getParent() != null) {
                ((ViewGroup) containerScript.getParent()).removeView(containerScript); // <- fix
            }
            if (mCameraContent.getParent() != null) {
                ((ViewGroup) mCameraContent.getParent()).removeView(mCameraContent); // <- fix
            }
            if (mScrollText.getParent() != null) {
                ((ViewGroup) mScrollText.getParent()).removeView(mScrollText); // <- fix
            }
            //  containerContents.addView(containerScript);
            // containerContents.addView(mCameraContent);
            mScrollText.setText(mScrollString);
            verticalOuterLayout.addView(mScrollText);
        }
    }

    public void changeViewRotation(float rotationAngle) {
        // verticalOuterLayout.setRotation(rotationAngle);

        //  mScrollContainer.setRotation(rotationAngle);
        mSlideShowScroll.setRotation(rotationAngle);
        // mScrollText.setRotation(rotationAngle);

        mPlayStatus.setRotation(rotationAngle);
    }

    private void setToggleMarker(boolean is) {
        if (isCameraEnable) {
            mToggleMarker.setVisibility(View.INVISIBLE);
            mUpView.setVisibility(View.INVISIBLE);
            mDownView.setVisibility(View.INVISIBLE);
        } else if (is) {
            mToggleMarker.setVisibility(View.VISIBLE);
            mUpView.setVisibility(View.VISIBLE);
            mDownView.setVisibility(View.VISIBLE);
        } else {
            mToggleMarker.setVisibility(View.INVISIBLE);
            mUpView.setVisibility(View.INVISIBLE);
            mDownView.setVisibility(View.INVISIBLE);
        }
        /*if (rotationAngle == 270.0 || rotationAngle == 90.0){
            mToggleMarker.setVisibility(View.INVISIBLE);
            mUpView.setVisibility(View.INVISIBLE);
            mDownView.setVisibility(View.INVISIBLE);
        }else{
             if (is) {
            mToggleMarker.setVisibility(View.VISIBLE);
            mUpView.setVisibility(View.VISIBLE);
            mDownView.setVisibility(View.VISIBLE);
        } else {
            mToggleMarker.setVisibility(View.INVISIBLE);
            mUpView.setVisibility(View.INVISIBLE);
            mDownView.setVisibility(View.INVISIBLE);
        }
        }*/


    }

    private void setHorizontalMode(boolean is) {
        if (is) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        }
    }


    @Override
    public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
        Log.i(TAG, "onUserEarnedReward");
    }

    @Override
    public void onClose(int type) {

    }


    @Override
    public void switchToPhoto() {

    }

    @Override
    public boolean checkIfPhoneMemoryIsBelowLowestThresholdForVideo() {
        return mCameraUtils.checkIfPhoneMemoryIsBelowLowThreshold();
    }

    @Override
    public void action(int timeSpeed) {
        mTimeSpeed = timeSpeed;
        Log.d(TAG, "on action timeSpeed=" + timeSpeed);

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (SharedPrefManager.getInstance(this).isCameraEnabled()) {
            mCameraUtils.onTouchGestureDetector(event);
        }
        return true;
    }

    @Override
    public void askPermission() {
        askCameraPermission();
    }


    public void askCameraPermission() {
        Log.d(TAG, "start permission act to get permissions");
        permissionsUtils.getPermissionsStatus();

    }


    @Override
    public void onChanged(boolean status) {
        allPermissionsGranted = status;
        Log.d(TAG, "on permissions values changed == " + allPermissionsGranted);

        setCameraShow(status);
        if (!status) {
            permissionsUtils.quitAppCam();
        }
    }


    public void determineOrientation(int orientation) {
        if (orientation != -1) {
            if (((orientation >= 315 && orientation <= 360) || (orientation >= 0 && orientation <= 45)) || (orientation >= 135 && orientation <= 195)) {
                if (orientation >= 135 && orientation <= 195) {
                    //Reverse portrait
                    rotationAngle = 180f;
                } else {
                    //Portrait
                    rotationAngle = 0f;
                }
            } else {
                if (orientation >= 46 && orientation <= 134) {
                    //Reverse Landscape
                    rotationAngle = 270f;
                } else {
                    //Landscape
                    rotationAngle = 90f;
                }
            }
        }

        setVerticalOuterLayoutParams(rotationAngle, SharedPrefManager.getInstance(this).isCameraEnabled());
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    void setVerticalOuterLayoutParams(float val, boolean isCameraEnabled) {
        Log.d(TAG, "onRotationChanged angle =" + val);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        Log.d(TAG, "height  =" + height);
        Log.d(TAG, "width  =" + width);

        FrameLayout.LayoutParams verticalOuterLayoutParams;
        CoordinatorLayout.LayoutParams containerScriptLayoutParams;
        FrameLayout.LayoutParams slideShowScrollLayoutParams;
        // RelativeLayout.LayoutParams upViewLayoutParams;
        // RelativeLayout.LayoutParams downViewLayoutParams;

        if (!isTablet) {
            if (isCameraEnabled) {
                mPlayStatus.setVisibility(View.INVISIBLE);
                mDownView.setVisibility(View.INVISIBLE);
                mToggleMarker.setVisibility(View.INVISIBLE);
                mUpView.setVisibility(View.INVISIBLE);

                // add some color
                // You can add your random color generator here
                // and set color


                slideShowScrollLayoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.MATCH_PARENT);
                //containerScriptLayoutParams = new CoordinatorLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.MATCH_PARENT);
               // verticalOuterLayoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.MATCH_PARENT);

                slideShowScrollLayoutParams.setMargins(width / 30, height / 10, width / 30, (int) (height / 2.4));
                // containerScriptLayoutParams.setMargins(0, 0, 0, height / 2);
               //  verticalOuterLayoutParams.setMargins(0, 0, 0, (int) (height/1.2));

               // verticalOuterLayout.setLayoutParams(verticalOuterLayoutParams);
                mSlideShowScroll.setLayoutParams(slideShowScrollLayoutParams);
                mSlideShowScroll.setBackgroundResource(R.drawable.tags_rounded_corners);
                GradientDrawable drawable = (GradientDrawable) mSlideShowScroll.getBackground();
                drawable.setColor(getResources().getColor(R.color.colorTransparent));
                verticalOuterLayout.removeView(mScrollText);
                verticalOuterLayout.setBackgroundColor(getResources().getColor(R.color.colorTransparentBackTextBlack));
                verticalOuterLayout.addView(mScrollText);
               // containerScript.setLayoutParams(containerScriptLayoutParams);
            } else {
                verticalOuterLayoutParams = new FrameLayout.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                slideShowScrollLayoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.MATCH_PARENT);
                containerScriptLayoutParams = new CoordinatorLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, CoordinatorLayout.LayoutParams.MATCH_PARENT);

                slideShowScrollLayoutParams.setMargins(0, 0, 0, 0);
                containerScriptLayoutParams.setMargins(0, 0, 0, 0);

                //verticalOuterLayoutParams.setMargins(0, 0, 0, 0);

                mSlideShowScroll.setLayoutParams(slideShowScrollLayoutParams);
                verticalOuterLayout.setLayoutParams(verticalOuterLayoutParams);
                containerScript.setLayoutParams(containerScriptLayoutParams);


            }

        }
        setViewsColors(isCameraEnabled);
    }


       /* if (!isTablet) {
            if(isCameraEnabled){

                if (val != 180.0) {
                    // mPlayStatus.setRotation(val);
                    mSlideShowScroll.setRotation(val);
                    // mContainerToggle.setRotation(val);
                    containerScript.setRotation(val);
                    //verticalOuterLayout.removeAllViews();
                    if (val == 270.0 || val == 90.0) {
                        mDownView.setVisibility(View.INVISIBLE);
                        mToggleMarker.setVisibility(View.INVISIBLE);
                        mUpView.setVisibility(View.INVISIBLE);

                        containerScriptLayoutParams = (new CoordinatorLayout.LayoutParams(height / 2, CoordinatorLayout.LayoutParams.MATCH_PARENT));
                        // containerToggleLayoutParams = (new RelativeLayout.LayoutParams(height,width));
                        //containerToggleLayoutParams.setMargins(0,width/4, width/4, 0);
                        //upViewLayoutParams = (new RelativeLayout.LayoutParams(width,height/2));
                        // downViewLayoutParams = (new RelativeLayout.LayoutParams(width, (int) getResources().getDimension(R.dimen.down_view_height)));
                        //  mUpView.setBackgroundColor(getResources().getColor(R.color.lime_green));
                        //  mDownView.setBackgroundColor(getResources().getColor(R.color.colorAccentDark));

                  *//*  if (isCameraEnabled) {
                        mSlideShowScroll.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,width));

                        Log.d(TAG, "360 landscape angle ="+val);
                        Log.d(TAG, "on change rotation isCameraEnabled  ="+val);

                        verticalOuterLayoutParams = (new FrameLayout.LayoutParams((int) (height/3), WindowManager.LayoutParams.MATCH_PARENT));

                        if(val==270.0){
                            verticalOuterLayoutParams.setMargins((int) (height/9),0, height/10, 0);
                        }else if(val==90.0){
                            verticalOuterLayoutParams.setMargins(height/10,0, height/10, 0);
                        }
                    }else{

                        Log.d(TAG, "on change rotation isCameraDisabled  ="+val);

                        verticalOuterLayoutParams = (new FrameLayout.LayoutParams( WindowManager.LayoutParams.WRAP_CONTENT,  WindowManager.LayoutParams.MATCH_PARENT));
                        if(val==270.0){
                            verticalOuterLayoutParams.setMargins(height/80,0, height/3, 0);
                        }else if(val==90.0){
                            verticalOuterLayoutParams.setMargins(height/10,height/10, height/10, 0);
                        }
                        mSlideShowScroll.setLayoutParams(new FrameLayout.LayoutParams( height-3, FrameLayout.LayoutParams.MATCH_PARENT));

                    }
*//*
                        Log.d(TAG, "on change rotation isCameraDisabled  =" + val);
                        slideShowScrollLayoutParams = new FrameLayout.LayoutParams((int) (height / 1.4), FrameLayout.LayoutParams.MATCH_PARENT);
                        //slideShowScrollLayoutParams.setMargins(height/8,0, height/8, 0);
                        verticalOuterLayoutParams = (new FrameLayout.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.MATCH_PARENT));
                        if (val == 270.0) {
                            verticalOuterLayoutParams.setMargins(height / 80, 0, height / 3, 0);
                        } else if (val == 90.0) {
                            verticalOuterLayoutParams.setMargins(height / 10, height / 10, height / 10, 0);

                        }

                        mSlideShowScroll.setLayoutParams(slideShowScrollLayoutParams);

                        verticalOuterLayout.setLayoutParams(verticalOuterLayoutParams);
                        //mContainerToggle.setLayoutParams(containerToggleLayoutParams);
                        containerScript.setLayoutParams(containerScriptLayoutParams);
                        //mUpView.setLayoutParams(upViewLayoutParams);
                        //mDownView.setLayoutParams(downViewLayoutParams);

                        // verticalOuterLayout.setGravity( END );
                        //* verticalOuterLayout.removeAllViews();
                        // mScrollText.setText(mScrollString);
                        // verticalOuterLayout.addView(mScrollText);
                    } else {
                        slideShowScrollLayoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.MATCH_PARENT);

                        slideShowScrollLayoutParams.setMargins(0, height / 20, 0, height / 8);

                        verticalOuterLayout.setLayoutParams(new FrameLayout.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT));
                        // containerContents.setLayoutParams(new CoordinatorLayout.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT));
                        mSlideShowScroll.setLayoutParams(slideShowScrollLayoutParams);

                    }
                } else {
                    slideShowScrollLayoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.MATCH_PARENT);
                    slideShowScrollLayoutParams.setMargins(0, height / 20, 0, height / 8);
                    mSlideShowScroll.setLayoutParams(slideShowScrollLayoutParams);

                    verticalOuterLayout.setLayoutParams(new FrameLayout.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT));

                    if (isCameraEnabled) {
                        mSlideShowScroll.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, height));
                    } else {
                        mSlideShowScroll.setLayoutParams(new FrameLayout.LayoutParams(width, FrameLayout.LayoutParams.MATCH_PARENT));

                    }
                    mPlayStatus.setRotation(0);
                    mSlideShowScroll.setRotation(0);
                    mScrollText.setRotation(0);
                    mContainerToggle.setRotation(0);
                    mSlideShowScroll.setRotation(0);
                    // mContainerToggle.setRotation(val);
                    containerScript.setRotation(0);

                }
            }else{
                verticalOuterLayoutParams = (new FrameLayout.LayoutParams( WindowManager.LayoutParams.WRAP_CONTENT,  WindowManager.LayoutParams.MATCH_PARENT));
                if(val==270.0){
                    verticalOuterLayoutParams.setMargins(height/80,0, height/3, 0);
                }else if(val==90.0){
                    verticalOuterLayoutParams.setMargins(height/10,height/10, height/10, 0);
                }
                mSlideShowScroll.setLayoutParams(new FrameLayout.LayoutParams( height-3, FrameLayout.LayoutParams.MATCH_PARENT));

            }
        }*/


}
