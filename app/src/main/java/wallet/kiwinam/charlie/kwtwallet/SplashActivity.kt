package wallet.kiwinam.charlie.kwtwallet

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import wallet.kiwinam.charlie.kwtwallet.wallet.WalletActivity
import wallet.kiwinam.charlie.kwtwallet.wallet.WalletActivityJava

class SplashActivity : AppCompatActivity() {
    private var mDelayHandler : Handler? = null
    private val splashDelay: Long = 1500
    private val mRunnable : Runnable = Runnable {
        val prefs : SharedPreferences? = getSharedPreferences("CurrentWallet", Context.MODE_PRIVATE)

        val nextIntent =
                if(prefs!!.getString("name","none") != "none"){
                    //Intent(applicationContext, WalletActivity::class.java)
                    Intent(applicationContext, WalletActivityJava::class.java)
                }else{
                    Intent(applicationContext, MainActivity::class.java)
                }
        if(prefs.getString("name","none") != "none"){
            nextIntent.putExtra("isAutoRun",true)
        }
        startActivity(nextIntent)
        finish()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        mDelayHandler = Handler() // 핸들러 초기화
        mDelayHandler!!.postDelayed(mRunnable, splashDelay) // 3초 만큼의 지연 후 mRunnable 실행
    }
    override fun onDestroy() {
        if (mDelayHandler != null){
            mDelayHandler!!.removeCallbacks(mRunnable)
        }
        super.onDestroy()
    }
}