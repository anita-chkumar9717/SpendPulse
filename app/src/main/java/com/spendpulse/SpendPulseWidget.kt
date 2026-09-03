package com.spendpulse
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
class SpendPulseWidget : AppWidgetProvider() {
    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) = WidgetUpdater.update(context)
    override fun onEnabled(context: Context) = WidgetUpdater.update(context)
}
