package com.example.sharedexpenseapp


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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