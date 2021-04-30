package com.and.ibrahim.teleprompter.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import com.and.ibrahim.teleprompter.callback.AsyncTaskCompleteListener;
import com.and.ibrahim.teleprompter.mvp.model.DataObj;

import java.util.ArrayList;

@SuppressLint("StaticFieldLeak")
public class FetchDataAsyncTask extends AsyncTask<String, Void, ArrayList<DataObj>> {
    private final Context context;
    private AsyncTaskCompleteListener listener;

    public FetchDataAsyncTask(Context context) {
        this.context = context;
    }

    @Override
    protected ArrayList<DataObj> doInBackground(String... params) {

        try {
            return GetData.getTeleprompters(context);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(ArrayList<DataObj> ListData) {

        super.onPostExecute(ListData);
        if (listener != null) {
            listener.onExampleAsyncTaskFinished(ListData);
        }
    }

    public void setListener(AsyncTaskCompleteListener listener) {
        this.listener = listener;
    }


}