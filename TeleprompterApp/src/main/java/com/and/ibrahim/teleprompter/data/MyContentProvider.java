package com.and.ibrahim.teleprompter.data;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class MyContentProvider extends ContentProvider {

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

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

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

                long id = db.insertWithOnConflict(Contract.BakeEntry.TABLE_TELEPROMPTER, null, values, SQLiteDatabase.CONFLICT_REPLACE);

                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(Contract.BakeEntry.PATH_TELEPROMPTER_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }

                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

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

                // Use selections/selectionArgs to filter for this ID
                idDeleted = db.delete(Contract.BakeEntry.TABLE_TELEPROMPTER, selection, selectionArgs);
                break;

            case TELEPROMPTER_WITH_ID:
                idDeleted = db.delete(
                        Contract.BakeEntry.TABLE_TELEPROMPTER, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (idDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
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

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }


}
