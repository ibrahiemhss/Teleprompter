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
import com.and.ibrahim.teleprompter.callback.FragmentEditListRefreshListener;
import com.and.ibrahim.teleprompter.callback.OnDataPassListener;
import com.and.ibrahim.teleprompter.callback.OnItemViewClickListener;
import com.and.ibrahim.teleprompter.data.Contract;
import com.and.ibrahim.teleprompter.modules.display.DisplayActivity;
import com.and.ibrahim.teleprompter.mvp.model.DataObj;
import com.and.ibrahim.teleprompter.mvp.view.RecyclerViewItemClickListener;
import com.and.ibrahim.teleprompter.mvp.view.RecylerViewClickListener;
import com.and.ibrahim.teleprompter.util.FabAnimations;
import com.and.ibrahim.teleprompter.util.GetData;
import com.and.ibrahim.teleprompter.util.LinedEditText;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.Gravity.BOTTOM;
import static android.view.Gravity.CENTER;

@SuppressWarnings("ALL")
public class ListContentsFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "ListContentsFragment";

    @BindView(R.id.edit_container)
    protected RelativeLayout mEditContainer;
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.delete_all)
    protected ImageView mDeleteImage;
    @BindView(R.id.select_all)
    protected CheckBox mCheckBox;
    @BindView(R.id.text_delete)
    protected TextView mDeleteText;
    @BindView(R.id.return_up)
    protected ImageView mReturnUp;
    @BindView(R.id.fab)
    protected FloatingActionButton mFab;
    @BindView(R.id.fab_add)
    protected FloatingActionButton mFabAdd;
    @BindView(R.id.fab_storage)
    protected FloatingActionButton mFabStorage;
    @BindView(R.id.fab_cloud)
    protected FloatingActionButton mFabCloud;
    @BindView(R.id.recycler_view)
    protected RecyclerView mRecyclerView;
    boolean isFirstOpen;
    private Dialog mDialog;
    private boolean isAdded = false;
    private boolean isOpen = false;
    private OnDataPassListener dataPasser;
    private ArrayList<DataObj> mArrayList;
    private TeleprompterAdapter teleprompterAdapter;
    private FabAnimations mFabAnimations;
    private boolean isDialogShow;
    private String mLastTitleAdding;
    private String mLastContentAdding;
    private LinedEditText mEditContent;
    private MaterialEditText mEditTitle;
    private void readBundle(Bundle bundle) {//get Value from activity///

        if (bundle != null && bundle.containsKey(Contract.EXTRA_TEXT)) {
            int mFlag = bundle.getInt(Contract.EXTRA_FLAG);
            boolean isChecked = bundle.getBoolean(Contract.EXTRA_SELECTED);
            Log.d(TAG, "myFlag is " + String.valueOf(mFlag));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_content_fragment, container, false);
        ButterKnife.bind(this, view);
        isFirstOpen = true;
        Bundle extras = this.getArguments();
        if (extras != null) {
            readBundle(extras);
        }
        initializeView();
        initializeList();

        if (isTablet()) {
            ((DisplayActivity) Objects.requireNonNull(getActivity())).setFragmentEditListRefreshListener(new FragmentEditListRefreshListener() {
                @Override
                public void onRefresh() {

                    mEditContainer.setVisibility(View.VISIBLE);
                }
            });

        }
        if (!isTablet()) {
            ((ListContentsActivity) getActivity()).setFragmentEditListRefreshListener(new FragmentEditListRefreshListener() {
                @Override
                public void onRefresh() {

                    mEditContainer.setVisibility(View.VISIBLE);
                }
            });
        }
        return view;

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (isTablet()) {
            dataPasser = (OnDataPassListener) context;

        }
    }

    public void passData(String data) {
        dataPasser.onDataPass(data);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        if (savedInstanceState != null) {

            isDialogShow = savedInstanceState.getBoolean(Contract.EXTRA_SHOW_DIALOG);
            if (isDialogShow) {
                mLastTitleAdding = savedInstanceState.getString(Contract.EXTRA_STRING_CONTENT);
                mLastTitleAdding = savedInstanceState.getString(Contract.EXTRA_STRING_TITLE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    launchAddDialog(mLastTitleAdding, mLastContentAdding);
                }
                mDialog.show();

            }
        }

        mFab.setOnClickListener(this);
        mFabAdd.setOnClickListener(this);
        mFabStorage.setOnClickListener(this);
        mFabCloud.setOnClickListener(this);
        mDeleteImage.setOnClickListener(this);
        mReturnUp.setOnClickListener(this);

        OnTouchRecyclerView();

        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b) {
                    mDeleteImage.setVisibility(View.VISIBLE);
                    mDeleteText.setVisibility(View.VISIBLE);
                    chekAll();

                } else {
                    mDeleteImage.setVisibility(View.GONE);
                    mDeleteText.setVisibility(View.GONE);
                    unCheckAll();
                }


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

                    launchAddDialog(null, null);
                    mDialog.show();

                }
                Log.d(TAG, "FabAction is " + "Fab Add");

                break;
            case R.id.fab_storage:

                Log.d(TAG, "FabAction is " + "Fab Storage");

                break;
            case R.id.fab_cloud:
                // CustomDialogClass cdd=new CustomDialogClass(this, R.style.PauseDialog);
                launchAddDialog(null, null);
                mDialog.show();
                Log.d(TAG, "FabAction is " + "Fab Cloud");
            case R.id.delete_all:
                launchSelectedDialog();
                break;
            case R.id.return_up:
                mEditContainer.setVisibility(View.GONE);
                unCheckAll();
            default:
                break;
        }

    }

    public void initializeView() {
        mFabAnimations = new FabAnimations(getActivity(), mFab, mFabAdd, mFabStorage, mFabCloud);
        teleprompterAdapter = new TeleprompterAdapter(getLayoutInflater(), new OnItemViewClickListener() {
            @Override
            public void onEditImgClickListener(int position, View v) {
                launchPopUpMenu(v, position);
            }
            @Override
            public void onTextClickListener(int position, View v) {

                if (isTablet()) {

                    passData(getString(R.string.mytest));
                } else {
                    Intent intent = new Intent(getActivity(), DisplayActivity.class);
                    intent.putExtra(Contract.EXTRA_TEXT, getString(R.string.mytest));
                    startActivity(intent);
                }

            }

            @Override
            public void onImageClickListener(int position, View v) {

                if (isTablet()) {

                    passData(getString(R.string.mytest));
                } else {
                    Intent intent = new Intent(getActivity(), DisplayActivity.class);
                    intent.putExtra(Contract.EXTRA_TEXT, getString(R.string.mytest));
                    startActivity(intent);
                }

            }

            @Override
            public void onViewGroupClickListener(int adapterPosition, View view) {
                if (isTablet()) {

                    passData(getString(R.string.mytest));
                } else {
                    Intent intent = new Intent(getActivity(), DisplayActivity.class);
                    intent.putExtra(Contract.EXTRA_TEXT, getString(R.string.mytest));
                    startActivity(intent);
                }

            }

            @Override
            public void onItemCheckListener(int pos, View item) {

                ContentValues values = new ContentValues();
                int id = mArrayList.get(pos).getId();

                values.put(Contract.BakeEntry.COL_SELECT, 1);
                getActivity().getContentResolver().update(Contract.BakeEntry.PATH_TELEPROMPTER_URI, values,
                        Contract.BakeEntry._ID + "=?",
                        new String[]{String.valueOf(id)});
                values.clear();
                Log.d(TAG, "cheked_ite = checked" + String.valueOf(id));

            }

            @Override
            public void onItemUncheckListener(int pos, View item) {
                ContentValues values = new ContentValues();
                int id = mArrayList.get(pos).getId();

                values.put(Contract.BakeEntry.COL_SELECT, 0);
                getActivity().getContentResolver().update(Contract.BakeEntry.PATH_TELEPROMPTER_URI, values,
                        Contract.BakeEntry._ID + "=?",
                        new String[]{String.valueOf(id)});
                values.clear();
                Log.d(TAG, "cheked_item = unchecked" + String.valueOf(id));

            }
        });

        mArrayList = new ArrayList<>();
        mFabAnimations.addFabAnimationRes();

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void initializeList() {

        Display display = Objects.requireNonNull(getActivity()).getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float density = getResources().getDisplayMetrics().density;
        float dpHeight = outMetrics.heightPixels / density;
        float dpWidth = outMetrics.widthPixels / density;
        Log.d(TAG, "screen width = " + String.valueOf(dpWidth));
        Log.d(TAG, "screen height = " + String.valueOf(dpHeight));

        mArrayList = GetData.getTeleprompters(getActivity());
        mRecyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = null;

        gridLayoutManager = new GridLayoutManager(getActivity(), 1);

        mRecyclerView.setLayoutManager(gridLayoutManager);
        teleprompterAdapter.addNewContent(mArrayList);

        mRecyclerView.setAdapter(teleprompterAdapter);
    }

    private void OnTouchRecyclerView() {
        mRecyclerView.addOnItemTouchListener(new RecyclerViewItemClickListener(getActivity(), new RecylerViewClickListener() {
            @Override
            public void onClick(View view, final int position) {

            }

        }));

    }

    private void launchPopUpMenu(View view, final int position) {

        Context wrapper = new ContextThemeWrapper(getActivity(), R.style.MyPopupMenu);
        PopupMenu popup = new PopupMenu(wrapper, view);
        popup.getMenuInflater().inflate(R.menu.clipboard_popup, popup.getMenu());
        popup.show();
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.edit:

                        Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), " Install Clicked at position " + " : " + position, Toast.LENGTH_LONG).show();

                        break;
                    case R.id.delete:

                        Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), "Add to Wish List Clicked at position " + " : " + position, Toast.LENGTH_LONG).show();
                        launchDeleteDialog(position);
                        break;


                    default:
                        break;
                }

                return true;
            }
        });


    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void launchAddDialog(String title, String content) {

        isAdded = false;
        mDialog = new Dialog(Objects.requireNonNull(getActivity()));
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(mDialog.getWindow()).getAttributes());
        lp.width = 48;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = BOTTOM | CENTER;
        lp.windowAnimations = R.style.Theme_Dialog;
        mDialog.getWindow().setAttributes(lp);

        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.custom_dialog_add_content);

        TextView mAdd = mDialog.findViewById(R.id.txt_add);
        TextView mCancel = mDialog.findViewById(R.id.txt_cancel);
        mEditContent = mDialog.findViewById(R.id.linededit_text_content);
        mEditTitle = mDialog.findViewById(R.id.edit_title);

        if (title != null || content != null) {
            mEditTitle.setText(title);
            mEditContent.setText(content);
        }
        mLastTitleAdding = mEditTitle.getText().toString();
        mLastContentAdding = mEditContent.getText().toString();
        isDialogShow = true;
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mDialog.dismiss();
                isDialogShow = false;

            }
        });


        mAdd.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                isDialogShow = false;

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

        mDialog.show();
        isOpen = false;

    }

    private void launchSelectedDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                Objects.requireNonNull(getActivity()));
        alertDialogBuilder.setTitle("Delete");

        alertDialogBuilder
                .setMessage("Click yes to delete selected files!")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (GetData.getTeleprompters(getActivity()).size() > 0) {

                            Cursor countCursor = getActivity().getContentResolver().query(Contract.BakeEntry.PATH_TELEPROMPTER_URI,
                                    new String[]{"count(*) AS count"},
                                    null,
                                    null,
                                    null);

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                Objects.requireNonNull(countCursor).moveToFirst();
                            }
                            for (int i = 0; i < mArrayList.size(); i++) {
                                DataObj item = mArrayList.get(i);

                                if (item.getIsChecked() == 1) {

                                    deletSelectedItem(i);
                                }


                            }

                            unCheckAll();
                            refreshList();
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    public boolean isTablet() {
        return (Objects.requireNonNull(getActivity()).getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public void launchDeleteDialog(final int position) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                Objects.requireNonNull(getActivity()));
        alertDialogBuilder.setTitle("Delete");

        // set dialog message
        alertDialogBuilder
                .setMessage("Click yes to delete this file!")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        deletSelectedItem(position);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
    }

    private void deletSelectedItem(int position) {

             /* ContentValues songValues = new ContentValues();
                            getContentResolver().delete(Contract.BakeEntry.PATH_TELEPROMPTER_URI,
                                    Contract.BakeEntry._ID + " = ?",
                                    new String[]{songValues.getAsString(String.valueOf(position))});*/

        if (GetData.getTeleprompters(getActivity()).size() > 0) {

            String stringId = String.valueOf(mArrayList.get(position).getId());

            Uri uri = Contract.BakeEntry.PATH_TELEPROMPTER_URI;

            uri = uri.buildUpon().appendPath(stringId).build();

            Objects.requireNonNull(getActivity()).getContentResolver().delete(uri, null, null);
            Log.d("contentResolver delete", "delete success");

            refreshList();
            if (mArrayList.size() > 4) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    mRecyclerView.smoothScrollToPosition(Objects.requireNonNull(mRecyclerView.getAdapter()).getItemCount() - 1);
                }

            }

        }
    }

    private void refreshList() {
        mArrayList.clear();
        teleprompterAdapter.removeContent();
        mArrayList = GetData.getTeleprompters(getActivity());
        teleprompterAdapter.addNewContent(mArrayList);
        mRecyclerView.setAdapter(teleprompterAdapter);

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mEditTitle != null && mEditContent != null) {
            outState.putString(Contract.EXTRA_STRING_TITLE, mEditTitle.getText().toString());
            outState.putString(Contract.EXTRA_STRING_CONTENT, mEditContent.getText().toString());

        }
        outState.putBoolean(Contract.EXTRA_SHOW_DIALOG, isDialogShow);
    }

    @Override
    public void onPause() {
        super.onPause();
        unCheckAll();
        mEditContainer.setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unCheckAll();
    }

    @Override
    public void onStart() {
        super.onStart();
        unCheckAll();

    }

    private void chekAll() {
        ContentValues values = new ContentValues();
        values.put(Contract.BakeEntry.COL_SELECT, 1);
        Objects.requireNonNull(getActivity()).getContentResolver().update(Contract.BakeEntry.PATH_TELEPROMPTER_URI, values, null, null);

        refreshList();
        values.clear();


    }

    private void unCheckAll() {
        ContentValues values = new ContentValues();
        values.put(Contract.BakeEntry.COL_SELECT, 0);
        Objects.requireNonNull(getActivity()).getContentResolver().update(Contract.BakeEntry.PATH_TELEPROMPTER_URI, values, null, null);

        refreshList();
        values.clear();

    }
}
