package com.and.ibrahim.teleprompter.modules.display;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import com.and.ibrahim.teleprompter.R;
import com.and.ibrahim.teleprompter.data.Contract;
import com.and.ibrahim.teleprompter.data.SharedPrefManager;
import com.and.ibrahim.teleprompter.modules.display.camera_preferences.CustomListPreference;
import com.and.ibrahim.teleprompter.modules.display.camera_preferences.ShutterCheckboxPreference;
import com.and.ibrahim.teleprompter.mvp.model.Dimension;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

public class VideoResolutionActivity extends AppCompatActivity {

    public static final String TAG = "VideoSettingsActivity";
    static boolean VERBOSE = false;
    static Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(VERBOSE)Log.d(TAG, "onCreate");
        mContext = getApplicationContext();
        getFragmentManager().beginTransaction().replace(android.R.id.content, new VideoSettingFragment()).commit();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public static class VideoSettingFragment extends PreferenceFragment {
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View rootView = super.onCreateView(inflater, container, savedInstanceState);
            rootView.setBackgroundColor(getResources().getColor(R.color.colorTransparent));
            return rootView;
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (VERBOSE) Log.d(TAG, "VideoSettingFragment onCreate");
            addPreferencesFromResource(R.xml.pref_base);
            Resources resources = getActivity().getResources();
            //Video Resolutions
            addResolutionList();
            //Show Memory
            final CheckBoxPreference memoryConsumedPref = new ShutterCheckboxPreference(getActivity(), true, Contract.SHOW_MEMORY_CONSUMED_MSG);
            memoryConsumedPref.setTitle(resources.getString(R.string.showMemConsumed));
            memoryConsumedPref.setSummary(resources.getString(R.string.showMemConsumedMsg));
            memoryConsumedPref.setKey(Contract.SHOW_MEMORY_CONSUMED_MSG);
            memoryConsumedPref.setLayoutResource(R.layout.shutter_checkbox_setting);
            boolean memCon = PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean(Contract.SHOW_MEMORY_CONSUMED_MSG, false);
            if(VERBOSE)Log.d(TAG, "MEMORY CONSUMED PREF MGR = "+memCon);
            getPreferenceScreen().addPreference(memoryConsumedPref);
            //Video Player
            SharedPreferences settingsPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
            ListPreference playerPreference = new CustomListPreference(getActivity(), true);
            Set<String> playerEntries = new LinkedHashSet<>();
            playerEntries.add(resources.getString(R.string.videoFCPlayer));
            playerEntries.add(resources.getString(R.string.videoExternalPlayer));
            CharSequence[] playerSummaries = new CharSequence[playerEntries.size()];
            int index = 0;
            Iterator<String> playerIter = playerEntries.iterator();
            while (playerIter.hasNext()) {
                String resol = playerIter.next();
                playerSummaries[index++] = resol;
            }
            playerPreference.setEntries(playerSummaries);
            playerPreference.setEntryValues(playerSummaries);
            playerPreference.setTitle(resources.getString(R.string.videoPlayerHeading));
            playerPreference.setSummary(resources.getString(R.string.videoPlayerSummary));
            playerPreference.setKey(Contract.SELECT_VIDEO_PLAYER);
            playerPreference.setValue(settingsPrefs.getString(Contract.SELECT_VIDEO_PLAYER,
                    resources.getString(R.string.videoExternalPlayer)));
            playerPreference.setDialogTitle(getResources().getString(R.string.videoPlayerHeading));
            playerPreference.setLayoutResource(R.layout.custom_list_setting);
            getPreferenceScreen().addPreference(playerPreference);
            playerPreference.setOnPreferenceChangeListener((preference,newValue ) -> {
                String newRes = (String) newValue;
                if(VERBOSE)Log.d(TAG, "onPreferenceChange 2222 = " + newRes);
                if(VERBOSE)Log.d(TAG, "onPreferenceChange pref 2222 = " + preference.getKey());
                return true;
            });
        }

        private void addResolutionList(){
            ListPreference listPreference;
            Set<String> entries;
            SharedPreferences settingsPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            SharedPreferences.Editor editor = settingsPrefs.edit();
            listPreference = new CustomListPreference(getActivity(), true);
            entries = settingsPrefs.getStringSet(Contract.SUPPORT_VIDEO_RESOLUTIONS, null);
            int index=0;
            TreeSet<Dimension> sortedVidsSizes = new TreeSet<>();
            if (VERBOSE) Log.d(TAG, "videoRes SIZE = " + entries.size());
            int width = 0, height = 0;
            //Sort all sizes in descending order.
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Log.d(TAG, "Use forEach");
                entries.forEach((resol) -> {
                    int wid = Integer.parseInt(resol.substring(0, resol.indexOf(" ")));
                    int heig = Integer.parseInt(resol.substring(resol.lastIndexOf(" ") + 1));
                    sortedVidsSizes.add(new Dimension(wid, heig));
                });
            }
            else {
                for (String resol : entries) {
                    width = Integer.parseInt(resol.substring(0, resol.indexOf(" ")));
                    height = Integer.parseInt(resol.substring(resol.lastIndexOf(" ") + 1));
                    sortedVidsSizes.add(new Dimension(width, height));
                }
            }
            CharSequence[] resEntries;
            resEntries = new CharSequence[sortedVidsSizes.size()];
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                ArrayList<CharSequence> resEntList = new ArrayList<>(sortedVidsSizes.size());
                sortedVidsSizes.forEach((dimension) -> {
                    int wid = dimension.getWidth();
                    int heig = dimension.getHeight();
                    resEntList.add(wid + " X " + heig);
                });
                resEntries = resEntList.toArray(resEntries);
            } else {
                Iterator<Dimension> resolIter = sortedVidsSizes.iterator();
                while (resolIter.hasNext()) {
                    Dimension dimen = resolIter.next();
                    width = dimen.getWidth();
                    height = dimen.getHeight();
                    resEntries[index++] = width + " X " + height;
                }
            }
            listPreference.setEntries(resEntries);
            listPreference.setEntryValues(resEntries);
            listPreference.setPersistent(true);
            listPreference.setDialogTitle(getResources().getString(R.string.videoResolutionHeading));
            listPreference.setTitle(getResources().getString(R.string.videoResolutionHeading));
            listPreference.setSummary(getResources().getString(R.string.videoResolutionSummary));
            listPreference.setKey(Contract.SELECT_VIDEO_RESOLUTION);
            listPreference.setValue(settingsPrefs.getString(Contract.SELECT_VIDEO_RESOLUTION, null));
            listPreference.setLayoutResource(R.layout.custom_list_setting);
            getPreferenceScreen().addPreference(listPreference);
        }
    }
}
