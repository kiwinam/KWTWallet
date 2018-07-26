package wallet.kiwinam.charlie.kwtwallet.wallet

interface SendCallback {
    public fun sendTo(address : String, value : Int)
    fun scan()
}