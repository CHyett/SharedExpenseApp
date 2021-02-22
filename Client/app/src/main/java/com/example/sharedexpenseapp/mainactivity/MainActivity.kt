package com.example.sharedexpenseapp.mainactivity


import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.airbnb.lottie.LottieAnimationView
import com.example.sharedexpenseapp.R
import com.example.sharedexpenseapp.databinding.ActivityMainBinding
import com.example.sharedexpenseapp.navdrawer.CustomDrawerAdapter
import com.example.sharedexpenseapp.navdrawer.DrawerItem
import com.example.sharedexpenseapp.util.BlurController
import com.google.firebase.iid.FirebaseInstanceId
import java.util.*
import com.example.sharedexpenseapp.util.isConnected
import kotlinx.android.synthetic.main.home_page_fragment.view.*
import android.view.DragEvent as DragEvent


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
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        val viewModelFactory = MainActivityViewModelFactory(application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainActivityViewModel::class.java)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        BlurController.context = this
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
        MainActivityViewModel.isLoggedIn.observe(this, {
            if(it && isConnected(application))
                viewModel.sendToServer()
        })
        viewModel.showNavDrawer.observe(this, {
            if(it)
                binding.mainActivityDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            else
                binding.mainActivityDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        })
        viewModel.isNavDrawerOpen.observe(this, {
            if(it) {
                binding.mainActivityToolbarHamburger.setMinAndMaxProgress(0.25f, 0.34f)
                BlurController.blurScreen()
            } else {
                binding.mainActivityToolbarHamburger.setMinAndMaxProgress(0.75f, 0.84f)
                BlurController.clearBlur()
            }
            binding.mainActivityToolbarHamburger.playAnimation()
        })

        //Click listeners
        binding.mainActivityToolbarHamburger.setOnClickListener {
            PartemDrawerListener.toolbarClicked = true
            if(viewModel.isNavDrawerOpen.value!!)
                binding.mainActivityDrawerLayout.closeDrawer(Gravity.RIGHT)
            else
                binding.mainActivityDrawerLayout.openDrawer(Gravity.RIGHT)
            viewModel.setNavDrawerStatus(!viewModel.isNavDrawerOpen.value!!)
        }

        //Set up firebase
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener(this) { MainActivityViewModel.firebaseToken = it?.token }

    }

    private fun initNavDrawer() {
        PartemDrawerListener.drawerButton = binding.mainActivityToolbarHamburger
        PartemDrawerListener.viewModel = viewModel
        PartemDrawerListener.drawerButtonListener = {
            PartemDrawerListener.toolbarClicked = true
            if(viewModel.isNavDrawerOpen.value!!)
                binding.mainActivityDrawerLayout.closeDrawer(Gravity.RIGHT)
            else
                binding.mainActivityDrawerLayout.openDrawer(Gravity.RIGHT)
            viewModel.setNavDrawerStatus(!viewModel.isNavDrawerOpen.value!!)
        }
        binding.mainActivityToolbarHamburger.speed = 2f
        binding.mainActivityDrawerLayoutList.bringToFront()
        binding.mainActivityDrawerLayout.requestLayout()
        binding.mainActivityDrawerLayout.setScrimColor(ContextCompat.getColor(this, R.color.transparent))
        binding.mainActivityDrawerLayout.addDrawerListener(PartemDrawerListener)
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
            if(groupPosition == 0)
                findNavController(R.id.nav_host_fragment_container_view).navigate(R.id.splashScreen)
            else if(groupPosition == 4)
                viewModel.logOut()
            if(!listView.expandGroup(groupPosition, true))
                listView.collapseGroup(groupPosition)
            true
        }
        binding.mainActivityDrawerLayoutList.setOnChildClickListener { _, _, groupPosition, childPosition, _ ->
            when (childList[headerList[groupPosition]]?.get(childPosition)?.itemName) {
                "Groups" -> findNavController(R.id.nav_host_fragment_container_view).navigate(R.id.testGroupFragment)
                "Pay" -> findNavController(R.id.nav_host_fragment_container_view).navigate(R.id.testPayingFragment)
                "Charge" -> findNavController(R.id.nav_host_fragment_container_view).navigate(R.id.testChargingFragment)
            }
            true
        }
    }

    private object PartemDrawerListener: DrawerLayout.DrawerListener {
        lateinit var viewModel: MainActivityViewModel
        lateinit var drawerButton: LottieAnimationView
        lateinit var drawerButtonListener: (View) -> Unit
        private var isDrawerButtonListenerPresent = true
        private var isDrawerDragging = false
        var toolbarClicked = false
        override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}
        override fun onDrawerOpened(drawerView: View) = handleDrawerMovement()
        override fun onDrawerClosed(drawerView: View) = handleDrawerMovement()
        override fun onDrawerStateChanged(newState: Int) {
            if(newState == DrawerLayout.STATE_DRAGGING) {
                isDrawerDragging = true
                drawerButton.setOnClickListener {}
                isDrawerButtonListenerPresent = false
            }
        }
        private fun handleDrawerMovement() {
            if(isDrawerDragging || !toolbarClicked) {
                viewModel.setNavDrawerStatus(!viewModel.isNavDrawerOpen.value!!)
                isDrawerDragging = false
            }
            if(toolbarClicked) toolbarClicked = false
            if(!isDrawerButtonListenerPresent) {
                isDrawerButtonListenerPresent = true
                drawerButton.setOnClickListener(drawerButtonListener)
            }
        }
    }

}

//Snackbar code
/*fab.setOnClickListener { view ->
    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
        .setAction("Action", null).show()
}*/

/*
*
* TODO:
*  Data cache critical data: List of groups user is a member of, charges/expenses of these groups, list of friends (names only). (That's it for now)
*
* */