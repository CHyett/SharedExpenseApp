package com.example.sharedexpenseapp.homepage

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sharedexpenseapp.R
import com.example.sharedexpenseapp.enums.Endpoints
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header


class HomePageViewModel: ViewModel() {

    private val client = AsyncHttpClient()

    //LiveData to handle whether create group button is clickable or not
    internal var liveIsClickable = MutableLiveData<Boolean>(false)

    //LiveData to hold group name in create group form
    internal val liveGroupName = MutableLiveData<String>()

    //Boolean to represent state of dual buttton motion layout
    internal var isExpensesClicked = true

    //LiveData to hold expenses button color
    private val liveExpensesButtonTextColor = MutableLiveData(R.color.colorSecondary)
    internal val expensesButtonTextColor: LiveData<Int>
        get() = liveExpensesButtonTextColor

    //LiveData to hold charges button color
    private val liveChargesButtonTextColor = MutableLiveData(R.color.colorBlack)
    internal val chargesButtonTextColor: LiveData<Int>
        get() = liveExpensesButtonTextColor

    private val liveDualButtonHighlightWidth = MutableLiveData<Int>()
    internal val dualButtonHighlightWidth: LiveData<Int>
        get() = liveDualButtonHighlightWidth

    //LiveData to show or contain group creation form
    private val liveShowGroupNameForm = MutableLiveData<Int>(View.GONE)
    internal val showGroupNameForm: LiveData<Int>
        get() = liveShowGroupNameForm

    //Old code from create group testing (might be able to salvage)
    /*internal fun createGroup(username: String, callback: (status: String) -> Unit) {
        val params = RequestParams()
        params.put("groupname", liveGroupName.value!!.trim())
        params.put("username", username)
        client.post(Endpoints.CREATE_GROUP_ENDPOINT.endpoint, params, object : AsyncHttpResponseHandler() {
                override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?) {
                    liveShowGroupNameForm.value = View.GONE
                    liveGroupName.value = ""
                    callback(responseBody?.let { String(responseBody) } ?: "No response from the server.")
                }

                override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable?) {
                    error?.let { println("Throwable was ${error.message}") }
                    callback("Failed! ${error?.message}")
                }
            })
    }

    internal fun showCreateGroupForm() {
        liveShowGroupNameForm.value = View.VISIBLE
    }*/

    /*
    *
    *TODO:
    *   Make the profile picture functionality work and change LiveData types
    *
    */

}