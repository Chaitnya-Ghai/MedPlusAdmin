package com.example.medplusadmin.presentation.screens.activities

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.example.medplusadmin.R
import com.example.medplusadmin.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import androidx.core.net.toUri

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private lateinit var navController: NavController

    private val PERMISSION_REQUEST_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        navController = findNavController(R.id.host)
        binding.bottomNavView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.category -> {
                    navController.navigate(R.id.categoriesFragment)
                    true
                }
                R.id.medicines -> {
                    if (navController.currentDestination?.id != R.id.medicinesFragment) {
                        navController.navigate(R.id.medicinesFragment)
                    }
                    true
                }
                R.id.shopkeepers -> {
                    if (navController.currentDestination?.id != R.id.manageUsersFragment) {
                        navController.navigate(R.id.manageUsersFragment)
                    }
                    true
                }
                else -> false
            }
        }
        checkAndRequirePermission()
    }


    private fun handleDarkMode(){
        /* IMPLEMENT KRNA HAI*/
    }
    fun arePermissionsGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }
    // permission nhi hai = settings sai lao
    fun requestManageExternalStoragePermission() {
        try {
            val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                    data = "package:${this@MainActivity.packageName}".toUri()
                }
            }
            else {
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = "package:${this@MainActivity.packageName}".toUri()
                }
            }
            startActivity(intent)
        }
        catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "Permission settings not found ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    fun checkAndRequirePermission(){
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.Q){
            if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.R){
                if (Environment.isExternalStorageManager()){ /*permission granted*/ }
                else{ requestManageExternalStoragePermission()}
            }
            else{
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.MANAGE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED
                )
                {
                    requestManageExternalStoragePermission()
                }
            }
        }
        else{
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE  )!= PackageManager.PERMISSION_GRANTED ){
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),PERMISSION_REQUEST_CODE)
            }
        }
    }

}