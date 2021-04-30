package com.and.ibrahim.teleprompter.mvp.view;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.and.ibrahim.teleprompter.R;
import com.and.ibrahim.teleprompter.data.SharedPrefManager;
import com.and.ibrahim.teleprompter.modules.adapter.ColorsAdapter;
import com.and.ibrahim.teleprompter.modules.display.DisplayActivity;
import com.and.ibrahim.teleprompter.modules.setting.SettingsActivity;
import com.and.ibrahim.teleprompter.util.AdsUtils;
import com.and.ibrahim.teleprompter.util.CameraUtils;
import com.and.ibrahim.teleprompter.util.DisplayUtils;
import com.and.ibrahim.teleprompter.util.ScrollingTextView;
import com.google.android.gms.ads.AdView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

import static android.view.Gravity.BOTTOM;
import static android.view.Gravity.END;

public class DisplayNavView {
    final NavigationView mNavView;
    final Context mContext;

    public DisplayNavView(NavigationView mNavView, Context mContext) {
        this.mNavView = mNavView;
        this.mContext = mContext;
    }
/*    private void initNavigationDrawer(TextView mTextSpeed, ScrollView mSlideShowScroll,CameraUtils mCameraUtils, LinearLayout mVideoBrightness, ScrollingTextView mScrollText, DisplayUtils displayUtils, AdsUtils mAdUtils, int textSpeedValue, int mTimeSpeed, boolean isOpen) {
        SeekBar mSeekScrollSpeed = mNavView.findViewById(R.id.seek_speed_up);
        SeekBar mSeekTextSize = mNavView.findViewById(R.id.seek_text_size);
        TextView OtherSetting = mNavView.findViewById(R.id.other_setting);
        AdView adView3 = mNavView.findViewById(R.id.adView3);
        CheckBox checkboxOpenCamera = mNavView.findViewById(R.id.checkboxOpenCamera);
        LinearLayout videoSettingsContainer = mNavView.findViewById(R.id.videoSettingsContainer);
        LinearLayout videoSettings = mNavView.findViewById(R.id.videoSettings);

        final LinearLayout onClickDialogTextColor = mNavView.findViewById(R.id.ln_launch_text_color);
        final TextView defaultText = mNavView.findViewById(R.id.default_text);
        final TextView undoText = mNavView.findViewById(R.id.undo_text);
        mTextSpeed = mNavView.findViewById(R.id.text_font);
        checkboxOpenCamera.setChecked(SharedPrefManager.getInstance(mContext).isCameraEnabled());
        checkboxOpenCamera.setText(SharedPrefManager.getInstance(mContext).isCameraEnabled() ? mContext.getResources().getString(R.string.close_camera) : mContext.getResources().getString(R.string.open_camera));
        checkboxOpenCamera.setOnCheckedChangeListener((buttonView, isChecked) -> {
            setCameraShow(isChecked);
            if (isChecked) {
                mCameraUtils.showVideoFragment();
                checkboxOpenCamera.setText(mContext.getResources().getString(R.string.close_camera));
                videoSettingsContainer.setVisibility(View.VISIBLE);
            } else {
                checkboxOpenCamera.setText(mContext.getResources().getString(R.string.open_camera));
                videoSettingsContainer.setVisibility(View.GONE);

            }

        });
        videoSettings.setOnClickListener(v -> goToSettings());
        mVideoBrightness.setOnClickListener(v -> openBrightnessPopup());

        mAdUtils.initializeBannerAd(adView3);
        if (!SharedPrefManager.getInstance(mContext).isFirstSetText()) {
            mSeekTextSize.setProgress(20);
            mScrollText.setTextSize(20);

        } else {
            mSeekTextSize.setProgress(SharedPrefManager.getInstance(mContext).getPrefTextSize());
            mScrollText.setTextSize(SharedPrefManager.getInstance(mContext).getPrefTextSize());
        }

        if (!SharedPrefManager.getInstance(mContext).isFirstSetSpeed()) {
            displayUtils.setSpeed(40);
            mSeekScrollSpeed.setProgress(40);

        } else {
            displayUtils.setSpeed(SharedPrefManager.getInstance(mContext).getPrefSpeed());
            mSeekScrollSpeed.setProgress(SharedPrefManager.getInstance(mContext).getPrefSpeed());

        }

        mSeekScrollSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                displayUtils.stopAutoScrolling();
                SharedPrefManager.getInstance(mContext).setFirstSetSpeed(true);
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
                SharedPrefManager.getInstance(mContext).setPrefSpeed(progress);
            }
        });


        mSeekTextSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                SharedPrefManager.getInstance(mContext).setFirstSetText(true);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBark, int progress, boolean fromUser) {
                if (progress > 10) {
                    mScrollText.setTextSize(progress);
                    SharedPrefManager.getInstance(mContext).setPrefTextSize(progress);

                }

            }
        });


        textSpeedValue = SharedPrefManager.getInstance(mContext).getPrefSpeed();
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
            } else {
                return;
            }

        });
        defaultText.setOnClickListener(view -> {
            mScrollText.setTextColor(mContext.getResources().getColor(R.color.White));
            mSlideShowScroll.setBackgroundColor(mContext.getResources().getColor(R.color.Black));
            SharedPrefManager.getInstance(mContext).setColorPref(false);

        });
        undoText.setOnClickListener(view -> {
            if (!SharedPrefManager.getInstance(mContext).isFirstSetColor()) {
                //Toast.makeText(DisplayActivity.this, getResources().getString(R.string.first_set_color), Toast.LENGTH_LONG).show();

            } else {
                mScrollText.setTextColor(SharedPrefManager.getInstance(mContext)
                        .getPrefUndoTextSize());
                mSlideShowScroll.setBackgroundColor(SharedPrefManager.getInstance(mContext)
                        .getPrefUndoBackgroundColor());

            }

        });
        OtherSetting.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, SettingsActivity.class);
            mContext.startActivity(intent);
        });

    }*/

}
