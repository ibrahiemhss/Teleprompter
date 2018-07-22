package com.and.ibrahim.teleprompter.util;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.and.ibrahim.teleprompter.R;

public class FabAnimations {

    private static final String TAG="MainActivity";
    private Context mContext;
    private FloatingActionButton mFab,mFab1,mFab2;
    private Animation fab_open,fab_close,rotate_forward,rotate_backward;
    private boolean isFab=false;

    public FabAnimations(Context mContext, FloatingActionButton mFab, FloatingActionButton mFab1, FloatingActionButton mFab2) {
        this.mFab = mFab;
        this.mFab1 = mFab1;
        this.mFab2 = mFab2;
        this.mContext = mContext;
    }

    public void addFabAnimationRes() {
        fab_open = AnimationUtils.loadAnimation(mContext.getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(mContext.getApplicationContext(),R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(mContext.getApplicationContext(),R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(mContext.getApplicationContext(),R.anim.rotate_backward);

    }

    public void animateFAB(){

        if(isFab){

            mFab.startAnimation(rotate_backward);
            mFab1.startAnimation(fab_close);
            mFab2.startAnimation(fab_close);
            mFab1.setClickable(false);
            mFab2.setClickable(false);
            isFab = false;
            Log.d(TAG, "FabAction is "+"close");

        } else {

            mFab.startAnimation(rotate_forward);
            mFab1.startAnimation(fab_open);
            mFab2.startAnimation(fab_open);
            mFab1.setClickable(true);
            mFab2.setClickable(true);
            isFab = true;
            Log.d(TAG,"FabAction is "+"open");

        }
    }
}
