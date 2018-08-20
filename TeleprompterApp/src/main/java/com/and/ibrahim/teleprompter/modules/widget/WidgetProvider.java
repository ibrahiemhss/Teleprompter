package com.and.ibrahim.teleprompter.modules.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.TaskStackBuilder;
import android.widget.RemoteViews;

import com.and.ibrahim.teleprompter.R;
import com.and.ibrahim.teleprompter.modules.display.DisplayActivity;
import com.and.ibrahim.teleprompter.modules.listContents.ListContentsActivity;

import java.util.Objects;
/*
 *Created by ibrahim on 26/06/18.
 */

public class WidgetProvider extends AppWidgetProvider {


    @SuppressWarnings("unused")
    private static final String TAG = "WidgetProvider";
    private boolean isTablet;

    public static void sendRefreshBroadcast(Context context) {
        Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.setComponent(new ComponentName(context, WidgetProvider.class));
        context.sendBroadcast(intent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (Objects.requireNonNull(action).equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
            // refresh all your widgets
            AppWidgetManager mgr = AppWidgetManager.getInstance(context);
            ComponentName cn = new ComponentName(context, WidgetProvider.class);
            mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn), R.id.widgetListView);
        }
        isTablet = context.getResources().getBoolean(R.bool.isTablet);
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            RemoteViews rv = new RemoteViews(context.getPackageName(),
                    R.layout.main_widget);
            Intent intent = new Intent(context, WidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    appWidgetId);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            rv.setRemoteAdapter(R.id.widgetListView, intent);
            // click event handler for the title, launches the app when the user clicks on title
            Intent titleIntent;
            if (isTablet) {
                titleIntent = new Intent(context, DisplayActivity.class);

            } else {
                titleIntent = new Intent(context, ListContentsActivity.class);

            }

            PendingIntent titlePendingIntent = PendingIntent.getActivity(context, 0, titleIntent, 0);
            rv.setOnClickPendingIntent(R.id.widgetTitle, titlePendingIntent);
            rv.setRemoteAdapter(R.id.widgetListView, intent);
            // template to handle the click listener for each item
            Intent clickIntentTemplate = new Intent(context, DisplayActivity.class);
            PendingIntent clickPendingIntentTemplate = TaskStackBuilder.create(context)
                    .addNextIntentWithParentStack(clickIntentTemplate)
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            rv.setPendingIntentTemplate(R.id.widgetListView, clickPendingIntentTemplate);
            ComponentName component = new ComponentName(context, WidgetProvider.class);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widgetListView);
            appWidgetManager.updateAppWidget(component, rv);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }


}