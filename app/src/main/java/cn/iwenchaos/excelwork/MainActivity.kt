package cn.iwenchaos.excelwork

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import cn.iwenchaos.excelwork.ui.main.MainFragment
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.PermissionRequest

class MainActivity : AppCompatActivity() {

    private val REQUEST_PERMISSION_CAMERA_CODE: Int = 100
    //读写权限
    val permissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        //判断当前系统是否高于或等于6.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //当前系统大于等于6.0
            if (ContextCompat.checkSelfPermission(this,permissions[0]) == PackageManager.PERMISSION_GRANTED
                &&ContextCompat.checkSelfPermission(this,permissions[1]) == PackageManager.PERMISSION_GRANTED) {
                //具有权限 go on

            } else {
                //不具有权限，需要进行权限申请
                ActivityCompat.requestPermissions(this,permissions, REQUEST_PERMISSION_CAMERA_CODE);
            }
        } else {
            //当前系统小于6.0，
            //具有权限 go on
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }
    }
}
