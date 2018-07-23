package wallet.kiwinam.charlie.kwtwallet

import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.bs_gen_result.*
import net.glxn.qrgen.android.QRCode


class GenResultBottomSheet : BottomSheetDialogFragment(){
    private var name : String = ""
    private var address : String = ""

    private lateinit var genFinishListener: GenFinishListener

    companion object {
        fun newInstance() : GenResultBottomSheet { return GenResultBottomSheet() }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bs_gen_result, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        genResultNameTv.text = name
        genResultAddressTv.text = address
        val addressQRCodeBitmap = QRCode.from("www.example.org").bitmap()
        genResultQRCodeIv.setImageBitmap(addressQRCodeBitmap)
        genResultConfirmBtn.setOnClickListener { dismiss() }
    }

    fun setKeyData(name : String, address : String, genFinishListener: GenFinishListener){
        this.name = name
        this.address = address
        this.genFinishListener = genFinishListener
    }

    override fun onDestroyView() {
        genFinishListener.isFinish()
        super.onDestroyView()
    }
}