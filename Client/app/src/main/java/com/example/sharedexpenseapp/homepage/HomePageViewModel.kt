package com.example.sharedexpenseapp.homepage

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sharedexpenseapp.enums.Endpoints
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header

class HomePageViewModel: ViewModel() {

    private val liveProfilePicture = MutableLiveData<String>()
    internal val profilePicture: LiveData<String>
        get() = liveProfilePicture

    private val liveShowGroupNameForm = MutableLiveData<Int>(View.GONE)
    internal val showGroupNameForm: LiveData<Int>
        get() = liveShowGroupNameForm

    internal val liveIsClickable = MutableLiveData<Boolean>(false)

    internal val liveGroupName = MutableLiveData<String>()

    private val client = AsyncHttpClient()


    internal fun createGroup(username: String, callback: (status: String) -> Unit) {
        val params = RequestParams()
        params.put("groupname", liveGroupName.value!!.trim())
        params.put("username", username)
        client.post(Endpoints.CREATE_GROUP_ENDPOINT.endpoint, params,  object: AsyncHttpResponseHandler() {
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
    }

    /*
    *
    *TODO:
    *Make the profile picture functionality work and change LiveData types
    *
    */

}