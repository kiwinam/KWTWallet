package wallet.kiwinam.charlie.kwtwallet

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_gen_wallet.*
import org.web3j.crypto.Keys
import org.web3j.crypto.ECKeyPair
import org.web3j.crypto.WalletUtils
import org.web3j.utils.Numeric
import wallet.kiwinam.charlie.kwtwallet.db.KeyDBHelper
import wallet.kiwinam.charlie.kwtwallet.wallet.WalletActivity
import wallet.kiwinam.charlie.kwtwallet.wallet.WalletActivityJava
import java.io.File

/**
 * 토큰 지갑 생성하는 액티비티
 *
 * 토큰 생성 요청시 비밀번호를 입력하게 한다.
 * 디바이스에 키 파일을 생성한다. 생성된 파일의 이름 중 -- 뒤에 있는 주소를 SharedPreference 와 SQLite DB 에 저장한다.
 */
class GenWalletActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var keyDBHelper: KeyDBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gen_wallet)

        genWalletBackIv.setOnClickListener(this)
        genWalletSubmitBtn.setOnClickListener(this)
        keyDBHelper = KeyDBHelper(applicationContext,"keyList",null,1)
        keyDBHelper.keyDB()
    }
    override fun onClick(v: View?) {
        when(v){
            genWalletBackIv->onBackPressed()    // 뒤로가기
            genWalletSubmitBtn->genWallet()     // 지갑 생성
        }
    }

    // 지갑 생성
    private fun genWallet() {
        if (genWalletPwEt.length() == 0) {
            genWalletPwEt.error = "비밀번호를 입력해주세요"
            return
        }
        val password = genWalletPwEt.text.toString()
        try {
            val fileDir = File(Environment.getExternalStorageDirectory().path + "/LightWallet")
            if (!fileDir.exists()) {
                fileDir.mkdir()
            }
            val ecKeyPair : ECKeyPair = Keys.createEcKeyPair()

            // 카드 생성
            val fileName = WalletUtils.generateWalletFile(password, ecKeyPair, fileDir, false)  // 키 파일 생성

            val address = KeyStoreUtils.genKeyStoreToFiles(ecKeyPair,applicationContext)   // 지갑 주소를 가져온다.

            val privateKey = Numeric.encodeQuantity(ecKeyPair.privateKey)
            val publicKey = Numeric.encodeQuantity(ecKeyPair.publicKey)

            Log.d("fileName",fileName)
            Log.d("address",address)
            Log.d("privateKey",privateKey)
            Log.d("publicKey",publicKey)


            // 현재 등록한 지갑의 정보를 SP 에 등록한다.
            // To do
            val lastSp = getSharedPreferences("lastNameNum", Context.MODE_PRIVATE)
            val lastNum = lastSp.getInt("lastNum",1)

            val walletName = "Wallet $lastNum"

            // 현재 지갑 저장
            val currentSp = getSharedPreferences("CurrentWallet", Context.MODE_PRIVATE)
            var editor = currentSp.edit()
            editor.putString("name",walletName)
            editor.putString("address",address)
            editor.putString("fileName",fileName)
            editor.apply()

            // 마지막 지갑 번호 상승
            editor = lastSp.edit()
            editor.putInt("lastNum",(lastNum+1))
            editor.apply()

            // 현재 등록한 지갑의 정보를 데이터 베이스에 저장한다.
            if(keyDBHelper.insertNewKey(walletName,address)){
                Log.d("Save Wallet on DB", "OK")
            }else{
                Log.d("Save Wallet on DB", "NO")
            }

            // 결과를 바텀 시트로 보여준다.
            val resultBottomSheet = GenResultBottomSheet.newInstance()
            resultBottomSheet.setKeyData(walletName,address,object : GenFinishListener{
                // 바텀 시트 종료 후 지갑 액티비티로 이동한다.
                override fun isFinish() {
                    val intent = Intent(applicationContext, WalletActivityJava::class.java)
                    intent.putExtra("isAutoRun",false)
                    intent.putExtra("name",walletName)
                    intent.putExtra("address",address)

                    startActivity(intent)
                    finish()
                }
            })
            resultBottomSheet.show(supportFragmentManager, "resultBs")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}