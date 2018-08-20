package com.and.ibrahim.teleprompter.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;

import com.and.ibrahim.teleprompter.mvp.model.DataObj;
import com.and.ibrahim.teleprompter.mvp.view.AsyncTaskCompleteListener;

import java.util.ArrayList;

public class FetchData extends AsyncTaskLoader<ArrayList<DataObj>> {
    private final AsyncTaskCompleteListener<ArrayList<DataObj>> listener;
    private ArrayList<DataObj> dataObjArrayList;

    public FetchData(@NonNull Context context, AsyncTaskCompleteListener<ArrayList<DataObj>> listener) {
        super(context);
        this.listener = listener;
    }

    @Override
    protected void onStartLoading() {
        if (dataObjArrayList != null) {
            // Delivers any previously loaded data immediately
            deliverResult(dataObjArrayList);
        } else {
            // Force a new load
            forceLoad();
        }
    }

    @Nullable
    @Override
    public ArrayList<DataObj> loadInBackground() {

        try {
            return GetData.getTeleprompters(getContext());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }


    }

    public void deliverResult(ArrayList<DataObj> resultDataObjs) {
        dataObjArrayList = resultDataObjs;
        super.deliverResult(resultDataObjs);
        listener.onTaskComplete(resultDataObjs);

    }

}
