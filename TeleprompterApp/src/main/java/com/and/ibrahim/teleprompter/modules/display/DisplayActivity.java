package com.and.ibrahim.teleprompter.modules.display;

import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;

import com.and.ibrahim.teleprompter.R;
import com.and.ibrahim.teleprompter.base.BaseActivity;
import com.and.ibrahim.teleprompter.modules.fragments.DisplayFragment;

public class DisplayActivity extends BaseActivity {

    Fragment mDisplayFragment;

    @Override
    public int getResourceLayout() {
        return R.layout.activity_display;
    }

    @Override
    public void getExtra() {

    }

    @Override
    public void init() {
        mDisplayFragment=new DisplayFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fram_contianer, mDisplayFragment)
                .commit();
    }

    @Override
    public void setListener() {

    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

}
