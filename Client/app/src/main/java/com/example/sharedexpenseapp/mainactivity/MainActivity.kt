package com.example.sharedexpenseapp.mainactivity


import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.ExpandableListView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModelFactory = MainActivityViewModelFactory(application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainActivityViewModel::class.java)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this
        initNavDrawer()
        populateExpandableList()

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
        headerList.add(DrawerItem("Settings", true, false))
        headerList.add(DrawerItem("Log Out", true, false))
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
        binding.mainActivityDrawerLayoutList.setOnChildClickListener { _, _, _, _, _ -> true }
    }

}






//Previous working code

/*package com.example.sharedexpenseapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.braintreepayments.api.BraintreeFragment
import com.braintreepayments.api.DataCollector
import com.braintreepayments.api.dropin.DropInActivity
import com.braintreepayments.api.dropin.DropInRequest
import com.braintreepayments.api.dropin.DropInResult
import com.braintreepayments.api.exceptions.InvalidArgumentException
import com.example.sharedexpenseapp.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var act: Context
    private lateinit var viewModel: MainActivityViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var braintreeFragment: BraintreeFragment
    private lateinit var deviceData: String

    override fun onCreate(savedInstanceState: Bundle?) {

        //Init stuff
        super.onCreate(savedInstanceState)
        act = this
        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this

        //LiveData observers
        viewModel.clientToken.observe(this, Observer {
            println(viewModel.clientToken.value)
        })
        viewModel.responseMessage.observe(this, Observer {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        })
        viewModel.errorMessage.observe(this, Observer {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        })

        //onClickListeners
        binding.connectButton.setOnClickListener { viewModel.getClientToken { initializeBraintreeFragment() } }
        binding.transactionButton.setOnClickListener {
            val amount = checkIfAmountEmpty(viewModel.liveAmount.value)
            if(amount != null) {
                val dropInRequest = DropInRequest().clientToken(viewModel.clientToken.value)
                startActivityForResult(dropInRequest.getIntent(this), REQUEST_CODE)
            } else Toast.makeText(this, "Enter a valid amount", Toast.LENGTH_SHORT).show() //Maybe gray out (disable) the transaction button until number greater than 0 is entered
        }
        fab.setOnClickListener {viewModel.snackBarFunction(fab)}
    }

    private fun initializeBraintreeFragment() {
        try {
            this.braintreeFragment = BraintreeFragment.newInstance(this, viewModel.clientToken.value)
            DataCollector.collectDeviceData(braintreeFragment) {deviceData = it}
        } catch (e: InvalidArgumentException) {
            Toast.makeText(this, "There was an error with your initialization token", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE) {
            when(resultCode) {
                Activity.RESULT_OK -> { val result: DropInResult? = data?.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT)
                        val nonce = result?.paymentMethodNonce?.nonce
                        val amount = checkIfAmountEmpty(viewModel.liveAmount.value)
                        if(amount != null) viewModel.sendPayment(amount, nonce, this.deviceData) else Toast.makeText(this, "Enter a valid amount", Toast.LENGTH_SHORT).show()
                    }
                Activity.RESULT_CANCELED -> { Toast.makeText(this, "User cancelled", Toast.LENGTH_SHORT).show() }
                else -> { val error = data?.getSerializableExtra(DropInActivity.EXTRA_ERROR) as Exception?
                    Log.d("DROP_IN_ERROR", error.toString())
                }
            }
        }
    }

    private fun checkIfAmountEmpty(amount: String?): Float? {
        return try {
            amount?.toFloat()
        } catch(e: NumberFormatException) {
            null
        }
    }

}*/

//Snackbar code
/*fab.setOnClickListener { view ->
    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
        .setAction("Action", null).show()
}*/