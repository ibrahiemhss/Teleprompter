package com.and.ibrahim.teleprompter.modules.splash;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.and.ibrahim.teleprompter.modules.display.DisplayActivity;
import com.and.ibrahim.teleprompter.modules.listContents.ListContentsActivity;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent mIntent;
        if (isTablet()) {
            mIntent = new Intent(this, DisplayActivity.class);
        } else {
            mIntent = new Intent(this, ListContentsActivity.class);

        }
        startActivity(mIntent);
        finish();
    }

    private boolean isTablet() {
        return (SplashActivity.this.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }
}