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
    public static final String EXTRA_STRING_TITLE = "extra_string_title";
    public static final String EXTRA_STRING_CONTENT = "extra_string_content";
    public static final String EXTRA_SHOW_DIALOG = "extra_show_dialog";
    public static final String EXTRA_SCROLL_STRING = "extra_scroll_string";
    public static final String EXTRA_SCROLL_POS = "extra_scroll_to";


    //content provider
    static final String PATH = "teleprompter";

    static final String AUTHORITY = "com.and.ibrahim.teleprompter";
    private static final String SCHEMA = "content://";
    private static final Uri BASE_CONTENT_URI = Uri.parse(SCHEMA + AUTHORITY);

    public static final class Entry implements BaseColumns {
        public static final Uri PATH_TELEPROMPTER_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH).build();

        //database name
        static final String DB_NAME = "teleprompter_app.db";

        //for table of teleprompter
        public static final String TABLE_TELEPROMPTER = "teleprompter_bake";
        public static final String COL_UNIQUE_ID = "unique_id";

        public static final String COL_CONTENTS = "contents";
        public static final String COL_TITLE = "title";
        public static final String COL_SELECT = "flag";

        public static final String DROP_TELEPROMPTER_TELEPROMPTER = "DROP TABLE IF EXISTS " + TABLE_TELEPROMPTER;
        public static final String CREATE_TABLE_TELEPROMPTER = "create table " + TABLE_TELEPROMPTER + "(" +
                _ID + " INTEGER primary key autoincrement not null," +
                COL_UNIQUE_ID + " INTEGER  ," +
                COL_TITLE + " text  null," +
                COL_CONTENTS + " text not null ," +
                COL_SELECT + " INTEGER )";



    }


}