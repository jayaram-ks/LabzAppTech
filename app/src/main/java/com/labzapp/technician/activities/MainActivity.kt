package com.labzapp.technician.activities

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.labzapp.technician.R
import com.labzapp.technician.adapters.NavigationRVAdapter
import com.labzapp.technician.databinding.ActivityMainBinding
import com.labzapp.technician.listeners.ClickListener
import com.labzapp.technician.listeners.RecyclerTouchListener
import com.labzapp.technician.model.NavigationItemModel
import com.labzapp.technician.storage.SharedPrefManager
import com.labzapp.technician.ui.bookings.BookingsFragment
import com.labzapp.technician.ui.labs.LabsFragment
import com.labzapp.technician.ui.profile.ProfileFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    lateinit var drawerLayout: DrawerLayout
    private lateinit var adapter: NavigationRVAdapter

    private lateinit var binding: ActivityMainBinding

    private  lateinit var sharedViewModel: MainViewModel

    private var actparam1: String? = null
    private var actparam2: String? = null

    private var items = arrayListOf(
        NavigationItemModel(R.drawable.ic_booking, "Bookings - New Allocations"),
        NavigationItemModel(R.drawable.ic_booking, "Bookings - Sample Collected"),
        NavigationItemModel(R.drawable.ic_booking, "Bookings - Print Not Given"),
        NavigationItemModel(R.drawable.ic_booking, "Bookings - Print Given"),
        NavigationItemModel(R.drawable.ic_booking, "Bookings - Completed"),
        NavigationItemModel(R.drawable.ic_lab_2, "Labs Allocated"),
        NavigationItemModel(R.drawable.ic_man, "My Profile"),
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        sharedViewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        drawerLayout = findViewById(R.id.drawer_layout)

        // Set the toolbar
        setSupportActionBar(binding.activityMainToolbar)

        // Setup Recyclerview's Layout
        binding.navigationRv.layoutManager = LinearLayoutManager(this)
        binding.navigationRv.setHasFixedSize(true)

        // Add Item Touch Listener
        val bookingFragment = BookingsFragment()
        val labFrag = LabsFragment()
        val profileFrag = ProfileFragment()

        binding.navigationRv.addOnItemTouchListener(RecyclerTouchListener(this, object :
            ClickListener {
            override fun onClick(view: View, position: Int) {
                when (position) {
                    0 -> { setCurrentFragment(bookingFragment,false,"Bookings - New Allocations","2","noprint") }
                    1 -> { setCurrentFragment(bookingFragment,false,"Bookings - Sample Collected","3","noprint") }
                    2 -> { setCurrentFragment(bookingFragment,false,"Bookings - Print Not Given","1","printyes") }
                    3 -> { setCurrentFragment(bookingFragment,false,"Bookings - Print Given","2","printyes") }
                    4 -> { setCurrentFragment(bookingFragment,false,"Bookings - Completed","4","noprint") }
                    5 -> { setCurrentFragment(labFrag,false,"Labs Allocated","","") }
                    6 -> { setCurrentFragment(profileFrag,false,"My Profile","","") }
                }
                updateAdapter(position)
               lifecycleScope.launch {
                   delay(200)
                   drawerLayout.closeDrawer(GravityCompat.START)
               }

            }
        }))

        // Update Adapter with item data and highlight the default menu item ('Home' Fragment)
        updateAdapter(0)

        // Set 'Booking' as the default fragment when the app starts

        setCurrentFragment(bookingFragment,false,"Bookings - New Allocations","2","noprint")

        // Close the soft keyboard when you open or close the Drawer
        val toggle: ActionBarDrawerToggle = object : ActionBarDrawerToggle(this, drawerLayout, binding.activityMainToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            override fun onDrawerClosed(drawerView: View) {
                // Triggered once the drawer closes
                super.onDrawerClosed(drawerView)
                try {
                    val inputMethodManager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
                } catch (e: Exception) {
                    e.stackTrace
                }
            }

            override fun onDrawerOpened(drawerView: View) {
                // Triggered once the drawer opens
                super.onDrawerOpened(drawerView)
                try {
                    val inputMethodManager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
                } catch (e: Exception) {
                    e.stackTrace
                }
            }
        }
        drawerLayout.addDrawerListener(toggle)

        toggle.syncState()

        // Set Header Image
        //binding.navigationHeaderImg.setImageResource(R.drawable.ic_launcher_background)

        // Set background of Drawer
        binding.navigationLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))


    }

    fun setActionBarTitle(title: String?) {
        supportActionBar!!.title = title
    }



    private fun setCurrentFragment(fragmentnew: Fragment, addtoBackStck:Boolean, ptitle:String, param1:String, param2:String){

        if (supportFragmentManager.backStackEntryCount > 0) {
            val count: Int = supportFragmentManager.backStackEntryCount
            for (i in 0 until count) {
                supportFragmentManager.popBackStack()
            }
        }

        if(param1 != "") {  //IF bookings
            sharedViewModel.getBookings(param1,param2)
            sharedViewModel.setTitl(ptitle)
        }
        actparam1 = param1
        actparam2 = param2

        val transnew = supportFragmentManager.beginTransaction()
        if(addtoBackStck) {
            transnew.addToBackStack(null)
        }

        transnew.replace(R.id.activity_main_content_id,fragmentnew)
        transnew.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        transnew.commit()
    }


    private fun updateAdapter(highlightItemPos: Int) {
        adapter = NavigationRVAdapter(items, highlightItemPos)
        binding.navigationRv.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            // Checking for fragment count on back stack
            if (supportFragmentManager.backStackEntryCount > 0) {
                //actparam1?.let { actparam2?.let { it1 -> sharedViewModel.getBookings(it, it1) } }
                // Go to the previous fragment
                supportFragmentManager.popBackStack()

            } else {

                val builder = AlertDialog.Builder(this)
                builder.setTitle("Exit App")
                builder.setPositiveButton("Yes") { dialog, which ->
                    dialog.dismiss()
                    super.onBackPressed()
                }
                builder.setNegativeButton("No") { dialog, which ->
                    dialog.dismiss()
                }
                builder.show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if( actparam1 != "") {
            actparam1?.let { actparam2?.let { it1 -> sharedViewModel.getBookings(it, it1) } }
        }
    }

    override fun onStart() {
        super.onStart()
        if(!SharedPrefManager.getInstance(this).isLoggedIn){
            val intent = Intent(applicationContext, AuthActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}