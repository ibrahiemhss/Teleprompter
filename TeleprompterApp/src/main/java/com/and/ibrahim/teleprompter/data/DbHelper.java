package com.and.ibrahim.teleprompter.data;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;



/*
 * Created by ibrahim on 22/05/18.
 */
public class DbHelper extends SQLiteOpenHelper {

    private static final String TAG = DbHelper.class.getSimpleName();

    public DbHelper(Context context) {
        super(context, Contract.BakeEntry.DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(Contract.BakeEntry.CREATE_TABLE_TELEPROMPTER);
            db.execSQL("CREATE UNIQUE INDEX event_idx ON " + Contract.BakeEntry.TABLE_TELEPROMPTER + " ( " + Contract.BakeEntry._ID + " )");

        } catch (SQLException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(Contract.BakeEntry.DROP_TELEPROMPTER_TELEPROMPTER);


        onCreate(db);
    }


}

