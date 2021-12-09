package com.galenit.sampleusagesdk

import android.Manifest
import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.galenit.device.api.lib.GalenIT
import com.galenit.sampleusagesdk.ui.main.SectionsPagerAdapter
import com.galenit.sampleusagesdk.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.CAMERA
            ),
            1
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults
        )
        if (grantResults.contains(-1)) {
            // Скажем что без разрешений пользоваться нельзя и закроем приложение
            AlertDialog.Builder(this)
                .setTitle("Ошибка запуска")
                .setMessage("Приложение не может работать без запрошенных разрешений и будет закрыто.")
                .setPositiveButton(R.string.button_ok) { _, _ ->
                    exitProcess(0)
                }
                .setCancelable(false)
                .show()
        }
        else {
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val apiKey = BuildConfig.GALENIT_TOKEN
                    if (!GalenIT.started)
                        GalenIT.start(apiKey)

                    val sectionsPagerAdapter = SectionsPagerAdapter(this@MainActivity, supportFragmentManager)
                    val viewPager: ViewPager = binding.viewPager
                    viewPager.adapter = sectionsPagerAdapter
                    val tabs: TabLayout = binding.tabs
                    tabs.setupWithViewPager(viewPager)
                }
                catch (e: Exception) {
                    AlertDialog.Builder(this@MainActivity)
                        .setMessage("При проверке лицензии возникла ошибка. Продолжение работы невозможно.")
                        .setPositiveButton(R.string.button_ok) { _, _ ->
                            exitProcess(0)
                        }
                        .setCancelable(false)
                        .show()
                }
            }
        }
    }
}