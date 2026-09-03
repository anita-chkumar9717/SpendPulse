package com.spendpulse

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import java.security.MessageDigest

class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return
        val store = SmsStore(context.applicationContext)
        for (m in Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
            val body = m.messageBody ?: continue; val parsed = TransactionParser.parse(body) ?: continue
            val fp = sha256("${m.originatingAddress.orEmpty()}|${m.timestampMillis}|$body")
            store.insertIfNew(fp, parsed.amountPaise, m.timestampMillis)
        }
        WidgetUpdater.update(context.applicationContext)
    }
    private fun sha256(v: String) = MessageDigest.getInstance("SHA-256").digest(v.toByteArray()).joinToString("") { "%02x".format(it) }
}
