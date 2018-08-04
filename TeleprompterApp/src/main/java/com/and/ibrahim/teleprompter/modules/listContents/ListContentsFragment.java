package com.and.ibrahim.teleprompter.modules.listContents;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.and.ibrahim.teleprompter.R;
import com.and.ibrahim.teleprompter.data.Contract;
import com.and.ibrahim.teleprompter.interfaces.FragmentEditListRefreshListener;
import com.and.ibrahim.teleprompter.interfaces.OnCheckBoxChangeListner;
import com.and.ibrahim.teleprompter.interfaces.OnItemViewClickListner;
import com.and.ibrahim.teleprompter.interfaces.onDisplayActivityCallBackListner;
import com.and.ibrahim.teleprompter.modules.display.DisplayActivity;
import com.and.ibrahim.teleprompter.mvp.model.DataObj;
import com.and.ibrahim.teleprompter.mvp.view.RecyclerViewItemClickListener;
import com.and.ibrahim.teleprompter.mvp.view.RecylerViewClickListener;
import com.and.ibrahim.teleprompter.util.FabAnimations;
import com.and.ibrahim.teleprompter.util.GetData;
import com.and.ibrahim.teleprompter.util.GetScreenOrientation;
import com.and.ibrahim.teleprompter.util.LinedEditText;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.Gravity.BOTTOM;
import static android.view.Gravity.CENTER;

public class ListContentsFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "ListContentsFragment";

    @BindView(R.id.edit_container)
    protected RelativeLayout mEditContainer;
    @BindView(R.id.delete_all)
    protected ImageView mDeletImage;
    @BindView(R.id.select_all)
    protected CheckBox mCheckBox;
    @BindView(R.id.text_delet)
    protected TextView mDeletText;

    @BindView(R.id.fab)
    protected
    FloatingActionButton mFab;
    @BindView(R.id.fab_add)
    protected
    FloatingActionButton mFabAdd;
    @BindView(R.id.fab_storage)
    protected
    FloatingActionButton mFabStorage;
    @BindView(R.id.fab_cloud)
    protected
    FloatingActionButton mFabCloud;
    @BindView(R.id.recycler_view)
    protected RecyclerView mRecyclerView;
    private Dialog dialog;
    private boolean isAdded = false;
    private boolean isOpen = false;
    private String mData;
    OnDataPass dataPasser;
    private ArrayList<DataObj> mArrayList;
    private TeleprompterAdapter teleprompterAdapter;
    private int mFlag;
    boolean isFirstOpen;
    private boolean isCheked;

    private FabAnimations mFabAnimations;

    private onDisplayActivityCallBackListner mOnDisplayActivityCallBackListner;

    private void readBundle(Bundle bundle) {

        if (bundle != null && bundle.containsKey(Contract.EXTRA_TEXT)) {
            //mScrollString = bundle.getString(Contract.EXTRA_TEXT);
           mFlag =bundle.getInt(Contract.EXTRA_FLAG);
           isCheked=bundle.getBoolean(Contract.EXTRA_SELECTED);
            Log.d(TAG, "myFlag is " + String.valueOf(mFlag));


        }

    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_content_fragment, container, false);
        ButterKnife.bind(this, view);
        isFirstOpen=true;
        Bundle extras = this.getArguments();
        if (extras != null) {
            readBundle(extras);
        }
        initializeView();
        initializeList();



        ((ListContentsActivity)getActivity()).setFragmentEditListRefreshListener(new FragmentEditListRefreshListener() {
            @Override
            public void onRefresh() {

                    mEditContainer.setVisibility(View.VISIBLE);
                    }
        });
        return view;

    }


    public interface OnDataPass {
        public void onDataPass(String data);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (isTablet()) {
            dataPasser = (OnDataPass) context;

        }
    }
    public void passData(String data) {
        dataPasser.onDataPass(data);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {

                    }

        mFab.setOnClickListener(this);
        mFabAdd.setOnClickListener(this);
        mFabStorage.setOnClickListener(this);
        mFabCloud.setOnClickListener(this);

        OnTouchRecyclerView();

        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                ContentValues values = new ContentValues();

                if(b){
                    mDeletImage.setVisibility(View.VISIBLE);
                    mDeletText.setVisibility(View.VISIBLE);
                    values.put(Contract.BakeEntry.COL_SELECT, 1);


                }else {
                    mDeletImage.setVisibility(View.GONE);
                    mDeletText.setVisibility(View.GONE);
                    values.put(Contract.BakeEntry.COL_SELECT, 0);

                }

                getActivity().getContentResolver().update(Contract.BakeEntry.PATH_TELEPROMPTER_URI, values,null,null);

                refreshList();
                values.clear();

            }
        });

    }



    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View view) {

        int id = view.getId();
        switch (id) {
            case R.id.fab:
                mFabAnimations.animateFAB();
                break;
            case R.id.fab_add:
                if (!isOpen) {
                    isOpen = true;

                    launchAddDailog();
                    dialog.show();

                }
                Log.d(TAG, "FabAction is " + "Fab Add");

                break;
            case R.id.fab_storage:

                Log.d(TAG, "FabAction is " + "Fab Storage");

                break;
            case R.id.fab_cloud:
                // CustomDialogClass cdd=new CustomDialogClass(this, R.style.PauseDialog);
                launchAddDailog();
                dialog.show();
                Log.d(TAG, "FabAction is " + "Fab Cloud");

                break;

        }


    }



    public void initializeView(){
        mFabAnimations = new FabAnimations(getActivity(), mFab, mFabAdd, mFabStorage, mFabCloud);
        teleprompterAdapter = new TeleprompterAdapter(getActivity(), getLayoutInflater(), new OnItemViewClickListner() {
            @Override
            public void onEditImgClickListner(int position, View v) {
                launchPopUpMenu(v, position);

            }

            @Override
            public void onTextClickListner(int position, View v) {

                if (isTablet()) {

                    passData(getString(R.string.mytest));
                } else {
                    Intent intent = new Intent(getActivity(), DisplayActivity.class);
                    intent.putExtra(Contract.EXTRA_TEXT, getString(R.string.mytest));
                    startActivity(intent);
                }

            }

            @Override
            public void onImageClickListner(int position, View v) {

                if (isTablet()) {

                    passData(getString(R.string.mytest));
                } else {
                    Intent intent = new Intent(getActivity(), DisplayActivity.class);
                    intent.putExtra(Contract.EXTRA_TEXT, getString(R.string.mytest));
                    startActivity(intent);
                }

            }

            @Override
            public void onViewGroupClickListner(int adapterPosition, View view) {
                if (isTablet()) {

                    passData(getString(R.string.mytest));
                } else {
                    Intent intent = new Intent(getActivity(), DisplayActivity.class);
                    intent.putExtra(Contract.EXTRA_TEXT, getString(R.string.mytest));
                    startActivity(intent);
                }

            }
        }, new OnCheckBoxChangeListner() {
            @Override
            public void onChekedListner(int position, CompoundButton v, boolean is) {
                ContentValues values = new ContentValues();
                int id=mArrayList.get(position).getId();
                if(is){
                    values.put(Contract.BakeEntry.COL_SELECT, 1);
                    getActivity().getContentResolver().update(Contract.BakeEntry.PATH_TELEPROMPTER_URI, values,
                            Contract.BakeEntry._ID+"=?",
                            new String[]{String.valueOf(id)});
                    values.clear();
                }else {
                    values.put(Contract.BakeEntry.COL_SELECT, 0);
                    getActivity().getContentResolver().update(Contract.BakeEntry.PATH_TELEPROMPTER_URI, values,
                            Contract.BakeEntry._ID+"=?",
                            new String[]{String.valueOf(id)});
                    values.clear();
                }


            }
        });


        mArrayList = new ArrayList<>();

        mFabAnimations.addFabAnimationRes();

    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void initializeList() {


        Display display = getActivity().getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float density = getResources().getDisplayMetrics().density;
        float dpHeight = outMetrics.heightPixels / density;
        float dpWidth = outMetrics.widthPixels / density;

        Log.d(TAG, "screen width = " + String.valueOf(dpWidth));
        Log.d(TAG, "screen height = " + String.valueOf(dpHeight));

        mArrayList = GetData.getTeleprompters(getActivity());
        mRecyclerView.setHasFixedSize(true);
        int columnCount = getResources().getInteger(R.integer.list_column_count);
        GridLayoutManager gridLayoutManager = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (GetScreenOrientation.GetListByScreenSize(getActivity())) {
                gridLayoutManager = new GridLayoutManager(getActivity(), 3);

            } else {
                gridLayoutManager = new GridLayoutManager(getActivity(), columnCount);

            }
        }
        mRecyclerView.setLayoutManager(gridLayoutManager);
        //Pass a list of images with inflater ​​in adapter


        teleprompterAdapter.addNewContent(mArrayList);

        mRecyclerView.setAdapter(teleprompterAdapter);
    }

    private void OnTouchRecyclerView() {
        mRecyclerView.addOnItemTouchListener(new RecyclerViewItemClickListener(getActivity(), mRecyclerView, new RecylerViewClickListener() {
            @Override
            public void onClick(View view, final int position) {

            }

            @Override
            public void onLongClick(View view, final int position) {


            }
        }));

    }

    private void launchPopUpMenu(View view,final  int position){

        Context wrapper = new ContextThemeWrapper(getActivity(), R.style.MyPopupMenu);
        PopupMenu popup = new PopupMenu(wrapper, view);
        popup.getMenuInflater().inflate(R.menu.clipboard_popup,
                popup.getMenu());
        popup.show();

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.edit:

                        //Or Some other code you want to put here.. This is just an example.
                        Toast.makeText(getActivity().getApplicationContext(), " Install Clicked at position " + " : " + position, Toast.LENGTH_LONG).show();

                        break;
                    case R.id.delete:

                        Toast.makeText(getActivity().getApplicationContext(), "Add to Wish List Clicked at position " + " : " + position, Toast.LENGTH_LONG).show();
                        launchDeletDialog(position);
                        break;

                    default:
                        break;
                }

                return true;
            }
        });


    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void launchAddDailog() {
        isAdded = false;

        dialog = new Dialog(Objects.requireNonNull(getActivity()));
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.width = 48;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = BOTTOM | CENTER;
        lp.windowAnimations = R.style.Theme_Dialog;
        dialog.getWindow().setAttributes(lp);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_add_content);

        TextView mAdd = dialog.findViewById(R.id.txt_add);
        TextView mCancel = dialog.findViewById(R.id.txt_cancel);
        final LinedEditText mEditContent = dialog.findViewById(R.id.linededit_text_content);
        final MaterialEditText mEditTitle = dialog.findViewById(R.id.edit_title);

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                dialog.cancel();
                dialog.hide();


            }
        });


        mAdd.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {

                if (!isAdded) {
                    Cursor countCursor = getActivity().getContentResolver().query(Contract.BakeEntry.PATH_TELEPROMPTER_URI,
                            new String[]{"count(*) AS count"},
                            null,
                            null,
                            null);

                    Objects.requireNonNull(countCursor).moveToFirst();
                    int count = countCursor.getInt(0);

                    ContentValues values = new ContentValues();
                    values.put(Contract.BakeEntry.COL_TITLE, mEditTitle.getText().toString());
                    values.put(Contract.BakeEntry.COL_CONTENTS, Objects.requireNonNull(mEditContent.getText()).toString());
                    values.put(Contract.BakeEntry.COL_UNIQUE_ID, count + 1);


                    final Uri uriInsert = getActivity().getContentResolver().insert(Contract.BakeEntry.PATH_TELEPROMPTER_URI, values);
                    if (uriInsert != null) {
                        Log.d("contentResolver insert", "first added success");

                        values.clear();
                    }

                    refreshList();
                    values.clear();
                    isAdded = true;
                }


            }
        });


        //  dialog.setCanceledOnTouchOutside(false);
        // dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        // dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.show();
        isOpen = false;


    }
    public boolean isTablet() {
        return (getActivity().getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public void launchDeletDialog(final int position){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getActivity());        alertDialogBuilder.setTitle("Delete");

        // set dialog message
        alertDialogBuilder
                .setMessage("Click yes to delete this file!")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                           /* ContentValues songValues = new ContentValues();
                            getContentResolver().delete(Contract.BakeEntry.PATH_TELEPROMPTER_URI,
                                    Contract.BakeEntry._ID + " = ?",
                                    new String[]{songValues.getAsString(String.valueOf(position))});*/

                        if (GetData.getTeleprompters(getActivity()).size() > 0) {

                            //  getContentResolver().delete(uri, null, null);


                            // getContentResolver().delete(uri, mySelection, mySelectionArgs);
                            // int id = (int) viewHolder.itemView.getTag();

                            // Build appropriate uri with String row id appended
                            String stringId = String.valueOf(mArrayList.get(position).getId());

                            Uri uri = Contract.BakeEntry.PATH_TELEPROMPTER_URI;

                            uri = uri.buildUpon().appendPath(stringId).build();

                            // COMPLETED (2) Delete a single row of data using a ContentResolver
                            getActivity().getContentResolver().delete(uri, null, null);
                            Log.d("contentResolver delete", "delete success");
                            // teleprompterAdapter.removeContent(mArrayList);
                            refreshList();
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    private void refreshList(){
        mArrayList.clear();
        teleprompterAdapter.removeContent();
        mArrayList = GetData.getTeleprompters(getActivity());
        teleprompterAdapter.addNewContent(mArrayList);
        mRecyclerView.setAdapter(teleprompterAdapter);
        mRecyclerView.smoothScrollToPosition(Objects.requireNonNull(mRecyclerView.getAdapter()).getItemCount() - 1);

    }
}
