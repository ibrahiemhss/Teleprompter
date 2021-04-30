package com.and.ibrahim.teleprompter.mvp.view;

import android.accounts.AccountManager;
import android.app.Dialog;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.and.ibrahim.teleprompter.ControlVisbilityPreference;
import com.and.ibrahim.teleprompter.R;
import com.and.ibrahim.teleprompter.data.Contract;
import com.and.ibrahim.teleprompter.modules.display.MemoryLimitActivity;
import com.and.ibrahim.teleprompter.modules.display.VideoResolutionActivity;
import com.and.ibrahim.teleprompter.util.SDCardUtil;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.android.Auth;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.CreateFolderResult;
import com.dropbox.core.v2.files.DbxUserFilesRequests;
import com.dropbox.core.v2.files.GetMetadataErrorException;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveClient;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.DriveStatusCodes;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.metadata.CustomPropertyKey;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.Iterator;

public class SettingVideoDialogFragment extends DialogFragment {

     public static final String TAG = "SettingsActivity";
    LinearLayout phoneMemParentVert;
    TextView thresholdText;
    TextView phoneMemTextMsg;
    ImageView greenArrow;
    SharedPreferences settingsPref;
    SharedPreferences.Editor settingsEditor;
    RadioButton phoneMemBtn;
    RadioButton sdCardBtn;
    Dialog sdCardDialog;
    LinearLayout sdcardlayout;
    TextView sdCardPathMsg;
    LayoutInflater layoutInflater;
   // CheckBox switchOnDrive;
   // CheckBox switchOnDropbox;
  //  Dialog saveToCloud;
    TextView savetocloudtitle;
    DriveClient mDriveClient;
    DriveResourceClient mDriveResourceClient;
    static final int REQUEST_CODE_SIGN_IN = 0;
    GoogleSignInOptions signInOptions;
    GoogleSignInClient googleSignInClient = null;
    boolean signedInDrive = false;
    Dialog cloudUpload;
    View cloudUploadRoot;
    View signInProgressRoot;
    View saveToCloudRoot;
    View autoUploadEnabledWithFolderRoot;
    View autoUploadEnabledRoot;
    View autoUploadDisabledRoot;
    View uploadFolderCheckRoot;
    View accessGrantedDropboxRoot;
    View shareMediaRoot;
    TextView uploadFolderTitle;
    TextView uploadFolderMsg;
    int cloud = 0; //Default to Google Drive. 1 for Dropbox.
    AccountManager accountManager;
    final int GET_ACCOUNTS_PERM = 100;
    boolean signInProgress = false;
    Dialog permissionAccount;
    Dialog signInProgressDialog;
    ImageView uploadDestIcon;
    Dialog autoUploadEnabledWithFolder;
    Dialog autoUploadEnabled;
    Dialog autoUploadDisabled;
    Dialog uploadFolderCheck;
    Dialog accesGrantedDropbox;
    Dialog shareMedia;
    DbxClientV2 dbxClientV2;
    DbxRequestConfig dbxRequestConfig;
    boolean goToDropbox = false;
    View warningMsgRoot;
    Dialog warningMsg;
    Button okButton;
    SDCardEventReceiver sdCardEventReceiver;
    IntentFilter mediaFilters;
    AppWidgetManager appWidgetManager;
    ControlVisbilityPreference controlVisbilityPreference;
    boolean VERBOSE = false;
   // LinearLayout photoResolutionParent;
    LinearLayout videoResolutionParent;
    EditText feedback_information;

    public EditText getFeedback_information() {
        return feedback_information;
    }

    private EditText mEditText;

    public SettingVideoDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static SettingVideoDialogFragment newInstance(String title) {
        SettingVideoDialogFragment frag = new SettingVideoDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_camera_setting, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String title = getArguments().getString("title", "Enter Name");
        getDialog().setTitle(title);


        mediaFilters = new IntentFilter();
        sdCardEventReceiver = new SDCardEventReceiver();
        phoneMemParentVert = (LinearLayout)view.findViewById(R.id.phoneMemParentVert);
        phoneMemTextMsg = (TextView)view.findViewById(R.id.phoneMemTextMsg);
        thresholdText = (TextView)view.findViewById(R.id.thresholdText);
        greenArrow = (ImageView)view.findViewById(R.id.greenArrow);
        phoneMemBtn = (RadioButton)view.findViewById(R.id.phoneMemButton);
        sdCardBtn = (RadioButton)view.findViewById(R.id.sdCardbutton);
        sdCardPathMsg = (TextView)view.findViewById(R.id.sdcardpathmsg);
       // switchOnDropbox = (CheckBox) view.findViewById(R.id.switchOnDropbox);
       // switchOnDrive = (CheckBox) view.findViewById(R.id.switchOnDrive);
        sdcardlayout = (LinearLayout)view.findViewById(R.id.sdcardlayout);
        //photoResolutionParent = (LinearLayout)view.findViewById(R.id.photoResolutionParent);
        videoResolutionParent = (LinearLayout)view.findViewById(R.id.videoResolutionParent);
        thresholdText.setText(getString(R.string.memoryThresholdLimit, getResources().getInteger(R.integer.minimumMemoryWarning) + "MB"));
        // getSupportActionBar().setTitle(getResources().getString(R.string.settingTitle));
        settingsPref = getActivity().getSharedPreferences(Contract.FC_SETTINGS, Context.MODE_PRIVATE);
        settingsEditor = settingsPref.edit();
        if(VERBOSE)if(VERBOSE) Log.d(TAG,"SD Card Path onCreate = "+settingsPref.getString(Contract.SD_CARD_PATH,""));
        if(settingsPref.contains(Contract.SD_CARD_PATH) && !settingsPref.getString(Contract.SD_CARD_PATH,"").equals("")) {
            String sdcardpath = settingsPref.getString(Contract.SD_CARD_PATH, "");
            showSDCardPath(sdcardpath);
        }
        else{
            hideSDCardPath();
        }
        layoutInflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        saveToCloudRoot = layoutInflater.inflate(R.layout.save_to_cloud,null);
        cloudUploadRoot = layoutInflater.inflate(R.layout.cloud_upload_folder,null);
        signInProgressRoot = layoutInflater.inflate(R.layout.task_in_progress,null);
        autoUploadEnabledWithFolderRoot = layoutInflater.inflate(R.layout.auto_upload_enabled_with_folder, null);
        autoUploadEnabledRoot = layoutInflater.inflate(R.layout.auto_upload_enabled, null);
        autoUploadDisabledRoot = layoutInflater.inflate(R.layout.auto_upload_disabled, null);
        uploadFolderCheckRoot = layoutInflater.inflate(R.layout.upload_folder_check, null);
        accessGrantedDropboxRoot = layoutInflater.inflate(R.layout.access_granted_dropbox, null);
        warningMsgRoot = layoutInflater.inflate(R.layout.warning_message,null);
        shareMediaRoot = layoutInflater.inflate(R.layout.share_media, null);
        warningMsg = new Dialog(getActivity());
        sdCardDialog = new Dialog(getActivity());
        //saveToCloud = new Dialog(getActivity());
        cloudUpload = new Dialog(getActivity());
        permissionAccount = new Dialog(getActivity());
        signInProgressDialog = new Dialog(getActivity());
        autoUploadEnabledWithFolder = new Dialog(getActivity());
        autoUploadEnabled = new Dialog(getActivity());
        autoUploadDisabled = new Dialog(getActivity());
        uploadFolderCheck = new Dialog(getActivity());
        accesGrantedDropbox = new Dialog(getActivity());
        shareMedia = new Dialog(getActivity());
        //feedback_information = (EditText) view.findViewById(R.id.feedback_information);
        accountManager = (AccountManager)getActivity().getSystemService(Context.ACCOUNT_SERVICE);
        appWidgetManager = (AppWidgetManager)getActivity().getSystemService(Context.APPWIDGET_SERVICE);
        controlVisbilityPreference = (ControlVisbilityPreference)getActivity().getApplicationContext();
        //photoResolutionParent.setOnClickListener(photoResolutionParentListener);
        videoResolutionParent.setOnClickListener(videoResolutionParentListener);
    }

    View.OnClickListener photoResolutionParentListener = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            // Intent photoSettings = new Intent(getApplicationContext(), PhotoSettingsActivity.class);
            // startActivity(photoSettings);
        }
    };

    View.OnClickListener videoResolutionParentListener = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            //showSettingVideoDialogFragment();
            Intent videoSettings = new Intent(getActivity().getApplicationContext(), VideoResolutionActivity.class);
            startActivity(videoSettings);
        }
    };

    class SDCardEventReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context ctx, Intent intent) {
            if(VERBOSE) Log.d(TAG, "onReceive = "+intent.getAction());
            if(intent.getAction().equalsIgnoreCase(Intent.ACTION_MEDIA_UNMOUNTED)){
                //Check if SD Card was selected
                if(!settingsPref.getBoolean(Contract.SAVE_MEDIA_PHONE_MEM, true)){
                    showSDCardUnavailMessage();
                }
            }
        }
    }

    public void showSDCardUnavailMessage(){
        if(VERBOSE) Log.d(TAG, "SD Card Removed");
        phoneMemBtn.setChecked(true);
        sdCardBtn.setChecked(false);
        settingsEditor.putBoolean(Contract.SAVE_MEDIA_PHONE_MEM, true);
        settingsEditor.commit();
        hideSDCardPath();
        LinearLayout warningParent = (LinearLayout)warningMsgRoot.findViewById(R.id.warningParent);
        warningParent.setBackgroundColor(getResources().getColor(R.color.colorAccentDark));
        TextView warningTitle = (TextView)warningMsgRoot.findViewById(R.id.warningTitle);
        warningTitle.setText(getString(R.string.sdCardRemovedTitle));
        ImageView warningSign = (ImageView)warningMsgRoot.findViewById(R.id.warningSign);
        warningSign.setVisibility(View.VISIBLE);
        TextView warningText = (TextView)warningMsgRoot.findViewById(R.id.warningText);
        warningText.setText(getString(R.string.sdCardNotPresentForRecord));
        okButton = (Button)warningMsgRoot.findViewById(R.id.okButton);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                warningMsg.dismiss();
            }
        });
        warningMsg.setContentView(warningMsgRoot);
        warningMsg.setCancelable(false);
        warningMsg.show();
    }

    public void showSDCardWriteErrorMessage(){
        if(VERBOSE) Log.d(TAG, "SD Card Removed");
        phoneMemBtn.setChecked(true);
        sdCardBtn.setChecked(false);
        settingsEditor.putBoolean(Contract.SAVE_MEDIA_PHONE_MEM, true);
        settingsEditor.commit();
        hideSDCardPath();
        LinearLayout warningParent = (LinearLayout)warningMsgRoot.findViewById(R.id.warningParent);
        warningParent.setBackgroundColor(getResources().getColor(R.color.colorAccentDark));
        TextView warningTitle = (TextView)warningMsgRoot.findViewById(R.id.warningTitle);
        warningTitle.setText(getString(R.string.sdCardWriteError));
        ImageView warningSign = (ImageView)warningMsgRoot.findViewById(R.id.warningSign);
        warningSign.setVisibility(View.VISIBLE);
        TextView warningText = (TextView)warningMsgRoot.findViewById(R.id.warningText);
        warningText.setText(getString(R.string.sdCardWriteErrorMessage));
        okButton = (Button)warningMsgRoot.findViewById(R.id.okButton);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                warningMsg.dismiss();
            }
        });
        warningMsg.setContentView(warningMsgRoot);
        warningMsg.setCancelable(false);
        warningMsg.show();
    }

  /*  public void sendFeedback(View view){
        if(!getFeedback_information().getText().toString().trim().equals("")){

            new FeedbackMailTask(getActivity().getApplicationContext(), this).execute();
        }
        else{
            Toast.makeText(getActivity().getApplicationContext(),getResources().getString(R.string.noFeedbackMsg), Toast.LENGTH_SHORT).show();
        }
    }
*/

    public void updateSettingsValues(){
        //Update Save Media in
        if(settingsPref.contains(Contract.SAVE_MEDIA_PHONE_MEM)){
            if(VERBOSE) Log.d(TAG,"Phone memory exists");
            if(settingsPref.getBoolean(Contract.SAVE_MEDIA_PHONE_MEM, true)){
                if(VERBOSE) Log.d(TAG,"Phone memory is true");
                phoneMemBtn.setChecked(true);
                sdCardBtn.setChecked(false);
                hideSDCardPath();
            }
            else{
                if(VERBOSE) Log.d(TAG,"Phone memory is false");
                if(SDCardUtil.doesSDCardExist(getActivity().getApplicationContext()) != null) {
                    phoneMemBtn.setChecked(false);
                    sdCardBtn.setChecked(true);
                    if(SDCardUtil.isPathWritable(settingsPref.getString(Contract.SD_CARD_PATH, ""))) {
                        showSDCardPath(settingsPref.getString(Contract.SD_CARD_PATH, ""));
                    }
                    else{
                        showSDCardWriteErrorMessage();
                    }
                }
                else{
                    showSDCardUnavailMessage();
                }
            }
        }
        else{
            if(VERBOSE) Log.d(TAG,"Phone memory NOT exists");
            phoneMemBtn.setChecked(true);
            sdCardBtn.setChecked(false);
        }
        //Video Resolution
        String selRes = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).getString(Contract.SELECT_VIDEO_RESOLUTION, null);
        if(VERBOSE) Log.d(TAG, "SELECTED VIDEO RES PREF MGR = "+selRes);
        //Photo Resolution
        selRes = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).getString(Contract.SELECT_PHOTO_RESOLUTION, null);
        if(VERBOSE) Log.d(TAG, "SELECTED PIC RES PREF MGR = "+selRes);
        //Memory consumed
        boolean memCon = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).getBoolean(Contract.SHOW_MEMORY_CONSUMED_MSG, false);
        if(VERBOSE) Log.d(TAG, "MEMORY CONSUMED PREF MGR = "+memCon);
        //Update Phone memory
        if(settingsPref.contains(Contract.PHONE_MEMORY_DISABLE)){
            if(!settingsPref.getBoolean(Contract.PHONE_MEMORY_DISABLE, true)){
                String memoryLimit = settingsPref.getString(Contract.PHONE_MEMORY_LIMIT, getResources().getInteger(R.integer.minimumMemoryWarning) + "");
                String memoryMetric = settingsPref.getString(Contract.PHONE_MEMORY_METRIC, "MB");
                thresholdText.setText(getString(R.string.memoryThresholdLimit, Integer.parseInt(memoryLimit) + " " + memoryMetric));
            }
            else{
                thresholdText.setText(getString(R.string.memoryThresholdLimit, getString(R.string.phoneMemoryLimitDisabled)));
            }
        }
        else{
            thresholdText.setText(getString(R.string.memoryThresholdLimit, getString(R.string.phoneMemoryLimitDisabled)));
        }
        //Update Auto upload
        //Google Drive
       /* if(settingsPref.contains(Contract.SAVE_TO_GOOGLE_DRIVE)){
            if(settingsPref.getBoolean(Contract.SAVE_TO_GOOGLE_DRIVE, false)){
                switchOnDrive.setChecked(true);
            }
            else{
                switchOnDrive.setChecked(false);
            }
        }
        else{
            switchOnDrive.setChecked(false);
        }
        //Dropbox
        if(settingsPref.contains(Contract.SAVE_TO_DROPBOX)){
            if(settingsPref.getBoolean(Contract.SAVE_TO_DROPBOX, false)){
                switchOnDropbox.setChecked(true);
            }
            else{
                switchOnDropbox.setChecked(false);
            }
        }
        else{
            switchOnDropbox.setChecked(false);
        }*/
    }

    public void selectSaveMedia(View view){
        switch (view.getId()){
            case R.id.phoneMemButton:
                if(VERBOSE) Log.d(TAG,"Save in phone memory");
                settingsEditor.putBoolean(Contract.SAVE_MEDIA_PHONE_MEM,true);
                settingsEditor.commit();
                phoneMemBtn.setChecked(true);
                sdCardBtn.setChecked(false);
                hideSDCardPath();
                controlVisbilityPreference.setMediaSelectedPosition(0);
                break;
            case R.id.sdCardbutton:
                if(VERBOSE) Log.d(TAG,"Save in sd card");
                phoneMemBtn.setChecked(false);
                sdCardBtn.setChecked(true);
                String sdCardPath = SDCardUtil.doesSDCardExist(getActivity().getApplicationContext());
                if(sdCardPath == null){
                    if(VERBOSE) Log.d(TAG, "No SD Card");
                    phoneMemBtn.setChecked(true);
                    sdCardBtn.setChecked(false);
                    settingsEditor.putBoolean(Contract.SAVE_MEDIA_PHONE_MEM, true);
                    settingsEditor.commit();
                    hideSDCardPath();
                    LinearLayout warningParent = (LinearLayout)warningMsgRoot.findViewById(R.id.warningParent);
                    warningParent.setBackgroundColor(getResources().getColor(R.color.colorAccentDark));
                    TextView warningTitle = (TextView)warningMsgRoot.findViewById(R.id.warningTitle);
                    warningTitle.setText(getString(R.string.sdCardNotDetectTitle));
                    ImageView warningSign = (ImageView)warningMsgRoot.findViewById(R.id.warningSign);
                    warningSign.setVisibility(View.VISIBLE);
                    TextView warningText = (TextView)warningMsgRoot.findViewById(R.id.warningText);
                    warningText.setText(getString(R.string.sdCardNotDetectMessage));
                }
                else{
                    if(SDCardUtil.isPathWritable(sdCardPath)) {
                        settingsEditor.putBoolean(Contract.SAVE_MEDIA_PHONE_MEM, false);
                        settingsEditor.putString(Contract.SD_CARD_PATH, sdCardPath);
                        settingsEditor.commit();
                        showSDCardPath(sdCardPath);
                        LinearLayout warningParent = (LinearLayout) warningMsgRoot.findViewById(R.id.warningParent);
                        warningParent.setBackgroundColor(getResources().getColor(R.color.colorAccentDark));
                        ImageView warningSign = (ImageView) warningMsgRoot.findViewById(R.id.warningSign);
                        warningSign.setVisibility(View.GONE);
                        TextView warningTitle = (TextView) warningMsgRoot.findViewById(R.id.warningTitle);
                        warningTitle.setText(getString(R.string.sdCardDetectTitle));
                        TextView warningText = (TextView) warningMsgRoot.findViewById(R.id.warningText);
                        warningText.setText(getString(R.string.sdCardDetectMessage, sdCardPath));
                        controlVisbilityPreference.setMediaSelectedPosition(0);
                    }
                    else{
                        phoneMemBtn.setChecked(true);
                        sdCardBtn.setChecked(false);
                        settingsEditor.putBoolean(Contract.SAVE_MEDIA_PHONE_MEM, true);
                        settingsEditor.commit();
                        hideSDCardPath();
                        showSDCardWriteErrorMessage();
                        break;
                    }
                }
                warningMsg.setContentView(warningMsgRoot);
                warningMsg.setCancelable(false);
                warningMsg.show();
                okButton = (Button)warningMsgRoot.findViewById(R.id.okButton);
                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        warningMsg.dismiss();
                    }
                });
                break;
        }
    }

    public void showSDCardPath(String path){
        sdCardPathMsg.setText(path);
        sdcardlayout.setVisibility(View.VISIBLE);
    }

    public void hideSDCardPath(){
        sdcardlayout.setVisibility(View.GONE);
    }

    public void showMemoryConsumed(View view){
        Intent memoryAct = new Intent(getActivity(), MemoryLimitActivity.class);
        startActivity(memoryAct);
        getActivity().overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left);
    }
//--------------------------------------------------------------------------------------------------

//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
   /* @Override
    public void onBackPressed() {
        super.onBackPressed();
        getActivity().overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }*/
//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------

   /* public void saveToCloudDrive(View view) {
        cloud = Contract.GOOGLE_DRIVE_CLOUD;
        if (switchOnDrive.isChecked()) {
            savetocloudtitle = (TextView)saveToCloudRoot.findViewById(R.id.savetocloudtitle);
            savetocloudtitle.setText(getString(R.string.saveToCloudTitle, getString(R.string.googleDrive)));
            ImageView placeHolderIcon = (ImageView)saveToCloudRoot.findViewById(R.id.placeHolderIconSavetoCloud);
            placeHolderIcon.setImageDrawable(getActivity().getDrawable(R.drawable.google_drive));
            LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            imageParams.width = (int)getResources().getDimension(R.dimen.googleDriveIconWidth);
            imageParams.height = (int)getResources().getDimension(R.dimen.googleDriveIconHeight);
            placeHolderIcon.setLayoutParams(imageParams);
            TextView savetoCloudMsg = (TextView)saveToCloudRoot.findViewById(R.id.savetocloudmsg);
            savetoCloudMsg.setText(getString(R.string.continueToCloud));
            saveToCloud.setContentView(saveToCloudRoot);
            saveToCloud.setCancelable(false);
            saveToCloud.show();
        }
        else{
            boolean saveToDrive = settingsPref.getBoolean(Contract.SAVE_TO_GOOGLE_DRIVE, false);
            if(saveToDrive) {
                if(googleSignInClient == null){
                    initializeGoogleSignIn();
                }
                googleSignInClient.signOut();
                signedInDrive = false;
                showUploadDisabled();
                settingsEditor.putBoolean(Contract.SAVE_TO_GOOGLE_DRIVE , false);
                settingsEditor.commit();
            }
        }
    }

    public void saveToDropBox(View view){
        cloud = Contract.DROPBOX_CLOUD;
        if(switchOnDropbox.isChecked()){
            savetocloudtitle = (TextView)saveToCloudRoot.findViewById(R.id.savetocloudtitle);
            savetocloudtitle.setText(getString(R.string.saveToCloudTitle, getString(R.string.dropbox)));
            ImageView placeHolderIcon = (ImageView)saveToCloudRoot.findViewById(R.id.placeHolderIconSavetoCloud);
            placeHolderIcon.setImageDrawable(getActivity().getDrawable(R.drawable.dropbox));
            LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            imageParams.width = (int)getResources().getDimension(R.dimen.dropBoxIconWidth);
            imageParams.height = (int)getResources().getDimension(R.dimen.dropBoxIconHeight);
            placeHolderIcon.setLayoutParams(imageParams);
            TextView savetoCloudMsg = (TextView)saveToCloudRoot.findViewById(R.id.savetocloudmsg);
            savetoCloudMsg.setText(getString(R.string.saveToCloudDropbox));
            saveToCloud.setContentView(saveToCloudRoot);
            saveToCloud.setCancelable(false);
            saveToCloud.show();
        }
        else{
            if(dbxClientV2 == null){
                dbxRequestConfig = new DbxRequestConfig("dropbox/flipCam");
                dbxClientV2 = new DbxClientV2(dbxRequestConfig, settingsPref.getString(Contract.DROPBOX_ACCESS_TOKEN,""));
            }
            revokeAccessFromDropbox();
            disableDropboxInSetting();
            showUploadDisabled();
        }
    }*/
//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------

    public void createDropboxFolder(View view){
        accesGrantedDropbox.dismiss();
        createUploadFolder();
    }

    public boolean doesPackageExist(Context c, String targetPackage) {
        PackageManager pm = c.getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(targetPackage, PackageManager.GET_META_DATA);
            if(info != null) {
                if(VERBOSE) Log.d(TAG, "package name= " + info.packageName);
            }
        } catch (PackageManager.NameNotFoundException e) {
            if(VERBOSE) Log.d(TAG,"Package "+targetPackage+" does NOT exist");
            return false;
        }
        return true;
    }
//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------

  /*  public void signInToCloud(View view){
        switch (view.getId()){
            case R.id.continueSignIn:
                saveToCloud.dismiss();
                if(cloud == Contract.GOOGLE_DRIVE_CLOUD){
                    //Sign in to Google Drive
                    int permissionCheck = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.GET_ACCOUNTS);
                    if(permissionCheck == PackageManager.PERMISSION_GRANTED) {
                        continueToGoogleDrive();
                    }
                    else{
                        permissionAccount.setContentView(R.layout.permission_account);
                        permissionAccount.setCancelable(false);
                        permissionAccount.show();
                    }
                }
                else if(cloud == Contract.DROPBOX_CLOUD){
                    if(!isConnectedToInternet()){
                        Toast.makeText(getActivity().getApplicationContext(),getString(R.string.noConnectionMessage), Toast.LENGTH_SHORT).show();
                        switchOnDropbox.setChecked(false);
                        return;
                    }
                    //Sign in to Dropbox
                    goToDropbox = true;
                    Auth.startOAuth2Authentication(getActivity().getApplicationContext(), getString(R.string.dropBoxAppKey));
                    signInProgress = true;
                    TextView signInText = (TextView)signInProgressRoot.findViewById(R.id.signInText);
                    TextView signInprogressTitle = (TextView)signInProgressRoot.findViewById(R.id.savetocloudtitle);
                    signInprogressTitle.setText(getString(R.string.signInProgressTitle, getString(R.string.dropbox)));
                    if(doesPackageExist(getActivity(), "com.dropbox.android")){
                        signInText.setText("Opening Dropbox App...");
                    }
                    else {
                        signInText.setText(getString(R.string.signInProgressDropbox));
                    }
                    ImageView signInImage = (ImageView) signInProgressRoot.findViewById(R.id.signInImage);
                    signInImage.setImageDrawable(getActivity().getDrawable(R.drawable.dropbox));
                    signInProgressDialog.setContentView(signInProgressRoot);
                    signInProgressDialog.setCancelable(false);
                    signInProgressDialog.show();
                }
                break;
            case R.id.cancelSignIn:
                saveToCloud.dismiss();
                switch(cloud) {
                    case Contract.GOOGLE_DRIVE_CLOUD:
                        settingsEditor.putBoolean(Contract.SAVE_TO_GOOGLE_DRIVE, false);
                        settingsEditor.commit();
                        switchOnDrive.setChecked(false);
                        break;
                    case Contract.DROPBOX_CLOUD:
                        settingsEditor.putBoolean(Contract.SAVE_TO_DROPBOX, false);
                        settingsEditor.commit();
                        switchOnDropbox.setChecked(false);
                        break;
                }
                break;
        }
    }
*/
    //--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
/*
    public void accountsPermission(View view){
        switch (view.getId()){
            case R.id.yesPermission:
                if(VERBOSE) Log.d(TAG,"yesPermission");
                permissionAccount.dismiss();
                ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.GET_ACCOUNTS}, GET_ACCOUNTS_PERM);
                break;
            case R.id.noPermission:
                if(VERBOSE) Log.d(TAG,"noPermission");
                if(cloud == Contract.GOOGLE_DRIVE_CLOUD) {
                    switchOnDrive.setChecked(false);
                }
                permissionAccount.dismiss();
                break;
        }
    }*/
    //--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------


    public void initializeGoogleSignIn(){
        signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestScopes(Drive.SCOPE_FILE)
                        .requestScopes(Drive.SCOPE_APPFOLDER)
                        .build();
        googleSignInClient = GoogleSignIn.getClient(getActivity(), signInOptions);
    }

    @Override
    public void onPause() {
        super.onPause();
        if(VERBOSE) Log.d(TAG,"onPause");
        try {
            getActivity().unregisterReceiver(sdCardEventReceiver);
        }catch (IllegalArgumentException illegal){
            if(VERBOSE) Log.d(TAG, "Receiver was never registered");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        mediaFilters.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        mediaFilters.addAction(Intent.ACTION_MEDIA_MOUNTED);
        mediaFilters.addDataScheme("file");
        getActivity().registerReceiver(sdCardEventReceiver, mediaFilters);
        updateSettingsValues();
        if (signInProgress) {
            signInProgressDialog.dismiss();
            if(VERBOSE) Log.d(TAG, "Reset signinprogess");
            signInProgress = false;
        }
        if(goToDropbox) {
            goToDropbox = false;
            if(VERBOSE) Log.d(TAG, "Access token = " + Auth.getOAuth2Token());
            if(Auth.getOAuth2Token() == null){
                Toast.makeText(getActivity().getApplicationContext(),getString(R.string.signInDropboxFail), Toast.LENGTH_LONG).show();
                //--------------------------------------------------------------------------------------------------
                //--------------------------------------------------------------------------------------------------
               // switchOnDropbox.setChecked(false);
                disableDropboxInSetting();
            }
            else{
                settingsEditor.putString(Contract.DROPBOX_ACCESS_TOKEN, Auth.getOAuth2Token());
                settingsEditor.commit();
                dbxRequestConfig = new DbxRequestConfig("dropbox/flipCam");
                dbxClientV2 = new DbxClientV2(dbxRequestConfig, Auth.getOAuth2Token());
                if(settingsPref.contains(Contract.DROPBOX_FOLDER) && (!settingsPref.getString(Contract.DROPBOX_FOLDER,"").equals("")
                        && !settingsPref.getString(Contract.DROPBOX_FOLDER,"").equalsIgnoreCase(getString(R.string.app_name)))){
                    checkIfFolderCreatedInDropbox();
                }
                else {
                    //Folder name is same as app name
                    updateDropboxInSetting(getString(R.string.app_name), true);
                    TextView dropBoxfolderCreated = (TextView) accessGrantedDropboxRoot.findViewById(R.id.dropBoxFolderCreated);
                    dropBoxfolderCreated.setText(getString(R.string.autouploadFolderUpdated, getString(R.string.app_name),
                            getString(R.string.dropbox)));
                    CheckBox dropBoxFolderCreateEnable = (CheckBox) accessGrantedDropboxRoot.findViewById(R.id.dropBoxFolderCreateEnable);
                    dropBoxFolderCreateEnable.setChecked(false);
                    accesGrantedDropbox.setContentView(accessGrantedDropboxRoot);
                    accesGrantedDropbox.setCancelable(false);
                    accesGrantedDropbox.show();
 //--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------

                   // switchOnDropbox.setChecked(true);
                }
            }
        }
    }

    public void accessGrantedDropbox(View view){
        accesGrantedDropbox.dismiss();
    }

    @Override
    public void onStart() {
        super.onStart();
        if(VERBOSE) Log.d(TAG,"onStart");
    }

    @Override
    public void onStop() {
        super.onStop();
        if(VERBOSE) Log.d(TAG,"onStop");
        if(signInProgress){
            signInProgressDialog.dismiss();
            if(VERBOSE) Log.d(TAG,"Reset signinprogess");
            signInProgress = false;
        }
    }

    String accName;
    public void continueToGoogleDrive(){
        if(!isConnectedToInternet()){
            Toast.makeText(getActivity().getApplicationContext(),getString(R.string.noConnectionMessage), Toast.LENGTH_SHORT).show();
//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------

          //  switchOnDrive.setChecked(false);
            return;
        }
        initializeGoogleSignIn();
        if(VERBOSE) Log.d(TAG,"startActivity");
        signInProgress = true;
        TextView signInText = (TextView)signInProgressRoot.findViewById(R.id.signInText);
        TextView signInprogressTitle = (TextView)signInProgressRoot.findViewById(R.id.savetocloudtitle);
        if(cloud == Contract.GOOGLE_DRIVE_CLOUD) {
            signInprogressTitle.setText(getString(R.string.signInProgressTitle, getString(R.string.googleDrive)));
            signInText.setText(getString(R.string.signInProgress, getString(R.string.googleDrive)));
            ImageView signInImage = (ImageView) signInProgressRoot.findViewById(R.id.signInImage);
            signInImage.setImageDrawable(getActivity().getDrawable(R.drawable.google_drive));
        }
        signInProgressDialog.setContentView(signInProgressRoot);
        signInProgressDialog.setCancelable(false);
        signInProgressDialog.show();
        startActivityForResult(googleSignInClient.getSignInIntent(), REQUEST_CODE_SIGN_IN);
    }

    public boolean isConnectedToInternet(){
        ConnectivityManager cm =
                (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    public void checkIfFolderCreatedInDropbox(){
        if(!isConnectedToInternet()){
            Toast.makeText(getActivity().getApplicationContext(),getString(R.string.noConnectionMessage), Toast.LENGTH_SHORT).show();
//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------

           // switchOnDropbox.setChecked(false);
            return;
        }
        TextView uploadFolderMsg = (TextView)uploadFolderCheckRoot.findViewById(R.id.uploadFolderMsg);
        uploadFolderMsg.setText(getString(R.string.uploadCheckDropboxMsg, getString(R.string.dropbox)));
        ImageView signinImage = (ImageView)uploadFolderCheckRoot.findViewById(R.id.signInImage);
        signinImage.setImageDrawable(getActivity().getDrawable(R.drawable.dropbox));
        uploadFolderCheck.setContentView(uploadFolderCheckRoot);
        uploadFolderCheck.setCancelable(false);
        uploadFolderCheck.show();
        final String folderName = settingsPref.getString(Contract.DROPBOX_FOLDER, "");
        if(VERBOSE) Log.d(TAG, "saved folderName = "+folderName);
        if (folderName != null && !folderName.equals("")) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(3000);
                        com.dropbox.core.v2.files.Metadata metadata = dbxClientV2.files().getMetadata("/"+folderName);
                        if(VERBOSE) Log.d(TAG, "dropbox path display = "+metadata.getPathDisplay());
                        if(VERBOSE) Log.d(TAG, "multiline = "+metadata.toStringMultiline());
                        if(!metadata.getName().equals("")) {
                            if(VERBOSE) Log.d(TAG, "Save folder name in setting");
                            ImageView placeholdericon = (ImageView) autoUploadEnabledRoot.findViewById(R.id.placeHolderIconAutoUpload);
                            placeholdericon.setImageDrawable(getActivity().getDrawable(R.drawable.dropbox));
                            TextView autoUploadMsg = (TextView) autoUploadEnabledRoot.findViewById(R.id.autoUploadMsg);
                            autoUploadMsg.setText(getString(R.string.autouploadFolderUpdated, metadata.getName(), getString(R.string.dropbox)));
                            TextView folderNameTxt = (TextView) autoUploadEnabledRoot.findViewById(R.id.folderName);
                            folderNameTxt.setText(metadata.getName());
                            autoUploadEnabled.setContentView(autoUploadEnabledRoot);
                            autoUploadEnabled.setCancelable(false);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    uploadFolderCheck.dismiss();
                                    autoUploadEnabled.show();
 //--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------

                                    //switchOnDropbox.setChecked(true);
                                }
                            });
                            updateDropboxInSetting(metadata.getName(), true);
                        }
                        else{
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    createUploadFolder();
                                }
                            });
                        }
                    }
                    catch(GetMetadataErrorException metadataerror){
                        if(VERBOSE) Log.d(TAG, "Folder not present = "+metadataerror.getMessage());
                        TextView signInText = (TextView)signInProgressRoot.findViewById(R.id.signInText);
                        TextView signInprogressTitle = (TextView)signInProgressRoot.findViewById(R.id.savetocloudtitle);
                        signInprogressTitle.setText(getString(R.string.uploadFolderNotExist));
                        signInText.setText(getString(R.string.uploadFolderMovedDeleted, folderName));
                        ImageView signInImage = (ImageView) signInProgressRoot.findViewById(R.id.signInImage);
                        signInImage.setImageDrawable(getActivity().getDrawable(R.drawable.dropbox));
                        signInProgressDialog.setContentView(signInProgressRoot);
                        signInProgressDialog.setCancelable(false);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                uploadFolderCheck.dismiss();
                                signInProgressDialog.show();
                            }
                        });
                        try {
                            Thread.sleep(3500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        //Reset folder to FlipCam and give option to user to recreate a subfolder.
                        updateDropboxInSetting(getString(R.string.app_name), true);
                        TextView dropBoxfolderCreated = (TextView) accessGrantedDropboxRoot.findViewById(R.id.dropBoxFolderCreated);
                        dropBoxfolderCreated.setText(getString(R.string.autouploadFolderUpdated, getString(R.string.app_name),
                                getString(R.string.dropbox)));
                        final CheckBox dropBoxFolderCreateEnable = (CheckBox) accessGrantedDropboxRoot.findViewById(R.id.dropBoxFolderCreateEnable);
                        accesGrantedDropbox.setContentView(accessGrantedDropboxRoot);
                        accesGrantedDropbox.setCancelable(false);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                signInProgressDialog.dismiss();
                                dropBoxFolderCreateEnable.setChecked(false);
                                accesGrantedDropbox.show();
//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------

                               // switchOnDropbox.setChecked(true);
                            }
                        });
                    }
                    catch (DbxException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        else{
            uploadFolderCheck.dismiss();
            createUploadFolder();
        }
    }

    public void checkIfFolderCreatedInDrive(){
        TextView uploadFolderMsg = (TextView)uploadFolderCheckRoot.findViewById(R.id.uploadFolderMsg);
        uploadFolderMsg.setText(getString(R.string.uploadCheckMessage, getString(R.string.googleDrive)));
        ImageView signinImage = (ImageView)uploadFolderCheckRoot.findViewById(R.id.signInImage);
        signinImage.setImageDrawable(getActivity().getDrawable(R.drawable.google_drive));
        uploadFolderCheck.setContentView(uploadFolderCheckRoot);
        uploadFolderCheck.setCancelable(false);
        uploadFolderCheck.show();
        final String folderName = settingsPref.getString(Contract.GOOGLE_DRIVE_FOLDER, "");
        if(VERBOSE) Log.d(TAG, "saved folderName = "+folderName);
        if (folderName != null && !folderName.equals("")) {
            mDriveClient.requestSync()
                    .addOnSuccessListener(getActivity(), new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            if(VERBOSE) Log.d(TAG, "sync success");
                            try {
                                Thread.sleep(1100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            queryForFolder(folderName);
                        }
                    })
                    .addOnFailureListener(getActivity(), new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if(!isConnectedToInternet()) {
                                Toast.makeText(getActivity().getApplicationContext(),getString(R.string.noConnectionMessage), Toast.LENGTH_SHORT).show();
                                uploadFolderCheck.dismiss();
//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------

                               // switchOnDrive.setChecked(false);
                                disableGoogleDriveInSetting();
                                googleSignInClient.signOut();
                            }
                            else if(e.getMessage().contains(String.valueOf(DriveStatusCodes.DRIVE_RATE_LIMIT_EXCEEDED))){
                                if(VERBOSE) Log.d(TAG, "sync already done");
                                //Continue as is, since already synced.
                                try {
                                    Thread.sleep(1100);
                                } catch (InterruptedException e1) {
                                    e.printStackTrace();
                                }
                                queryForFolder(folderName);
                            }
                            else if(e.getMessage().contains(String.valueOf(CommonStatusCodes.TIMEOUT))){
                                Toast.makeText(getActivity().getApplicationContext(),getString(R.string.timeoutErrorSync), Toast.LENGTH_SHORT).show();
                                uploadFolderCheck.dismiss();
 //--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------

                               // switchOnDrive.setChecked(false);
                                disableGoogleDriveInSetting();
                                googleSignInClient.signOut();
                            }
                        }
                    });
        }
        else{
            uploadFolderCheck.dismiss();
            createUploadFolder();
        }
    }

    CustomPropertyKey ownerKey = new CustomPropertyKey("owner", CustomPropertyKey.PUBLIC);
    Query query = null;
    public void queryForFolder(final String folder){
        if(query == null){
            query = new Query.Builder()
                    .addFilter(Filters.eq(SearchableField.TITLE, folder))
                    .addFilter(Filters.eq(SearchableField.MIME_TYPE, "application/vnd.google-apps.folder"))
                    .addFilter(Filters.eq(SearchableField.TRASHED, false))
                    .addFilter(Filters.eq(ownerKey, accName))
                    .build();
        }
        mDriveResourceClient.query(query)
                .addOnSuccessListener(new OnSuccessListener<MetadataBuffer>() {
                    @Override
                    public void onSuccess(MetadataBuffer metadatas) {
                        if(VERBOSE) Log.d(TAG, "result metadata = " + metadatas);
                        Iterator<Metadata> iterator = metadatas.iterator();
                        if (metadatas.getCount() > 0 && iterator.hasNext()) {
                            Metadata metadata = iterator.next();
                            final String driveFolderName = metadata.getTitle();
                            if(VERBOSE) Log.d(TAG, "MD title = " + metadata.getTitle());
                            if(VERBOSE) Log.d(TAG, "MD created date = " + metadata.getCreatedDate());
                            if(VERBOSE) Log.d(TAG, "MD drive id = " + metadata.getDriveId());
                            if(VERBOSE) Log.d(TAG, "MD resource id = " + metadata.getDriveId().getResourceId());
                            mDriveClient.getDriveId(metadata.getDriveId().getResourceId())
                                    .addOnSuccessListener(new OnSuccessListener<DriveId>() {
                                        @Override
                                        public void onSuccess(DriveId driveId) {
                                            uploadFolderCheck.dismiss();
                                            ImageView placeholdericon = (ImageView) autoUploadEnabledRoot.findViewById(R.id.placeHolderIconAutoUpload);
                                            placeholdericon.setImageDrawable(getActivity().getDrawable(R.drawable.google_drive));
                                            TextView autoUploadMsg = (TextView) autoUploadEnabledRoot.findViewById(R.id.autoUploadMsg);
                                            autoUploadMsg.setText(getString(R.string.autouploadFolderUpdated, driveFolderName, getString(R.string.googleDrive)));
                                            TextView folderName = (TextView) autoUploadEnabledRoot.findViewById(R.id.folderName);
                                            folderName.setText(driveFolderName);
                                            autoUploadEnabled.setContentView(autoUploadEnabledRoot);
                                            autoUploadEnabled.setCancelable(false);
                                            autoUploadEnabled.show();
//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------

                                           // switchOnDrive.setChecked(true);
                                            updateGoogleDriveInSetting(driveFolderName,true,accName);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            if(!isConnectedToInternet()) {
                                                Toast.makeText(getActivity().getApplicationContext(),getString(R.string.noConnectionMessage), Toast.LENGTH_SHORT).show();
                                            }
                                            else if(e.getMessage().contains(String.valueOf(CommonStatusCodes.TIMEOUT))){
                                                Toast.makeText(getActivity().getApplicationContext(),getString(R.string.timeoutErrorSync), Toast.LENGTH_SHORT).show();
                                            }
                                            uploadFolderCheck.dismiss();
//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------

                                           // switchOnDrive.setChecked(false);
                                            disableGoogleDriveInSetting();
                                            googleSignInClient.signOut();
                                        }
                                    });
                        } else {
                            if(VERBOSE) Log.d(TAG, "No folder exists with name = " + folder);
                            uploadFolderCheck.dismiss();
                            createUploadFolder();
                        }
                        metadatas.release();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if(VERBOSE) Log.d(TAG, "Failure = " + e.getMessage());
                        if(!isConnectedToInternet()) {
                            Toast.makeText(getActivity().getApplicationContext(),getString(R.string.noConnectionMessage), Toast.LENGTH_SHORT).show();
                        }
                        else if(e.getMessage().contains(String.valueOf(CommonStatusCodes.TIMEOUT))){
                            Toast.makeText(getActivity().getApplicationContext(),getString(R.string.timeoutErrorSync), Toast.LENGTH_SHORT).show();
                        }
                        uploadFolderCheck.dismiss();
//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------

                       // switchOnDrive.setChecked(false);
                        disableGoogleDriveInSetting();
                        googleSignInClient.signOut();
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case GET_ACCOUNTS_PERM:
                if(permissions != null && permissions.length > 0) {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        if(VERBOSE) Log.d(TAG,"permission given");
                        if(cloud == Contract.GOOGLE_DRIVE_CLOUD) {
                            continueToGoogleDrive();
                        }
                    } else {
                        if(VERBOSE) Log.d(TAG,"permission rational");
//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------

                       // saveToCloud.dismiss();
                        if(cloud == Contract.GOOGLE_DRIVE_CLOUD) {
                            //switchOnDrive.setChecked(false);
                        }
                    }
                }
                else{
                    super.onRequestPermissionsResult(requestCode,permissions,grantResults);
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "resultCode = "+resultCode);
        switch (requestCode) {
            case REQUEST_CODE_SIGN_IN:
                if (resultCode != getActivity().RESULT_OK) {
                    //Sign in failed due to connection problem or user cancelled it.
                    if(VERBOSE) Log.d(TAG, "Sign-in failed.");
                    Toast.makeText(getActivity().getApplicationContext(),getString(R.string.signinfail), Toast.LENGTH_LONG).show();
                    if(cloud == Contract.GOOGLE_DRIVE_CLOUD) {
//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------

                        //switchOnDrive.setChecked(false);
                        signedInDrive = false;
                        disableGoogleDriveInSetting();
                    }
                    return;
                }
                Task<GoogleSignInAccount> getAccountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
                if (getAccountTask.isSuccessful()) {
                    if(VERBOSE) Log.d(TAG,"isSuccessful");
                    getDriveClient(getAccountTask.getResult());
                    accName = getAccountTask.getResult().getDisplayName();
                    if(VERBOSE) Log.d(TAG, "getAccountTask.getResult().getDisplayName() = "+getAccountTask.getResult().getDisplayName());
                    signedInDrive = true;
                    //Check For Connectivity again.
                    if(!isConnectedToInternet()){
                        if(VERBOSE) Log.d(TAG,"NO Internet");
                        Toast.makeText(getActivity().getApplicationContext(),getString(R.string.noConnectionMessage), Toast.LENGTH_SHORT).show();
//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------

                        //switchOnDrive.setChecked(false);
                        disableGoogleDriveInSetting();
                    }
                    else {
                        checkIfFolderCreatedInDrive();
                    }
                } else {
                    Log.e(TAG, "Sign-in failed 222.");
                    Toast.makeText(getActivity().getApplicationContext(),getString(R.string.signinfail), Toast.LENGTH_LONG).show();
                    if(cloud == Contract.GOOGLE_DRIVE_CLOUD) {
 //--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------

                       // switchOnDrive.setChecked(false);
                        signedInDrive = false;
                        disableGoogleDriveInSetting();
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void disableGoogleDriveInSetting(){
        settingsEditor.putBoolean(Contract.SAVE_TO_GOOGLE_DRIVE, false);
        settingsEditor.commit();
    }

    public void disableDropboxInSetting(){
        settingsEditor.putBoolean(Contract.SAVE_TO_DROPBOX, false);
        settingsEditor.commit();
    }

    private void getDriveClient(GoogleSignInAccount signInAccount) {
        if(VERBOSE) Log.d(TAG,"getDriveClient");
        mDriveClient = Drive.getDriveClient(getActivity().getApplicationContext(), signInAccount);
        mDriveResourceClient = Drive.getDriveResourceClient(getActivity().getApplicationContext(), signInAccount);
        if(VERBOSE) Log.d(TAG, "Sign-in SUCCESS.");
    }

    EditText folderNameText;
    public void createUploadFolder(){
        uploadFolderMsg = (TextView)cloudUploadRoot.findViewById(R.id.uploadFolderMsg);
        uploadFolderTitle = (TextView)cloudUploadRoot.findViewById(R.id.uploadFolderTitle);
        uploadDestIcon = (ImageView) cloudUploadRoot.findViewById(R.id.uploadDestIcon);
        if(cloud == Contract.GOOGLE_DRIVE_CLOUD) {
            uploadFolderMsg.setText(getString(R.string.uploadFolder, getString(R.string.googleDrive)));
            uploadFolderTitle.setText(getString(R.string.uploadFolderTitle));
            uploadDestIcon.setImageDrawable(getActivity().getDrawable(R.drawable.google_drive));
        }
        else if(cloud == Contract.DROPBOX_CLOUD){
            uploadFolderMsg.setText(getString(R.string.uploadFolder, getString(R.string.dropbox)));
            uploadFolderTitle.setText(getString(R.string.uploadFolderTitle));
            uploadDestIcon.setImageDrawable(getActivity().getDrawable(R.drawable.dropbox));
        }
        if(VERBOSE) Log.d(TAG,"Open cloud upload dialog");
        cloudUpload.setContentView(cloudUploadRoot);
        cloudUpload.setCancelable(false);
        cloudUpload.show();
        folderNameText = (EditText)cloudUploadRoot.findViewById(R.id.folderNameText);
        folderNameText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(VERBOSE) Log.d(TAG,"hasFocus = "+hasFocus);
                if(hasFocus){
                    cloudUpload.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });
    }

    private boolean validateFolderNameDropBox(){
        String folderName = ((EditText)cloudUploadRoot.findViewById(R.id.folderNameText)).getText().toString();
        String[] invalidChars = new String[]{"\\","/","?",":","*","\"","|"};
        for(int i=0;i<invalidChars.length;i++){
            if(folderName.contains(invalidChars[i])){
                return false;
            }
        }
        return true;
    }

    private boolean validateFolderNameIsNotEmpty(){
        String folderName = ((EditText)cloudUploadRoot.findViewById(R.id.folderNameText)).getText().toString();
        return !folderName.trim().equals("");
    }

    public void uploadFolder(View view) {
        switch (view.getId()) {
            case R.id.createFolder:
                if(cloud == Contract.GOOGLE_DRIVE_CLOUD) {
                    if (validateFolderNameIsNotEmpty()) {
                        cloudUpload.dismiss();
                        if(!isConnectedToInternet()){
                            Toast.makeText(getActivity().getApplicationContext(),getString(R.string.noConnectionMessage), Toast.LENGTH_SHORT).show();

//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------

                            // switchOnDrive.setChecked(false);
                            disableDropboxInSetting();
                            return;
                        }
                        showCreateProgress();
                        mDriveResourceClient
                                .getRootFolder()
                                .continueWithTask(new Continuation<DriveFolder, Task<DriveFolder>>() {
                                    @Override
                                    public Task<DriveFolder> then(@NonNull Task<DriveFolder> task)
                                            throws Exception {
                                        DriveFolder parentFolder = task.getResult();
                                        CustomPropertyKey ownerKey = new CustomPropertyKey("owner", CustomPropertyKey.PUBLIC);
                                        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                                .setTitle(folderNameText.getText().toString())
                                                .setMimeType(DriveFolder.MIME_TYPE)
                                                .setCustomProperty(ownerKey, accName)
                                                .build();
                                        if(VERBOSE) Log.d(TAG,"Creating folder in Drive");
                                        return mDriveResourceClient.createFolder(parentFolder, changeSet);
                                    }
                                })
                                .addOnSuccessListener(getActivity(),
                                        new OnSuccessListener<DriveFolder>() {
                                            @Override
                                            public void onSuccess(DriveFolder driveFolder) {
                                                signInProgressDialog.dismiss();
                                                ImageView placeholdericon = (ImageView) autoUploadEnabledWithFolderRoot.findViewById(R.id.placeHolderIconAutoUpload);
                                                placeholdericon.setImageDrawable(getActivity().getDrawable(R.drawable.google_drive));
                                                TextView folderCreated = (TextView) autoUploadEnabledWithFolderRoot.findViewById(R.id.folderCreatedMsg);
                                                folderCreated.setText(getString(R.string.folderCreatedSuccess, folderNameText.getText().toString()));
                                                TextView autoUploadMsg = (TextView) autoUploadEnabledWithFolderRoot.findViewById(R.id.autoUploadMsg);
                                                autoUploadMsg.setText(getString(R.string.autouploadFolderCreated, getString(R.string.googleDrive)));
                                                autoUploadEnabledWithFolder.setContentView(autoUploadEnabledWithFolderRoot);
                                                autoUploadEnabledWithFolder.setCancelable(false);
                                                autoUploadEnabledWithFolder.show();
//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------

                                               // switchOnDrive.setChecked(true);
                                                updateGoogleDriveInSetting(folderNameText.getText().toString(), true, accName);
                                            }
                                        })
                                .addOnFailureListener(getActivity(), new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        signInProgressDialog.dismiss();
                                        if(VERBOSE) Log.d(TAG, "Unable to create folder", e);
                                        Toast.makeText(getActivity().getApplicationContext(),
                                                getString(R.string.foldercreateErrorGoogleDrive, getString(R.string.googleDrive)),
                                                Toast.LENGTH_SHORT).show();
//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------

                                       // switchOnDrive.setChecked(false);
                                        signedInDrive = false;
                                        updateGoogleDriveInSetting("", false, "");
                                        googleSignInClient.signOut();
                                    }
                                });
                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), getString(R.string.uploadFolderEmpty), Toast.LENGTH_SHORT).show();
                    }
                }
                else if(cloud == Contract.DROPBOX_CLOUD){
                    if(validateFolderNameIsNotEmpty() && validateFolderNameDropBox()) {
                        cloudUpload.dismiss();
                        if(!isConnectedToInternet()){
                            Toast.makeText(getActivity().getApplicationContext(),getString(R.string.noConnectionMessage), Toast.LENGTH_SHORT).show();
//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------

                           // switchOnDropbox.setChecked(false);
                            disableDropboxInSetting();
                            return;
                        }
                        showCreateProgress();
                        final DbxUserFilesRequests dbxUserFilesRequests = dbxClientV2.files();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    CreateFolderResult createFolderResult = dbxUserFilesRequests.createFolderV2("/" + folderNameText.getText().toString());
                                    String folderId = createFolderResult.getMetadata().getId();
                                    if (folderId != null && !folderId.equals("")) {
                                        ImageView placeholdericon = (ImageView) autoUploadEnabledWithFolderRoot.findViewById(R.id.placeHolderIconAutoUpload);
                                        placeholdericon.setImageDrawable(getActivity().getDrawable(R.drawable.dropbox));
                                        TextView folderCreated = (TextView) autoUploadEnabledWithFolderRoot.findViewById(R.id.folderCreatedMsg);
                                        folderCreated.setText(getString(R.string.folderCreatedSuccess, folderNameText.getText().toString()));
                                        TextView autoUploadMsg = (TextView) autoUploadEnabledWithFolderRoot.findViewById(R.id.autoUploadMsg);
                                        autoUploadMsg.setText(getString(R.string.autouploadFolderCreated, getString(R.string.dropbox)));
                                        autoUploadEnabledWithFolder.setContentView(autoUploadEnabledWithFolderRoot);
                                        autoUploadEnabledWithFolder.setCancelable(false);
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                signInProgressDialog.dismiss();
                                                autoUploadEnabledWithFolder.show();
//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------

                                               // switchOnDropbox.setChecked(true);
                                            }
                                        });
                                        if(VERBOSE) Log.d(TAG, "getPathDisplay = " + createFolderResult.getMetadata().getPathDisplay());
                                        updateDropboxInSetting(createFolderResult.getMetadata().getName(), true);
                                    } else {
                                        if(VERBOSE) Log.d(TAG, "Unable to create folder");
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                signInProgressDialog.dismiss();
                                                Toast.makeText(getActivity().getApplicationContext(),
                                                        getString(R.string.foldercreateErrorGoogleDrive, getString(R.string.dropbox)),
                                                        Toast.LENGTH_SHORT).show();
//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------

                                                //switchOnDropbox.setChecked(false);
                                            }
                                        });
                                        revokeAccessFromDropbox();
                                        updateDropboxInSetting("", false);
                                    }
                                } catch (DbxException e) {
                                    if(VERBOSE) Log.d(TAG, "Error in creating folder = "+e.getMessage());
                                    e.printStackTrace();
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            signInProgressDialog.dismiss();
                                            Toast.makeText(getActivity().getApplicationContext(),
                                                    getString(R.string.foldercreateErrorGoogleDrive, getString(R.string.dropbox)),
                                                    Toast.LENGTH_SHORT).show();
 //--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------

                                            //switchOnDropbox.setChecked(false);
                                        }
                                    });
                                    revokeAccessFromDropbox();
                                    updateDropboxInSetting("", false);
                                }
                            }
                        }).start();
                    }
                    else{
                        Toast.makeText(getActivity().getApplicationContext(), getString(R.string.uploadFolderDropbox), Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.cancelFolder:
                cloudUpload.dismiss();
                if(cloud == Contract.GOOGLE_DRIVE_CLOUD) {
//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------

                    //switchOnDrive.setChecked(false);
                    signedInDrive = false;
                    //updateGoogleDriveInSetting("",false,"");
                    disableGoogleDriveInSetting();
                    googleSignInClient.signOut();
                    showUploadDisabled();
                }
                else if(cloud == Contract.DROPBOX_CLOUD){
//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------

                   // switchOnDropbox.setChecked(false);
                    disableDropboxInSetting();
                    revokeAccessFromDropbox();
                    showUploadDisabled();
                }
        }
    }

    public void showFeedbackMessage(){
        TextView signInText = (TextView)signInProgressRoot.findViewById(R.id.signInText);
        signInText.setText(getString(R.string.sendFeedbackMsg));
        LinearLayout saveToHeader = (LinearLayout)signInProgressRoot.findViewById(R.id.saveToHeader);
        saveToHeader.setVisibility(View.GONE);
        signInProgressDialog.setContentView(signInProgressRoot);
        signInProgressDialog.setCancelable(false);
        signInProgressDialog.show();
    }

    public void hideFeedbackMessage(){
        signInProgressDialog.hide();
    }

    public void showCreateProgress(){
        TextView signInText = (TextView)signInProgressRoot.findViewById(R.id.signInText);
        TextView signInprogressTitle = (TextView)signInProgressRoot.findViewById(R.id.savetocloudtitle);
        signInprogressTitle.setText(getString(R.string.uploadCheckHeader));
        ImageView signInImage = (ImageView) signInProgressRoot.findViewById(R.id.signInImage);
        switch (cloud){
            case Contract.DROPBOX_CLOUD:
                signInText.setText(getString(R.string.createFolder, getString(R.string.dropbox)));
                signInImage.setImageDrawable(getActivity().getDrawable(R.drawable.dropbox));
                break;
            case Contract.GOOGLE_DRIVE_CLOUD:
                signInText.setText(getString(R.string.createFolder, getString(R.string.googleDrive)));
                signInImage.setImageDrawable(getActivity().getDrawable(R.drawable.google_drive));
                break;
        }
        signInProgressDialog.setContentView(signInProgressRoot);
        signInProgressDialog.setCancelable(false);
        signInProgressDialog.show();
    }
    public void revokeAccessFromDropbox(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    dbxClientV2.auth().tokenRevoke();
                    if(VERBOSE) Log.d(TAG, "Token revoked");
                } catch (DbxException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        settingsEditor.remove(Contract.DROPBOX_ACCESS_TOKEN);
        settingsEditor.commit();
    }

    public void showUploadDisabled(){
        ImageView placeholderIcon = (ImageView) autoUploadDisabledRoot.findViewById(R.id.placeHolderIconAutoUploadDisabled);
        TextView disabledMsg = (TextView) autoUploadDisabledRoot.findViewById(R.id.autoUploadDisabledMsg);
        switch (cloud){
            case Contract.GOOGLE_DRIVE_CLOUD:
                placeholderIcon.setImageDrawable(getActivity().getDrawable(R.drawable.google_drive));
                disabledMsg.setText(getString(R.string.signoutcloud, getString(R.string.googleDrive)));
                break;
            case Contract.DROPBOX_CLOUD:
                placeholderIcon.setImageDrawable(getActivity().getDrawable(R.drawable.dropbox));
                disabledMsg.setText(getString(R.string.üploadDisabledDropbox));
                break;
        }
        autoUploadDisabled.setContentView(autoUploadDisabledRoot);
        autoUploadDisabled.setCancelable(false);
        autoUploadDisabled.show();
    }

    public void closeAutoUploadWithFolder(View view){
        autoUploadEnabledWithFolder.dismiss();
    }

    public void closeAutoUpload(View view){
        autoUploadEnabled.dismiss();
    }

    public void closeAutoUploadDisabled(View view){
        autoUploadDisabled.dismiss();
    }

    public void updateGoogleDriveInSetting(String folderName, boolean saveTo, String accname){
        if(VERBOSE) Log.d(TAG, "Saving folder = "+folderName);
        settingsEditor.putString(Contract.GOOGLE_DRIVE_FOLDER,folderName);
        settingsEditor.putBoolean(Contract.SAVE_TO_GOOGLE_DRIVE, saveTo);
        settingsEditor.putString(Contract.GOOGLE_DRIVE_ACC_NAME, accname);
        settingsEditor.commit();
    }

    public void updateDropboxInSetting(String folderName, boolean saveTo){
        if(VERBOSE) Log.d(TAG, "Saving DB folder = "+folderName);
        settingsEditor.putString(Contract.DROPBOX_FOLDER,folderName);
        settingsEditor.putBoolean(Contract.SAVE_TO_DROPBOX, saveTo);
        settingsEditor.commit();
    }

}