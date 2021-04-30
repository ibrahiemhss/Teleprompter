package com.and.ibrahim.teleprompter.util;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.and.ibrahim.teleprompter.ControlVisbilityPreference;
import com.and.ibrahim.teleprompter.R;
import com.and.ibrahim.teleprompter.data.Contract;
import com.and.ibrahim.teleprompter.data.SharedPrefManager;
import com.and.ibrahim.teleprompter.modules.display.VideoFragment;
import com.and.ibrahim.teleprompter.mvp.view.PinchZoomGestureListener;

public class CameraUtils {


    final String TAG = "CameraUtils";
    boolean mCameraPermission = false;
    boolean mAudioPermission = false;
    boolean mStoragePermission = false;
    boolean mShowMessage = false;
    boolean mShowPermission = false;
    boolean mFromGallery = false;
    DialogInterface.OnClickListener mExitListener;
    AlertDialog.Builder mAlertDialog;
    ControlVisbilityPreference controlVisbilityPreference;

    final VideoFragment mVideoFragment;
    View mSettingsRootView;
    Dialog mSettingsDialog;
    View mWarningMsgRoot;
    Dialog mWarningMsg;
    Button mOkButton;
    LayoutInflater mLayoutInflater;


    public CameraUtils(VideoFragment mVideoFragment, Context mContext, LinearLayout brightness) {
        this.mVideoFragment = mVideoFragment;
        this.mContext = mContext;
        this.brightness = brightness;
        controlVisbilityPreference = (ControlVisbilityPreference) mContext.getApplicationContext();
    }

    Context mContext;
    LinearLayout brightness;
    PinchZoomGestureListener pinchZoomGestureListener;
    ScaleGestureDetector scaleGestureDetector;
    public int getBrightnessLevel() {
        return  controlVisbilityPreference.getBrightnessLevel();
    }
    public void setBrightnessLevel(int val) {
        controlVisbilityPreference.setBrightnessLevel(val);
    }
    public void setBrightnessProgress(float val) {
        controlVisbilityPreference.setBrightnessProgress(val);
    }

    public void resetPinchZoomGestureListener() {
        pinchZoomGestureListener.setProgress(0);

    }
    public PinchZoomGestureListener getPinchZoomGestureListener() {
        return pinchZoomGestureListener;
    }

    public void setPinchZoomScaleListener(VideoFragment videoFragment){
        if(pinchZoomGestureListener != null){
            pinchZoomGestureListener = null;
        }
        pinchZoomGestureListener = new PinchZoomGestureListener(mContext.getApplicationContext(), videoFragment);
        scaleGestureDetector = new ScaleGestureDetector(mContext.getApplicationContext(), pinchZoomGestureListener);
    }

    public boolean onTouchGestureDetector(MotionEvent event) {
        scaleGestureDetector.onTouchEvent(event);
        return true;
    }
    public void init() {
        SharedPrefManager.getInstance(mContext).setCameraStart(false);

    }

    public void permissionsResult(boolean cameraPermission, boolean audioPermission, boolean storagePermission) {

        if (cameraPermission && audioPermission && storagePermission) {
            mCameraPermission = cameraPermission;
            mAudioPermission = audioPermission;
            mStoragePermission = storagePermission;
            showVideoFragment();
        } else {
            quitCam();
        }

    }

    public void toStartCamera() {

        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mWarningMsgRoot = mLayoutInflater.inflate(R.layout.warning_message, null);
        mWarningMsg = new Dialog(mContext);
        mSettingsRootView = mLayoutInflater.inflate(R.layout.brightness_settings, null);
        mSettingsDialog = new Dialog(mContext);
        Log.d(TAG, "toStartCamera");

        if (SharedPrefManager.getInstance(mContext).isCameraStart()) {
            Log.d(TAG, "Quit the app");
        } else if (!mShowMessage) {
            Log.d(TAG, "Check permissions and Start camera = " + mShowPermission);
            int camerapermission = ContextCompat.checkSelfPermission(mContext.getApplicationContext(), Manifest.permission.CAMERA);
            int audiopermission = ContextCompat.checkSelfPermission(mContext.getApplicationContext(), Manifest.permission.RECORD_AUDIO);
            int storagepermission = ContextCompat.checkSelfPermission(mContext.getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (camerapermission == PackageManager.PERMISSION_GRANTED && audiopermission == PackageManager.PERMISSION_GRANTED &&
                    storagepermission == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "ALL permissions obtained.");
                mCameraPermission = true;
                mAudioPermission = true;
                mStoragePermission = true;

                showVideoFragment();
            } else if (!mShowPermission) {
                Log.d(TAG, "Permissions not obtained. Obtain explicitly");
                //Remove shared preferences. This is necessary, since for some devices it is pre-selected
                //leading to errors.
                String videoResPref = SharedPrefManager.getInstance(mContext).getVideoResolution();
                Log.d(TAG, "videoResPref = " + videoResPref);
                SharedPrefManager.getInstance(mContext).remove(Contract.SELECT_VIDEO_RESOLUTION);
                SharedPrefManager.getInstance(mContext).remove(Contract.SUPPORT_VIDEO_RESOLUTIONS);
                SharedPrefManager.getInstance(mContext).remove(Contract.VIDEO_DIMENSION_HIGH);
                SharedPrefManager.getInstance(mContext).remove(Contract.VIDEO_DIMENSION_MEDIUM);
                SharedPrefManager.getInstance(mContext).remove(Contract.VIDEO_DIMENSION_LOW);
                SharedPrefManager.getInstance(mContext).remove(Contract.SUPPORT_PHOTO_RESOLUTIONS);
                SharedPrefManager.getInstance(mContext).remove(Contract.SELECT_PHOTO_RESOLUTION);
                SharedPrefManager.getInstance(mContext).remove(Contract.SUPPORT_PHOTO_RESOLUTIONS_FRONT);
                SharedPrefManager.getInstance(mContext).remove(Contract.SELECT_PHOTO_RESOLUTION_FRONT);
                SharedPrefManager.getInstance(mContext).remove(Contract.SELECT_VIDEO_PLAYER);
                SharedPrefManager.getInstance(mContext).setShowExternalPlayerMessage(true);

                String phoneLoc = mContext.getResources().getString(R.string.phoneLocation);
                SharedPrefManager.getInstance(mContext).setMediaCountMem(true);
                SharedPrefManager.getInstance(mContext).setMediaLocation(phoneLoc);
                SharedPrefManager.getInstance(mContext).setPreviousMediaLocation(phoneLoc);

                Log.d(TAG, "REMOVED SHAREDPREFS");
                ActivityCompat.requestPermissions(((Activity) mContext),
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PermissionsUtils.ALL_PERMISSIONS);
                mShowPermission = true;


            }
        }
    }

    public void showVideoFragment() {
        SharedPrefManager.getInstance(mContext).setCameraStart(true);
        if (mVideoFragment == null) {
            Log.d(TAG, "creating videofragment");
            mVideoFragment.setApplicationContext(mContext.getApplicationContext());
        }
        FragmentTransaction fragmentTransaction = ((FragmentActivity) ((Activity) mContext)).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.cameraPreview, mVideoFragment).commit();
        //Log.d(TAG, "brightnessLevel SET to = "+controlVisbilityPreference.getBrightnessLevel());
        brightness.setVisibility(View.VISIBLE);
        setPinchZoomScaleListener(mVideoFragment);

        controlVisbilityPreference.setBrightnessLevel(Contract.NORMAL_BRIGHTNESS);
        controlVisbilityPreference.setBrightnessProgress(0.0f);
    }


    void quitCam() {
        mExitListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        };
        mAlertDialog = new AlertDialog.Builder(mContext);
        mAlertDialog.setTitle(mContext.getString(R.string.no_permission_title));
        mAlertDialog.setMessage(mContext.getString(R.string.unavailable_permissions_message));
        mAlertDialog.setNeutralButton(R.string.exit, mExitListener);
        mAlertDialog.setCancelable(false);
        mAlertDialog.show();
        mShowMessage = true;
    }


    public void displaySDCardNotDetectMessage() {
        Log.d(TAG, "displaySDCardNotDetectMessage");
        //The below variable is needed to check if there was SD Card removed in MediaActivity which caused the control
        // to come here.
        if (mFromGallery) {
            //Show SD Card not detected, please insert sd card to try again.
            TextView warningTitle = (TextView) mWarningMsgRoot.findViewById(R.id.warningTitle);
            warningTitle.setText(mContext.getResources().getString(R.string.sdCardNotDetectTitle));
            TextView warningText = (TextView) mWarningMsgRoot.findViewById(R.id.warningText);
            warningText.setText(mContext.getResources().getString(R.string.sdCardNotDetectMessage));
            mOkButton = (Button) mWarningMsgRoot.findViewById(R.id.okButton);
            mOkButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mWarningMsg.dismiss();
                    mVideoFragment.getLatestFileIfExists();
                }
            });
            mWarningMsg.setContentView(mWarningMsgRoot);
            mWarningMsg.setCancelable(false);
            mWarningMsg.show();
            Log.d(TAG, "MESSAGE SHOWN");
        }
    }

    public boolean checkIfPhoneMemoryIsBelowLowThreshold() {

        if (SharedPrefManager.getInstance(mContext).isSavedMediaMem()) {
            StatFs storageStat = new StatFs(Environment.getDataDirectory().getPath());
            int lowestThreshold = mContext.getResources().getInteger(R.integer.minimumMemoryWarning);
            long lowestMemory = lowestThreshold * (long) Contract.MEGA_BYTE;
            Log.d(TAG, "lowestMemory = " + lowestMemory);
            Log.d(TAG, "avail mem = " + storageStat.getAvailableBytes());
            if (storageStat.getAvailableBytes() < lowestMemory) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
