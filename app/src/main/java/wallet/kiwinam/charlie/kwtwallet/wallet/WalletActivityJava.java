package wallet.kiwinam.charlie.kwtwallet.wallet;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tuyenmonkey.mkloader.MKLoader;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;
import java.text.DecimalFormat;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import wallet.kiwinam.charlie.kwtwallet.KeyStoreUtils;
import wallet.kiwinam.charlie.kwtwallet.R;
import wallet.kiwinam.charlie.kwtwallet.Web3Service;
import wallet.kiwinam.charlie.kwtwallet.contract.KiwiTestToken;
import wallet.kiwinam.charlie.kwtwallet.db.KeyDBHelper;
import wallet.kiwinam.charlie.kwtwallet.kakaopay.ChargeActivity;

public class WalletActivityJava extends AppCompatActivity implements View.OnClickListener {
    // View 객체들
    private TextView walletNameTv;              // 지갑 이름 텍스트 뷰
    private TextView walletAddressTv;           // 지갑 주소 텍스트뷰
    private TextView walletRefillTv;            // 충전하기 텍스트 뷰
    private TextView walletAccountTv;           // 현재 잔액 텍스트뷰
    private TextView walletNoTransactionTv;     // 트랜잭션 표시 텍스트뷰

    private EditText walletNameEt;              // 지갑 이름 바꾸기 EditText

    private ImageView walletMenuIv;             // 메뉴 이미지 뷰
    private ImageView walletNameEditIv;         // 이름 수정 이미지 뷰
    private ImageView walletRefreshIv;          // 새로고침 이미지 뷰
    private ImageView walletNameConfirmIv;      // 이름 변경 확인 이미지 뷰

    private Button walletSendBtn;               // 보내기 버튼
    private Button walletReceiveBtn;            // 받기 버튼

    private LinearLayout walletAddrLo;          // 지갑 주소 레이아웃

    private SwipeRefreshLayout walletSwipeLo;   // 당겨서 새로고침 레이아웃
    private RecyclerView walletTransactionRv;   // 트랜잭션 RecyclerView

    private MKLoader walletLoader;

    private String contractAddress = "0x7FA51E096a26fBa76212c1451Abd28BF90330E07";  // 계약서 주소
    private String name, address;
    private Web3j web3j;
    private KiwiTestToken kiwiToken;
    private Boolean isInitWallet = false;
    private DecimalFormat decimalFormat = new DecimalFormat("#,##0");

    private Animation rotateAnim;    // 리프레시 회전 애니메이션

    private KeyDBHelper keyDBHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        if(getIntent().getBooleanExtra("isAutoRun",false)){  // 이미 지갑을 연적이 있다면
            name = getSharedPreferences("CurrentWallet", MODE_PRIVATE).getString("name","none");
            address = getSharedPreferences("CurrentWallet", MODE_PRIVATE).getString("address","none");
        }else{  // 새로 지갑을 생성한 경우
            name = getIntent().getStringExtra("name");
            address = getIntent().getStringExtra("address");
        }

        initWallet();   // 지갑을 초기화한다.
        bindViews();    // View 와 객체를 연결한다.
        rotateAnim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.anim_rotate);
        keyDBHelper = new KeyDBHelper(getApplicationContext(),"keyList",null,1);
        keyDBHelper.keyDB();
    }

    /*
     * View 와 객체를 연결한다.
     */
    private void bindViews(){
        walletNameTv = findViewById(R.id.walletNameTv);
        walletAddressTv = findViewById(R.id.walletAddressTv);
        walletRefillTv = findViewById(R.id.walletRefillTv);
        walletAccountTv = findViewById(R.id.walletAccountTv);
        walletNoTransactionTv = findViewById(R.id.walletNoTransactionTv);
        walletMenuIv = findViewById(R.id.walletMenuIv);
        walletNameEditIv = findViewById(R.id.walletNameEditIv);
        walletSendBtn = findViewById(R.id.walletSendBtn);
        walletReceiveBtn = findViewById(R.id.walletReceiveBtn);
        walletAddrLo = findViewById(R.id.walletAddrLo);
        walletSwipeLo = findViewById(R.id.walletSwipeLo);
        walletTransactionRv = findViewById(R.id.walletTransactionRv);
        walletRefreshIv = findViewById(R.id.walletRefreshIv);
        walletNameEt = findViewById(R.id.walletNameEt);
        walletNameConfirmIv = findViewById(R.id.walletNameConfirmIv);
        walletLoader = findViewById(R.id.walletLoader);

        walletNameTv.setText(name);
        walletNameEt.setText(name);
        walletAddressTv.setText(address);
        setClickListeners();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.walletMenuIv: // 메뉴 버튼
                break;
            case R.id.walletRefillTv:   // 충전하기
                Intent intent = new Intent(getApplicationContext(), ChargeActivity.class);
                intent.putExtra("token",walletAccountTv.getText().toString());  // 현재 잔액을 가져온다.
                startActivity(intent);
                finish();
                break;
            case R.id.walletNameEditIv: // 지갑 이름 변경
                walletNameEt.setVisibility(View.VISIBLE);
                walletNameTv.setVisibility(View.GONE);

                walletNameEditIv.setVisibility(View.GONE);
                walletNameConfirmIv.setVisibility(View.VISIBLE);
                break;
            case R.id.walletNameConfirmIv:  // 지갑 이름 변경 확인
                String newWalletName = walletNameEt.getText().toString();
                SharedPreferences sharedPreferences = getSharedPreferences("CurrentWallet",MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putString("name",newWalletName);
                editor.apply();

                keyDBHelper.updateWalletName(newWalletName, address);   // DB 업데이트

                walletNameTv.setText(newWalletName);    // 새로운 이름 설정
                walletNameEt.setVisibility(View.GONE);  // Et 숨김
                walletNameTv.setVisibility(View.VISIBLE);   // 텍스트뷰 보이기

                walletNameConfirmIv.setVisibility(View.GONE);   // 변경 확인 버튼 숨기기
                walletNameEditIv.setVisibility(View.VISIBLE );  // 변경 버튼 보이기
                break;
            case R.id.walletSendBtn:    // 보내기 버튼
                SendBottomSheet sendBottomSheet = SendBottomSheet.Companion.newInstance();
                sendBottomSheet.setCallback((address, value) -> {   // 보내기 버튼이 눌러졌을 때 실행되는 콜백 메소드
                    Log.d("target",address);
                    Log.d("value",".."+value);
                    walletLoader.setVisibility(View.VISIBLE);
                    walletSendBtn.setVisibility(View.INVISIBLE);
                    Snackbar.make(walletReceiveBtn, "전송 요청하였습니다.",Snackbar.LENGTH_SHORT).show();
                    sendTransaction(address, (long) value);
                });
                sendBottomSheet.show(getSupportFragmentManager(),"send");
                break;
            case R.id.walletReceiveBtn: // 받기 버튼
                ReceiveBottomSheet receiveBottomSheet = new ReceiveBottomSheet();
                receiveBottomSheet.setAddress(address); // 내 주소를 넘겨준다.
                receiveBottomSheet.show(getSupportFragmentManager(),"receive");
                break;
            case R.id.walletAddrLo:     // 지갑 주소 복사 버튼
                ClipboardManager clipboardManager = (ClipboardManager)getApplicationContext().getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("label", address);
                if (clipboardManager != null) {
                    clipboardManager.setPrimaryClip(clipData);
                }
                Snackbar.make(walletReceiveBtn, "클립 보드에 복사되었습니다.",Snackbar.LENGTH_SHORT).show();
                break;
            case R.id.walletRefreshIv:
                walletRefreshIv.startAnimation(rotateAnim);
                getWalletInfo();
                break;
        }
    }

    private void setClickListeners(){
        walletMenuIv.setOnClickListener(this);
        walletRefillTv.setOnClickListener(this);
        walletNameEditIv.setOnClickListener(this);
        walletSendBtn.setOnClickListener(this);
        walletReceiveBtn.setOnClickListener(this);
        walletAddrLo.setOnClickListener(this);
        walletRefreshIv.setOnClickListener(this);
        walletNameConfirmIv.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isInitWallet){
            getWalletInfo();
        }
    }

    /*
     * 지갑을 초기화하는 메소드
     */
    @SuppressLint("CheckResult")
    private void initWallet(){
        if(web3j == null){
            web3j = Web3Service.getInstance();
        }
        try{
            Observable.create((ObservableOnSubscribe<KiwiTestToken>) emitter -> {

                KiwiTestToken kiwiToken = new KiwiTestToken(contractAddress,
                        Web3Service.getInstance(),
                        KeyStoreUtils.getCredentials(address,getApplicationContext()),
                        BigInteger.valueOf(41),
                        BigInteger.valueOf(3000000));
                emitter.onNext(kiwiToken);
                emitter.onComplete();

            })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(kiwiToken -> {
                        this.kiwiToken = kiwiToken;
                        Log.d("Contract","OK");
                        isInitWallet = true;    // 지갑 초기화가 완료 되면 true 를 저장한다.
                        getWalletInfo();    //  지갑 정보를 가져온다.
                    }, Throwable::printStackTrace);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /*
     * 지갑의 현재 토큰 개수를 확인한다.
     */
    @SuppressLint({"CheckResult", "SetTextI18n"})
    private void getWalletInfo() {

        Observable.create((ObservableOnSubscribe<BigInteger>) e -> {
            Log.d("tokenOwner",address+"..");
            //BigInteger send = kiwiToken.balanceOf("0x"+address).send();
            BigInteger send = kiwiToken.balanceOf(address).send();
            e.onNext(send);
            e.onComplete();
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(value -> {
                    //tvContent.setText(value.toString());
                    Log.d("value",value.toString()+"..");
                    walletAccountTv.setText(decimalFormat.format(value)+" KWT");
                    Snackbar.make(walletReceiveBtn, "지갑을 갱신하였습니다.",Snackbar.LENGTH_SHORT).show();
                }, Throwable::printStackTrace);

        /*kiwiToken.transferEventObservable(DefaultBlockParameterName.EARLIEST, DefaultBlockParameterName.LATEST).subscribe(tx ->{
           Log.d("to",tx.to);
           Log.d("from",tx.from);
           Log.d("hash",tx.tokens.toString());
        });*/
    }

    /*
     *
     */
    @SuppressLint("CheckResult")
    private void sendTransaction(String to, Long value){
        Observable
                .create((ObservableOnSubscribe<TransactionReceipt>) e -> {
                    TransactionReceipt send  = kiwiToken.transfer(to,BigInteger.valueOf(value)).send();
                    e.onNext(send);
                    e.onComplete();
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(respons -> {
                    String result = respons.getBlockHash();
                    if (result != null) {
                        Log.e("transaction OK", result + "..");
                        walletLoader.setVisibility(View.GONE);
                        walletSendBtn.setVisibility(View.VISIBLE);
                        Snackbar.make(walletReceiveBtn, "토큰을 전송하였습니다.",Snackbar.LENGTH_SHORT).show();
                    } else {
                        //tvMgs.setText(respon.getError().getMessage() + "");
                        Log.e("transaction NO",  "..");
                    }
                }, Throwable::printStackTrace);
    }
}
