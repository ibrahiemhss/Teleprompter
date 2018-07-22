package com.and.ibrahim.teleprompter.modules.main;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.and.ibrahim.teleprompter.R;
import com.and.ibrahim.teleprompter.modules.CustomDialogClass;
import com.and.ibrahim.teleprompter.mvp.model.Teleprmpter;
import com.and.ibrahim.teleprompter.util.FabAnimations;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG="MainActivity";

    @BindView(R.id.collapsing_toolbar)
    protected
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    @BindView(R.id.toolbar)
    protected
    Toolbar toolbar;
    @BindView(R.id.fab)
    protected
    FloatingActionButton mFab;
    @BindView(R.id.fab1)
    protected
    FloatingActionButton mFab1;
    @BindView(R.id.fab2)
    protected
    FloatingActionButton mFab2;
    @BindView(R.id.recycler_view)
    protected RecyclerView mRecyclerView;

    private ArrayList<Teleprmpter> mArrayList;
    private Teleprmpter mTeleprmpter;


    private Unbinder unbinder;
    private FabAnimations mFabAnimations;

    private final TeleprompterAdapter.OnBakeClickListener onBakeClickListener = new TeleprompterAdapter.OnBakeClickListener() {


        @Override
        public void onClick(int position) {

        }};

        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder= ButterKnife.bind(this);

        mFabAnimations =new FabAnimations(this,mFab,mFab1,mFab2);

        mCollapsingToolbarLayout.setTitle(getString(R.string.app_name));
        mCollapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));

        mFabAnimations.addFabAnimationRes();
        mFab.setOnClickListener(this);
        mFab1.setOnClickListener(this);
        mFab2.setOnClickListener(this);

            mArrayList=new ArrayList<>();
            mTeleprmpter=new Teleprmpter();
            for (int i=0;i<10;i++){
                mTeleprmpter.setTextTitle("kkgjkjgk jgkgjk gjkgjkg");
                mTeleprmpter.setTextContent("kgjkgjkjg kjgo dfeooer fdfdkfjd fkdlkfldkf fdlfkdlfkld dfkdlfkldfk dflkdlfkldkf ldkfldfk" +
                        "dfjdhfjdfh dkfjkdjfkjd dkjfdkjfkdj dkjfkdjfkdj dkjfkdjkdjf dkfjdkjf dkjkd fkjdk dkjfkdj fdkjfk");
                mArrayList.add(mTeleprmpter);

            }

            initialiseListWithPhoneScreen();

        }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.fab:
                mFabAnimations.animateFAB();
                break;
            case R.id.fab1:

                Log.d(TAG,"FabAction is "+"Fab 1");

                break;
            case R.id.fab2:
                CustomDialogClass cdd=new CustomDialogClass(this, R.style.PauseDialog);
                cdd.show();
                Log.d(TAG,"FabAction is "+"Fab 2");

                break;
        }
    }

    public boolean isTablet() {
        return (MainActivity.this.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    private void initialiseListWithPhoneScreen() {


        ButterKnife.bind(this);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        //Pass a list of images with inflater ​​in adapter
        TeleprompterAdapter teleprompterAdapter = new TeleprompterAdapter( getLayoutInflater());
        teleprompterAdapter.addNewContent(mArrayList);
        teleprompterAdapter.setBakeClickListener(onBakeClickListener);

        mRecyclerView.setAdapter(teleprompterAdapter);
    }

    //initialiseList to show values inside mBake_list
    private void initialiseListWithsLargeSize() {


        ButterKnife.bind(this);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2,
                GridLayoutManager.VERTICAL, false));
        //Pass a list of images with inflater ​​in adapter
        TeleprompterAdapter teleprompterAdapter = new TeleprompterAdapter( getLayoutInflater());
        teleprompterAdapter.addNewContent(mArrayList);
        teleprompterAdapter.setBakeClickListener(onBakeClickListener);

        mRecyclerView.setAdapter(teleprompterAdapter);
    }

}
