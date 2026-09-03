package com.spendpulse

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Telephony
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.security.MessageDigest

class MainActivity : AppCompatActivity() {
    private lateinit var permissionText: TextView; private lateinit var totalText: TextView; private lateinit var statusText: TextView
    companion object { private const val REQ = 1001 }
    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); setContentView(R.layout.activity_main)
        permissionText=findViewById(R.id.permissionText); totalText=findViewById(R.id.totalText); statusText=findViewById(R.id.statusText)
        findViewById<Button>(R.id.permissionButton).setOnClickListener { requestPermissions() }
        findViewById<Button>(R.id.scanButton).setOnClickListener { scanExistingSms() }; refresh()
    }
    override fun onResume(){super.onResume();refresh()}
    private fun allowed() = ContextCompat.checkSelfPermission(this,Manifest.permission.READ_SMS)==PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,Manifest.permission.RECEIVE_SMS)==PackageManager.PERMISSION_GRANTED
    private fun requestPermissions(){ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.READ_SMS,Manifest.permission.RECEIVE_SMS),REQ)}
    private fun scanExistingSms(){ if(!allowed()){statusText.text="Grant SMS access first.";requestPermissions();return}; val store=SmsStore(this);var count=0
        val p=arrayOf(Telephony.Sms._ID,Telephony.Sms.ADDRESS,Telephony.Sms.DATE,Telephony.Sms.BODY)
        contentResolver.query(Telephony.Sms.Inbox.CONTENT_URI,p,null,null,Telephony.Sms.DATE+" DESC")?.use { c ->
            val id=c.getColumnIndexOrThrow(Telephony.Sms._ID);val addr=c.getColumnIndexOrThrow(Telephony.Sms.ADDRESS);val date=c.getColumnIndexOrThrow(Telephony.Sms.DATE);val body=c.getColumnIndexOrThrow(Telephony.Sms.BODY)
            while(c.moveToNext()){val i=c.getString(id);val a=c.getString(addr).orEmpty();val d=c.getLong(date);val b=c.getString(body).orEmpty();val x=TransactionParser.parse(b)?:continue;val fp=sha256("$i|$a|$d|$b");if(store.insertIfNew(fp,x.amountPaise,d))count++}
        };WidgetUpdater.update(this);statusText.text="Scan complete. $count new debit transaction(s) detected.";refresh()
    }
    private fun refresh(){permissionText.text=if(allowed())"SMS access granted. Debit SMS messages will be processed automatically." else "SMS access is required to detect debit messages.";val t=SmsStore(this).currentMonthTotalPaise();val r=t/100;val p=t%100;totalText.text="₹"+"%,d".format(r)+if(p==0L)"" else ".%02d".format(p)}
    private fun sha256(v:String)=MessageDigest.getInstance("SHA-256").digest(v.toByteArray()).joinToString(""){ "%02x".format(it)}
}
