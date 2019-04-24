package com.niewall.nfccrypto

import android.app.AlertDialog
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater
import android.view.View
import android.widget.*

import kotlinx.android.synthetic.main.activity_write.*
import kotlinx.android.synthetic.main.dialog.view.*
import kotlinx.android.synthetic.main.content_write.*


class WriteActivity : AppCompatActivity() {
    private lateinit var mAlertDialog: AlertDialog
    private lateinit var mDialogView : View



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write)

        val address = findViewById<EditText>(R.id.addressText)
        val walletname = findViewById<EditText>(R.id.walletnameText)
        val privatekey = findViewById<EditText>(R.id.privatekeyText)
        val dropdown = findViewById<Spinner>(R.id.spinnerCoins)
        val writeBtn = findViewById<Button>(R.id.setForWrite)

        var items : Array<String> = arrayOf("zcash","bitcoin")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, items)
        dropdown.adapter = adapter


        writeBtn.setOnClickListener{

            var text = "${address.text}#${walletname.text}#${privatekey.text}#${dropdown.selectedItem.toString()}"

            ScanAddress.Info.nfctextToWrite = text
            ScanAddress.Info.nfcUse = "write"

            mDialogView = LayoutInflater.from(this).inflate(R.layout.dialog, null)
            //AlertDialogBuilder
            //Inflate the dialog with custom view
            val mBuilder = AlertDialog.Builder(this)
                .setView(mDialogView)
                .setTitle("Please tap Tag")
            //show dialog
            mAlertDialog = mBuilder.show()
            if(ScanAddress.Info.coin == "zcash"){
                mDialogView.image.setImageResource(R.drawable.zcash)}
            else{mDialogView.image.setImageResource(R.drawable.bitcoin)}

            mDialogView.txt.text = "waiting for tag"


            mDialogView.dialogButton.setOnClickListener {
                //dismiss dialog
                mAlertDialog.dismiss()
            }

        }

    }

}
