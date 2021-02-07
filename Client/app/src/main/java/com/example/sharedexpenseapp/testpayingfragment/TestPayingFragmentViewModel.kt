package com.example.sharedexpenseapp.testpayingfragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sharedexpenseapp.enums.Endpoints
import com.example.sharedexpenseapp.mainactivity.MainActivityViewModel
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header

class TestPayingFragmentViewModel: ViewModel() {

    //LiveData to hold group name
    val liveGroupName = MutableLiveData<String>()

    //LiveData to hold amount of money
    val liveGroupAmount = MutableLiveData<String>()

    private val client = AsyncHttpClient()

    fun payCharge(id: String) {
        println("Your id is $id")

        //Use this code when actually testing
        /*val params = RequestParams()
        //params.add("", liveGroupName.value)
        params.add("username", MainActivityViewModel.user.value)
        params.add("amount", liveGroupAmount.value)
        params.add("groupID", id)
        client.post(Endpoints.PAY_GROUP_CHARGE.endpoint, params, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?) {
                responseBody?.let { println("Response is ${String(it)}") }
            }

            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable?) {
                error?.let { println("Error is:\n${it.message}") }
            }
        })*/
    }

}