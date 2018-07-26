package wallet.kiwinam.charlie.kwtwallet.wallet

import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.bs_send.*
import wallet.kiwinam.charlie.kwtwallet.R

/**
 * 토큰 보내기 바텀 시트
 */
class SendBottomSheet : BottomSheetDialogFragment() , View.OnClickListener{
    private lateinit var sendCallback: SendCallback
    private var targetAddress = ""
    companion object {
        fun newInstance() : SendBottomSheet { return SendBottomSheet() }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bs_send, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sendTargetAddressEt.setText(targetAddress)
        Log.d("onViewCreated","targetAddress $targetAddress")
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

    fun setAddress(address : String){
        Log.d("bs address",address)
        targetAddress = address
    }

    // 클릭 이벤트 처리.
    override fun onClick(v: View?) {
        when(v){
            sendCloseIv->dismiss()  // 닫기 버튼
            sendBtn->send() // 보내기 버튼
            sendScanBtn->{  // 스캔 버튼
                dismiss()
                sendCallback.scan()
            }
            sendPasteBtn->{ // 붙여넣기 버튼

            }
        }
    }
}