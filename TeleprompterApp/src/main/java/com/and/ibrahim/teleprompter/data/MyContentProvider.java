package com.and.ibrahim.teleprompter.data;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;

import java.util.Objects;

public class MyContentProvider extends ContentProvider {
    private static final String TAG = "MyContentProvider";

    private static final int SCRIPTS_CODE = 200;
    private static final int SCRIPTS_WITH_ID = 250;
    private static final int SCRIPTS_WITH_NO_ID = 300;
    private static final int INSERT_MEDIA = 350;
    private static final int DELETE_MEDIA = 425;
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private DbHelper mDbHelper;


    private static UriMatcher buildUriMatcher() {

            UriMatcher  uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(Contract.AUTHORITY, Contract.PATH, SCRIPTS_CODE);
        uriMatcher.addURI(Contract.AUTHORITY, Contract.PATH + "/*", SCRIPTS_WITH_ID);
        uriMatcher.addURI(Contract.AUTHORITY, Contract.PATH + "/#", SCRIPTS_WITH_NO_ID);
        uriMatcher.addURI(Contract.AUTHORITY,Contract.AUTHORITY+"/addMedia",INSERT_MEDIA);
        uriMatcher.addURI(Contract.AUTHORITY,Contract.AUTHORITY+"/deleteMedia",DELETE_MEDIA);


        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mDbHelper = new DbHelper(context);

        return true;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        int uriType;
        int insertCount = 0;
        try {

            uriType = sUriMatcher.match(uri);
            SQLiteDatabase sqlDB = mDbHelper.getWritableDatabase();

            if (uriType == SCRIPTS_CODE) {
                try {
                    sqlDB.beginTransaction();
                    for (ContentValues value : values) {
                        if (sqlDB.insertOrThrow(Contract.Entry.SCRIPTS_TABLE, null, value) == -1) {
                            throw new Exception("Unknown error while inserting entry in database.");
                        }
                        insertCount++;
                    }

                    sqlDB.setTransactionSuccessful();
                } catch (Exception e) {
                    // Your error handling
                } finally {
                    sqlDB.endTransaction();
                }
            } else {
                throw new IllegalArgumentException("Unknown URI: " + uri);
            }
        } catch (Exception ignored) {
        }

        return insertCount;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = mDbHelper.getReadableDatabase();

        int match=buildUriMatcher().match(uri);
        Cursor retCursor=null;

        switch (match) {
            case SCRIPTS_WITH_ID:

                retCursor = db.query(Contract.Entry.SCRIPTS_TABLE,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                Log.d(TAG, "SCRIPTS_TABLE query = "+db.query(Contract.Entry.SCRIPTS_TABLE,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder).toString());
                break;
            case SCRIPTS_CODE:
                retCursor = db.query(Contract.Entry.SCRIPTS_TABLE,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                Log.d(TAG, "SCRIPTS_CODE query =");
                break;
            case INSERT_MEDIA:
                retCursor = db.query(Contract.Entry.MEDIA_TABLE,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);

                Log.d(TAG, "MEDIA_TABLE query = "+db.query(Contract.Entry.MEDIA_TABLE,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder).toString());

                break;


            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            retCursor.setNotificationUri(Objects.requireNonNull(getContext()).getContentResolver(), uri);
        }

        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {

        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        Log.d(TAG,"inserting uri =" +uri.toString());

        int match = sUriMatcher.match(uri);
        Uri returnUri=null;

        if(uri.toString().contains(Contract.ADD_MEDIA)){
            Log.d(TAG,"inserting Media1");
            long id = db.insertWithOnConflict(Contract.Entry.MEDIA_TABLE, null, values, SQLiteDatabase.CONFLICT_IGNORE);

            if (id > 0) {
                returnUri = ContentUris.withAppendedId(Contract.Entry.PATH_ADD_MEDIA_URI, id);
            } else {
                throw new SQLException("Failed to insert row into 1" + uri);
            }
        }else
        if (match == SCRIPTS_CODE) {
            long id = db.insertWithOnConflict(Contract.Entry.SCRIPTS_TABLE, null, values, SQLiteDatabase.CONFLICT_IGNORE);

            if (id > 0) {
                returnUri = ContentUris.withAppendedId(Contract.Entry.PATH_TELEPROMPTER_URI, id);
            } else {
                throw new SQLException("Failed to insert row into " + uri);
            }
        } else if(match == INSERT_MEDIA){
            Log.d(TAG,"inserting Media");
            long id = db.insertWithOnConflict(Contract.Entry.MEDIA_TABLE, null, values, SQLiteDatabase.CONFLICT_IGNORE);

            if (id > 0) {
                returnUri = ContentUris.withAppendedId(Contract.Entry.PATH_ADD_MEDIA_URI, id);
            } else {
                throw new SQLException("Failed to insert row into " + uri);
            }
        }else {
            throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);
        }

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {


        // Get access to the database and write URI matching code to recognize a single item
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        int idDeleted=0; // starts as 0
        Uri returnUri=null;
        Log.d(TAG,"uri data = "+uri.toString());

        if(uri.toString().contains(Contract.DELETE_MEDIA)){
            Log.d(TAG,"deleting Media = "+selectionArgs[0]);
            String id = uri.getPathSegments().get(0);
            if (id != null) {
                // Use selections/selectionArgs to filter for this ID
                idDeleted = db.delete(Contract.Entry.MEDIA_TABLE,
                        Contract.Entry.FILE_NAME + " =?",
                        new String[]{id});
                Log.d(TAG, "idDeleted =is " + id);
            } else {
                idDeleted = db.delete(Contract.Entry.MEDIA_TABLE, selection, selectionArgs);
                Log.d(TAG, "idDeleted = all ");

            }

        }else{
            switch (match) {
                case SCRIPTS_WITH_NO_ID:
                    String id = uri.getPathSegments().get(1);
                    if (id != null) {
                        // Use selections/selectionArgs to filter for this ID
                        idDeleted = db.delete(Contract.Entry.SCRIPTS_TABLE,
                                Contract.Entry.COL_UNIQUE_ID + " =?",
                                new String[]{id});
                        Log.d(TAG, "idDeleted =is " + id);
                    } else {
                        idDeleted = db.delete(Contract.Entry.SCRIPTS_TABLE, selection, selectionArgs);
                        Log.d(TAG, "idDeleted = all ");

                    }
                    // Use selections/selectionArgs to filter for this ID
                    break;

                case SCRIPTS_CODE:
                case SCRIPTS_WITH_ID:
                    idDeleted = db.delete(
                            Contract.Entry.SCRIPTS_TABLE, selection, selectionArgs);
                    Log.d(TAG, "idDeleted =TELEPROMPTER_WITH_ID ");

                    break;


                default:
                    throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
            if (idDeleted != 0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);
                }
            }
        }


        return idDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count;
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);

        if (match == SCRIPTS_CODE) {
            count = db.update(Contract.Entry.SCRIPTS_TABLE, values, selection, selectionArgs);
        } else {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);
        }
        return count;
    }

    public  SQLiteDatabase getWriteMediaDatabase(){
        final SQLiteDatabase writeMediaDatabase = mDbHelper.getWritableDatabase();

        if(writeMediaDatabase == null){
            String myPath = Contract.DB_PATH + Contract.Entry.DB_NAME;
            try {
                writeMediaDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
                if (writeMediaDatabase != null) {
                    return writeMediaDatabase;
                }
            }
            catch(SQLiteCantOpenDatabaseException sqlEx)
            {

                Log.d(TAG,"SQLiteCantOpenDatabaseException Path == "+sqlEx.toString());

                //writeMediaDatabase = mDbHelper.getWritableDatabase();
            }
            Log.d(TAG,"DB Path == "+writeMediaDatabase.getPath());
        }
        return writeMediaDatabase;
    }

}