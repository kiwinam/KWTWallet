package wallet.kiwinam.charlie.kwtwallet.wallet

import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import wallet.kiwinam.charlie.kwtwallet.R
import kotlinx.android.synthetic.main.bs_receive.*
import net.glxn.qrgen.android.QRCode

class ReceiveBottomSheet : BottomSheetDialogFragment() {
    private var address = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bs_receive, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        receiveAddressTv.text = address
        val addressQRCodeBitmap = QRCode.from(address).bitmap()
        receiveQRCodeIv.setImageBitmap(addressQRCodeBitmap)

        receiveCloseIv.setOnClickListener { dismiss() }

    }

    fun setAddress(address : String){
        this.address = address
    }
}