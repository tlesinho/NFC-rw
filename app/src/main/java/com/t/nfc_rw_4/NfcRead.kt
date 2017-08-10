package com.t.nfc_rw_4

import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.nfc.*
import android.nfc.tech.Ndef
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.lang.StringBuilder
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import kotlin.experimental.and

/**
 * Created by user on 7/4/2017.
 */
class NfcRead {

    internal var nfcAdapter: NfcAdapter? = null
    lateinit var pendingIntent: PendingIntent


    var myTag: Tag? = null

    var tagDetected: IntentFilter? = null
    var text = ""
    lateinit var writeTagFilters: Array<IntentFilter?>

      //////////               /////////
     ////////// READ FROM NFC /////////
    //////////               /////////

     fun readFromIntent(intent: Intent) {
        val action = intent.action
        if (NfcAdapter.ACTION_TAG_DISCOVERED == action
                || NfcAdapter.ACTION_TECH_DISCOVERED == action
                || NfcAdapter.ACTION_NDEF_DISCOVERED == action) {
            val rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
            var msgs: Array<NdefMessage?>? = null

            if (rawMsgs != null) {
                msgs = arrayOfNulls<NdefMessage>(rawMsgs.size)
                for (i in rawMsgs.indices) {
                    msgs[i] = rawMsgs[i] as NdefMessage
                }
            }
            buildTagViews(msgs)
        }
    }
    private fun buildTagViews(msgs: Array<NdefMessage?>?):String? {
        if (msgs == null || msgs.isEmpty()) return null


        //        String tagId = new String(msgs[0].getRecords()[0].getType());
        val payload = msgs[0]!!.records[0].payload

        //need this?
        val textEncoding: Charset = if (payload[0] and 128.toByte() == 0.toByte()) Charsets.UTF_8 else StandardCharsets.UTF_16 // Get the Text Encoding


        val languageCodeLength = payload[0] and 51.toByte() // Get the Language Code, e.g. "en"
        // String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
//        var a=kotlin.text.String(myTag!!.getId())
//
//
//        a=kotlin.text.String(myTag!!.id,0,myTag!!.getId().size, US_ASCII)


        try {
            // Get the Text
//            text = kotlin.text.String(payload, languageCodeLength + 1, payload.size - languageCodeLength - 1, textEncoding)
            text = kotlin.text.String(payload, 2 + 1, payload.size - 2 - 1, StandardCharsets.US_ASCII)
//                text=text.takeLast(payload.size-languageCodeLength-1)

        } catch (e: UnsupportedEncodingException) {
            Log.e("UnsupportedEncoding", e.toString())
        }

//        tvNFCContent.text = "Read content: " + text
        return text
    }

//     fun onNewIntent(intent: Intent) {
//        setIntent(intent)
//        readFromIntent(intent)
//        if (NfcAdapter.ACTION_TAG_DISCOVERED == intent.action) {
//            myTag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
////            Toast.makeText(this, byteArrayToHex(myTag?.id), Toast.LENGTH_LONG).show()
//        }
//    }
      //////////                        /////////
     //////////   FOREGROUND DISPATCH  /////////
    //////////                        /////////

    fun assignIntents (context: Context, pendingIntent: PendingIntent) {
//        pendingIntent = PendingIntent.getActivity(context, 0, Intent(context, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0)
        this.pendingIntent=pendingIntent
        tagDetected = IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)
        tagDetected?.addCategory(Intent.CATEGORY_DEFAULT)
        writeTagFilters = arrayOf(tagDetected)
    }

     fun WriteModeOn(activity: Activity) {

        nfcAdapter?.enableForegroundDispatch(activity, pendingIntent, writeTagFilters, null)
    }

     fun WriteModeOff(activity: Activity) {

        nfcAdapter?.disableForegroundDispatch(activity)
    }

    fun getNfc (nfcAdapter: NfcAdapter) {
        this.nfcAdapter=nfcAdapter
    }

    fun byteArrayToHex(a: ByteArray?): String {
        val sb = StringBuilder(a!!.size * 2)
        for (b in a)
            sb.append(String.format("%02x", b))
        return sb.toString()
    }


}