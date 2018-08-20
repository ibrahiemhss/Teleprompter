package com.and.ibrahim.teleprompter.mvp.view;


public interface AsyncTaskCompleteListener<T> {
    /**
     * Invoked when the AsyncTask has completed its execution.
     *
     * @param result The resulting object from the AsyncTask.
     */
    void onTaskComplete(T result);
}