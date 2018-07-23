package wallet.kiwinam.charlie.kwtwallet.wallet

import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import wallet.kiwinam.charlie.kwtwallet.R
import kotlinx.android.synthetic.main.bs_send.*

/**
 *
 */
class SendBottomSheet : BottomSheetDialogFragment() {
    private lateinit var sendCallback: SendCallback

    companion object {
        fun newInstance() : SendBottomSheet { return SendBottomSheet() }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bs_send, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sendCloseIv.setOnClickListener { dismiss() }    // 닫기 이미지 뷰 클릭 시 바텀 시트 종료
        sendBtn.setOnClickListener { send() }   // 보내기 버튼 클릭 시 토큰 전송을 요청하는 메소드를 호출한다.
    }

    private fun send(){
        if(sendTargetAddressEt.text.isNotEmpty()){  //  주소가 있는 경우
            sendTargetAddressEt.error = ""  // 에러 초기화
            sendCallback.sendTo(sendTargetAddressEt.text.toString(), Integer.valueOf(sendValueEt.text.toString()))
            dismiss()
        } else{ // 받는 사람 주소가 없다면
            sendTargetAddressEt.error = "받는 사람의 주소를 입력해주세요."    // 에러 표시
        }
    }
    fun setCallback(sendCallback : SendCallback){
        this.sendCallback = sendCallback
    }
}