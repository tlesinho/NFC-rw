package com.t.nfc_rw_4

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    lateinit var nfcReadText: TextView
    lateinit var nfcRead: NfcRead
    lateinit var nfcWrite: NfcWrite
    lateinit var nfcAdapter: NfcAdapter
    lateinit var message:TextView
    lateinit var wrtButton:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        message=findViewById(R.id.editText) as TextView
        wrtButton=findViewById(R.id.button) as Button
        nfcReadText = findViewById(R.id.nfcRead) as TextView

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        nfcRead = NfcRead()
        nfcWrite = NfcWrite()

        nfcRead.getNfc(nfcAdapter)
        nfcWrite.getNfc(nfcAdapter)

        assignIntentRead(this, nfcRead)
        assignIntentWrite(this, nfcWrite)

        nfcRead.readFromIntent(intent)

        nfcReadText.text = nfcRead.text

        nfcWrite.writeMessage(this, wrtButton, message)
    }

    override fun onResume() {
        super.onResume()
        nfcWrite.WriteModeOn(this)
        nfcRead.WriteModeOn(this)
    }

    override fun onPause() {
        super.onPause()
        nfcRead.WriteModeOff(this)
        nfcWrite.WriteModeOff(this)
    }

    override fun onNewIntent(intent: Intent) {

        setIntent(intent)
        nfcRead.readFromIntent(intent)
        nfcReadText.text = nfcRead.text

        if (NfcAdapter.ACTION_TAG_DISCOVERED == intent.action) {
            nfcRead.myTag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
            nfcWrite.myTag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
            Toast.makeText(this, nfcRead.byteArrayToHex(nfcRead.myTag?.id), Toast.LENGTH_LONG).show()
        }
    }

    fun assignIntentRead(context: Context, nfcRead: NfcRead) {
        val pendingIntent = PendingIntent.getActivity(context, 0, Intent(context, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0)
        nfcRead.assignIntents(this, pendingIntent)
    }
    fun assignIntentWrite(context: Context, nfcWrite: NfcWrite) {
        val pendingIntent = PendingIntent.getActivity(context, 0, Intent(context, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0)
        nfcWrite.assignIntents(this, pendingIntent)
    }
}
