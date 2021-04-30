package com.and.ibrahim.teleprompter.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.and.ibrahim.teleprompter.data.Contract;
import com.and.ibrahim.teleprompter.mvp.model.DataObj;

import java.util.ArrayList;

public class GetData {
    private static final String TAG = "GetData";

    public static ArrayList<DataObj> getTeleprompters(Context context)

    {
        Cursor c = null;
        DataObj dataObj;
        ArrayList<DataObj> dataObjArrayList = new ArrayList<>();
        Uri uri = Contract.Entry.PATH_TELEPROMPTER_URI;
        if (uri != null) {
            c = context.getContentResolver().query(uri,
                    null,
                    null,
                    null,
                    null);

        }

        if (c != null) {
            while (c.moveToNext()) {
                dataObj = new DataObj();
                String id = c.getString(c.getColumnIndexOrThrow(Contract.Entry.COL_UNIQUE_ID));
                String title = c.getString(c.getColumnIndexOrThrow(Contract.Entry.COL_TITLE));
                String content = c.getString(c.getColumnIndexOrThrow(Contract.Entry.COL_CONTENTS));
                int isSelected = c.getInt(c.getColumnIndexOrThrow(Contract.Entry.COL_SELECT));

                dataObj.setTextTitle(title);
                dataObj.setId(Integer.parseInt(id));
                dataObj.setTextContent(content);
                dataObjArrayList.add(dataObj);

                Log.i(TAG, "FetchTeleprompterTexts \n title =" + title + "\n content =" + content + "\nSelected value =" + isSelected);
            }
            c.close();
        }


        return dataObjArrayList;
    }


}