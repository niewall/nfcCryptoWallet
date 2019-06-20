package com.niewall.nfccrypto

class Coins() {


    object methoden {
        fun invalidAddress(kürzel: String) {
            when (kürzel) {
                "bitcoin" -> {
                    bitcoin.lastBalance = 0.0
                    bitcoin.address = "invalid Address"
                }
                "zcash" -> {
                    zcash.lastBalance = 0.0
                    zcash.address = "invalid Address"
                }
            }
        }

        fun setAddress(kürzel:String, address:String){
            when (kürzel) {
                "bitcoin" -> {
                    bitcoin.address = address
                }
                "zcash" -> {
                    zcash.address = address
                }
            }

        }

    }



    object bitcoin{

        var kürzel = "bitcoin"
        var priceAddress = "https://api.coingecko.com/api/v3/simple/price?ids=bitcoin&vs_currencies=USD"
        var lastPrice:Double = 0.0
        var lastBalance:Double = 0.0
        var usdWert = ""
        var address = ""
        var privateKey = ""

    }

    object zcash{

        var kürzel = "zcash"
        var priceAddress = "https://api.coingecko.com/api/v3/simple/price?ids=zcash&vs_currencies=USD"
        var lastPrice:Double = 0.0
        var lastBalance:Double = 0.0
        var usdWert = ""
        var address = ""
        var privateKey = ""
        var recvCount = 0
        var sentCount = 0

    }

    object ethereum{

        var kürzel = "ethereum"
        var priceAddress = "https://api.coingecko.com/api/v3/simple/price?ids=ethereum&vs_currencies=USD"
        var lastPrice:Double = 0.0
        var lastBalance:Double = 0.0
        var usdWert = ""
        var address = ""
        var privateKey = ""

    }






}
