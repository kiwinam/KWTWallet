package wallet.kiwinam.charlie.kwtwallet.wallet

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_wallet.*
import org.ethereum.geth.BigInt
import org.web3j.abi.FunctionEncoder
import org.web3j.abi.FunctionReturnDecoder
import org.web3j.abi.TypeReference
import org.web3j.abi.datatypes.Address
import org.web3j.abi.datatypes.Type
import org.web3j.abi.datatypes.generated.Uint256
import wallet.kiwinam.charlie.kwtwallet.R
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.core.methods.response.EthGetBalance
import org.web3j.protocol.http.HttpService
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.methods.request.Transaction
import org.web3j.protocol.core.methods.response.EthGetTransactionCount
import org.web3j.utils.Numeric
import wallet.kiwinam.charlie.kwtwallet.KeyStoreUtils
import wallet.kiwinam.charlie.kwtwallet.Web3Service
import wallet.kiwinam.charlie.kwtwallet.contract.KiwiTestToken
import java.math.BigDecimal
import java.math.BigInteger


class WalletActivity : AppCompatActivity() , View.OnClickListener {
    private var name = ""
    private var address = ""
    private lateinit var web3j : Web3j
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallet)

        if(intent.getBooleanExtra("isAutoRun",false)){  // 이미 지갑을 연적이 있다면
            name = getSharedPreferences("CurrentWallet", Context.MODE_PRIVATE).getString("name","none")
            address = getSharedPreferences("CurrentWallet", Context.MODE_PRIVATE).getString("address","none")
        }else{  // 새로 지갑을 생성한 경우
            name = intent.getStringExtra("name")
            address = intent.getStringExtra("address")
        }
        // 지갑의 정보를 가져온다.

        walletNameTv.text = name
        walletAddressTv.text = address

        setClickListeners()
    }

    override fun onResume() {
        super.onResume()
        getWalletInfo()
    }

    private fun setClickListeners(){
        walletMenuIv.setOnClickListener(this)
        walletNameEditIv.setOnClickListener(this)
        walletAddrLo.setOnClickListener(this)
        walletRefillTv.setOnClickListener(this)
        walletSendBtn.setOnClickListener(this)
        walletReceiveBtn.setOnClickListener(this)
    }


    override fun onClick(v: View?) {
        when(v){
            walletMenuIv->{ // 메뉴 오픈

            }
            walletNameEditIv->{ // 이름 변경

            }
            walletAddrLo->{ // 지갑 주소 복사
                val clipboardManager = applicationContext.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                val clipData = ClipData.newPlainText("label", address)
                clipboardManager.primaryClip = clipData
                Snackbar.make(walletActionLo, "클립 보드에 복사되었습니다.",Snackbar.LENGTH_SHORT).show()
                //Toast.makeText(context, context.getString(R.string.toast_text_clipboard_adress), Toast.LENGTH_SHORT).show()
            }
            walletRefillTv->{   // 충전하기

            }
            walletSendBtn->{    // 보내기

            }
            walletReceiveBtn->{ // 받기

            }
        }
    }

    private fun getWalletInfo(){
        // connect to node
        //val web3 = Web3j.build(HttpService())  // defaults to http://localhost:8545/

        // send asynchronous requests to get balance
        val ethGetBalance = Web3Service.getInstance()
                .ethGetBalance("0x$address", DefaultBlockParameterName.LATEST)
                .sendAsync()
                .get()

        val wei = ethGetBalance.balance

        val credential = KeyStoreUtils.getCredentials(address,applicationContext)

        web3j = Web3Service.getInstance()

        val gasPrice : BigInteger = BigInteger.valueOf(1)
        val gasLimit : BigInteger = BigInteger.valueOf(3000000)


        val token = KiwiTestToken.load(address,web3j,credential,gasPrice,gasLimit)
        //val kiwiToken : KiwiTestToken= KiwiTestToken(address,web3j,credential,gasPrice,gasLimit)

        Thread{
            Log.d("tokenOwner","0x$address")
            Log.d("web3j version",web3j.web3ClientVersion().toString()+"..")
            val a = token.balanceOf("0x$address").send()
            Log.e("a length",a.bitLength().toString()+"..")
            //val a = token.balanceOf("0x$address").sendAsync().get().toString()
            //val result:Type = kiwiToken.balanceOf("0x$address").send();
            Log.e("token balance","$a ..")
        }.start()


        Log.e("balance",wei.toString()+"..")


    }
   /* @Throws(Exception::class)
    private fun getBalance(wallet: Wallet, tokenInfo: TokenInfo): BigDecimal? {
        val function = balanceOf(wallet.address)
        val responseValue = callSmartContractFunction(function, tokenInfo.address, wallet)

        val response = FunctionReturnDecoder.decode(
                responseValue, function.outputParameters)
        return if (response.size == 1) {
            BigDecimal((response[0] as Uint256).value)
        } else {
            null
        }
    }

    private fun balanceOf(owner: String): org.web3j.abi.datatypes.Function {
        return org.web3j.abi.datatypes.Function(
                "balanceOf",
                listOf<Type>(Address(owner)),
                listOf<TypeReference<*>>(object : TypeReference<Uint256>() {

                }))
    }

    @Throws(Exception::class)
    private fun callSmartContractFunction(
            function: org.web3j.abi.datatypes.Function, contractAddress: String, wallet: Wallet): String {
        val encodedFunction = FunctionEncoder.encode(function)

        val response = web3j.ethCall(
                Transaction.createEthCallTransaction(wallet.address, contractAddress, encodedFunction),
                DefaultBlockParameterName.LATEST)
                .sendAsync().get()

        return response.value
    }*/
}