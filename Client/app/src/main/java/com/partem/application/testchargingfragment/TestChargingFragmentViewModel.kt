package com.partem.application.testchargingfragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.partem.application.enums.Endpoints
import com.partem.application.mainactivity.MainActivityViewModel
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header

class TestChargingFragmentViewModel: ViewModel() {

    //LiveData to hold group name string
    val liveGroupName = MutableLiveData<String>()

    //LiveData to hold group charge amount
    val liveGroupCharge = MutableLiveData<String>()

    //HTTP Client
    private val client = AsyncHttpClient()

    fun addCharge(id: String) {
        //println("Your group id is $id")

        //Use this code when actually testing
        val params = RequestParams()
        //params.add("", liveGroupName.value)
        params.add("username", MainActivityViewModel.user.value)
        params.add("amount", liveGroupCharge.value)
        params.add("groupID", id)
        client.post(Endpoints.ADD_GROUP_CHARGE, params, object: AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?) {
                responseBody?.let { println("The response is: ${String(it)}") }
            }

            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable?) {
                error?.let { println("onFailure was called.\nThe error is ${it.message}") }
            }
        })
    }

}