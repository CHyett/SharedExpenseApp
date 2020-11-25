package com.example.sharedexpenseapp.mainactivity


import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.sharedexpenseapp.R
import com.example.sharedexpenseapp.databinding.ActivityMainBinding
import com.example.sharedexpenseapp.navdrawer.CustomDrawerAdapter
import com.example.sharedexpenseapp.navdrawer.DrawerItem
import com.google.firebase.iid.FirebaseInstanceId
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainActivityViewModel

    private lateinit var binding: ActivityMainBinding

    private lateinit var adapter: CustomDrawerAdapter

    private val headerList = ArrayList<DrawerItem>()

    private val childList = HashMap<DrawerItem, List<DrawerItem>>()

    private val notificationChannelMappings = HashMap<String, String>()

    private val notificationChannelList = arrayOf("Group invitations", "Friend requests", "Group members", "Finance notifications")

    init {

        for(i in 0..3)
            notificationChannelMappings[i.toString()] = notificationChannelList[i]

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModelFactory = MainActivityViewModelFactory(application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainActivityViewModel::class.java)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this
        initNavDrawer()
        populateExpandableList()
        for((i, notificationChannelName) in notificationChannelList.withIndex()) {
            val channel = NotificationChannel(i.toString(), notificationChannelName, NotificationManager.IMPORTANCE_HIGH)
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        //LiveData observers
        //observe login status and send firebase token if user is logged in
        viewModel.isLoggedIn.observe(this, Observer {
            if(it)
                viewModel.sendToServer()
        })
        viewModel.orientation.observe(this, Observer {
            this.requestedOrientation = it
        })
        viewModel.showNavDrawer.observe(this, Observer {
            if(it)
                binding.mainActivityDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            else
                binding.mainActivityDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        })
        viewModel.appBackgroundDrawable.observe(this, Observer {
            binding.mainActivityLinearLayout.background = ContextCompat.getDrawable(applicationContext, it)
        })
        viewModel.isNavDrawerOpen.observe(
            this, Observer {
            if(it) {
                binding.mainActivityToolbarHamburger.setMinAndMaxProgress(0.25f, 0.34f)
            } else {
                binding.mainActivityToolbarHamburger.setMinAndMaxProgress(0.75f, 0.84f)
            }
            binding.mainActivityToolbarHamburger.playAnimation()
        })

        //Click listeners
        binding.mainActivityToolbarHamburger.setOnClickListener {
            if(viewModel.isNavDrawerOpen.value!!)
                binding.mainActivityDrawerLayout.closeDrawer(Gravity.RIGHT)
            else
                binding.mainActivityDrawerLayout.openDrawer(Gravity.RIGHT)
        }

        //Set up firebase
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener(this) { MainActivityViewModel.firebaseToken = it?.token }

    }

    private fun initNavDrawer() {
        binding.mainActivityToolbarHamburger.speed = 2f
        binding.mainActivityDrawerLayoutList.bringToFront()
        binding.mainActivityDrawerLayout.requestLayout()
        binding.mainActivityDrawerLayout.setScrimColor(resources.getColor(R.color.transparent))
        binding.mainActivityDrawerLayout.addDrawerListener(object: DrawerLayout.DrawerListener {
            override fun onDrawerStateChanged(newState: Int) {
                if(newState == DrawerLayout.STATE_SETTLING)
                    viewModel.setNavDrawerStatus(!viewModel.isNavDrawerOpen.value!!)
            }
            override fun onDrawerOpened(drawerView: View) {}
            override fun onDrawerClosed(drawerView: View) {}
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}
        })
        var childModelsList = ArrayList<DrawerItem>()
        var menuItem = DrawerItem("History", true, false)
        headerList.add(menuItem)
        childList[menuItem] = emptyList()
        menuItem = DrawerItem("Expenses", true, true)
        headerList.add(menuItem)
        childModelsList.add(DrawerItem("Groups", false, false))
        childModelsList.add(DrawerItem("Pay", false, false))
        childList[menuItem] = childModelsList
        childModelsList = ArrayList()
        menuItem = DrawerItem("Charges", true, true)
        headerList.add(menuItem)
        childModelsList.add(DrawerItem("Groups", false, false))
        childModelsList.add(DrawerItem("Charge", false, false))
        childList[menuItem] = childModelsList
        menuItem = DrawerItem("Settings", true, false)
        headerList.add(menuItem)
        childList[menuItem] = emptyList()
        menuItem = DrawerItem("Log Out", true, false)
        headerList.add(menuItem)
        childList[menuItem] = emptyList()
    }

    private fun populateExpandableList() {
        adapter = CustomDrawerAdapter(this, headerList, childList)
        binding.mainActivityDrawerLayoutList.setAdapter(adapter)
        binding.mainActivityDrawerLayoutList.setOnGroupClickListener { listView, _, groupPosition, _ ->
            listView?.let {
                if(!listView.expandGroup(groupPosition, true))
                    listView.collapseGroup(groupPosition)
            }
            true
        }
        binding.mainActivityDrawerLayoutList.setOnChildClickListener { parent, view, groupPosition, childPosition, id ->
            if(childList[headerList[groupPosition]]?.get(childPosition)?.itemName == "Groups")
                findNavController(R.id.nav_host_fragment_container_view).navigate(R.id.testGroupFragment)
            true
        }
    }

}

//Snackbar code
/*fab.setOnClickListener { view ->
    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
        .setAction("Action", null).show()
}*/