package com.niewall.nfccrypto

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.nfc.NfcAdapter
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.google.gson.GsonBuilder
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.android.synthetic.main.activity_scann_adress.view.*
import kotlinx.android.synthetic.main.dialog.view.*
import okhttp3.*
import java.io.IOException
import java.lang.Exception
import java.security.AccessController.getContext
import java.util.concurrent.TimeUnit

object Info {
    var address = ""
    var balance = 0.0
    var sentCount = 0
    var recvCount = 0
    var zcashPrice = 0.0
    var bitcoinPrice = 0.0
    var QRscanned = ""
    var coin = "zcash"
    var nfcText = ""
    var nfcPass = "nichts"
    var nfcPriKey = "nichts"
    var nfcCoin = ""
    var nfctextToWrite = ""
    var nfcUse = ""


}


@SuppressLint("SetTextI18n")
class ScanAddress : AppCompatActivity() {

    private lateinit var mAlertDialog: AlertDialog
    private lateinit var mDialogView : View
    private lateinit var scanQRB :TextView
    private lateinit var eaddress :TextView
    private lateinit var textaddress :TextView
    private lateinit var balance :TextView
    private lateinit var readNFCButton:TextView
    private lateinit var balanceButton :TextView
    private lateinit var switchCoin :TextView
    private lateinit var coinImage : ImageView
    private lateinit var ivBarcode : ImageView






    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scann_adress)


        //______________ImageView__________________

        scanQRB = findViewById<Button>(R.id.button3)
        eaddress = findViewById<EditText>(R.id.eAdd)
        textaddress = findViewById(R.id.displayedName)
        balance = findViewById(R.id.balance)
        readNFCButton = findViewById<Button>(R.id.button2)
        balanceButton = findViewById<Button>(R.id.bButton)
        switchCoin = findViewById<Button>(R.id.SwitchBZ)
        coinImage = findViewById(R.id.imageView3)
        ivBarcode = findViewById(R.id.iv_barcode)
        val showPrivateKey = findViewById<Button>(R.id.getPrivateKey)
        val privateKeyText = findViewById<TextView>(R.id.editTextPrivateKEy)





        thread.start() //For updating every 10000 milliseconds

        //_____________NFC-Check____________________________
        val nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        println("NFC supported"+ (nfcAdapter != null).toString())
        println("NFC enabled" + (nfcAdapter?.isEnabled).toString())
        if (intent != null) {
            processIntent(intent)
        }


        eaddress.text = Info.QRscanned
        updateData()





        showPrivateKey.setOnClickListener {
            if(Info.nfcPriKey != "nichts") {

                if(showPrivateKey.text == "HIDE KEY"){
                    privateKeyText.text = ""
                    privateKeyText.hint = "*****"
                    showPrivateKey.text = "GET PRIVATE KEY"
                }else
                if(showPrivateKey.text == "GET PRIVATE KEY"){
                    privateKeyText.text = Info.nfcPriKey
                    showPrivateKey.text = "HIDE KEY"
                }

            }else{
                privateKeyText.hint = "no wallet scanned"
            }


        }

        /*
        writeToTagBtn.setOnClickListener{

            val intent = Intent(this@ScanAddress, WriteActivity::class.java)

            startActivity(intent)

        }
*/

        switchCoin.setOnClickListener {

            when (Info.coin) {
                "bitcoin" -> {Info.coin = "zcash"
                              coinImage.setImageResource(R.drawable.zcash)}
                "zcash" -> { Info.coin = "ethereum"
                             coinImage.setImageResource(R.drawable.ethereum)}
                "ethereum" -> { Info.coin = "bitcoin"
                                coinImage.setImageResource(R.drawable.bitcoin)}
            }


            Info.nfcUse = "read-fast" //Preis wird nicht neu gecheckt
            showBalance()
            switchCoin.text = Info.coin

        }

        balanceButton.setOnClickListener{

            Info.nfcUse="read"
            showBalance()
        }

        scanQRB.setOnClickListener{


            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                //ask for authorisation
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 50)
                //start your camera
            } else {

                eaddress.text = ""
                Info.QRscanned = ""
                val intent = Intent(this@ScanAddress, ScanQR::class.java)

                startActivity(intent)

                eaddress.text = Info.QRscanned

            }
        }



        readNFCButton.setOnClickListener {

            Info.nfcUse = "read"
            mDialogView = LayoutInflater.from(this).inflate(R.layout.dialog, null)
            //AlertDialogBuilder
            //Inflate the dialog with custom view
            val mBuilder = AlertDialog.Builder(this)
                .setView(mDialogView)
                .setTitle("Please tap Tag")
            //show dialog
            mAlertDialog = mBuilder.show()
            if(Info.coin == "zcash"){
                mDialogView.image.setImageResource(R.drawable.zcash)}
            else{mDialogView.image.setImageResource(R.drawable.bitcoin)}

            mDialogView.txt.text = "waiting for tag"


            mDialogView.dialogButton.setOnClickListener {
                //dismiss dialog
                mAlertDialog.dismiss()
            }
        }

        eaddress.hideKeyboard()
    }
    fun generateQRCode(){
        val tvAddress = findViewById<TextView>(R.id.TextAddresse)
        try{
            if(eaddress.text.toString() != "") {
                val encoder = BarcodeEncoder()
                var bitmap = encoder.encodeBitmap(eaddress.text.toString(), BarcodeFormat.QR_CODE, 500, 500)
                ivBarcode.setImageBitmap(bitmap)
                tvAddress.text = eaddress.text.toString()
            }
        }catch(e: Exception){
                e.printStackTrace()
        }



    }

    private fun updateData(){

        println("QR Scanned: " + Info.QRscanned)

        if (Info.QRscanned != ""){
            eaddress.text = Info.QRscanned
            Info.nfcUse="read"
            Info.nfcPass = Info.QRscanned
            showBalance()
            Info.QRscanned = ""
        }


        switchCoin.text = Info.coin
        when (Info.coin) {
            "bitcoin" -> {Info.coin = "bitcoin"
                coinImage.setImageResource(R.drawable.bitcoin)
                }
            "zcash" -> { Info.coin = "zcash"
                coinImage.setImageResource(R.drawable.zcash)}
            "ethereum" -> { Info.coin = "ethereum"
                coinImage.setImageResource(R.drawable.ethereum)}
        }
    }


    fun showBalance(){

        if(Info.nfcUse == "read" ||Info.nfcUse == "read-fast"){
        if (!eaddress.text.toString().contains(" ") && eaddress.text.toString() != "") {
            println(Info.QRscanned)
            val eURL = eaddress.text.toString()
            textaddress.text = eaddress.text



                getBalanceInfo(eURL.toString(), Info.coin)

            // balanceInfo("https://api.zcha.in/v2/mainnet/accounts/" + eURL)
            getPrice(Info.coin)

            if(Info.nfcUse == "read") {
                TimeUnit.MILLISECONDS.sleep(3000L)
            }
            val pbalance = "%.5f".format(Info.balance)
            Coins.bitcoin.usdWert = "%.2f".format(Coins.bitcoin.lastPrice * Coins.bitcoin.lastBalance)
            Coins.zcash.usdWert = "%.2f".format(Coins.zcash.lastPrice * Coins.zcash.lastBalance)
            Coins.ethereum.usdWert = "%.2f".format(Coins.ethereum.lastPrice * Coins.ethereum.lastBalance)



            // Displaying the Balance and Worth

            when (Info.coin) {
                "bitcoin" -> balance.text = "${Coins.bitcoin.lastBalance} (${Coins.bitcoin.usdWert} $)"
                "zcash" -> balance.text = "${Coins.zcash.lastBalance} (${Coins.zcash.usdWert} $)"
                "ethereum" -> balance.text = "${Coins.ethereum.lastBalance} (${Coins.ethereum.usdWert} $)"

            }


        }
            Info.nfcUse = ""
    }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        println("intent")
        if (intent != null) {
            processIntent(intent)
        }
    }



    private var thread: Thread = object : Thread() {

        @SuppressLint("SetTextI18n")
        override fun run() {
            try {
                while (!this.isInterrupted) {
                    Thread.sleep(1000)
                    runOnUiThread {
                        generateQRCode()
                        updateData()

                        if(Info.nfcText != ""){
                            Coins.methoden.setAddress(Info.nfcCoin,Info.nfcText)
                            eaddress.text = Info.nfcText
                            Info.coin = Info.nfcCoin
                            Info.nfcCoin = ""
                            showBalance()

                            try{
                                mAlertDialog.dismiss()
                            }catch(e:Exception){
                            }
                            Info.nfcText = ""
                        }

                        println("threadUpdate")
                    }
                }
            } catch (e: InterruptedException) {
            }

        }
    }

}

private fun processIntent(checkIntent: Intent) {
    // Check if intent has the action of a discovered NFC tag
    // with NDEF formatted contents
    if (checkIntent.action == NfcAdapter.ACTION_NDEF_DISCOVERED) {

        // Retrieve the raw NDEF message from the tag
        val rawMessages = checkIntent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
        println(rawMessages.toString())
        // ...

    }
}



class AddressInfo(var address:String,var balance:Double,var sentCount:Int, var recvCount:Int,var addrStr:String,var result:String)


fun getBalanceInfo(purl :String,pCoin:String){
    println("Fetch JSON Versuch")

    if(pCoin == "bitcoin" || pCoin == "zcash"|| pCoin == "ethereum"){
    var url = ""
    when(pCoin){
        "bitcoin" -> url = "https://blockexplorer.com/api/addr/$purl"
        "zcash" -> url = "https://api.zcha.in/v2/mainnet/accounts/$purl"
        "ethereum" -> url ="https://api.etherscan.io/api?module=account&action=balance&address=$purl"
    }




    val request = Request.Builder().url(url).build()
    val client = OkHttpClient()
    client.newCall(request).enqueue(object: Callback {
        override fun onResponse(call: Call, response: Response) {
            val body = response.body()?.string()
            println(body)
            if(!body.toString().contains(' ')&& body != ""){
            val gson = GsonBuilder().create()
            val addressInfo = gson.fromJson(body,AddressInfo::class.java)
                println(addressInfo)

            when(pCoin){
                "bitcoin" -> {  Coins.bitcoin.lastBalance = addressInfo.balance
                                Coins.bitcoin.address = addressInfo.addrStr}
                "zcash"   -> {  Coins.zcash.address = addressInfo.address
                                Coins.zcash.lastBalance = addressInfo.balance
                                Coins.zcash.recvCount = addressInfo.recvCount
                                Coins.zcash.sentCount = addressInfo.sentCount}
                "ethereum" -> { Coins.ethereum.lastBalance = addressInfo.result.toDouble()/1000000000000000000
                                Coins.ethereum.address = url}
            }

        }else{
                Coins.methoden.invalidAddress(pCoin)

            }
        }
        override fun onFailure(call: Call, e: IOException) {

        }
    })

    }else{
        println("getBalance failed")
        Coins.methoden.invalidAddress(pCoin)

    }
}
class Coin(var usd:Double)
class PriceInfo(var zcash:Coin,var bitcoin:Coin, var ethereum:Coin)
fun getPrice(pCoin:String){

    if(pCoin == "bitcoin" || pCoin == "zcash" || pCoin == "ethereum"){
    var url = ""

        when (pCoin) {
            "bitcoin" -> url = Coins.bitcoin.priceAddress
            "zcash" -> url = Coins.zcash.priceAddress
            "ethereum" -> url = Coins.ethereum.priceAddress
        }


    val request = Request.Builder().url(url).build()
    val client = OkHttpClient()
    client.newCall(request).enqueue(object: Callback {
        override fun onResponse(call: Call, response: Response) {
            val body = response.body()?.string()
            println(body)

            val gson = GsonBuilder().create()
            val pInfo = gson.fromJson(body,PriceInfo::class.java)

            if(pCoin == "bitcoin"){
                Coins.bitcoin.lastPrice = pInfo.bitcoin.usd}
            if(pCoin == "zcash"){
                Coins.zcash.lastPrice = pInfo.zcash.usd}
            if(pCoin == "ethereum"){
                Coins.ethereum.lastPrice = pInfo.ethereum.usd}

            println("zcash price:${Coins.bitcoin.lastPrice}, bitcoin price:${Coins.zcash.lastPrice}, balance: ${Coins.bitcoin.lastBalance},${Coins.zcash.lastBalance} ")
        }
        override fun onFailure(call: Call, e: IOException) {

        }
        })


}else{
        println("getPrice failed")
        Coins.methoden.invalidAddress(pCoin)

    }}




fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}


