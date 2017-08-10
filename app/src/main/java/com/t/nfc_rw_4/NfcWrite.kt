package com.t.nfc_rw_4

import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.nfc.*
import android.nfc.tech.Ndef
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.lang.StringBuilder

/**
 * Created by user on 7/21/2017.
 */
class NfcWrite {
    val ERROR_DETECTED = "No NFC tag detected!"
    val WRITE_SUCCESS = "Text written to the NFC tag successfully!"
    val WRITE_ERROR = "Error during writing, is the NFC tag close enough to your device?"

    internal var nfcAdapter: NfcAdapter? = null
    lateinit var pendingIntent: PendingIntent
    lateinit var writeTagFilters: Array<IntentFilter?>

    var myTag: Tag? = null

    var tagDetected: IntentFilter? = null

    fun assignIntents(context: Context, pendingIntent: PendingIntent) {
//        pendingIntent = PendingIntent.getActivity(context, 0, Intent(context, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0)
        this.pendingIntent = pendingIntent
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

    fun getNfc(nfcAdapter: NfcAdapter) {
        this.nfcAdapter = nfcAdapter
    }

    fun byteArrayToHex(a: ByteArray?): String {
        val sb = StringBuilder(a!!.size * 2)
        for (b in a)
            sb.append(String.format("%02x", b))
        return sb.toString()
    }

    @Throws(IOException::class, FormatException::class)
    private fun write(text: String, tag: Tag?) {
        val records = arrayOf<NdefRecord>(createRecord(text))
        val message = NdefMessage(records)
        // Get an instance of Ndef for the tag.
        val ndef = Ndef.get(tag)
        // Enable I/O
        ndef.connect()
        // Write the message
        ndef.writeNdefMessage(message)
        // Close the connection
        ndef.close()
    }

    @Throws(UnsupportedEncodingException::class)
    private fun createRecord(text: String): NdefRecord {
        val lang = "en"
        val textBytes = text.toByteArray()
        val langBytes = lang.toByteArray(charset("US-ASCII"))
        val langLength = langBytes.size
        val textLength = textBytes.size
        val payload = ByteArray(1 + langLength + textLength)

        // set status byte (see NDEF spec for actual bits)
        payload[0] = langLength.toByte()

        // copy langbytes and textbytes into payload
        System.arraycopy(langBytes, 0, payload, 1, langLength)
        System.arraycopy(textBytes, 0, payload, 1 + langLength, textLength)

        val recordNFC = NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, ByteArray(0), payload)

        return recordNFC
    }

    fun writeMessage(context: Context, button: Button, textView: TextView) {
        button.setOnClickListener {
            try {
                if (myTag == null) {
                    Toast.makeText(context, ERROR_DETECTED, Toast.LENGTH_LONG).show()
                } else {
                    write(textView.text.toString(), myTag)

                    Toast.makeText(context, WRITE_SUCCESS, Toast.LENGTH_LONG).show()
                }
            } catch (e: IOException) {
                Toast.makeText(context, WRITE_ERROR, Toast.LENGTH_LONG).show()
                e.printStackTrace()
            } catch (e: FormatException) {
                Toast.makeText(context, WRITE_ERROR, Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        }
    }
}