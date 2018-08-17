package com.and.ibrahim.teleprompter.data;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by ibrahim on 30/12/17.
 * SharedPrefManager will save the value of selected sort of show that will base in query url
 *
 * @see <a href="https://github.com/ibrahiemhss/Mashaweer-master/blob/master/app/src/main/java/com/mashaweer/ibrahim/mashaweer/data/SharedPrefManager.java"">https://github.com</a>
 */
public class SharedPrefManager {
    private static final String PREF_SCROLL_SPEED = "pref_speed";
    private static final String PREF_TEXT_SIZE = "pref_text_size";
    private static final String PREF_TEXT_COLOR = "pref_text_color";
    private static final String PREF_BACKGROUND_COLOR = "pref_background_color";
    private static final String PREF_UNDO_TEXT_COLOR = "pref_undo_text_color";
    private static final String PREF_UNDO_BACKGROUND_COLOR = "pref_uno_background_color";
    private static final String PREF_COLOR_PREF = "color_pref";
    private static final String PREF_FIRST_ENTRY = "pref_first_entry";
    private static final String PREF_SIGN_IN = "pref_sign_in";



    private static final String SHARED_PREF_NAME = "save_contents";
    private static SharedPrefManager mInstance;
    private final SharedPreferences pref;

    private SharedPrefManager(Context context) {
        pref = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }

    public int getPrefTextSize() {
        return pref.getInt(PREF_TEXT_SIZE, 0);
    }

    public void setPrefTextSize(int size) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(PREF_TEXT_SIZE, size);
        editor.apply();
        editor.commit();
    }


    public int getPrefSpeed() {
        return pref.getInt(PREF_SCROLL_SPEED, 0);
    }

    public void setPrefSpeed(int progress) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(PREF_SCROLL_SPEED, progress);
        editor.apply();
        editor.commit();
    }

    public int getPrefTextColor() {
        return pref.getInt(PREF_TEXT_COLOR, 0);
    }

    public void setPrefTextColor(int color) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(PREF_TEXT_COLOR, color);
        editor.apply();
        editor.commit();
    }

    public int getPrefUndoTextSize() {
        return pref.getInt(PREF_UNDO_TEXT_COLOR, 0);
    }

    public void setPrefUndoTextSize(int size) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(PREF_UNDO_TEXT_COLOR, size);
        editor.apply();
        editor.commit();
    }

    public int getPrefUndoBackgroundColor() {
        return pref.getInt(PREF_UNDO_BACKGROUND_COLOR, 0);
    }

    public void setPrefUndoBackgroundColor(int color) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(PREF_UNDO_BACKGROUND_COLOR, color);
        editor.apply();
        editor.commit();
    }

    public int getPrefBackgroundColor() {
        return pref.getInt(PREF_BACKGROUND_COLOR, 0);
    }

    public void setPrefBackgroundColor(int color) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(PREF_BACKGROUND_COLOR, color);
        editor.apply();
        editor.commit();
    }

    public boolean isColorPref() {
        return pref.getBoolean(PREF_COLOR_PREF, false);
    }

    public void setColorPref(boolean is) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(PREF_COLOR_PREF, is);
        editor.apply();
        editor.commit();
    }

    public boolean isFirstEntry() {
        return pref.getBoolean(PREF_FIRST_ENTRY, false);
    }

    public void setFirstEntry(boolean is) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(PREF_FIRST_ENTRY, is);
        editor.apply();
        editor.commit();
    }

}