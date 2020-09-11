package com.example.sharedexpenseapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header

private const val REGISTER_ENDPOINT = "https://ourapp.live/register"

class RegisterViewModel : ViewModel() {

    internal val newUserUsername = MutableLiveData<String>()
    internal val newUserPassword = MutableLiveData<String>()
    internal val newUserEmail = MutableLiveData<String>()
    private val liveRegistrationStatus = MutableLiveData<String>()
    internal val registrationStatus: LiveData<String>
        get() = liveRegistrationStatus

    internal fun register() {
        val params = RequestParams()
        params.put("username", newUserUsername.value)
        params.put("password", newUserPassword.value)
        params.put("email", newUserEmail.value)
        val httpClient = AsyncHttpClient()
        httpClient.post(REGISTER_ENDPOINT, params, object: AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?) {
                responseBody?.let { liveRegistrationStatus.value = String(it) }
            }

            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable?) {
                responseBody?.let { liveRegistrationStatus.value = "Failed! ${error?.message}" }
                error?.let { println("Throwable was ${error.message}") }
            }
        })
    }

}