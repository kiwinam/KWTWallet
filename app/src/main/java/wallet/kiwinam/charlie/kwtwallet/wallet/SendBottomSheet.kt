package wallet.kiwinam.charlie.kwtwallet.wallet

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.zxing.integration.android.IntentIntegrator
import wallet.kiwinam.charlie.kwtwallet.R
import kotlinx.android.synthetic.main.bs_send.*
import wallet.kiwinam.charlie.kwtwallet.R.id.sendTargetAddressEt
import wallet.kiwinam.charlie.kwtwallet.R.id.sendValueEt

/**
 * 토큰 보내기 바텀 시트
 */
class SendBottomSheet : BottomSheetDialogFragment() , View.OnClickListener{
    private lateinit var sendCallback: SendCallback

    companion object {
        fun newInstance() : SendBottomSheet { return SendBottomSheet() }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bs_send, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setOnClickListeners()
    }

    private fun setOnClickListeners(){
        sendCloseIv.setOnClickListener(this)    // 닫기 이미지 뷰 클릭 시 바텀 시트 종료
        sendBtn.setOnClickListener(this)   // 보내기 버튼 클릭 시 토큰 전송을 요청하는 메소드를 호출한다.
        sendScanBtn.setOnClickListener(this)
        sendPasteBtn.setOnClickListener(this)
    }

    // 보내기 버튼
    private fun send(){
        if(sendTargetAddressEt.text.isNotEmpty()){  //  주소가 있는 경우
            if(sendValueEt.text.isNotEmpty()){
                sendTargetAddressEt.error = ""  // 에러 초기화
                sendValueEt.error = ""
                // 콜백 메소드를 통해 지갑 액티비티에 주소와 금액을 전달한다.
                sendCallback.sendTo(sendTargetAddressEt.text.toString(), Integer.valueOf(sendValueEt.text.toString()))
                dismiss()   // 바텀시트 종료
            }else{
                sendValueEt.error = "보낼 금액을 입력해주세요."
            }

        } else{ // 받는 사람 주소가 없다면
            sendTargetAddressEt.error = "받는 사람의 주소를 입력해주세요."    // 에러 표시
        }
    }

    /*
     * 콜백 메소드 설정
     */
    fun setCallback(sendCallback : SendCallback){
        this.sendCallback = sendCallback
    }

    // 클릭 이벤트 처리.
    override fun onClick(v: View?) {
        when(v){
            sendCloseIv->dismiss()  // 닫기 버튼
            sendBtn->send() // 보내기 버튼
            sendScanBtn->{  // 스캔 버튼
                val integrator = IntentIntegrator.forSupportFragment(this)
                integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
                integrator.setPrompt("Scan")
                integrator.setCameraId(0)
                integrator.setBeepEnabled(false)
                integrator.setBarcodeImageEnabled(false)
                integrator.setOrientationLocked(true)

                integrator.initiateScan()
            }
            sendPasteBtn->{ // 붙여넣기 버튼

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data)
        if(result != null){
            if(result.contents == null){
                Log.d("QR Scan","cancelled scan")
            }else{
                Log.d("QR Scan","Scanned")
                Log.d("QR Result", result.contents+"..")
            }
        }else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}