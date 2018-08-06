package com.and.ibrahim.teleprompter.data;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Objects;

public class MyContentProvider extends ContentProvider {
    private static final String TAG = "MyContentProvider";

    private static final int TELEPROMPTER_CODE = 200;
    private static final int TELEPROMPTER_WITH_ID = 250;
    private static final int TELEPROMPTER_WITH_NO_ID = 300;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private DbHelper mDbHelper;

    private static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(Contract.AUTHORITY, Contract.PATH, TELEPROMPTER_CODE);
        uriMatcher.addURI(Contract.AUTHORITY, Contract.PATH + "/*", TELEPROMPTER_WITH_NO_ID);
        uriMatcher.addURI(Contract.AUTHORITY, Contract.PATH + "/#", TELEPROMPTER_WITH_ID);

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

            switch (uriType) {
                case TELEPROMPTER_CODE:
                    try {
                        sqlDB.beginTransaction();
                        for (ContentValues value : values) {
                            if (sqlDB.insertOrThrow(Contract.BakeEntry.TABLE_TELEPROMPTER, null, value) == -1) {
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
                    break;
                default:
                    throw new IllegalArgumentException("Unknown URI: " + uri);
            }
            // getContext().getContentResolver().notifyChange(uri, null);
        } catch (Exception e) {
            // Your error handling
        }

        return insertCount;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = mDbHelper.getReadableDatabase();

        int match = sUriMatcher.match(uri);

        Cursor retCursor;

        switch (match) {

            case TELEPROMPTER_CODE:

                retCursor = db.query(Contract.BakeEntry.TABLE_TELEPROMPTER,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);

                break;
            case TELEPROMPTER_WITH_ID:

                retCursor = db.query(Contract.BakeEntry.TABLE_TELEPROMPTER,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);

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

        int match = sUriMatcher.match(uri);

        Uri returnUri;

        switch (match) {

            case TELEPROMPTER_CODE:

                long id = db.insertWithOnConflict(Contract.BakeEntry.TABLE_TELEPROMPTER, null, values, SQLiteDatabase.CONFLICT_IGNORE);

                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(Contract.BakeEntry.PATH_TELEPROMPTER_URI, id);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }

                break;

            default:
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
        int idDeleted; // starts as 0

        switch (match) {
            case TELEPROMPTER_WITH_NO_ID:
                String id = uri.getPathSegments().get(1);
                if (id != null) {
                    // Use selections/selectionArgs to filter for this ID
                    idDeleted = db.delete(Contract.BakeEntry.TABLE_TELEPROMPTER,
                            Contract.BakeEntry.COL_UNIQUE_ID + " =?",
                            new String[]{id});
                    Log.d(TAG, "idDeleted =is " + id);
                } else {
                    idDeleted = db.delete(Contract.BakeEntry.TABLE_TELEPROMPTER, selection, selectionArgs);

                }
                // Use selections/selectionArgs to filter for this ID
                break;

            case TELEPROMPTER_WITH_ID:
                idDeleted = db.delete(
                        Contract.BakeEntry.TABLE_TELEPROMPTER, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (idDeleted != 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);
            }
        }


        return idDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count;
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);

        switch (match) {
            case TELEPROMPTER_CODE:
                count = db.update(Contract.BakeEntry.TABLE_TELEPROMPTER, values, selection, selectionArgs);

                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);
        }
        return count;
    }


}