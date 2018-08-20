package com.and.ibrahim.teleprompter.modules.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.and.ibrahim.teleprompter.R;
import com.and.ibrahim.teleprompter.data.Contract;
import com.and.ibrahim.teleprompter.mvp.model.DataObj;
import com.and.ibrahim.teleprompter.util.GetData;

import java.util.ArrayList;
import java.util.List;


class WidgetRemoteViewsFactorys implements RemoteViewsService.RemoteViewsFactory {
    private static final String TAG = "WidgetRemoteViewsFactor";
    private final Context context;
    private List<DataObj> widgetList = new ArrayList<>();

    public WidgetRemoteViewsFactorys(Context context, Intent intent) {
        this.context = context;
        int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        Log.d("AppWidgetId", String.valueOf(appWidgetId));

    }

    private void updateWidgetListView() {
        widgetList.clear();
        this.widgetList = GetData.getTeleprompters(context);
    }

    @Override
    public int getCount() {

        return widgetList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        Log.d(TAG, "WidgetCreatingView");
        RemoteViews remoteView = new RemoteViews(context.getPackageName(),
                R.layout.list_item_widget);

        remoteView.setTextViewText(R.id.text_title_widget, widgetList.get(position).getTextTitle());
        Intent fillInIntent = new Intent();

        String mScrollString = widgetList.get(position).getTextContent();
        fillInIntent.putExtra(Contract.EXTRA_TEXT, mScrollString);
        Log.d(TAG, "ItemWidget_TextContent_send = " +
                mScrollString);
        remoteView.setOnClickFillInIntent(R.id.widgetItemContainer, fillInIntent);
        return remoteView;
    }

    @Override
    public int getViewTypeCount() {
        return widgetList.size();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void onCreate() {
        widgetList.clear();
        Log.d(TAG, "ItemWidget = onCreate");
        updateWidgetListView();
    }

    @Override
    public void onDataSetChanged() {
        widgetList.clear();
        Log.d(TAG, "ItemWidget = onDataSetChanged");

        updateWidgetListView();
    }

    @Override
    public void onDestroy() {
        widgetList.clear();
        Log.d(TAG, "ItemWidget = onDestroy");


    }


}