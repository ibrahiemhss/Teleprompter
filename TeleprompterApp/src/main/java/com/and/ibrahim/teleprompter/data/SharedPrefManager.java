package com.and.ibrahim.teleprompter.data;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;


/**
 * Created by ibrahim on 30/12/17.
 * SharedPrefManager will save the value of selected sort of show that will base in query url
 *
 * @see <a href="https://github.com/ibrahiemhss/Mashaweer-master/blob/master/app/src/main/java/com/mashaweer/ibrahim/mashaweer/data/SharedPrefManager.java"">https://github.com</a>
 */
public class SharedPrefManager {
    private static final String SHARED_NAME = "save_contents";

    private static SharedPrefManager mInstance;
    private final SharedPreferences pref;

    private Context mContext;
    private SharedPrefManager(Context context) {
        this.mContext=context;
        pref = context.getSharedPreferences(SHARED_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }

    public SharedPreferences mySharedPreferences(){
        return mContext.getSharedPreferences(SHARED_NAME, Context.MODE_PRIVATE);
    }
    public void remove(String key) {
        SharedPreferences.Editor editor = pref.edit();
        editor.remove(key);
        editor.commit();
    }
    public int getPrefTextSize() {
        return pref.getInt(Contract.TEXT_SIZE, 0);
    }

    public void setPrefTextSize(int size) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(Contract.TEXT_SIZE, size);
        editor.commit();
        editor.commit();
    }

    public String getCurrentText() {
        return pref.getString(Contract.CURRENT_TEXT, null);
    }
    public void  setCurrentText(String val) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(Contract.CURRENT_TEXT, val);
        editor.commit();
        editor.commit();
    }

    public int getPrefSpeed() {
        return pref.getInt(Contract.SCROLL_SPEED, 0);
    }

    public void setPrefSpeed(int progress) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(Contract.SCROLL_SPEED, progress);
        editor.commit();
        editor.commit();
    }

    public int getPrefTextColor() {
        return pref.getInt(Contract.TEXT_COLOR, 0);
    }

    public void setPrefTextColor(int color) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(Contract.TEXT_COLOR, color);
        editor.commit();
        editor.commit();
    }

    public int getPrefUndoTextSize() {
        return pref.getInt(Contract.UNDO_TEXT_COLOR, 0);
    }

    public void setPrefUndoTextSize(int size) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(Contract.UNDO_TEXT_COLOR, size);
        editor.commit();
        editor.commit();
    }

    public int getPrefUndoBackgroundColor() {
        return pref.getInt(Contract.UNDO_BACKGROUND_COLOR, 0);
    }

    public void setPrefUndoBackgroundColor(int color) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(Contract.UNDO_BACKGROUND_COLOR, color);
        editor.commit();
        editor.commit();
    }

    public int getPrefBackgroundColor() {
        return pref.getInt(Contract.BACKGROUND_COLOR, 0);
    }

    public void setPrefBackgroundColor(int color) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(Contract.BACKGROUND_COLOR, color);
        editor.commit();
        editor.commit();
    }

    public boolean isColorPref() {
        return pref.getBoolean(Contract.COLOR_PREF, false);
    }

    public void setColorPref(boolean is) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(Contract.COLOR_PREF, is);
        editor.commit();
        editor.commit();
    }

    public boolean isInitedSdk() {
        return pref.getBoolean(Contract.INIT_SDK, false);
    }

    public void setInitedSdk(boolean is) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(Contract.INIT_SDK, is);
        editor.commit();
        editor.commit();
    }

    public boolean isFirstEntry() {
        return pref.getBoolean(Contract.FIRST_ENTRY, false);
    }

    public void setFirstEntry(boolean is) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(Contract.FIRST_ENTRY, is);
        editor.commit();
        editor.commit();
    }

    public boolean isFirstSetColor() {
        return pref.getBoolean(Contract.FIRST_COLOR_SET, false);
    }

    public void setFirstSetColor(boolean is) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(Contract.FIRST_COLOR_SET, is);
        editor.commit();
        editor.commit();
    }

    public boolean isAllAdsOn() {
        return pref.getBoolean(Contract.REMOTE_CONFIG_AlL_ADs, false);
    }

    public void setAllAdsOn(boolean is) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(Contract.REMOTE_CONFIG_AlL_ADs, is);
        editor.commit();
        editor.commit();
    }



    public boolean isFullScreenAdShown() {
        return pref.getBoolean(Contract.FULL_SCREEN_AD_SHOWN, false);
    }

    public void setFullScreenAdShown(boolean is) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(Contract.FULL_SCREEN_AD_SHOWN, is);
        editor.commit();
        editor.commit();
    }
    public boolean isFullScreenAdsOn() {
        return pref.getBoolean(Contract.REMOTE_CONFIG_FULL_SCREEN_ADs, false);
    }

    public void setFullScreenAdsOn(boolean is) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(Contract.REMOTE_CONFIG_FULL_SCREEN_ADs, is);
        editor.commit();
        editor.commit();
    }

    public boolean isCameraEnabled() {
        return pref.getBoolean(Contract.IS_CAMERA_ENAPLED, false);
    }

    public void setCameraEnabled(boolean value) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(Contract.IS_CAMERA_ENAPLED, value);
        editor.commit();
        editor.commit();
    }
    public String getPhoneMemoryLimit() {
        return pref.getString(Contract.PHONE_MEMORY_LIMIT, "1");
    }

    public void setPhoneMemoryLimit(String value) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(Contract.PHONE_MEMORY_LIMIT, value);
        editor.commit();
        editor.commit();
    }

    public String getPhoneMemoryMetric() {
        return pref.getString(Contract.PHONE_MEMORY_METRIC, "GB");
    }

    public void setPhoneMemoryMetric(String val) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(Contract.PHONE_MEMORY_METRIC, val);
        editor.commit();
        editor.commit();
    }

    public boolean isPhoneMemoryDisable() {
        return pref.getBoolean(Contract.PHONE_MEMORY_DISABLE, false);
    }

    public void setPhoneMemoryDisable(boolean is) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(Contract.PHONE_MEMORY_DISABLE, is);
        editor.commit();
        editor.commit();
    }


    public String getPreviewResolution() {
        return pref.getString(Contract.PREVIEW_RESOLUTION, null);
    }
    public void  setPreviewResolution(String val) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(Contract.PREVIEW_RESOLUTION, val);
        editor.commit();
        editor.commit();
    }
    public String getVideoResolution() {
        return pref.getString(Contract.SELECT_VIDEO_RESOLUTION, null);
    }
    public void  setVideoResolution(String val) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(Contract.SELECT_VIDEO_RESOLUTION, val);
        editor.commit();
        editor.commit();
    }
    public String getSelectVideoResolution() {
        return pref.getString(Contract.SELECT_VIDEO_RESOLUTION, null);
    }
    public void  setSelectVideoResolution(String val) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(Contract.SELECT_VIDEO_RESOLUTION, val);
        editor.commit();
        editor.commit();
    }


    public boolean isVideoCapture(boolean defValue) {
        return pref.getBoolean(Contract.VIDEO_CAPTUE, defValue);
    }
    public void  setVideoCapture(boolean v) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(Contract.VIDEO_CAPTUE, v);
        editor.commit();
        editor.commit();
    }
    public String getSelectVideoPlayer(String defValue) {
        return pref.getString(Contract.SELECT_VIDEO_PLAYER, defValue);
    }
    public void  setSelectVideoPlayer(String val) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(Contract.SELECT_VIDEO_PLAYER, val);
        editor.commit();
        editor.commit();
    }

    public boolean isShutterSound() {
        return pref.getBoolean(Contract.SHUTTER_SOUND, false);
    }
    public void  setShutterSound(boolean is) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(Contract.SHUTTER_SOUND, is);
        editor.commit();
        editor.commit();
    }
    public String getSelectPhotoResolution() {
        return pref.getString(Contract.SELECT_PHOTO_RESOLUTION, null);
    }
    public void  setSelectPhotoResolution(String val) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(Contract.SELECT_PHOTO_RESOLUTION, val);
        editor.commit();
        editor.commit();
    }
    public String getSelectFrontPhotoResolution() {
        return pref.getString(Contract.SELECT_PHOTO_RESOLUTION_FRONT, null);
    }
    public void  setSelectFrontPhotoResolution(String val) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(Contract.SELECT_PHOTO_RESOLUTION_FRONT, val);
        editor.commit();
        editor.commit();
    }
    public Set<String> getSupportVideoResolution() {
        return pref.getStringSet(Contract.SUPPORT_VIDEO_RESOLUTIONS, null);
    }
    public void  setSupportVideoResolution(Set<String> value) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putStringSet(Contract.SUPPORT_VIDEO_RESOLUTIONS, value);
        editor.commit();
        editor.commit();
    }

    public Set<String> getSupportCamersResolution() {
        return pref.getStringSet(Contract.SUPPORT_PHOTO_RESOLUTIONS, null);
    }
    public void  setSupportCamersResolution(Set<String> value) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putStringSet(Contract.SUPPORT_PHOTO_RESOLUTIONS, value);
        editor.commit();
        editor.commit();
    }
    public Set<String> getSupportFrontCamersResolution() {
        return pref.getStringSet(Contract.SUPPORT_PHOTO_RESOLUTIONS_FRONT, null);
    }
    public void  setSupportFrontCamersResolution(Set<String> value) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putStringSet(Contract.SUPPORT_PHOTO_RESOLUTIONS_FRONT, value);
        editor.commit();
        editor.commit();
    }

    public boolean isSavedMediaMem() {
        return pref.getBoolean(Contract.SAVE_MEDIA_PHONE_MEM, false);
    }
    public void  setSavedMediaMem(boolean is) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(Contract.SAVE_MEDIA_PHONE_MEM, is);
        editor.commit();
        editor.commit();
    }

    public boolean mediaCountMem() {
        return pref.getBoolean(Contract.SAVE_MEDIA_PHONE_MEM, false);
    }
    public void  setMediaCountMem(boolean is) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(Contract.SAVE_MEDIA_PHONE_MEM, is);
        editor.commit();
        editor.commit();
    }

    public String getMediaLocation() {
        return pref.getString(Contract.MEDIA_LOCATION_VIEW_SELECT, null);
    }
    public void  setMediaLocation(String val) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(Contract.MEDIA_LOCATION_VIEW_SELECT, val);
        editor.commit();
        editor.commit();
    }


    public String getSdCardPath() {
        return pref.getString(Contract.SD_CARD_PATH, "");
    }
    public void  setSdCardPath(String val) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(Contract.SD_CARD_PATH, val);
        editor.commit();
        editor.commit();
    }



    public boolean isShowMemoryConsumedText() {
        return pref.getBoolean(Contract.SHOW_MEMORY_CONSUMED_MSG, false);
    }
    public void  setShowMemoryConsumedText(boolean is) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(Contract.SHOW_MEMORY_CONSUMED_MSG, is);
        editor.commit();
        editor.commit();
    }
    public String getPreviousMediaLocation() {
        return pref.getString(Contract.MEDIA_LOCATION_VIEW_SELECT_PREV, null);
    }
    public void  setPreviousMediaLocation(String val) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(Contract.MEDIA_LOCATION_VIEW_SELECT_PREV, val);
        editor.commit();
        editor.commit();
    }

    public boolean showExternalPlayerMessage() {
        return pref.getBoolean(Contract.SHOW_EXTERNAL_PLAYER_MESSAGE, false);
    }
    public void  setShowExternalPlayerMessage(boolean is) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(Contract.SHOW_EXTERNAL_PLAYER_MESSAGE, is);
        editor.commit();
        editor.commit();
    }



    public boolean isSaveToDrive() {
        return pref.getBoolean(Contract.SAVE_TO_GOOGLE_DRIVE, false);
    }
    public void  setSaveToDrive(boolean is) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(Contract.SAVE_TO_GOOGLE_DRIVE, is);
        editor.commit();
        editor.commit();
    }

    public String getGoogleDriveAccName() {
        return pref.getString(Contract.GOOGLE_DRIVE_ACC_NAME, null);
    }
    public void  setGoogleDriveAccName(String val) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(Contract.GOOGLE_DRIVE_ACC_NAME, val);
        editor.commit();
        editor.commit();
    }

    public String getGoogleDriveFolder() {
        return pref.getString(Contract.GOOGLE_DRIVE_FOLDER, null);
    }
    public void  setGoogleDriveFolder(String val) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(Contract.GOOGLE_DRIVE_FOLDER, val);
        editor.commit();
        editor.commit();
    }
    public String getDropBoxAccessToken() {
        return pref.getString(Contract.DROPBOX_ACCESS_TOKEN, null);
    }
    public void  setDropBoxAccessToken(String val) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(Contract.DROPBOX_ACCESS_TOKEN, val);
        editor.commit();
        editor.commit();
    }
    public boolean isSaveToDropBox() {
        return pref.getBoolean(Contract.SAVE_TO_DROPBOX, false);
    }
    public void  setSaveToDropBox(boolean is) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(Contract.SAVE_TO_DROPBOX, is);
        editor.commit();
        editor.commit();
    }
    public String getDropboxFolder() {
        return pref.getString(Contract.DROPBOX_FOLDER, null);
    }
    public void  setDropboxFolder(String val) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(Contract.DROPBOX_FOLDER, val);
        editor.commit();
        editor.commit();
    }
    public boolean isCameraStart() {
        return pref.getBoolean(Contract.START_CAMERA, false);
    }
    public void  setCameraStart(boolean is) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(Contract.START_CAMERA, is);
        editor.commit();
        editor.commit();
    }
    public boolean isFirstSetText() {
        return pref.getBoolean(Contract.FIRST_TEXT_SET, false);
    }

    public void setFirstSetText(boolean is) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(Contract.FIRST_TEXT_SET, is);
        editor.commit();
        editor.commit();
    }

    public boolean isFirstSetSpeed() {
        return pref.getBoolean(Contract.FIRST_SPEED_SET, false);
    }

    public void setFirstSetSpeed(boolean is) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(Contract.FIRST_SPEED_SET, is);
        editor.commit();
        editor.commit();
    }

}