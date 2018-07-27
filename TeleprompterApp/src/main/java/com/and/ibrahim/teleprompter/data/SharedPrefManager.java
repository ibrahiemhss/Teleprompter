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
    private static final String PREF_SEEK_PROGRESS = "pref_seek_progress";
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

    public int getPrefIndex() {
        return pref.getInt(PREF_SEEK_PROGRESS, 0);
    }
    public void setPrefIndex(int progress) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(PREF_SEEK_PROGRESS, progress);
        editor.apply();
        editor.commit();
    }
}