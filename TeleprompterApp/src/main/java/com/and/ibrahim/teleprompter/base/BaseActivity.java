package com.and.ibrahim.teleprompter.base;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.drive.DriveResourceClient;

import butterknife.ButterKnife;


/**
 * the parent Activity for all activities in app
 */
@SuppressWarnings("ALL")
public abstract class BaseActivity extends AppCompatActivity {
    private DriveResourceClient mDriveResourceClient;

    //ProgressDialog to view any message wanted in the child activity
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
          /*this getContentView  will inherit by the
           child classes to pass their layout */
        setContentView(getResourceLayout());
        ButterKnife.bind(this);
        onViewReady(savedInstanceState, getIntent());
        init();
        setListener();
        getResourceLayout();
    }


    protected void onViewReady(Bundle savedInstanceState, Intent intent) {


    }

    @Override
    protected void onDestroy() {
        ButterKnife.bind(this).unbind();
        super.onDestroy();
    }

    protected abstract int getResourceLayout();

    protected abstract void init();

    protected abstract void setListener();

    /**
     * using generally in child activities to display status of coming data
     * using here
     * {@linkplain com.and.ibrahim.teleprompter.mvp.view}
     * {@linkplain com.and.ibrahim.teleprompter.mvp.presenter}
     * {@linkplain  com.and.ibrahim.teleprompter.modules.listContents}
     */
    protected void showDialog(String message) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setCancelable(true);
        }
        mProgressDialog.setMessage(message);
        mProgressDialog.show();
    }

    protected void hideDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }


}