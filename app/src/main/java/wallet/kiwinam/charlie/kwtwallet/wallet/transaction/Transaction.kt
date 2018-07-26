package wallet.kiwinam.charlie.kwtwallet.wallet.transaction

data class Transaction (val hash : String,
                        val from : String,
                        val to : String,
                        val Quantity : Long,
                        val state : Int     // 0 < 보내기, 1 < 받기 , 2 < 토큰 요청
                        )