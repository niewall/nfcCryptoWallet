package com.niewall.nfccrypto

import android.content.Intent
import android.nfc.NfcAdapter
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.gson.GsonBuilder
import okhttp3.*
import java.io.IOException

class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



       // fetchJson("https://api.zcha.in/v2/mainnet/accounts/" )

        var buttonRead = findViewById<Button>(R.id.button)
        var buttonWrite = findViewById<Button>(R.id.button5)

        //fetchJson("https://api.zcha.in/v2/mainnet/accounts/t1NPgG69g3zuhCtnjQeJdCoWXzNAx1fQEzj")

        buttonRead.setOnClickListener {
            val intent = Intent(this@MainActivity, ScanAddress::class.java)
            startActivity(intent)
        }
        buttonWrite.setOnClickListener{
            val intent2 = Intent(this@MainActivity, WriteActivity::class.java)
            startActivity(intent2)

        }
    }


}








