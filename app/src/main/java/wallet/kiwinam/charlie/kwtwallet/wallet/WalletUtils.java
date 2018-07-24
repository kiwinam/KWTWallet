package wallet.kiwinam.charlie.kwtwallet.wallet;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.tuyenmonkey.mkloader.MKLoader;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import wallet.kiwinam.charlie.kwtwallet.KeyStoreUtils;
import wallet.kiwinam.charlie.kwtwallet.contract.KiwiTestToken;

public class WalletUtils {
    private Web3j web3j;
    private Context context;
    private String address;
    private KiwiTestToken kiwiTestToken;
    private Application application;
    public WalletUtils(String address,Web3j web3j, Context context){
        this.web3j = web3j;
        this.context = context;
        this.address = address;
    }

    @SuppressLint("CheckResult")
    public void initWallet(){
        /*if(web3j == null){
            web3j = Web3jService.getInstance();
        }*/
        try{
            Observable.create((ObservableOnSubscribe<KiwiTestToken>) emitter -> {

                KiwiTestToken kiwiToken = new KiwiTestToken(KiwiTestToken.CONTRACT_ADDRESS,
                        web3j,
                        KeyStoreUtils.getCredentials(address,context),
                        //BigInteger.valueOf(41000000000L),
                        BigInteger.valueOf(7),          // Gas price
                        BigInteger.valueOf(300000));     // Gas limit
                emitter.onNext(kiwiToken);
                emitter.onComplete();

            })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(kiwiToken -> {
                        kiwiTestToken = kiwiToken;
                        Log.d("kiwi token gas price",kiwiToken.getGasPrice()+"..");
                        Log.d("Contract","OK");
                    }, Throwable::printStackTrace);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /*
     * 토큰 오너에게 토큰을 요청하는 메소드
     */
    @SuppressLint("CheckResult")
    public boolean requestToken(int value, MKLoader mkLoader){
        final boolean[] isSuccess = {false};    // 토큰 요청의 결과를 저장하는 변수
        mkLoader.setVisibility(View.VISIBLE);  // 프로그레스 바를 표시한다.

        Log.d("request address",address);
        Log.d("request value",value+"..");
        Observable  // 토큰 요청 시작
                .create((ObservableOnSubscribe<TransactionReceipt>) e -> {
                    TransactionReceipt send  = kiwiTestToken.requestToken(address,BigInteger.valueOf(value)).send();        // 에러 발생,
                    e.onNext(send);
                    e.onComplete();
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(respons -> {
                    String result = respons.getBlockHash();
                    if (result != null) {
                        Log.e("transaction OK", result + "..");
                        isSuccess[0] = true;
                        Toast.makeText(context,"요청이 성공했습니다.",Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("transaction NO",  "..");
                        isSuccess[0] = false;
                        Toast.makeText(context,"요청이 실패했습니다.",Toast.LENGTH_SHORT).show();
                    }
                }, Throwable::printStackTrace);
        mkLoader.setVisibility(View.GONE);  // 프로그레스 바를 숨긴다.

        return isSuccess[0];
    }

    @SuppressLint("CheckResult")
    private void sendTransaction(String to, Long value){
        Observable
                .create((ObservableOnSubscribe<TransactionReceipt>) e -> {
                    TransactionReceipt send  = kiwiTestToken.transfer(to,BigInteger.valueOf(value)).send();
                    e.onNext(send);
                    e.onComplete();
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(respons -> {
                    String result = respons.getBlockHash();
                    if (result != null) {
                        Log.e("transaction OK", result + "..");
                    } else {
                        Log.e("transaction NO",  "..");
                    }
                }, Throwable::printStackTrace);
    }
}
