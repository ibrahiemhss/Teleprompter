package com.and.ibrahim.teleprompter.modules.display;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.LoaderManager;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;


import com.and.ibrahim.teleprompter.ControlVisbilityPreference;
import com.and.ibrahim.teleprompter.R;
import com.and.ibrahim.teleprompter.data.Contract;
import com.and.ibrahim.teleprompter.modules.adapter.MediaAdapter;
import com.and.ibrahim.teleprompter.modules.adapter.MediaLoader;
import com.and.ibrahim.teleprompter.mvp.model.media.FileMedia;
import com.and.ibrahim.teleprompter.util.MediaUtil;
import com.and.ibrahim.teleprompter.util.SDCardUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GalleryActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<FileMedia[]>{


  public static final String TAG = "GalleryActivity";
    int scrollPosition = 0;
    SharedPreferences sharedPreferences;
    View warningMsgRoot;
    Dialog warningMsg;
    Button okButton;
    Dialog taskAlert;
    View taskInProgressRoot;
    LayoutInflater layoutInflater;
    AppWidgetManager appWidgetManager;
    IntentFilter mediaFilters;
    SDCardEventReceiver sdCardEventReceiver;
    boolean sdCardUnavailWarned = false;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.mediaCount)
    TextView mediaCount;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.mediaGrid)
    GridView mediaGrid;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.mediaSourceImage)
    ImageView mediaSourceImage;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.gridHeader)
    LinearLayout gridHeader;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.openMediaProgress)
    ProgressBar openMediaProgress;
    ControlVisbilityPreference controlVisbilityPreference;
    FileMedia[] medias;
    boolean VERBOSE = false;
    String phoneLoc;
    String sdcardLoc;
    String allLoc;
    boolean fromMedia = false;
    String selectedFolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(VERBOSE) Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_media_grid);
        ButterKnife.bind(this);
        sdCardEventReceiver = new SDCardEventReceiver();
        mediaFilters = new IntentFilter();
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        appWidgetManager = (AppWidgetManager)getSystemService(Context.APPWIDGET_SERVICE);
        warningMsgRoot = layoutInflater.inflate(R.layout.warning_message, null);
        taskInProgressRoot = layoutInflater.inflate(R.layout.task_in_progress, null);
        warningMsg = new Dialog(this);
        taskAlert = new Dialog(this);
        sharedPreferences = getSharedPreferences(Contract.FC_SETTINGS, Context.MODE_PRIVATE);
        controlVisbilityPreference = (ControlVisbilityPreference)getApplicationContext();
        phoneLoc = getResources().getString(R.string.phoneLocation);
        sdcardLoc = getResources().getString(R.string.sdcardLocation);
        allLoc = getResources().getString(R.string.allLocation);
/*        videoCapture.setOnClickListener((view) -> {
            Intent videoCamAct = new Intent(this, DisplayActivity.class);
            videoCamAct.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(videoCamAct);
            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
        });*/
        //fromMedia is set to true only if Gallery is opened from MediaActivity. This should be reset to false in onResume()
        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            fromMedia = bundle.getBoolean("fromMedia");
            selectedFolder = bundle.getString("selectedFolder");
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(VERBOSE) Log.d(TAG, "onDestroy");
        controlVisbilityPreference.setMediaSelectedPosition(0);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d(TAG, "onBackPressed");
        controlVisbilityPreference.setFromGallery(true);
        SharedPreferences.Editor mediaLocEdit = sharedPreferences.edit();
        //Since user did not select any media, go back to previous option for view
        mediaLocEdit.putString(Contract.MEDIA_LOCATION_VIEW_SELECT, sharedPreferences.getString(Contract.MEDIA_LOCATION_VIEW_SELECT_PREV, phoneLoc));
        mediaLocEdit.apply();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void updateMediaGridFromSource(){
        ImageView noImage = (ImageView) findViewById(R.id.noImage);
        TextView noImageText = (TextView) findViewById(R.id.noImageText);
        if(medias != null && medias.length > 0) {
            noImage.setVisibility(View.GONE);
            noImageText.setVisibility(View.GONE);
            mediaCount.setText(getResources().getString(R.string.galleryCount, MediaUtil.getVideosCount()));
            if(sharedPreferences.getString(Contract.MEDIA_LOCATION_VIEW_SELECT, phoneLoc).equalsIgnoreCase(phoneLoc)){
                if(VERBOSE) Log.d(TAG, "SET TO PHONE");
                mediaSourceImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_folder));
            }
            else if(sharedPreferences.getString(Contract.MEDIA_LOCATION_VIEW_SELECT, phoneLoc).equalsIgnoreCase(sdcardLoc)){
                if(VERBOSE) Log.d(TAG, "SET TO SDCARD");
                mediaSourceImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_sd_snippet_folder));
            }
            else{
                if(VERBOSE) Log.d(TAG, "SET TO ALL");
                mediaSourceImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_folder_special));
            }
            MediaAdapter mediaAdapter = new MediaAdapter(getApplicationContext(), medias);
            mediaGrid.setAdapter(mediaAdapter);
            mediaGrid.invalidate();
            mediaGrid.setVisibility(View.VISIBLE);
            mediaGrid.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView absListView, int i) {

                }

                @Override
                public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    scrollPosition = firstVisibleItem;
                }
            });
            mediaGrid.setOnItemClickListener((adapterView, view, position, l) -> {
                mediaGrid.setClickable(false);
                openMediaProgress.setVisibility(View.VISIBLE);
                if(VERBOSE) Log.d(TAG, "onItemSelected = "+position);
                Intent mediaAct = new Intent(getApplicationContext(), MediaActivity.class);
                mediaAct.putExtra("fromGallery",true);
                mediaAct.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_NEW_TASK);
                controlVisbilityPreference.setFromGallery(true);
                controlVisbilityPreference.setMediaSelectedPosition(position);
                SharedPreferences.Editor mediaLocEdit = sharedPreferences.edit();
                mediaLocEdit.putString(Contract.MEDIA_LOCATION_VIEW_SELECT, selectedFolder);
                mediaLocEdit.apply();
                if(VERBOSE) Log.d(TAG, "SAVE selectedFolder = "+selectedFolder);
                startActivity(mediaAct);
            });
            if(VERBOSE) Log.d(TAG, "selectedMedia Pos = "+controlVisbilityPreference.getMediaSelectedPosition());
            if(controlVisbilityPreference.getMediaSelectedPosition() != -1 && controlVisbilityPreference.getMediaSelectedPosition() < medias.length){
                mediaGrid.setSelection(controlVisbilityPreference.getMediaSelectedPosition());
            }
        }
        else{
            mediaCount.setText(getResources().getString(R.string.galleryCount,  0));
            noImage.setVisibility(View.VISIBLE);
            noImageText.setVisibility(View.VISIBLE);
            //Refresh Media Grid to remove any earlier stored media previews
            mediaGrid.setVisibility(View.GONE);
            if(sharedPreferences.getString(Contract.MEDIA_LOCATION_VIEW_SELECT, phoneLoc).equalsIgnoreCase(phoneLoc)){
                if(VERBOSE) Log.d(TAG, "SET TO PHONE 222");
                mediaSourceImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_folder));
            }
            else if(sharedPreferences.getString(Contract.MEDIA_LOCATION_VIEW_SELECT, phoneLoc).equalsIgnoreCase(sdcardLoc)){
                if(VERBOSE) Log.d(TAG, "SET TO SDCARD 222");
                mediaSourceImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_sd_snippet_folder));
            }
            else{
                if(VERBOSE) Log.d(TAG, "SET TO ALL 222");
                mediaSourceImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_folder_special));
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(VERBOSE) Log.d(TAG, "onPause");
        unregisterReceiver(sdCardEventReceiver);
        fromMedia = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(VERBOSE) Log.d(TAG, "onResume = "+fromMedia);
        mediaFilters.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        mediaFilters.addAction(Intent.ACTION_MEDIA_MOUNTED);
        mediaFilters.addDataScheme("file");
        registerReceiver(sdCardEventReceiver, mediaFilters);
        if(!sharedPreferences.getString(Contract.MEDIA_LOCATION_VIEW_SELECT, phoneLoc).equalsIgnoreCase(phoneLoc)) {
            //SD Card Location
            String sdCardPath = SDCardUtil.doesSDCardExist(getApplicationContext());
            if ((sdCardPath == null || sdCardPath.equalsIgnoreCase("")) && !sdCardUnavailWarned) {
                sdCardUnavailWarned = true;
                if(fromMedia) {
                    if(sharedPreferences.getString(Contract.MEDIA_LOCATION_VIEW_SELECT, phoneLoc).equalsIgnoreCase(sdcardLoc)) {
                        closePreviousMessages();
                        checkForSDCardAndShowMessage();
                    }
                    else if(sharedPreferences.getString(Contract.MEDIA_LOCATION_VIEW_SELECT, phoneLoc).equalsIgnoreCase(allLoc)){
                        closePreviousMessages();
                        checkForSDCardAndShowGalleryMessage();
                    }
                    fromMedia = false;
                }
                else{
                    //This Gallery window was minimized and reopened. In that case SD Card contents were shown earlier.
                    if(VERBOSE) Log.d(TAG, "medias content = "+medias);
                    if(medias != null && medias.length > 0){
                        closePreviousMessages();
                        checkForSDCardAndShowGalleryMessage();
                    }
                    else{
                        //No Media content was shown earlier.
                        closePreviousMessages();
                        checkForSDCardAndShowMessage();
                    }
                }
            }
            else if(sdCardPath != null){
                //SD Card Exists. Check if com.flipcam folder exists and is not empty
                if (!SDCardUtil.doesSDCardTeleprompter‏FolderContainMedia(sdCardPath, getApplicationContext())) {
                    showMessage(getResources().getString(R.string.sdCardFCFolderEmptyTitle), getResources().getString(R.string.sdCardFCFolderEmptyMessage), true);
                }
            }
        }
        getLoaderManager().initLoader(1, null, this).forceLoad();
    }

    private void loadMediaContent(){
        getLoaderManager().initLoader(1, null, this).forceLoad();
    }

    private void closePreviousMessages(){
        if(warningMsg != null && warningMsg.isShowing()){
            //Close any previous messages if not closed by clicking OK by user.
            warningMsg.dismiss();
        }
    }

    @Override
    public Loader<FileMedia[]> onCreateLoader(int i, Bundle bundle) {
        if(VERBOSE) Log.d(TAG, "onCreateLoader");
        TextView savetocloudtitle = (TextView)taskInProgressRoot.findViewById(R.id.savetocloudtitle);
        TextView signInText = (TextView)taskInProgressRoot.findViewById(R.id.signInText);
        ImageView signInImage = (ImageView)taskInProgressRoot.findViewById(R.id.signInImage);
        signInImage.setVisibility(View.INVISIBLE);
        signInText.setText(getResources().getString(R.string.loadMediaMsg));
        savetocloudtitle.setText(getResources().getString(R.string.loadTitle));
        taskInProgressRoot.setBackgroundColor(getResources().getColor(R.color.colorTransparent));
        taskAlert.setContentView(taskInProgressRoot);
        taskAlert.setCancelable(false);
        taskAlert.show();
        return new MediaLoader(getApplicationContext(), true);
    }

    @Override
    public void onLoadFinished(Loader<FileMedia[]> loader, FileMedia[] fileMedias) {
        if(VERBOSE) Log.d(TAG, "onLoadFinished");
        medias = fileMedias;
        updateMediaGridFromSource();
        taskAlert.dismiss();
    }

    @Override
    public void onLoaderReset(Loader<FileMedia[]> loader) {

    }

    class SDCardEventReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context ctx, Intent intent) {
            if(VERBOSE) Log.d(TAG, "onReceive = " + intent.getAction());
            if(VERBOSE) Log.d(TAG, "sdCardUnavailWarned = "+sdCardUnavailWarned);
            //Reset sdCardUnavailWarned to false since these messages need to be shown always
            sdCardUnavailWarned = false;
            String receivedAction = intent.getAction();
            if (receivedAction.equalsIgnoreCase(Intent.ACTION_MEDIA_UNMOUNTED) ||
                    receivedAction.equalsIgnoreCase(Contract.MEDIA_UNMOUNTED)) {
                //Check if SD Card was selected
                if (sharedPreferences.getString(Contract.MEDIA_LOCATION_VIEW_SELECT, phoneLoc).equalsIgnoreCase(allLoc)
                        && !sdCardUnavailWarned) {
                    if(VERBOSE) Log.d(TAG, "SD Card Removed For ALL Select");
                    closePreviousMessages();
                    sdCardUnavailWarned = true;
                    checkForSDCardAndShowGalleryMessage();
                    loadMediaContent();
                }
                else if(sharedPreferences.getString(Contract.MEDIA_LOCATION_VIEW_SELECT, phoneLoc).equalsIgnoreCase(sdcardLoc)
                    && !sdCardUnavailWarned){
                    if(VERBOSE) Log.d(TAG, "SD Card Removed For SD Card Select");
                    closePreviousMessages();
                    sdCardUnavailWarned = true;
                    checkForSDCardAndShowMessage();
                    loadMediaContent();
                }
            }
            else if(receivedAction.equalsIgnoreCase(Intent.ACTION_MEDIA_MOUNTED) ||
                        receivedAction.equalsIgnoreCase(Contract.MEDIA_MOUNTED)){
                //Refresh the page to show the SD Card contents as well if chosen view is All or SD Card
                if ((sharedPreferences.getString(Contract.MEDIA_LOCATION_VIEW_SELECT, phoneLoc).equalsIgnoreCase(sdcardLoc)
                        || sharedPreferences.getString(Contract.MEDIA_LOCATION_VIEW_SELECT, phoneLoc).equalsIgnoreCase(allLoc))
                        && !sdCardUnavailWarned) {
                    if(VERBOSE) Log.d(TAG, "SD Card ADDED");
                    closePreviousMessages();
                    sdCardUnavailWarned = true;
                    //Check if FlipCam media exists
                    String sdCardPath = SDCardUtil.doesSDCardExist(getApplicationContext());
                    if(sdCardPath == null || sdCardPath.equalsIgnoreCase("")){
                        showMessage(getResources().getString(R.string.sdCardNotDetectTitle),
                                getResources().getString(R.string.sdCardNotDetectMessage), true);
                    }
                    else{
                        if(!SDCardUtil.isPathWritable(sdCardPath)) {
                            showMessage(getResources().getString(R.string.sdCardWriteError),
                                    getResources().getString(R.string.sdCardWriteErrorMessage), true);
                        }
                        else{
                            showMessage(getResources().getString(R.string.sdCardDetectTitle),
                                    getResources().getString(R.string.sdCardDetectGalleryView), false);
                            loadMediaContent();
                        }
                    }
                }
            }
        }
    }

    private void checkForSDCardAndShowGalleryMessage(){
        if(!sharedPreferences.getBoolean(Contract.SAVE_MEDIA_PHONE_MEM, true)){
            //If SD Card was selected, show an additional message about switching back to phone memory.
            showMessage(getResources().getString(R.string.sdCardRemovedTitle),
                    getResources().getString(R.string.sdCardNotPresentForViewDefLocChanged), true);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(Contract.SAVE_MEDIA_PHONE_MEM, true);
            editor.commit();
        }
        else {
            showMessage(getResources().getString(R.string.sdCardRemovedTitle),
                    getResources().getString(R.string.sdCardNotPresentForView), true);
        }
    }

    private void checkForSDCardAndShowMessage(){
        if(!sharedPreferences.getBoolean(Contract.SAVE_MEDIA_PHONE_MEM, true)){
            //If SD Card was selected, show an additional message about switching back to phone memory.
            showMessage(getResources().getString(R.string.sdCardNotDetectTitle),
                    getResources().getString(R.string.sdCardNotDetectMessageDefLocChanged), true);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(Contract.SAVE_MEDIA_PHONE_MEM, true);
            editor.commit();
        }
        else {
            showMessage(getResources().getString(R.string.sdCardNotDetectTitle),
                    getResources().getString(R.string.sdCardNotDetectMessage), true);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(VERBOSE) Log.d(TAG, "scrollPos = "+scrollPosition);
        mediaGrid.setSelection(scrollPosition);
    }

    private void showMessage(String title, String text, boolean warngSig){
        LinearLayout warningParent = (LinearLayout)warningMsgRoot.findViewById(R.id.warningParent);
        warningParent.setBackgroundColor(getResources().getColor(R.color.colorTransparent));
        TextView warningTitle = (TextView)warningMsgRoot.findViewById(R.id.warningTitle);
        warningTitle.setText(title);
        ImageView warningSign = (ImageView) warningMsgRoot.findViewById(R.id.warningSign);
        if(warngSig) {
            warningSign.setVisibility(View.VISIBLE);
        }
        else{
            warningSign.setVisibility(View.GONE);
        }
        TextView warningText = (TextView)warningMsgRoot.findViewById(R.id.warningText);
        warningText.setText(text);
        warningMsg.setContentView(warningMsgRoot);
        warningMsg.setCancelable(false);
        warningMsg.show();
        okButton = (Button)warningMsgRoot.findViewById(R.id.okButton);
        okButton.setOnClickListener((view) -> {
            warningMsg.dismiss();
        });
    }
}
