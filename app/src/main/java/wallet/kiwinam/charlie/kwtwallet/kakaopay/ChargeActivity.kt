package wallet.kiwinam.charlie.kwtwallet.kakaopay

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import wallet.kiwinam.charlie.kwtwallet.R
import kotlinx.android.synthetic.main.activity_charge.*
import wallet.kiwinam.charlie.kwtwallet.wallet.WalletActivityJava
import java.text.DecimalFormat

/**
 * 토큰 충전하는 Activity
 */
class ChargeActivity : AppCompatActivity() {
    private val decimalFormat = DecimalFormat("#,##0")
    private var value = 0L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_charge)

        chargeCurrentValueTv.text = intent.getStringExtra("token")  // 현재 토큰 개수를 인텐트에서 전달받아 표시한다.

        // 닫기 버튼을 눌렀을 때 Activity 를 종료한다.
        chargeBackIv.setOnClickListener {
            //startActivity(Intent(applicationContext, WalletActivityJava::class.java))
            finish()
        }
        // 구매하기 버튼
        chargeSubmitBtn.setOnClickListener {

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
}