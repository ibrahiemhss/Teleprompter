package com.and.ibrahim.teleprompter.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.and.ibrahim.teleprompter.data.Contract;
import com.and.ibrahim.teleprompter.mvp.model.DataObj;

import java.util.ArrayList;

public class GetData {
    private static final String TAG = "getBakeUtils";

    public static ArrayList<DataObj> getTeleprompters(Context context)

    {
        Cursor c = null;
        DataObj dataObj;
        ArrayList<DataObj> dataObjArrayList = new ArrayList<>();
        /* get the ContentProvider URI */
        Uri uri = Contract.BakeEntry.PATH_TELEPROMPTER_URI;
        /* Perform the ContentProvider query */
        if (uri != null) {
            c = context.getContentResolver().query(uri,
                    /* Columns; leaving this null returns every column in the table */
                    null,
                    /* Optional specification for columns in the "where" clause above */
                    null,
                    /* Values for "where" clause */
                    null,
                    /* Sort order to return in Cursor */
                    null);

        }

        /*make sure if curser not null to bypass the mistake */
        if (c != null) {
            /*start cursor reading and move from column to other to find all data inside table*/
            while (c.moveToNext()) {
                dataObj = new DataObj();
                /*get all value by cursor while moving by get its column name and get value inside it*/

                String id = c.getString(c.getColumnIndexOrThrow(Contract.BakeEntry.COL_UNIQUE_ID));

                String title = c.getString(c.getColumnIndexOrThrow(Contract.BakeEntry.COL_TITLE));
                String content = c.getString(c.getColumnIndexOrThrow(Contract.BakeEntry.COL_CONTENTS));

                /*while cursor movement will get value of every column this value will save inside all movie object from Movies Class*/

                // dataObj.setId(Integer.parseInt(id));
                dataObj.setTextTitle(title);
                dataObj.setId(Integer.parseInt(id));
                dataObj.setTextContent(content);
                /*add all new value of movie object to moviesArrayList*/
                dataObjArrayList.add(dataObj);

                Log.i(TAG, "FetchTeleprompterTexts \n title =" + title + "\n content =" + content);
            }
            c.close();
        }


        return dataObjArrayList;
    }


}