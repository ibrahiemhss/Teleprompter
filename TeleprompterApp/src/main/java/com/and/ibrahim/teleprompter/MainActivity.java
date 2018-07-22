package com.and.ibrahim.teleprompter;

import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG="MainActivity";

    @BindView(R.id.collapsing_toolbar)
    private
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fab)
    private
    FloatingActionButton mFab;
    @BindView(R.id.fab1)
    private
    FloatingActionButton mFab1;
    @BindView(R.id.fab2)
    private
    FloatingActionButton mFab2;
    private Animation fab_open,fab_close,rotate_forward,rotate_backward;

    private Unbinder unbinder;

    private Boolean isFabOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        unbinder= ButterKnife.bind(this);
       // setSupportActionBar(toolbar);
        mCollapsingToolbarLayout.setTitle(getString(R.string.app_name));
        mCollapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));

        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_backward);
        mFab.setOnClickListener(this);
        mFab1.setOnClickListener(this);
        mFab2.setOnClickListener(this);
       // mCollapsingToolbarLayout.setContentScrimColor(palette.getMutedColor(primary));
      //  mCollapsingToolbarLayout.setStatusBarScrimColor(palette.getDarkMutedColor(primaryDark));


     //   fab.setRippleColor(lightVibrantColor);
       // fab.setBackgroundTintList(ColorStateList.valueOf(vibrantColor));
    }


    private void animateFAB(){

        if(isFabOpen){

            mFab.startAnimation(rotate_backward);
            mFab1.startAnimation(fab_close);
            mFab2.startAnimation(fab_close);
            mFab1.setClickable(false);
            mFab2.setClickable(false);
            isFabOpen = false;
            Log.d(TAG, "FabAction is "+"close");

        } else {

            mFab.startAnimation(rotate_forward);
            mFab1.startAnimation(fab_open);
            mFab2.startAnimation(fab_open);
            mFab1.setClickable(true);
            mFab2.setClickable(true);
            isFabOpen = true;
            Log.d(TAG,"FabAction is "+"open");

        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.fab:

                animateFAB();
                break;
            case R.id.fab1:

                Log.d(TAG,"FabAction is "+"Fab 1");

                break;
            case R.id.fab2:

                Log.d(TAG,"FabAction is "+"Fab 2");

                break;
        }
    }
}
