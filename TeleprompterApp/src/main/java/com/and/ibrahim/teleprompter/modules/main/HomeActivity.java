package com.and.ibrahim.teleprompter.modules.main;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.and.ibrahim.teleprompter.R;
import com.and.ibrahim.teleprompter.base.BaseActivity;
import com.and.ibrahim.teleprompter.data.Contract;
import com.and.ibrahim.teleprompter.modules.display.DisplayActivity;
import com.and.ibrahim.teleprompter.mvp.model.DataObj;
import com.and.ibrahim.teleprompter.mvp.view.RecyclerViewItemClickListener;
import com.and.ibrahim.teleprompter.mvp.view.RecylerViewClickListener;
import com.and.ibrahim.teleprompter.util.FabAnimations;
import com.and.ibrahim.teleprompter.util.GetData;
import com.and.ibrahim.teleprompter.util.GetScreenOrientation;
import com.and.ibrahim.teleprompter.util.LinedEditText;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.Unbinder;

public class HomeActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG="HomeActivity";
    @BindView(R.id.collapsing_toolbar)
    protected
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    @BindView(R.id.toolbar)
    protected
    Toolbar toolbar;
    @BindView(R.id.fab)
    protected
    FloatingActionButton mFab;
    @BindView(R.id.fab_add)
    protected
    FloatingActionButton mFabAdd;
    @BindView(R.id.fab_storag)
    protected
    FloatingActionButton mFabStorag;
    @BindView(R.id.fab_cloud)
    protected
    FloatingActionButton mFabCloud;
    @BindView(R.id.recycler_view)
    protected RecyclerView mRecyclerView;
    Dialog dialog;

    private ArrayList<DataObj> mArrayList;
    private DataObj mDataObj;
    TeleprompterAdapter teleprompterAdapter ;

    private Unbinder unbinder;
    private FabAnimations mFabAnimations;


    @Override
    protected void onViewReady(Bundle savedInstanceState, Intent intent) {
        super.onViewReady(savedInstanceState, intent);


    }


    @Override
    public int getResourceLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void getExtra() {

    }


    @Override
    public void init() {
        mFabAnimations =new FabAnimations(this,mFab,mFabAdd,mFabStorag,mFabCloud);
        teleprompterAdapter = new TeleprompterAdapter( getLayoutInflater());
        mCollapsingToolbarLayout.setTitle(getString(R.string.app_name));
        mCollapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));

        mFabAnimations.addFabAnimationRes();

        mArrayList=new ArrayList<>();
        mDataObj =new DataObj();
        initialiseListWithPhoneScreen();
    }

    @Override
    public void setListener() {
        mFab.setOnClickListener(this);
        mFabAdd.setOnClickListener(this);
        mFabStorag.setOnClickListener(this);
        mFabCloud.setOnClickListener(this);

        OnTouchRecyclerView();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.fab:
                mFabAnimations.animateFAB();
                break;
            case R.id.fab_add:
                launchDismissDlg();
                dialog.show();
                Log.d(TAG,"FabAction is "+"Fab Add");

                break;
            case R.id.fab_storag:

                Log.d(TAG,"FabAction is "+"Fab Storag");

                break;
            case R.id.fab_cloud:
                // CustomDialogClass cdd=new CustomDialogClass(this, R.style.PauseDialog);
                launchDismissDlg();
                dialog.show();
                Log.d(TAG,"FabAction is "+"Fab Cloud");

                break;
        }
    }
    private void initialiseListWithPhoneScreen() {

        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics ();
        display.getMetrics(outMetrics);

        float density  = getResources().getDisplayMetrics().density;
        float dpHeight = outMetrics.heightPixels / density;
        float dpWidth  = outMetrics.widthPixels / density;

        Log.d(TAG,"screen width = "+String.valueOf(dpWidth));
        Log.d(TAG,"screen height = "+String.valueOf(dpHeight));

        mArrayList=GetData.getTeleprmpters(this);
        mRecyclerView.setHasFixedSize(true);
        int columnCount = getResources().getInteger(R.integer.list_column_count);
        GridLayoutManager gridLayoutManager;
       if( GetScreenOrientation.GetListByScreenSize(this)){
           gridLayoutManager=new GridLayoutManager(this, 4);

       }else {
           gridLayoutManager=new GridLayoutManager(this, columnCount);

       }
        mRecyclerView.setLayoutManager(gridLayoutManager);
        //Pass a list of images with inflater ​​in adapter
        TeleprompterAdapter teleprompterAdapter = new TeleprompterAdapter( getLayoutInflater());
        teleprompterAdapter.addNewContent(mArrayList);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                gridLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        mRecyclerView.setAdapter(teleprompterAdapter);
    }

    public void OnTouchRecyclerView(){
        mRecyclerView.addOnItemTouchListener(new RecyclerViewItemClickListener(this, mRecyclerView, new RecylerViewClickListener() {
            @Override
            public void onClick(View view, int position) {

                Intent intent = new Intent(HomeActivity.this, DisplayActivity.class);
                startActivity(intent);


            }

            @Override
            public void onLongClick(View view, final int position) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        HomeActivity.this);

                // set title
                alertDialogBuilder.setTitle("Delete");

                // set dialog message
                alertDialogBuilder
                        .setMessage("Click yes to delete this file!")
                        .setCancelable(false)
                        .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {

                           /* ContentValues songValues = new ContentValues();
                            getContentResolver().delete(Contract.BakeEntry.PATH_TELEPROMPTER_URI,
                                    Contract.BakeEntry._ID + " = ?",
                                    new String[]{songValues.getAsString(String.valueOf(position))});*/

                                if (GetData.getTeleprmpters(HomeActivity.this).size() > 0) {

                                  //  getContentResolver().delete(uri, null, null);

                                    String mySelection=Contract.BakeEntry._ID+" =?";

                                   // getContentResolver().delete(uri, mySelection, mySelectionArgs);
                                   // int id = (int) viewHolder.itemView.getTag();

                                    // Build appropriate uri with String row id appended
                                    String stringId = String.valueOf(mArrayList.get(position).getId());

                                    Uri uri = Contract.BakeEntry.PATH_TELEPROMPTER_URI;

                                    uri = uri.buildUpon().appendPath(stringId).build();

                                    // COMPLETED (2) Delete a single row of data using a ContentResolver
                                    getContentResolver().delete(uri, null, null);
                                    if (uri != null) {
                                        Log.d("contentResolver delete", "delete success");
                                       // teleprompterAdapter.removeContent(mArrayList);
                                        mArrayList.clear();
                                        teleprompterAdapter.removeContent(mArrayList);
                                        mArrayList=GetData.getTeleprmpters(HomeActivity.this);
                                        teleprompterAdapter.addNewContent(mArrayList);
                                        mRecyclerView.setAdapter(teleprompterAdapter);

                                    }

                                }
                            }
                        })
                        .setNegativeButton("No",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // if this button is clicked, just close
                                // the dialog box and do nothing
                                dialog.cancel();
                            }
                        });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();                    }
        }));

    }
    private void launchDismissDlg() {
        dialog = new Dialog(this, R.style.PauseDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_item);
        dialog.setCanceledOnTouchOutside(true);

        TextView mAdd=dialog.findViewById(R.id.txt_add);
        TextView mCancel=dialog.findViewById(R.id.txt_cancel);
        final LinedEditText mEditContent=dialog.findViewById(R.id.linededit_text_content);
        final EditText mEditTitle=dialog.findViewById(R.id.edit_title);

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog. dismiss();
                dialog.cancel();
                dialog.hide();


            }
        });


        mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Cursor countCursor = getContentResolver().query(Contract.BakeEntry.PATH_TELEPROMPTER_URI,
                        new String[] {"count(*) AS count"},
                        null,
                        null,
                        null);

                countCursor.moveToFirst();
                int count = countCursor.getInt(0);

                ContentValues values = new ContentValues();
                values.put(Contract.BakeEntry.COL_TITLE, mEditTitle.getText().toString());
                values.put(Contract.BakeEntry.COL_COTENTS, mEditContent.getText().toString());
                values.put(Contract.BakeEntry.COL_UNIQUE_ID, count+1);


                final Uri uriInsert = getContentResolver().insert(Contract.BakeEntry.PATH_TELEPROMPTER_URI, values);
                if (uriInsert != null) {
                    Log.d("contentResolver insert", "first added success");

                    values.clear();
                }

                mArrayList.clear();
                teleprompterAdapter.removeContent(mArrayList);
                mArrayList=GetData.getTeleprmpters(HomeActivity.this);
                teleprompterAdapter.addNewContent(mArrayList);
                mRecyclerView.setAdapter(teleprompterAdapter);
                mRecyclerView.smoothScrollToPosition(mRecyclerView.getAdapter().getItemCount()-1);
                values.clear();

            }
        });
        //  dialog.setCanceledOnTouchOutside(false);
        // dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        // dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.show();

    }
    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
