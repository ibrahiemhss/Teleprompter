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
    private static final String PREF_FIRST_OPEN = "first_open";


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

    public int getPrefBackgroundColr() {
        return pref.getInt(PREF_BACKGROUND_COLOR, 0);
    }

    public void setPrefBackgroundColor(int color) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(PREF_BACKGROUND_COLOR, color);
        editor.apply();
        editor.commit();
    }

    public boolean isFirstOpen() {
        return pref.getBoolean(PREF_FIRST_OPEN, false);
    }

    public void setPrefFirstOpen(boolean is) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(PREF_FIRST_OPEN, is);
        editor.apply();
        editor.commit();
    }
}