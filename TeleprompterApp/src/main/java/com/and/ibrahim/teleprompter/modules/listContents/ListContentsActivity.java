package com.and.ibrahim.teleprompter.modules.listContents;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.support.v7.widget.SearchView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.and.ibrahim.teleprompter.R;
import com.and.ibrahim.teleprompter.base.BaseActivity;
import com.and.ibrahim.teleprompter.data.Contract;
import com.and.ibrahim.teleprompter.modules.setting.SettingsActivity;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.Objects;

import butterknife.BindView;

import static android.view.Gravity.BOTTOM;
import static android.view.Gravity.START;

public class ListContentsActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "ListContentsActivity";
    //@BindView(R.id.et_search)
    // protected AppCompatEditText mEtSearch;
    //@BindView(R.id.img_search)
    // protected ImageView mImgSearch;
    private SearchView searchView = null;
    private SearchView.OnQueryTextListener queryTextListener;
   // @BindView(R.id.text_app_name)
   // protected TextView mTxtAppName;
    @BindView(R.id.edit_container)
    protected RelativeLayout mEditContainer;
    @BindView(R.id.delete_all)
    protected ImageView mDeletImage;
    @BindView(R.id.select_all)
    protected CheckBox mCheckBox;
    @BindView(R.id.text_delet)
    protected TextView mDeletText;
    @BindView(R.id.list_contents_collapsing_toolbar)
    protected
    CollapsingToolbarLayout mCollapsingToolbarLayout;


    private SearchManager searchManager;
    boolean isVisibl;



    private Fragment mContentListFragment;

    @BindView(R.id.list_contents_toolbar)
     protected Toolbar mToolbar;

    @Override
    protected void onViewReady(Bundle savedInstanceState, Intent intent) {
        super.onViewReady(savedInstanceState, intent);

        mContentListFragment = new ListContentsFragment();
        if (savedInstanceState != null) {
            mContentListFragment =  getSupportFragmentManager().getFragment(savedInstanceState, Contract.EXTRA_TELEPROMPTER_FRAGMENT);

        }
        mCollapsingToolbarLayout.setTitleEnabled(false);

        setupSearchToolbar();
        // mImgSearch.setOnClickListener(this);
        isVisibl = false;
        /*SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

*/
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.content_list_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

            queryTextListener = new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextChange(String newText) {
                    Log.i("onQueryTextChange", newText);

                    return true;
                }
                @Override
                public boolean onQueryTextSubmit(String query) {
                    Log.i("onQueryTextSubmit", query);

                    return true;
                }
            };
            searchView.setOnQueryTextListener(queryTextListener);
        }
        super.onCreateOptionsMenu(menu);
        return  super.onCreateOptionsMenu(menu);


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
            case R.id.action_search:

                // Not implemented here
             break;
            case R.id.action_select:
                mEditContainer.setVisibility(View.VISIBLE);
                break;

            case R.id.action_setting:
                Intent intent=(new Intent(ListContentsActivity.this, SettingsActivity.class));
                startActivity(intent);

            default:
                break;
        }
        searchView.setOnQueryTextListener(queryTextListener);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setListener() {
        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if(b){
                    mDeletImage.setVisibility(View.VISIBLE);
                    mDeletText.setVisibility(View.VISIBLE);
                }else {
                    mDeletImage.setVisibility(View.GONE);
                    mDeletText.setVisibility(View.GONE);
                }

            }
        });

     /*   searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
               // searchFor(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
               // filterSearchFor(query);
                return true;
            }
        });*/
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {


        super.onSaveInstanceState(outState);
    }
    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
           case R.id.check_item:
               if(mCheckBox.isChecked()){
                   mDeletImage.setVisibility(View.VISIBLE);
                   mDeletText.setVisibility(View.VISIBLE);
               }else {
                   mDeletImage.setVisibility(View.GONE);
                   mDeletText.setVisibility(View.GONE);
               }

              //  mEtSearch.setVisibility(View.VISIBLE);
/*

                if (isVisibl) {
                    mEtSearch.setVisibility(View.GONE);
                    isVisibl=false;
                }else {
                    mEtSearch.setVisibility(View.VISIBLE);
                    Toast.makeText(this,"search from list",Toast.LENGTH_LONG).show();
                    isVisibl=true;
                }

*/



        }
    }


    private void initFragment(){
        Bundle bundle = new Bundle();

        bundle.putString(Contract.EXTRA_TEXT, getResources().getString(R.string.mytest));
        mContentListFragment.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.contents_container, mContentListFragment)
                .commit();
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


    public boolean isTablet() {
        return (ListContentsActivity.this.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }
}
