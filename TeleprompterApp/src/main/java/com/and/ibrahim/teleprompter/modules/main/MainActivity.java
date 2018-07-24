package com.and.ibrahim.teleprompter.modules.main;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.and.ibrahim.teleprompter.R;
import com.and.ibrahim.teleprompter.data.Contract;
import com.and.ibrahim.teleprompter.mvp.model.DataObj;
import com.and.ibrahim.teleprompter.mvp.view.RecylerViewClickListener;
import com.and.ibrahim.teleprompter.util.FabAnimations;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import com.and.ibrahim.teleprompter.util.GetData;
import com.and.ibrahim.teleprompter.util.LinedEditText;
import com.and.ibrahim.teleprompter.mvp.view.RecyclerViewItemClickListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener  {

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
    Dialog dialog;

    private ArrayList<DataObj> mArrayList;
    private DataObj mDataObj;
    TeleprompterAdapter teleprompterAdapter ;

    private Unbinder unbinder;
    private FabAnimations mFabAnimations;


        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder= ButterKnife.bind(this);

        mFabAnimations =new FabAnimations(this,mFab,mFab1,mFab2);
        teleprompterAdapter = new TeleprompterAdapter( getLayoutInflater());
        mCollapsingToolbarLayout.setTitle(getString(R.string.app_name));
        mCollapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));

        mFabAnimations.addFabAnimationRes();
        mFab.setOnClickListener(this);
        mFab1.setOnClickListener(this);
        mFab2.setOnClickListener(this);

            mArrayList=new ArrayList<>();
            mDataObj =new DataObj();
            for (int i=0;i<10;i++){
            //    mDataObj.setTextTitle("title contents review");
            //    mDataObj.setTextContent("kgjkgjkjg kjgo dfeooer fdfdkfjd fkdlkfldkf fdlfkdlfkld dfkdlfkldfk dflkdlfkldkf ldkfldfk" +
             //           "dfjdhfjdfh dkfjkdjfkjd dkjfdkjfkdj dkjfkdjfkdj dkjfkdjkdjf dkfjdkjf dkjkd fkjdk dkjfkdj fdkjfk");
              //  mArrayList.add(mDataObj);

            }

            initialiseListWithPhoneScreen();

            mRecyclerView.addOnItemTouchListener(new RecyclerViewItemClickListener(this, mRecyclerView, new RecylerViewClickListener() {
                @Override
                public void onClick(View view, int position) {
                               }

                @Override
                public void onLongClick(View view, int position) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            MainActivity.this);

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

                                    if (GetData.getTeleprmpters(MainActivity.this).size() > 0) {

                                        Uri uri = Contract.BakeEntry.PATH_TELEPROMPTER_URI;
                                        uri = uri.buildUpon().appendPath(null).build();
                                        getContentResolver().delete(uri, null, null);
                                        if (uri != null) {
                                            Log.d("contentResolver delete", "delete success");
                                        }
                                    }
                                    MainActivity.this.finish();
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
               // CustomDialogClass cdd=new CustomDialogClass(this, R.style.PauseDialog);
                launchDismissDlg();
                dialog.show();
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

        mArrayList=GetData.getTeleprmpters(this);
        mRecyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager=new GridLayoutManager(this, 3);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        //Pass a list of images with inflater ​​in adapter
        TeleprompterAdapter teleprompterAdapter = new TeleprompterAdapter( getLayoutInflater());
        teleprompterAdapter.addNewContent(mArrayList);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                gridLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        mRecyclerView.setAdapter(teleprompterAdapter);
    }


    //initialiseList to show values inside mBake_list
    private void initialiseListWithsLargeSize() {


        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2,
                GridLayoutManager.VERTICAL, false));
        //Pass a list of images with inflater ​​in adapter

        teleprompterAdapter.addNewContent(mArrayList);

        mRecyclerView.setAdapter(teleprompterAdapter);

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

                ContentValues values = new ContentValues();
                values.put(Contract.BakeEntry.COL_TITLE, mEditTitle.getText().toString());
                values.put(Contract.BakeEntry.COL_COTENTS, mEditContent.getText().toString());

                final Uri uriInsert = getContentResolver().insert(Contract.BakeEntry.PATH_TELEPROMPTER_URI, values);
                if (uriInsert != null) {
                    Log.d("contentResolver insert", "first added success");

                    values.clear();
                }

                mArrayList.clear();
                teleprompterAdapter.removeContent(mArrayList);
                mArrayList=GetData.getTeleprmpters(MainActivity.this);
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

}
