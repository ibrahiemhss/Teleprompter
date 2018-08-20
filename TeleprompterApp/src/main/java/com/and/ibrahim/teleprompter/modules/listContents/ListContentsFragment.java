package com.and.ibrahim.teleprompter.modules.listContents;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.and.ibrahim.teleprompter.R;
import com.and.ibrahim.teleprompter.mvp.view.OnDataPassListener;
import com.and.ibrahim.teleprompter.mvp.view.OnItemViewClickListener;
import com.and.ibrahim.teleprompter.data.Contract;
import com.and.ibrahim.teleprompter.modules.display.DisplayActivity;
import com.and.ibrahim.teleprompter.modules.widget.WidgetProvider;
import com.and.ibrahim.teleprompter.mvp.model.DataObj;
import com.and.ibrahim.teleprompter.mvp.view.RecyclerViewItemClickListener;
import com.and.ibrahim.teleprompter.util.FabAnimations;
import com.and.ibrahim.teleprompter.util.FetchData;
import com.and.ibrahim.teleprompter.util.GetData;
import com.and.ibrahim.teleprompter.util.LinedEditText;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveClient;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.OpenFileActivityOptions;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;
import static android.view.Gravity.BOTTOM;
import static android.view.Gravity.CENTER;

@SuppressWarnings({"deprecation", "WeakerAccess"})
public class ListContentsFragment extends Fragment implements View.OnClickListener, LoaderManager.LoaderCallbacks<ArrayList<DataObj>> {

    private static final String TAG = "ListContentsFragment";
    private static final int REQUEST_CODE_SIGN_IN = 0;
    private static final int REQUEST_CODE_OPEN_ITEM = 1;
    private static final int REQUEST_READ_STORAGE_CODE = 2;
    private static final int DATA_LOADER_ID = 3;

    @BindView(R.id.edit_container)
    protected RelativeLayout mEditContainer;
    @BindView(R.id.delete_all)
    protected ImageView mDeleteImage;
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
    @BindView(R.id.adView)
    protected AdView mAdView;

    private Dialog mAddDialog;
    private Dialog mUpdateDialog;
    private boolean isOpen = false;
    private OnDataPassListener dataPasser;
    private ArrayList<DataObj> mArrayList;
    private TeleprompterAdapter teleprompterAdapter;
    private FabAnimations mFabAnimations;
    private boolean isAddDialogShow;
    private boolean isUpdateDialogShow;
    private String mLastTitleAdding;
    private String mLastContentAdding;
    private String mLastTitleUpdating;
    private String mLastContentUpdating;
    private LinedEditText mEditTextAddContent;
    private MaterialEditText mEditTextAddTitle;
    private LinedEditText mEditTextUpdateContent;
    private MaterialEditText mEditTextUpdateTitle;
    private String mScrollString;
    private GoogleSignInAccount signInAccount;
    private DriveClient mDriveClient;
    private DriveResourceClient mDriveResourceClient;
    private TaskCompletionSource<DriveId> mOpenItemTaskSource;
    private boolean isTablet;
    private InterstitialAd mInterstitialAd;


    private void readBundle(Bundle bundle) {

        if (bundle != null && bundle.containsKey(Contract.EXTRA_TEXT)) {
            int mFlag = bundle.getInt(Contract.EXTRA_FLAG);
            Log.d(TAG, "myFlag is " + String.valueOf(mFlag));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_content_fragment, container, false);
        ButterKnife.bind(this, view);
        MobileAds.initialize(getActivity(),
                getResources().getString(R.string.app_ads_id));
        initializeAdd();
        LoaderCallbacks<ArrayList<DataObj>> callback = ListContentsFragment.this;

        isTablet = getResources().getBoolean(R.bool.isTablet);

        Bundle extras = this.getArguments();
        if (extras != null) {
            readBundle(extras);
        }

        initializeView();
        initializeList();

        signInAccount = GoogleSignIn.getLastSignedInAccount(Objects.requireNonNull(getActivity()));


        if (isTablet) {
            ((DisplayActivity) Objects.requireNonNull(getActivity())).setFragmentEditListRefreshListener(() -> {
                if (mArrayList.size() > 0) {
                    launchDeleteAllDialog();
                }
            });
        }
        if (!isTablet) {
            ((ListContentsActivity) getActivity()).setFragmentEditListRefreshListener(() -> {
                if (mArrayList.size() > 0) {
                    launchDeleteAllDialog();
                }
            });
        }

        getActivity().getSupportLoaderManager().initLoader(DATA_LOADER_ID, null, callback);
        return view;

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (isTablet) {
            dataPasser = (OnDataPassListener) context;

        }
    }

    private void passData(String data) {
        if (data != null) {
            dataPasser.onDataPass(data);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (isTablet) {
            dataPasser = (OnDataPassListener) getActivity();

        }

        if (savedInstanceState != null) {

            isAddDialogShow = savedInstanceState.getBoolean(Contract.EXTRA_SHOW_ADD_DIALOG);
            if (isAddDialogShow) {
                mLastTitleAdding = savedInstanceState.getString(Contract.EXTRA_STRING_CONTENT_ADD);
                mLastTitleAdding = savedInstanceState.getString(Contract.EXTRA_STRING_TITLE_ADD);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    launchAddDialog(mLastTitleAdding, mLastContentAdding);
                }
                if (!mAddDialog.isShowing()) {
                    mAddDialog.show();
                }

            }
            isUpdateDialogShow = savedInstanceState.getBoolean(Contract.EXTRA_SHOW_UPDATE_DIALOG);
            if (isUpdateDialogShow) {
                mLastContentUpdating = savedInstanceState.getString(Contract.EXTRA_STRING_CONTENT_UPDATE);
                mLastTitleUpdating = savedInstanceState.getString(Contract.EXTRA_STRING_TITLE_UPDATE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    launchUpdateDialog(mLastTitleUpdating, mLastContentUpdating, 1);
                }
                if (!mUpdateDialog.isShowing()) {
                    mUpdateDialog.show();
                }
            }
        }

        mFab.setOnClickListener(this);
        mFabAdd.setOnClickListener(this);
        mFabStorage.setOnClickListener(this);
        mFabCloud.setOnClickListener(this);
        mDeleteImage.setOnClickListener(this);
        mReturnUp.setOnClickListener(this);
        updateWidget();
        if (isTablet) {
            dataPasser = (OnDataPassListener) getActivity();

        }

        OnTouchRecyclerView();

    }


    private void initializeAdd() {


        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(
                        getString(R.string.banner_pup))
                .build();
        initializeInterstitialAds();
        mAdView.loadAd(adRequest);


    }

    private void initializeInterstitialAds() {
        if (getActivity() != null) {
            mInterstitialAd = new InterstitialAd(getActivity());
            mInterstitialAd.setAdUnitId(getResources().getString(R.string.interstitial_pub));
            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    requestNewInterstitial();
                    if (mScrollString != null) {
                        if (isTablet) {

                            passData(mScrollString);

                        } else {
                            Intent intent = new Intent(getActivity(), DisplayActivity.class);
                            intent.putExtra(Contract.EXTRA_TEXT, mScrollString);
                            startActivity(intent);

                        }

                    }
                }
            });
            requestNewInterstitial();
        }

    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder().build();
        mInterstitialAd.loadAd(adRequest);
    }

    private void addScrollString(String mScrollString) {


        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            if (mScrollString != null)
                if (isTablet) {

                    passData(mScrollString);
                } else {
                    Intent intent = new Intent(getActivity(), DisplayActivity.class);
                    intent.putExtra(Contract.EXTRA_TEXT, mScrollString);
                    startActivity(intent);

                }


        }

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
                    if (!mAddDialog.isShowing()) {
                        mAddDialog.show();
                    }
                }
                Log.d(TAG, "FabAction is " + "Fab Add");

                break;
            case R.id.fab_storage:

                readFromFile();
                Log.d(TAG, "FabAction is " + "Fab Storage");

                break;
            case R.id.fab_cloud:
                if (signInAccount != null) {
                    initializeDriveClient(signInAccount);
                } else {
                    signIn();
                }
                Log.d(TAG, "FabAction is " + "Fab Cloud");
                break;
            case R.id.return_up:
                mEditContainer.setVisibility(View.GONE);
                unCheckAll();
            default:
                break;
        }

    }

    private void initializeView() {


        mFabAnimations = new FabAnimations(getActivity(), mFab, mFabAdd, mFabStorage, mFabCloud);
        teleprompterAdapter = new TeleprompterAdapter(getLayoutInflater(), new OnItemViewClickListener() {
            @Override
            public void onEditImgClickListener(int position, View v) {
                launchPopUpMenu(v, position);
            }

            @Override
            public void onTextClickListener(int position, View v) {
                mScrollString = mArrayList.get(position).getTextContent();
                addScrollString(mScrollString);

            }

            @Override
            public void onImageClickListener(int position, View v) {
                mScrollString = mArrayList.get(position).getTextContent();

                addScrollString(mScrollString);

            }

            @Override
            public void onViewGroupClickListener(int position, View view) {
                mScrollString = mArrayList.get(position).getTextContent();

                addScrollString(mScrollString);


            }

        });

        mArrayList = new ArrayList<>();
        mFabAnimations.addFabAnimationRes();

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void initializeList() {
        Objects.requireNonNull(getActivity()).getSupportLoaderManager().restartLoader(DATA_LOADER_ID, null, this);

        Display display = Objects.requireNonNull(getActivity()).getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float density = getResources().getDisplayMetrics().density;
        float dpHeight = outMetrics.heightPixels / density;
        float dpWidth = outMetrics.widthPixels / density;
        Log.d(TAG, "screen width = " + String.valueOf(dpWidth));
        Log.d(TAG, "screen height = " + String.valueOf(dpHeight));

        mRecyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager;
        gridLayoutManager = new GridLayoutManager(getActivity(), 1);
        mRecyclerView.setLayoutManager(gridLayoutManager);

    }

    private void OnTouchRecyclerView() {
        mRecyclerView.addOnItemTouchListener(new RecyclerViewItemClickListener(getActivity(), (view, position) -> {

        }));

    }

    private void launchPopUpMenu(View view, final int position) {

        Context wrapper = new ContextThemeWrapper(getActivity(), R.style.MyPopupMenu);
        PopupMenu popup = new PopupMenu(wrapper, view);
        popup.getMenuInflater().inflate(R.menu.clipboard_popup, popup.getMenu());
        popup.show();
        popup.setOnMenuItemClickListener(item -> {

            switch (item.getItemId()) {
                case R.id.edit:


                    launchUpdateDialog(mArrayList.get(position).getTextTitle(), mArrayList.get(position).getTextContent(), position);
                    break;
                case R.id.delete:

                    launchDeleteDialog(position);
                    break;


                default:
                    break;
            }

            return true;
        });


    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void launchAddDialog(String title, String content) {

        mAddDialog = new Dialog(Objects.requireNonNull(getActivity()));
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(mAddDialog.getWindow()).getAttributes());
        lp.width = 48;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = BOTTOM | CENTER;
        lp.windowAnimations = R.style.Theme_Dialog;
        mAddDialog.getWindow().setAttributes(lp);

        mAddDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mAddDialog.setContentView(R.layout.custom_dialog_add_content);

        TextView mAdd = mAddDialog.findViewById(R.id.txt_add);
        TextView mCancel = mAddDialog.findViewById(R.id.txt_cancel);
        mEditTextAddContent = mAddDialog.findViewById(R.id.linededit_text_content);
        mEditTextAddTitle = mAddDialog.findViewById(R.id.edit_title);


        if (title != null || content != null) {
            mEditTextAddContent.setText(title);

            mEditTextAddContent.setText(content);

        }

        isAddDialogShow = true;
        mCancel.setOnClickListener(v -> {

            mAddDialog.dismiss();
            isAddDialogShow = false;

        });


        mAdd.setOnClickListener(v -> {
            isAddDialogShow = false;

            mLastTitleAdding = Objects.requireNonNull(mEditTextAddTitle.getText()).toString();
            mLastContentAdding = Objects.requireNonNull(mEditTextAddContent.getText()).toString();
            if (mLastTitleAdding != null) {

                if (mEditTextAddTitle.length() < 5) {
                    mEditTextAddTitle.setError(getResources().getString(R.string.short_title));
                } else if (mEditTextAddContent.length() < 99) {
                    mEditTextAddContent.setError(getResources().getString(R.string.short_script));
                } else {


                    Cursor cursorTitle = getActivity().getContentResolver().
                            query(Contract.Entry.PATH_TELEPROMPTER_URI, null,
                                    Contract.Entry.COL_TITLE + " = " +
                                            DatabaseUtils.sqlEscapeString(mLastTitleAdding), null, null);

                    Cursor cursorContent = getActivity().getContentResolver().
                            query(Contract.Entry.PATH_TELEPROMPTER_URI, null,
                                    Contract.Entry.COL_CONTENTS + " = " +
                                            DatabaseUtils.sqlEscapeString(mLastContentAdding), null, null);


                    if (Objects.requireNonNull(cursorTitle).getCount() != 0) {
                        Toast.makeText(getActivity(), getResources().getString(R.string.already_title), Toast.LENGTH_LONG).show();

                    } else if (Objects.requireNonNull(cursorContent).getCount() != 0) {
                        Toast.makeText(getActivity(), getResources().getString(R.string.already_text), Toast.LENGTH_LONG).show();

                    } else {
                        Cursor countCursor = getActivity().getContentResolver().query(Contract.Entry.PATH_TELEPROMPTER_URI,
                                new String[]{"count(*) AS count"},
                                null,
                                null,
                                null);

                        Objects.requireNonNull(countCursor).moveToFirst();
                        int count = countCursor.getInt(0);

                        ContentValues values = new ContentValues();
                        values.put(Contract.Entry.COL_TITLE, Objects.requireNonNull(mEditTextAddTitle.getText()).toString());
                        values.put(Contract.Entry.COL_CONTENTS, Objects.requireNonNull(mEditTextAddContent.getText()).toString());
                        values.put(Contract.Entry.COL_UNIQUE_ID, count + 1);

                        final Uri uriInsert = getActivity().getContentResolver().insert(Contract.Entry.PATH_TELEPROMPTER_URI, values);
                        if (uriInsert != null) {
                            Log.d("contentResolver insert", "first added success");

                            values.clear();
                        }
                        getActivity().getSupportLoaderManager().restartLoader(DATA_LOADER_ID, null, this);
                        values.clear();
                        mAddDialog.dismiss();
                    }
                }
            }


        });

        if (!mAddDialog.isShowing()) {
            mAddDialog.show();
        }
        isOpen = false;

    }

    private void launchUpdateDialog(String title, String content, final int position) {

        mUpdateDialog = new Dialog(Objects.requireNonNull(getActivity()));
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(mUpdateDialog.getWindow()).getAttributes());
        lp.width = 48;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = BOTTOM | CENTER;
        lp.windowAnimations = R.style.Theme_Dialog;
        mUpdateDialog.getWindow().setAttributes(lp);

        mUpdateDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mUpdateDialog.setContentView(R.layout.custom_dialog_add_content);

        TextView mUpdate = mUpdateDialog.findViewById(R.id.txt_add);
        mUpdate.setText(getActivity().getResources().getString(R.string.save_changes));
        TextView mCancel = mUpdateDialog.findViewById(R.id.txt_cancel);
        mEditTextUpdateContent = mUpdateDialog.findViewById(R.id.linededit_text_content);
        mEditTextUpdateTitle = mUpdateDialog.findViewById(R.id.edit_title);

        if (title != null || content != null) {
            mEditTextUpdateTitle.setText(title);
            mEditTextUpdateContent.setText(content);
        }
        mLastTitleUpdating = Objects.requireNonNull(mEditTextUpdateTitle.getText()).toString();
        mLastContentUpdating = Objects.requireNonNull(mEditTextUpdateContent.getText()).toString();
        isUpdateDialogShow = true;
        mCancel.setOnClickListener(v -> {

            mUpdateDialog.dismiss();
            isUpdateDialogShow = false;

        });


        mUpdate.setOnClickListener(v -> {
            isUpdateDialogShow = false;


            ContentValues values = new ContentValues();
            int id = mArrayList.get(position).getId();
            values.put(Contract.Entry.COL_TITLE, mEditTextUpdateTitle.getText().toString());
            values.put(Contract.Entry.COL_CONTENTS, Objects.requireNonNull(mEditTextUpdateContent.getText()).toString());
            getActivity().getContentResolver().update(Contract.Entry.PATH_TELEPROMPTER_URI, values, Contract._ID + "=?", new String[]{String.valueOf(id)}); //id is the id of the row you wan to update

            getActivity().getSupportLoaderManager().restartLoader(DATA_LOADER_ID, null, this);
            values.clear();

            mUpdateDialog.dismiss();
        });

        if (!mUpdateDialog.isShowing()) {
            mUpdateDialog.show();
        }
        isOpen = false;

    }

    private void launchDeleteAllDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                Objects.requireNonNull(getActivity()));
        alertDialogBuilder.setTitle(getResources().getString(R.string.Delete));

        alertDialogBuilder
                .setMessage(getResources().getString(R.string.click_delete))
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.yes), (dialog, id) -> {
                    deleteAll();
                    getActivity().getSupportLoaderManager().restartLoader(DATA_LOADER_ID, null, this);

                })
                .setNegativeButton(getResources().getString(R.string.no), (dialog, id) -> dialog.cancel());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }


    private void launchDeleteDialog(final int position) {


        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                Objects.requireNonNull(getActivity()));
        alertDialogBuilder.setTitle(getResources().getString(R.string.Delete));

        // set dialog message
        alertDialogBuilder
                .setMessage(getResources().getString(R.string.click_delete))
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.yes), (dialog, id) -> deleteSelectedItem(position))
                .setNegativeButton(getResources().getString(R.string.no), (dialog, id) -> dialog.cancel());

        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
    }


    private void deleteSelectedItem(int position) {

        if (GetData.getTeleprompters(getActivity()).size() > 0) {

            String stringId = String.valueOf(mArrayList.get(position).getId());

            Uri uri = Contract.Entry.PATH_TELEPROMPTER_URI;

            String[] selectionArgs = {stringId};
            Objects.requireNonNull(getActivity()).getContentResolver().delete(uri, Contract._ID + "=?", selectionArgs);
            Log.d("contentResolver delete", "delete success");

            getActivity().getSupportLoaderManager().restartLoader(DATA_LOADER_ID, null, this);
            if (mArrayList.size() > 4) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    mRecyclerView.smoothScrollToPosition(Objects.requireNonNull(mRecyclerView.getAdapter()).getItemCount() - 1);
                }

            }

        }
    }

    private void deleteAll() {


        Uri uri = Contract.Entry.PATH_TELEPROMPTER_URI;

        Objects.requireNonNull(getActivity()).getContentResolver().delete(uri, null, null);
        Log.d("contentResolver delete", "delete success");

        getActivity().getSupportLoaderManager().restartLoader(DATA_LOADER_ID, null, this);
        unCheckAll();


    }

    private void refreshList() {
        mArrayList.clear();
        teleprompterAdapter.removeContent();
        mArrayList = GetData.getTeleprompters(getActivity());
        teleprompterAdapter.addNewContent(mArrayList);
        mRecyclerView.setAdapter(teleprompterAdapter);
        updateWidget();


    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mEditTextAddTitle != null && mEditTextAddContent != null) {
            outState.putString(Contract.EXTRA_STRING_TITLE_ADD, Objects.requireNonNull(mEditTextAddTitle.getText()).toString());
            outState.putString(Contract.EXTRA_STRING_CONTENT_ADD, Objects.requireNonNull(mEditTextAddContent.getText()).toString());

        }
        if (mEditTextUpdateTitle != null && mEditTextUpdateContent != null) {
            outState.putString(Contract.EXTRA_STRING_TITLE_UPDATE, Objects.requireNonNull(mEditTextUpdateTitle.getText()).toString());
            outState.putString(Contract.EXTRA_STRING_CONTENT_UPDATE, Objects.requireNonNull(mEditTextUpdateContent.getText()).toString());

        }
        outState.putBoolean(Contract.EXTRA_SHOW_ADD_DIALOG, isAddDialogShow);
        outState.putBoolean(Contract.EXTRA_SHOW_UPDATE_DIALOG, isUpdateDialogShow);

    }


    @Override
    public void onPause() {
        super.onPause();
        unCheckAll();
        mEditContainer.setVisibility(View.GONE);
        if (mAddDialog != null) {
            if (!mAddDialog.isShowing()) {
                isAddDialogShow = false;
            }
        }
        if (mUpdateDialog != null) {
            if (!mUpdateDialog.isShowing()) {
                isUpdateDialogShow = false;
            }
        }

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


    private void unCheckAll() {
        ContentValues values = new ContentValues();
        values.put(Contract.Entry.COL_SELECT, 0);
        Objects.requireNonNull(getActivity()).getContentResolver().update(Contract.Entry.PATH_TELEPROMPTER_URI, values, null, null);

        getActivity().getSupportLoaderManager().restartLoader(DATA_LOADER_ID, null, this);
        values.clear();

    }

/////////////onActivityResult/////////

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_SIGN_IN:
                if (resultCode != RESULT_OK) {
                    // Sign-in may fail or be cancelled by the user. For this sample, sign-in is
                    // required and is fatal. For apps where sign-in is optional, handle
                    // appropriately
                    Log.e(TAG, "Sign-in failed.");
                    return;
                }

                Task<GoogleSignInAccount> getAccountTask =
                        GoogleSignIn.getSignedInAccountFromIntent(data);
                if (getAccountTask.isSuccessful()) {
                    initializeDriveClient(getAccountTask.getResult());
                } else {
                    Log.e(TAG, "Sign-in failed.");
                }
                break;
            case REQUEST_CODE_OPEN_ITEM:
                if (resultCode == RESULT_OK) {
                    DriveId driveId = data.getParcelableExtra(
                            OpenFileActivityOptions.EXTRA_RESPONSE_DRIVE_ID);
                    mOpenItemTaskSource.setResult(driveId);
                } else {
                    mOpenItemTaskSource.setException(new RuntimeException("Unable to open file"));
                }
                break;
            case REQUEST_READ_STORAGE_CODE:
                if (resultCode == RESULT_OK) {
                    try {
                        InputStream inputStream = Objects.requireNonNull(getActivity()).getContentResolver().openInputStream(Objects.requireNonNull(data.getData()));
                        java.util.Scanner s = new java.util.Scanner(Objects.requireNonNull(inputStream)).useDelimiter("\\A");
                        mLastContentAdding = s.hasNext() ? s.next() : "";
                        launchAddDialog(null, mLastContentAdding);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //////////////////Working With Device Storage///////////////
    private void readFromFile() {

        Intent intent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        }
        Objects.requireNonNull(intent).addCategory(Intent.CATEGORY_OPENABLE);
        intent = new Intent("android.intent.action.OPEN_DOCUMENT");
        intent.setType("text/*");

        startActivityForResult(intent, REQUEST_READ_STORAGE_CODE);

    }

    //////////////////Working With Google Drive///////////////////////////////
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void retrieveContents(DriveFile file) {
        // [START drive_android_open_file]
        Task<DriveContents> openFileTask =
                getDriveResourceClient().openFile(file, DriveFile.MODE_READ_ONLY);
        // [END drive_android_open_file]
        // [START drive_android_read_contents]
        openFileTask
                .continueWithTask(task -> {
                    DriveContents contents = task.getResult();
                    // Process contents...
                    // [START_EXCLUDE]
                    // [START drive_android_read_as_string]
                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(contents.getInputStream()))) {
                        StringBuilder builder = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            builder.append(line).append("\n");
                        }
                        showMessage(Objects.requireNonNull(getActivity()).getResources().getString(R.string.content_loaded));
                        //  mFileContents.setText(builder.toString());
                        launchAddDialog(null, builder.toString());
                    }
                    // [END drive_android_read_as_string]
                    // [END_EXCLUDE]
                    // [START drive_android_discard_contents]
                    // [END drive_android_discard_contents]
                    return getDriveResourceClient().discardContents(contents);
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    // [START_EXCLUDE]
                    Log.e(TAG, "Unable to read contents", e);
                    showMessage(Objects.requireNonNull(getActivity()).getResources().getString(R.string.read_failed));
                    //  finish();
                    // [END_EXCLUDE]
                });
        // [END drive_android_read_contents]
    }

    /**
     * Handles resolution callbacks.
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void onDriveClientReady() {

        pickTextFile()
                .addOnSuccessListener(Objects.requireNonNull(getActivity()),
                        driveId -> retrieveContents(driveId.asDriveFile()))
                .addOnFailureListener(getActivity(), e -> {
                    Log.e(TAG, "No file selected", e);
                    showMessage(getString(R.string.file_not_selected));
                    //finish();
                });

    }


    /**
     * Starts the sign-in process and initializes the Drive client.
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void signIn() {
        Set<Scope> requiredScopes = new HashSet<>(2);
        requiredScopes.add(Drive.SCOPE_FILE);
        requiredScopes.add(Drive.SCOPE_APPFOLDER);
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(Objects.requireNonNull(getActivity()));
        if (signInAccount != null && signInAccount.getGrantedScopes().containsAll(requiredScopes)) {
            initializeDriveClient(signInAccount);
        } else {
            GoogleSignInOptions signInOptions =
                    new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestScopes(Drive.SCOPE_FILE)
                            .requestScopes(Drive.SCOPE_APPFOLDER)
                            .build();
            GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(getActivity(), signInOptions);
            startActivityForResult(googleSignInClient.getSignInIntent(), REQUEST_CODE_SIGN_IN);
        }
    }

    /**
     * Continues the sign-in process, initializing the Drive clients with the current
     * user's account.
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void initializeDriveClient(GoogleSignInAccount signInAccount) {
        mDriveClient = Drive.getDriveClient(Objects.requireNonNull(getActivity()).getApplicationContext(), signInAccount);
        mDriveResourceClient = Drive.getDriveResourceClient(getActivity().getApplicationContext(), signInAccount);
        onDriveClientReady();
    }

    /**
     * Prompts the user to select a text file using OpenFileActivity.
     *
     * @return Task that resolves with the selected item's ID.
     */
    private Task<DriveId> pickTextFile() {
        OpenFileActivityOptions openOptions =
                new OpenFileActivityOptions.Builder()
                        .setSelectionFilter(Filters.eq(SearchableField.MIME_TYPE, "text/plain"))
                        .setActivityTitle(getString(R.string.select_file))
                        .build();
        return pickItem(openOptions);
    }


    /**
     * Prompts the user to select a folder using OpenFileActivity.
     *
     * @param openOptions Filter that should be applied to the selection
     * @return Task that resolves with the selected item's ID.
     */
    private Task<DriveId> pickItem(OpenFileActivityOptions openOptions) {
        mOpenItemTaskSource = new TaskCompletionSource<>();
        getDriveClient()
                .newOpenFileActivityIntentSender(openOptions)
                .continueWith((Continuation<IntentSender, Void>) task -> {
                    startIntentSenderForResult(
                            task.getResult(), REQUEST_CODE_OPEN_ITEM, null, 0, 0, 0, null);
                    return null;
                });
        return mOpenItemTaskSource.getTask();
    }

    /**
     * Shows a toast message.
     */
    private void showMessage(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }

    /**
     * Called after the user has signed in and the Drive client has been initialized.
     */

    private DriveClient getDriveClient() {
        return mDriveClient;
    }

    private DriveResourceClient getDriveResourceClient() {
        return mDriveResourceClient;
    }

    @NonNull
    @Override
    public Loader<ArrayList<DataObj>> onCreateLoader(int loaderId, @Nullable Bundle bundle) {

        switch (loaderId) {

            case DATA_LOADER_ID:
                /*return data from FetchData class*/
                return new FetchData(Objects.requireNonNull(getActivity()), result -> {

                });


            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }

    }

    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList<DataObj>> loader, ArrayList<DataObj> result) {
        if (result != null) {

            mArrayList = result;
            refreshList();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<DataObj>> loader) {

    }

    private void updateWidget() {

        Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
            // this will send the broadcast to update the appwidget
            WidgetProvider.sendRefreshBroadcast(getActivity());
        });


    }

}


