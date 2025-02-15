package com.example.medplusadmin

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.medplusadmin.Constants.Companion.category
import com.example.medplusadmin.databinding.ActivityMainBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class MainActivity : AppCompatActivity() {
    val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private lateinit var appBarConfiguration:AppBarConfiguration
    lateinit var navController: NavController
    private val firestore=Firebase.firestore
//    permissions
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
        drawerLayout=findViewById(R.id.main)
        actionBarDrawerToggle = ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this, R.color.lightGreen)))
        actionBarDrawerToggle.drawerArrowDrawable.color = ContextCompat.getColor(this, R.color.brightGreen)
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()//set automatically and sync whether drawer open or not
        appBarConfiguration = AppBarConfiguration(navController.graph,drawerLayout)
        setupActionBarWithNavController(navController,appBarConfiguration)
        binding.switchDarkMode.setOnClickListener{ handleDarkMode()}
        EnableNavigationFun()
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
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }
}
// permission nhi hai = settings sai lao
    fun requestManageExternalStoragePermission() {
        try {
            val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                    data = Uri.parse("package:${this@MainActivity.packageName}")
                }

            }
            else {
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.parse("package:${this@MainActivity.packageName}")
                }
            }
            startActivity(intent)
        }
        catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "Permission settings not found", Toast.LENGTH_SHORT).show()
        }
    }
    fun checkAndRequirePermission(){
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q){
            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.R){
                if (Environment.isExternalStorageManager()){ /*permission granted*/ }
                else{ requestManageExternalStoragePermission()}
            }
            else{
                if (ContextCompat.checkSelfPermission(this,
                        android.Manifest.permission.MANAGE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED
                    )
                {
                    requestManageExternalStoragePermission()
                }
            }
        }
        else{
            if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE  )!=PackageManager.PERMISSION_GRANTED ){
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),PERMISSION_REQUEST_CODE)
            }
        }
    }
    fun getDataByCategory(callback: (List<Pair<String, String>>) -> Unit) {
        firestore.collection(category).get()
            .addOnSuccessListener { documents ->
                val tempList = mutableListOf<Pair<String, String>>()
                for (document in documents) {
                    Log.d("firestore", "${document.id} => ${document.data}")
                    tempList.add(
                        document.getString("id").orEmpty() to document.getString("categoryName").orEmpty()
                    )
                }
                callback(tempList) // Return data after Firestore fetch
            }
            .addOnFailureListener { exception ->
                Log.w("firestore", "Error getting documents.", exception)
                callback(emptyList()) // Return empty list on failure
            }
    }
    // navigation view functionality code
    private fun EnableNavigationFun(){
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id != R.id.manageUsersFragment) {
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
                actionBarDrawerToggle.syncState()
            }
        }
        binding.navView.setNavigationItemSelectedListener{
                item->
            when(item.itemId){
                R.id.home ->{
                    navController.navigate(R.id.manageUsersFragment)
                }
                R.id.catNavItem ->{
                    navController.navigate(R.id.categoriesFragment)
                }
                R.id.mediNavItem ->{
                    navController.navigate(R.id.medicinesFragment)
                }
                R.id.dashNavItem ->{
                    navController.navigate(R.id.dashboardFragment)
                }
            }
            drawerLayout.closeDrawers()
            true
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        return  super.onSupportNavigateUp()|| navController.popBackStack()
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (actionBarDrawerToggle.onOptionsItemSelected(item)){
            true
        }else{
            super.onOptionsItemSelected(item)
        }
    }
}