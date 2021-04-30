package com.and.ibrahim.teleprompter.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.and.ibrahim.teleprompter.R;
import com.and.ibrahim.teleprompter.data.Contract;
import com.and.ibrahim.teleprompter.data.SharedPrefManager;

public class PermissionsUtils {

    private final Context mContext;
    final String TAG = "PermissionsUtils";
    public static final int ALL_PERMISSIONS = 0;
    static final String AUDIO_PERMISSION = "android.permission.RECORD_AUDIO";
    static final String CAMERA_PERMISSION = "android.permission.CAMERA";
    static final String STORAGE_PERMISSIONS = "android.permission.WRITE_EXTERNAL_STORAGE";
    public static final String FC_SHARED_PREFERENCE = "FC_Settings";
    public static final String FC_MEDIA_PREFERENCE = "FC_Media";
     boolean cameraPermission = false;
     boolean audioPermission = false;
      boolean storagePermission = false;

    public boolean isShowMessage() {
        return showMessage;
    }

    public void setShowMessage(boolean showMessage) {
        this.showMessage = showMessage;
    }

    boolean showMessage = false;
     boolean showPermission = false;
     DialogInterface.OnClickListener exitListener;
     AlertDialog.Builder alertDialog;
     boolean VERBOSE = false;


    public PermissionsUtils(Context context) {
        this.mContext=context;

    }

    public void requestPermissions(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
                if(VERBOSE) Log.d(TAG, "For camera == "+permissions[0]);
                if (permissions[0].equalsIgnoreCase(CAMERA_PERMISSION) && permissions[1].equalsIgnoreCase(AUDIO_PERMISSION) &&
                        permissions[2].equalsIgnoreCase(STORAGE_PERMISSIONS)) {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED
                            && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                        cameraPermission = true;
                        audioPermission = true;
                        storagePermission = true;

                    } else {
                        quitAppCam();
                    }
                }

    }

    public boolean getPermissionsStatus(){
        boolean permissionStatus = false;
       if(!isShowMessage()){
            if(VERBOSE) Log.d(TAG,"Check permissions and Start camera = "+showPermission);
            int camerapermission = ContextCompat.checkSelfPermission(mContext.getApplicationContext(), Manifest.permission.CAMERA);
            int audiopermission = ContextCompat.checkSelfPermission(mContext.getApplicationContext(), Manifest.permission.RECORD_AUDIO);
            int storagepermission = ContextCompat.checkSelfPermission(mContext.getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (camerapermission == PackageManager.PERMISSION_GRANTED && audiopermission == PackageManager.PERMISSION_GRANTED &&
                    storagepermission == PackageManager.PERMISSION_GRANTED) {
                if(VERBOSE) Log.d(TAG, "ALL permissions obtained.");
                cameraPermission = true;
                audioPermission = true;
                storagePermission= true;
                permissionStatus= true;
            } else if(!showPermission){
                if(VERBOSE) Log.d(TAG, "Permissions not obtained. Obtain explicitly");
                //Remove shared preferences. This is necessary, since for some devices it is pre-selected
                //leading to errors.
                SharedPreferences videoPref = PreferenceManager.getDefaultSharedPreferences(mContext.getApplicationContext());
                String videoResPref = videoPref.getString(Contract.SELECT_VIDEO_RESOLUTION, null);
                if(VERBOSE) Log.d(TAG, "videoResPref = "+videoResPref);
                SharedPreferences.Editor editor = videoPref.edit();
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
                SharedPrefManager.getInstance(mContext).setSavedMediaMem(true);
                SharedPrefManager.getInstance(mContext).setMediaLocation(phoneLoc);
                SharedPrefManager.getInstance(mContext).setPreviousMediaLocation(phoneLoc);
                if(VERBOSE) Log.d(TAG, "REMOVED SHAREDPREFS");
                ActivityCompat.requestPermissions((Activity) mContext,
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        ALL_PERMISSIONS);
                permissionStatus = false;
            }
        }
        return permissionStatus;
    }

    public  void quitAppCam()
    {
        exitListener = new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int which)
            {
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        };
        alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle(mContext.getString(R.string.no_permission_title));
        alertDialog.setMessage(mContext.getString(R.string.unavailable_permissions_message));
        alertDialog.setNeutralButton(R.string.exit, exitListener);
        alertDialog.setCancelable(false);
        alertDialog.show();
        setShowMessage(true);
    }



    public Bundle onSaveInstanceState(Bundle outState) {
        if(VERBOSE) Log.d(TAG,"Save before restart");
        if(isShowMessage()) {
            outState.putBoolean(Contract.EXTRA_QUIT, true);
            outState.putBoolean(Contract.EXTRA_QUIT,false);
            if(VERBOSE) Log.d(TAG, "Saved restart");
        }
        else if(cameraPermission && audioPermission && storagePermission){
            //The activity could be destroyed because of low memory. Keep a flag to quit the activity when you navigate back here.
            outState.putBoolean(Contract.EXTRA_QUIT,true);
            outState.putBoolean(Contract.EXTRA_RESTART,false);
            if(VERBOSE) Log.d(TAG, "Safe to quit");
        }
        outState.putBoolean(Contract.EXTRA_SHOW_PERMISSION,showPermission);
        return  outState;
    }

    public Bundle onRestoreInstanceState(Bundle savedInstanceState) {
        if(VERBOSE) Log.d(TAG,"Restore state = "+savedInstanceState);
        if(savedInstanceState!=null && savedInstanceState.getBoolean(Contract.EXTRA_QUIT)) {
            //The activity was restarted because of possible low memory situation.
            if(VERBOSE) Log.d(TAG, "Quit app");
            //TODO ACTION IF THERE NO Permissions===============================================================
        }
        else if(savedInstanceState!= null && savedInstanceState.getBoolean(Contract.EXTRA_SHOW_PERMISSION)){
            showPermission = savedInstanceState.getBoolean(Contract.EXTRA_SHOW_PERMISSION);
            if(VERBOSE) Log.d(TAG,"show permission = "+showPermission);
        }
        return  savedInstanceState;
    }
}
