package com.and.ibrahim.teleprompter.data;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


/*
 * Created by ibrahim on 22/05/18.
 */
class DbHelper extends SQLiteOpenHelper {

    private static final String TAG = DbHelper.class.getSimpleName();

    public DbHelper(Context context) {
        super(context, Contract.Entry.DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(Contract.Entry.CREATE_SCRIPTS_TABLE);
            db.execSQL("CREATE UNIQUE INDEX event_idx ON " + Contract.Entry.SCRIPTS_TABLE + " ( " + Contract.Entry._ID + " )");
            db.execSQL(Contract.Entry.CREATE_MEDIA_TABLE);
           // db.execSQL("CREATE UNIQUE INDEX event_idx ON " + Contract.Entry.MEDIA_TABLE + " ( " + Contract.Entry._ID + " )");

        } catch (SQLException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(Contract.Entry.DROP_SCRIPTS_TABLE);
        db.execSQL(Contract.Entry.DROP_MEDIA_TABLE);

        onCreate(db);
    }


}

