package com.and.ibrahim.teleprompter;

import android.app.Application;

import com.and.ibrahim.teleprompter.data.Contract;

import java.io.Serializable;

public class ControlVisbilityPreference extends Application implements Serializable {

    private boolean hideControl;
    private int mediaSelectedPosition;
    private int brightnessLevel = Contract.NORMAL_BRIGHTNESS;
    private float brightnessProgress = Contract.NORMAL_BRIGHTNESS_PROGRESS;
    private boolean fromGallery = false;

    public boolean isFromGallery() {
        return fromGallery;
    }

    public void setFromGallery(boolean fromGallery) {
        this.fromGallery = fromGallery;
    }

    public float getBrightnessProgress() {
        return brightnessProgress;
    }

    public void setBrightnessProgress(float brightnessProgress) {
        this.brightnessProgress = brightnessProgress;
    }

    public int getBrightnessLevel() {
        return brightnessLevel;
    }

    public void setBrightnessLevel(int brightnessLevel) {
        this.brightnessLevel = brightnessLevel;
    }

    public int getMediaSelectedPosition() {
        return mediaSelectedPosition;
    }

    public void setMediaSelectedPosition(int mediaSelectedPosition) {
        this.mediaSelectedPosition = mediaSelectedPosition;
    }

    public boolean isHideControl() {
        return hideControl;
    }

    public void setHideControl(boolean hideControl) {
        this.hideControl = hideControl;
    }
}
