package com.and.ibrahim.teleprompter.modules.setting;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.and.ibrahim.teleprompter.R;
import com.and.ibrahim.teleprompter.data.SharedPrefManager;

import butterknife.BindView;

public class SettingDialog extends Dialog implements
        android.view.View.OnClickListener {

    @BindView(R.id.seek_speed_up)
    protected SeekBar mSeekScrollSpeed;
    @BindView(R.id.seek_text_size)
    protected SeekBar mSeekTextSize;
    @BindView(R.id.ln_launch_text_color)
    protected LinearLayout onClickDialogTextColor;
    @BindView(R.id.cancel_edit_dialog)
    protected Button cancelBtn;
    @BindView(R.id.text_font)
    protected TextView mTextSpeed;
    private SeekBar.OnSeekBarChangeListener mSpeedUpSeekBarListener;
    private SeekBar.OnSeekBarChangeListener mResizeTextSeekBarListener;
    private int timeSpeed;

    public Activity mContext;

    public SettingDialog(Activity a) {
        super(a);
        this.mContext = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog_edit_scroll);

        onSeekBarChanProgress();
        mSeekTextSize.setOnSeekBarChangeListener(mResizeTextSeekBarListener);
        mSeekScrollSpeed.setOnSeekBarChangeListener(mSpeedUpSeekBarListener);
        onClickDialogTextColor.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
        mTextSpeed.setOnClickListener(this);



    }

    private void onSeekBarChanProgress() {


        mSpeedUpSeekBarListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
               // startAutoScrolling(timeSpeed);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //stopAutoScrolling();
            }

            @Override
            public void onProgressChanged(SeekBar seekBark, int progress, boolean fromUser) {
                setSpeed(progress);
                mTextSpeed.setText(String.valueOf(progress));
                SharedPrefManager.getInstance(mContext).setPrefSpeed(progress);
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
               // mScrollText.setTextSize(progress);
                SharedPrefManager.getInstance(mContext).setPrefTextSize(progress);

            }
        };

}

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.seek_speed_up:
                break;
            case R.id.seek_text_size:
                break;
            case R.id.ln_launch_text_color:
                mContext.finish();
                break;
            case R.id.cancel_edit_dialog:
                mContext.finish();
                break;
            case R.id.text_font:
                mContext.finish();
                break;
            default:
                break;
        }
        dismiss();
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
}
