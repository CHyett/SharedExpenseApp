package com.partem.application.testgroupfragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.partem.application.enums.Endpoints
import com.partem.application.mainactivity.MainActivityViewModel
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header

class TestGroupFragmentViewModel: ViewModel() {

    //LiveData to hold target user username to invite to group
    val liveTargetUserName = MutableLiveData<String>()

    //LiveData to hold the name of the group
    val liveGroupName = MutableLiveData<String>()

    private val client = AsyncHttpClient()

    internal inline fun submitGroupInfo(userFrom: String, crossinline callback: (failure: Boolean, message: String) -> Unit) {
        val params = RequestParams()
        params.put("userFrom", userFrom)
        params.put("userTo", liveTargetUserName.value?.trim())
        params.put("group", liveGroupName.value?.trim())
        params.put("token", MainActivityViewModel.firebaseToken)
        client.post(Endpoints.GROUP_INVITATION_ENDPOINT, params, object: AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?) {
                responseBody?.let { callback(false, String(it)) }
            }

            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable?) {
                println("Status code is: $statusCode")
                println("Error was: ${error?.message}")
                responseBody?.let { println("Message was: ${String(it)}") }
                responseBody?.let { callback(true, String(it)) }
            }
        })
    }

}