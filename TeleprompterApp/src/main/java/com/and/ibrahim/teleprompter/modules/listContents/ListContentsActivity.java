package com.and.ibrahim.teleprompter.modules.listContents;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.and.ibrahim.teleprompter.R;
import com.and.ibrahim.teleprompter.base.BaseActivity;
import com.and.ibrahim.teleprompter.callback.FragmentEditListRefreshListener;
import com.and.ibrahim.teleprompter.data.Contract;
import com.and.ibrahim.teleprompter.data.SharedPrefManager;
import com.and.ibrahim.teleprompter.modules.setting.SettingsActivity;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.analytics.FirebaseAnalytics;

import butterknife.BindView;
import io.fabric.sdk.android.Fabric;

@SuppressWarnings("ALL")
public class ListContentsActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "ListContentsActivity";
    @BindView(R.id.list_contents_collapsing_toolbar)
    protected
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    @BindView(R.id.list_contents_toolbar)
    protected Toolbar mToolbar;
    int mFlag;
    private SearchView searchView = null;
    private SearchView.OnQueryTextListener queryTextListener;
    private FragmentEditListRefreshListener fragmentEditListRefreshListener;
    private boolean ischecked;
    private Fragment mContentListFragment;
    private boolean isFirstEntry;
    private FirebaseAnalytics mFirebaseAnalytics;

    private FragmentEditListRefreshListener getFragmentEditListRefreshListener() {
        return fragmentEditListRefreshListener;
    }

    public void setFragmentEditListRefreshListener(FragmentEditListRefreshListener fragmentEditListRefreshListener) {
        this.fragmentEditListRefreshListener = fragmentEditListRefreshListener;
    }

    @Override
    protected void onViewReady(Bundle savedInstanceState, Intent intent) {
        super.onViewReady(savedInstanceState, intent);
        isFirstEntry = SharedPrefManager.getInstance(this).isFirstEntry();

        Fabric.with(this, new Crashlytics());

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        mContentListFragment = new ListContentsFragment();
        if (savedInstanceState != null) {
            mContentListFragment = getSupportFragmentManager().getFragment(savedInstanceState, Contract.EXTRA_FRAGMENT);

        }
        mCollapsingToolbarLayout.setTitleEnabled(false);

        setupSearchToolbar();
        boolean isVisible = false;
        ischecked = false;
        if (!isFirstEntry) {
            addDemo();

        }
    }

    @Override
    public void onResume() {
        // Start or resume the game.
        super.onResume();
        // showInterstitial();
    }

    private void showInterstitial() {

        InterstitialAd mInterstitialAd;
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_pub));
        AdRequest adRequestInterstitial = new AdRequest.Builder().addTestDevice("deviceid").build();
        mInterstitialAd.loadAd(adRequestInterstitial);

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {

            }

            @Override
            public void onAdLoaded() {
                //   mAdIsLoading = false;
                showInterstitial();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // mAdIsLoading = false;
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        showInterstitial();
    }

    @Override
    public int getResourceLayout() {
        return R.layout.activity_list_ccontents;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void init() {

        initFragment();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.content_list_menu, menu);
        super.onCreateOptionsMenu(menu);
        return super.onCreateOptionsMenu(menu);

    }

    private void setupSearchToolbar() {
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            mToolbar.setTitle(getResources().getString(R.string.app_name));

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                if (getFragmentEditListRefreshListener() != null) {
                    getFragmentEditListRefreshListener().onRefresh();
                }
                break;

            case R.id.action_setting:
                Intent intent = (new Intent(ListContentsActivity.this, SettingsActivity.class));
                startActivity(intent);

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setListener() {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, Contract.EXTRA_FRAGMENT, mContentListFragment);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
    }

    private void initFragment() {
        Bundle bundle = new Bundle();
        bundle.putBoolean(Contract.EXTRA_SELECTED, false);

        mContentListFragment.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.contents_container, mContentListFragment)
                .commit();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


    public void addDemo() {

        ContentValues values = new ContentValues();
        values.put(Contract.Entry.COL_TITLE, getResources().getString(R.string.demo_title));
        values.put(Contract.Entry.COL_CONTENTS, getResources().getString(R.string.demo_text));
        values.put(Contract.Entry.COL_UNIQUE_ID, 1);

        final Uri uriInsert = getContentResolver().insert(Contract.Entry.PATH_TELEPROMPTER_URI, values);
        SharedPrefManager.getInstance(this).setFirstEntry(true);

        if (uriInsert != null) {
            Log.d("contentResolver insert", "first added success");

            values.clear();
        }
    }
}
