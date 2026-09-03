package com.spendpulse

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.time.YearMonth
import java.time.ZoneId

class SmsStore(context: Context) : SQLiteOpenHelper(context, "spendpulse.db", null, 1) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE transactions (id INTEGER PRIMARY KEY AUTOINCREMENT, fingerprint TEXT NOT NULL UNIQUE, amount_paise INTEGER NOT NULL, sms_time_ms INTEGER NOT NULL, created_at_ms INTEGER NOT NULL)")
        db.execSQL("CREATE INDEX idx_transactions_time ON transactions(sms_time_ms)")
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}
    fun insertIfNew(fingerprint: String, amountPaise: Long, smsTimeMs: Long): Boolean {
        val v = ContentValues().apply { put("fingerprint", fingerprint); put("amount_paise", amountPaise); put("sms_time_ms", smsTimeMs); put("created_at_ms", System.currentTimeMillis()) }
        return writableDatabase.insertWithOnConflict("transactions", null, v, SQLiteDatabase.CONFLICT_IGNORE) != -1L
    }
    fun currentMonthTotalPaise(): Long {
        val zone = ZoneId.systemDefault(); val month = YearMonth.now(zone)
        val start = month.atDay(1).atStartOfDay(zone).toInstant().toEpochMilli(); val end = month.plusMonths(1).atDay(1).atStartOfDay(zone).toInstant().toEpochMilli()
        readableDatabase.rawQuery("SELECT COALESCE(SUM(amount_paise),0) FROM transactions WHERE sms_time_ms >= ? AND sms_time_ms < ?", arrayOf(start.toString(),end.toString())).use { c -> return if (c.moveToFirst()) c.getLong(0) else 0L }
    }
}
