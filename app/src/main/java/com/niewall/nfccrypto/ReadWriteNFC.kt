package com.niewall.nfccrypto

import android.os.Bundle
import android.support.v7.app.AppCompatActivity;
import kotlinx.android.synthetic.main.activity_read_write_nfc.*
import android.content.IntentFilter
import android.app.PendingIntent
import android.content.Context
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import android.widget.EditText
import java.io.IOException
import android.content.Intent
import android.annotation.SuppressLint
import android.nfc.*
import android.util.Log
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import kotlin.experimental.and
import android.nfc.NdefMessage
import android.nfc.tech.Ndef


class ReadWriteNFC : AppCompatActivity() {




    val ERROR_DETECTED = "No NFC tag detected!"
    val WRITE_SUCCESS = "Text written to the NFC tag successfully!"
    val WRITE_ERROR = "Error during writing, is the NFC tag close enough to your device?"
    var nfcAdapter: NfcAdapter? = null
    var pendingIntent: PendingIntent? = null
    var writeTagFilters: Array<IntentFilter>? = null
    var writeMode: Boolean = false
    lateinit var myTag: Tag
    var context: Context? = null

    lateinit var message : EditText


    @SuppressLint("SetTextI18n", "ServiceCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_read_write_nfc)
        setSupportActionBar(toolbar)

        myTag = if(NfcAdapter.ACTION_TAG_DISCOVERED == intent.action){
            intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
        }else{
            intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
        }


        if(ScanAddress.Info.nfcUse == ""){
            finish()
        }

        context = this
        val tvNFCContent =  findViewById<TextView>(R.id.nfc_contents)
        message =  findViewById(R.id.edit_message)
        val btnWrite =  findViewById<Button>(R.id.buttonWrite)

        when(ScanAddress.Info.nfcUse){
            "read" -> println("read")
            "write" -> {
                println("write")


                try {
                    if (myTag == null) {
                        println("nullllllll")
                        Toast.makeText(context, ERROR_DETECTED, Toast.LENGTH_LONG).show()
                    } else {
                        println("kein nulll")
                        //write(message.text.toString(), myTag!!)
                        println(myTag)

                        write(ScanAddress.Info.nfctextToWrite, myTag)
                        Toast.makeText(context, WRITE_SUCCESS, Toast.LENGTH_LONG).show()

                    }
                } catch (e: IOException) {
                    Toast.makeText(context, WRITE_ERROR, Toast.LENGTH_LONG).show()
                    e.printStackTrace()
                } catch (e: FormatException) {
                    Toast.makeText(context, WRITE_ERROR, Toast.LENGTH_LONG).show()
                    e.printStackTrace()
                }
                ScanAddress.Info.nfcUse = ""
                finish()
            }
            "" -> finish()
        }





        btnWrite.setOnClickListener{
                try {
                    if (myTag == null) {
                        Toast.makeText(context, ERROR_DETECTED, Toast.LENGTH_LONG).show()
                    } else {
                        //write(message.text.toString(), myTag!!)
                        println(myTag)
                        ScanAddress.Info.nfctextToWrite = message.text.toString()
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

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        if (nfcAdapter == null) {
            // Stop here, we definitely need NFC
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show()
            finish()
        }
        readFromIntent(intent)
        pendingIntent =
            PendingIntent.getActivity(this, 0, Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0)
        val tagDetected = IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT)
        writeTagFilters = arrayOf(tagDetected)

        tvNFCContent.text = ScanAddress.Info.nfcText

    }
    private fun readFromIntent(intent:Intent){
        if(ScanAddress.Info.nfcUse == "read"){
        val action = intent.action
        if (NfcAdapter.ACTION_TAG_DISCOVERED == action
            || NfcAdapter.ACTION_TECH_DISCOVERED == action
            || NfcAdapter.ACTION_NDEF_DISCOVERED == action
        ) {
            val rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
            var msgs: Array<NdefMessage?> = emptyArray()
            if (rawMsgs != null) {
                msgs = Array(rawMsgs.size, { null })
                for (i in rawMsgs.indices) {
                    msgs[i] = rawMsgs[i] as NdefMessage
                }
            }
            buildTagViews(msgs)
        }
    }}

    @SuppressLint("SetTextI18n")
    private fun buildTagViews(msgs: Array<NdefMessage?>) {
        if (msgs == null || msgs.isEmpty()) return

        var text = ""
        //        String tagId = new String(msgs[0].getRecords()[0].getType());
        val payload = msgs[0]!!.records[0].payload
        val textEncoding :Charset = if (payload[0].toInt() and 128 == 0) charset("UTF-8") else charset("UTF-16") // Get the Text Encoding
        val languageCodeLength = payload[0] and 51 // Get the Language Code, e.g. "en"
        // String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");

        try {
            // Get the Text
            text = String(payload, languageCodeLength + 1, payload.size - languageCodeLength - 1, textEncoding)
        } catch (e: UnsupportedEncodingException) {
            Log.e("UnsupportedEncoding", e.toString())
            println("Unsopported Encoding")
        }
        println(text)
        //tvNFCContent.text = "NFC Content: ${text.toString()}"
        var counter2 = 0
        var start = 0

        for(counter in 0 until text.length){
        if(text[counter] == '#' && counter2 == 0){
            ScanAddress.Info.nfcText = text.subSequence(0,counter).toString()
            start = counter+1
            counter2++
        }else if(text[counter] == '#' && counter2 ==1){
            ScanAddress.Info.nfcPass = text.subSequence(start,counter).toString()
            start = counter+1
            counter2++
        }else if(text[counter] == '#' && counter2 ==2){
            ScanAddress.Info.nfcPriKey = text.subSequence(start,counter).toString()
            start = counter+1
            counter2++
        }

        }

        ScanAddress.Info.nfcCoin = text.subSequence(start,text.length-1).toString() + text[text.length-1]

        println(ScanAddress.Info.nfcText)
        println(ScanAddress.Info.nfcPass)
        println(ScanAddress.Info.nfcPriKey)
        println(ScanAddress.Info.nfcCoin)

        //ScanAddress.Info.nfcText = text

        finish()

    }
    @Throws(IOException::class,FormatException::class)
    private fun write(text:String,tag:Tag){
    val records :NdefRecord = createRecord(text)
    val message = NdefMessage(records)
// Get an instance of Ndef for the tag.
        val ndef : Ndef = Ndef.get(tag)
        // Enable I/O
        ndef.connect()
        // Write the message
        ndef.writeNdefMessage(message)
        // Close the connection
        ndef.close()

    }
    @Throws(UnsupportedEncodingException::class)
    private fun createRecord(text: String): NdefRecord{
        val textBytes :ByteArray = text.toByteArray()
        val langBytes :ByteArray = text.toByteArray(charset("US-ASCII"))
        val langLength = langBytes.size
        val textLength = textBytes.size
        val payload = ByteArray(1 + langLength + textLength)

        // set status byte (see NDEF spec for actual bits)
        //payload[0] = langLength as Byte

        // copy langbytes and textbytes into payload
        System.arraycopy(langBytes, 0, payload, 1,langLength)
        System.arraycopy(textBytes, 0, payload, 1 + langLength, textLength)

        return NdefRecord(NdefRecord.TNF_WELL_KNOWN,  NdefRecord.RTD_TEXT, ByteArray(0) ,payload)

    }


    override fun onNewIntent (intent:Intent) {
        setIntent(intent)
        readFromIntent(intent)
        myTag = if(NfcAdapter.ACTION_TAG_DISCOVERED == intent.action){
            intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
        }else{
            intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
        }
    }
    override  fun onPause(){
        super.onPause()
        writeModeOff()
    }
    override fun onResume(){
        super.onResume()
        writeModeOn()
    }

    private fun writeModeOn(){
        writeMode = true
        nfcAdapter!!.enableForegroundDispatch(this,pendingIntent,writeTagFilters,null)
    }
    private fun writeModeOff(){
        writeMode = false
        nfcAdapter!!.disableForegroundDispatch(this)
    }


}
