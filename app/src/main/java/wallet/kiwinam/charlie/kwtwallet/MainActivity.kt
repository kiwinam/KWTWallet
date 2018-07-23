package wallet.kiwinam.charlie.kwtwallet

import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val REQUEST_EXTERNAL_STORAGE = 1
    private val PERMISSIONS_STORAGE =
            arrayOf("android.permission.READ_EXTERNAL_STORAGE",
                    "android.permission.WRITE_EXTERNAL_STORAGE",
                    "android.permission.MOUNT_UNMOUNT_FILESYSTEMS")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        permissionCheck() // 권한 확인 및 요청
        // 지갑 생성 버튼 리스너
        mainCreateBtn.setOnClickListener{
            startActivity(Intent(applicationContext, GenWalletActivity::class.java))
            finish()
        }

        // 지갑 가져오기 버튼 리스너
        mainGetBtn.setOnClickListener{

        }
    }

    private fun permissionCheck(){
        try{
            val permission = ActivityCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE")
            if(permission != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE)
            }
        }catch (e : Exception){

        }
    }
}
