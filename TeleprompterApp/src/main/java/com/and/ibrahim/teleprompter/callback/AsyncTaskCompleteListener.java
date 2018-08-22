package com.and.ibrahim.teleprompter.callback;

import com.and.ibrahim.teleprompter.mvp.model.DataObj;

import java.util.ArrayList;

public interface AsyncTaskCompleteListener {
    void onExampleAsyncTaskFinished(ArrayList<DataObj> dataObjs);
}
