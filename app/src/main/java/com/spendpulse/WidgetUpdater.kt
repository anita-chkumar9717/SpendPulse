package com.spendpulse

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.widget.RemoteViews
import java.text.NumberFormat
import java.util.Locale

object WidgetUpdater {
    fun update(context: Context) {
        val total = SmsStore(context).currentMonthTotalPaise(); val rupees = total / 100; val paise = total % 100
        val text = NumberFormat.getIntegerInstance(Locale("en","IN")).format(rupees) + if (paise == 0L) "" else ".%02d".format(paise)
        val views = RemoteViews(context.packageName, R.layout.widget_spendpulse); views.setTextViewText(R.id.widgetTotal,"₹$text")
        val manager = AppWidgetManager.getInstance(context); manager.updateAppWidget(ComponentName(context, SpendPulseWidget::class.java), views)
    }
}
