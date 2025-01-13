package com.example.medplusadmin

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.medplusadmin.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    lateinit var drawerLayout: DrawerLayout
    lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    lateinit var appBarConfiguration:AppBarConfiguration
    lateinit var navController: NavController

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
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()//set automatically and sync whether drawer open or not
        appBarConfiguration = AppBarConfiguration(navController.graph,drawerLayout)
        setupActionBarWithNavController(navController,appBarConfiguration)

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