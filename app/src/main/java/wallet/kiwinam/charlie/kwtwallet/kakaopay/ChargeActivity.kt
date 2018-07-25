package wallet.kiwinam.charlie.kwtwallet.kakaopay

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import wallet.kiwinam.charlie.kwtwallet.R
import kotlinx.android.synthetic.main.activity_charge.*
import org.web3j.protocol.Web3j
import wallet.kiwinam.charlie.kwtwallet.Web3jService
import wallet.kiwinam.charlie.kwtwallet.wallet.WalletUtils
import java.text.DecimalFormat

/**
 * 토큰 충전하는 Activity
 */
const val KAKAO_RESULT = 3000
class ChargeActivity : AppCompatActivity() {
    private val decimalFormat = DecimalFormat("#,##0")
    private var value = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_charge)

        init()  // 초기 값 설정
        setListeners()  // 리스너 설정
    }

    /*
     * 초기 데이터 설정
     */
    private fun init(){
        chargeCurrentValueTv.text = intent.getStringExtra("token")  // 현재 토큰 개수를 인텐트에서 전달받아 표시한다.
    }

    /*
     * 리스너 설정
     */
    private fun setListeners(){
        // 닫기 버튼을 눌렀을 때 Activity 를 종료한다.
        chargeBackIv.setOnClickListener {
            //startActivity(Intent(applicationContext, WalletActivity::class.java))
            finish()
        }
        // 구매하기 버튼
        chargeSubmitBtn.setOnClickListener {
            val kakaoIntent = Intent(applicationContext, KakaoWebActivity::class.java)
            kakaoIntent.putExtra("value",value.toInt())
            startActivityForResult(kakaoIntent, KAKAO_RESULT)
        }
        // 토큰 수량 변화시 원화의 값과 자리수에 맞는 콤마를 표시한다.
        chargeValueEt.addTextChangedListener(object  : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                if(s!!.isNotEmpty()){
                    value = s.toString().toLong()
                    chargeWonTv.text = decimalFormat.format(value)
                }
                else{
                    value = 0L
                    chargeWonTv.text = "0"
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    /*
     * KakaoWebActivity 의 결과를 받는 메소드
     *
     * 웹 뷰를 통해 카카오페이 결제 요청을 하고, 리턴된 결과를 처리한다.
     * 요청이 성공했으면 토큰을 요청하고, 요청이 실패 했으면 실패했다는 토스트 메시지를 띄워준다.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //super.onActivityResult(requestCode, resultCode, data)
        val resultIntent = Intent()
        if(requestCode == KAKAO_RESULT){    // 카카오페이 결제 요청 결과

            if(resultCode == Activity.RESULT_OK){   // 결제가 성공한 경우
                Log.d("kakao","ok")
                resultIntent.putExtra("value",value.toInt())
                setResult(Activity.RESULT_OK,resultIntent)
                finish()
            } else {  // 결제가 실패한 경우
                Log.d("kakao","cancel")
                //setResult(Activity.RESULT_CANCELED,resultIntent)
                Toast.makeText(applicationContext,"결제를 취소하였습니다.",Toast.LENGTH_SHORT).show()
            }
        }

    }
}