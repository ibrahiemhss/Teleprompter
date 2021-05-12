package com.and.ibrahim.teleprompter.data;

import android.net.Uri;
import android.provider.BaseColumns;

/*Created by ibrahim on 26/05/18.
 */
public class Contract implements BaseColumns {


    //extra strings saved in bundle
    public static final String EXTRA_TEXT = "extra_text_show";
    public static final String EXTRA_FRAGMENT = "teleprompter_fragment";
    public static final String EXTRA_SCROLL_POSITION = "extra_scroll_position";
    public static final String EXTRA_SELECTED = "extra_selected";
    public static final String EXTRA_FLAG = "extra_flag";
    public static final String EXTRA_STRING_TITLE_ADD = "extra_string_title_add";
    public static final String EXTRA_STRING_CONTENT_ADD = "extra_string_content_add";
    public static final String EXTRA_SHOW_ADD_DIALOG = "extra_show_dialog_add";
    public static final String EXTRA_SHOW_UPDATE_DIALOG = "extra_show_dialog_update";
    public static final String EXTRA_SHOW_COLOR_DIALOG = "extra_show_dialog_color";
    public static final String EXTRA_STRING_TITLE_UPDATE = "extra_string_title_update";
    public static final String EXTRA_STRING_CONTENT_UPDATE = "extra_string_content_update";
    public static final String EXTRA_SCROLL_STRING = "extra_scroll_string";
    public static final String EXTRA_SCROLL_POS = "extra_scroll_to";
    public static final String EXTRA_CHRONOTIME = "extra_chrono_time";

    public static final String EXTRA_SHOW_PERMISSION = "showPermission";
    public static final String EXTRA_RESTART = "restart";
    public static final String EXTRA_QUIT = "quit";
    public static final String EXTRA_FROM_GALLERY = "fromGallery";


    //content provider
    public static final String PATH = "teleprompter";
    public static final String DATA = "data";

    public static final String ADD_MEDIA = "addMedia";
    public static final String DELETE_MEDIA = "deleteMedia";

    public static final String AUTHORITY = "com.and.ibrahim.teleprompter";
    private static final String SCHEMA = "content://";
    public static final String BASE_CONTENT = "content://" + SCHEMA;

    public static final Uri BASE_CONTENT_URI = Uri.parse(SCHEMA + AUTHORITY);
    public static String DB_PATH = "/data/"+AUTHORITY+"/databases/";
    public static final class Entry implements BaseColumns {
        public static final Uri PATH_TELEPROMPTER_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH).build();
        public static final Uri PATH_ADD_MEDIA_URI = BASE_CONTENT_URI.buildUpon().appendPath(DATA).appendPath(ADD_MEDIA).build();

        //for table of teleprompter
        public static final String SCRIPTS_TABLE = "scripts";
        public static final String COL_UNIQUE_ID = "unique_id";
        public static final String COL_CONTENTS = "contents";
        public static final String COL_TITLE = "title";
        public static final String COL_SELECT = "flag";
        public static final String DROP_SCRIPTS_TABLE = "DROP TABLE IF EXISTS " + SCRIPTS_TABLE;
        public static final String CREATE_SCRIPTS_TABLE = "create table " + SCRIPTS_TABLE + "(" +
                _ID + " INTEGER primary key autoincrement not null," +
                COL_UNIQUE_ID + " INTEGER  ," +
                COL_TITLE + " text  null," +
                COL_CONTENTS + " text not null ," +
                COL_SELECT + " INTEGER )";

        public static final String MEDIA_TABLE = "media";
        public static final String FILE_NAME = "FILE_NAME";
        public static final String MEMORY_STORAGE = "MEMORY_STORAGE";
        public static final String DROP_MEDIA_TABLE = "DROP TABLE IF EXISTS " + MEDIA_TABLE;

        public static final String CREATE_MEDIA_TABLE = "CREATE TABLE " + MEDIA_TABLE + " ( "+
                _ID + " INTEGER NOT NULL PRIMARY KEY , "+
                FILE_NAME + " TEXT , "+
                MEMORY_STORAGE + " INTEGER "+
                " ); ";
        //database name
        static final String DB_NAME = "teleprompter_app.db";


    }


    public static final String SHARED_NAME = "SHARED_NAME_CONTENTS";
    public static final String CURRENT_TEXT = "current_text";

    public static final String SCROLL_SPEED = "Contract.speed";
    public static final String TEXT_SIZE = "Contract.text_size";
    public static final String TEXT_COLOR = "Contract.text_color";
    public static final String TRANSPARENT_TEXT_COLOR = "Contract.Transparent.text_color";
    public static final String BACKGROUND_COLOR = "Contract.background_color";
    public static final String TRANSPARENT_BACKGROUND_COLOR = "Contract.Transparent.background_color";

    public static final String UNDO_TEXT_COLOR = "Contract.undo_text_color";
    public static final String UNDO_BACKGROUND_COLOR = "Contract.uno_background_color";
    public static final String COLOR_PREF = "color_pref";

    public static final String FIRST_SET_PERMISSION_PREF = "firstSetPermission_pref";

    public static final String CAMERA_PERMISSION_PREF = "cameraPermission_pref";
    public static final String AUDIO_PERMISSION_PREF = "audioPermission_pref";
    public static final String STORAGE_PERMISSION_PREF = "storagePermission_pref";

    public static final String FIRST_ENTRY = "Contract.first_entry";
    public static final String INIT_SDK = "Contract.init_sdk";
    public static final String FIRST_COLOR_SET = "Contract.first_color";
    public static final String FIRST_TEXT_SET = "Contract.first_text";
    public static final String FIRST_SPEED_SET = "Contract.first_speed";
    public static final String REMOTE_CONFIG_AlL_ADs = "Contract.REMOTE_CONFIG_FULL_SCREEN_ADs";
    public static final String REMOTE_CONFIG_FULL_SCREEN_ADs = "Contract.REMOTE_CONFIG_FULL_SCREEN_ADs";
    public static final String FULL_SCREEN_AD_SHOWN = "Contract.FULL_SCREEN_AD_SHOWN";
    public static final String OPEN_FULL_SCREEN_ADS = "Contract.FULL_SCREEN_AD_SHOWN";
    public static final String OPEN_ALL_ADS = "Contract.FULL_SCREEN_AD_SHOWN";


    public static final int INTERSTITAI_ADS= 11;
    public static final int REAWRDED_ADS= 11;
    public static final int REAWRDED_AD_ACTION= 20;
    public static final int INTERSTITAI_AD_ACTION= 20;


    //Message to be sent to threads
    public final static int FRAME_AVAILABLE = 1000;
    public final static int RECORD_STOP = 2000;
    public final static int RECORD_START = 3000;
    public final static int RECORD_COMPLETE = 13000;
    public final static int RECORD_PAUSE = 14000;
    public final static int RECORD_RESUME = 15000;
    public final static int SHUTDOWN = 6000;
    public final static int GET_CAMERA_RENDERER_INSTANCE = 8000;
    public final static int SHOW_MEMORY_CONSUMED = 5000;
    public final static int SHOW_ELAPSED_TIME = 7000;
    public final static int HIDE_PAUSE_TEXT = 16000;
    public final static int SHOW_PAUSE_TEXT = 17000;
    public final static int RECORD_STOP_ENABLE = 9000;
    public final static int RECORD_STOP_LOW_MEMORY = 10000;
    public final static int RECORD_STOP_NO_SD_CARD = 11000;
    public final static int SHOW_SELFIE_TIMER = 12000;

    //File size for calculating memory consumed
    public final static double KILO_BYTE = 1024.0;
    public final static double MEGA_BYTE = KILO_BYTE * KILO_BYTE;
    public final static double GIGA_BYTE = KILO_BYTE * MEGA_BYTE;

    public static final String METRIC_KB = "KB";
    public static final String METRIC_MB = "MB";
    public static final String METRIC_GB = "GB";

    public static final int _4K_VIDEO_RESOLUTION = 2160;

    public static final String IS_CAMERA_ENAPLED = "isCameraEnabled";

    //To fetch first frame to display thumbnail
    public static final long FIRST_SEC_MICRO = 1000000;
    //To update seek bar
    public static final int VIDEO_SEEK_UPDATE = 100;
    //Message for upload progress
    public static final int UPLOAD_PROGRESS = 1000;
    //Settings prefs
    //Save Media
    public static final String FC_SETTINGS = "Teleprompter‏_Settings";

    public static final String SAVE_MEDIA_PHONE_MEM = "Save_Media_Phone_Mem";
    public static final String SD_CARD_PATH = "SD_Card_Path";
    //Media Location
    //To check if SD Card location is valid
    public static final String EMPTY = "";
    public static final String MEDIA_LOCATION_VIEW_SELECT = "Media_Location_View_Select";
    public static final String MEDIA_LOCATION_VIEW_SELECT_PREV = "Media_Location_View_Select_Previous";
    //Phone Memory Limit
    public static final String PHONE_MEMORY_LIMIT = "PhoneMemoryLimit";
    public static final String PHONE_MEMORY_METRIC = "PhoneMemoryMetric";
    public static final String PHONE_MEMORY_DISABLE = "PhoneMemoryDisable";
    //Save to cloud
    public static final String SAVE_TO_GOOGLE_DRIVE = "SaveToDrive";
    public static final String GOOGLE_DRIVE_FOLDER = "GoogleDriveFolder";
    public static final String GOOGLE_DRIVE_ACC_NAME = "AccName";
    public static final String SAVE_TO_DROPBOX = "SaveToDropBox";
    public static final String DROPBOX_FOLDER = "DropboxFolder";
    public static final int GOOGLE_DRIVE_CLOUD = 0;
    public static final int DROPBOX_CLOUD = 1;
    public static final String DROPBOX_ACCESS_TOKEN = "DropBoxAccessToken";
    //Show memory consumed msg
    public static final String SHOW_MEMORY_CONSUMED_MSG = "ShowMemoryConsumedText";
    public static final String MEDIA_COUNT_MEM = "mediaCountMem";
    public static final String MEDIA_COUNT_SD_CARD = "mediaCountSdCard";
    public static final String MEDIA_COUNT_ALL = "mediaCountAll";

    public static final String START_CAMERA = "startCamera";

    //Enable/Disable shutter sound
    public static final String SHUTTER_SOUND = "ShutterSound";
    public static final String VIDEO_CAPTUE = "VideoCapture";


    //Set Selfie Timer
    public static final String SELFIE_TIMER = "SelfieTimer";
    public static final String SELFIE_TIMER_ENABLE = "SelfieTimerEnable";
    //Screen Resolution
    public static final String PREVIEW_RESOLUTION = "PreviewResolution";
    //Video Resolution
    public static final String SELECT_VIDEO_RESOLUTION = "SelectVideoResolution";
    public static final String VIDEO_DIMENSION_HIGH = "videoDimensionHigh";
    public static final String VIDEO_DIMENSION_MEDIUM = "videoDimensionMedium";
    public static final String VIDEO_DIMENSION_LOW = "videoDimensionLow";
    //Video Player
    public static final String SELECT_VIDEO_PLAYER = "SelectVideoPlayer";
    public static final String SHOW_EXTERNAL_PLAYER_MESSAGE = "ShowExternalPlayerMessage";
    //Normal brightness level
    public static final int NORMAL_BRIGHTNESS = 5;
    public static final float NORMAL_BRIGHTNESS_PROGRESS = 0.0f;
    //Photo Resolution
    public static final String SELECT_PHOTO_RESOLUTION = "SelectPhotoResolution";
    public static final String SELECT_PHOTO_RESOLUTION_FRONT = "SelectPhotoResolutionFront";
    public static final String SUPPORT_PHOTO_RESOLUTIONS = "SupportedPhotoResolutions";
    public static final String SUPPORT_PHOTO_RESOLUTIONS_FRONT = "SupportedPhotoResolutionsFront";
    public static final String SUPPORT_VIDEO_RESOLUTIONS = "SupportVideoResolutions";
    //Unmounted/Mounted Intent
    public static final String MEDIA_UNMOUNTED = "android.intent.action.MEDIA_UNMOUNTED";
    public static final String MEDIA_MOUNTED = "android.intent.action.MEDIA_MOUNTED";
}