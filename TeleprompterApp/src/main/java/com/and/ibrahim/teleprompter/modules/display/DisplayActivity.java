package com.and.ibrahim.teleprompter.modules.display;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Camera;
import android.media.MediaRecorder;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.and.ibrahim.teleprompter.R;
import com.and.ibrahim.teleprompter.base.BaseActivity;
import com.and.ibrahim.teleprompter.data.Contract;
import com.and.ibrahim.teleprompter.modules.fragments.DisplayFragment;

public class DisplayActivity extends BaseActivity {

   private Fragment mDisplayFragment;

   /////////////
    @Override
    public int getResourceLayout() {
        return R.layout.activity_display;
    }
    @Override
    protected void onViewReady(Bundle savedInstanceState, Intent intent) {
        super.onViewReady(savedInstanceState, intent);
        mDisplayFragment=new DisplayFragment();

        if (savedInstanceState != null) {
            mDisplayFragment = getSupportFragmentManager().getFragment(savedInstanceState, Contract.EXTRA_TELEPROMPTER_FRAGMENT);

        }
    }
        @Override
    public void setListener() {

    }




    @Override
    public void getExtra() {

    }

    @Override
    public void init() {
        Bundle bundle = new Bundle();

        bundle.putString(Contract.EXTRA_TEXT, getResources().getString(R.string.mytest));
        mDisplayFragment.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fram_contianer, mDisplayFragment)
                .commit();
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        getSupportFragmentManager().putFragment(outState, Contract.EXTRA_TELEPROMPTER_FRAGMENT, mDisplayFragment);
        super.onSaveInstanceState(outState);
    }

}
