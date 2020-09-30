package com.example.sharedexpenseapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sharedexpenseapp.enums.Endpoints
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header

class RegisterViewModel : ViewModel() {

    internal val newUserUsername = MutableLiveData<String>()

    internal val newUserPassword = MutableLiveData<String>()

    internal val newUserEmail = MutableLiveData<String>()

    private val liveProgress = MutableLiveData(50)
    internal val progress: LiveData<Int>
        get() = liveProgress

    private val liveRegistrationStatus = MutableLiveData<String>()
    internal val registrationStatus: LiveData<String>
        get() = liveRegistrationStatus

    internal fun register() {
        val params = RequestParams()
        params.put("username", newUserUsername.value)
        params.put("password", newUserPassword.value)
        params.put("email", newUserEmail.value)
        val httpClient = AsyncHttpClient()
        httpClient.post(Endpoints.REGISTER_ENDPOINT.endpoint, params, object: AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?) {
                responseBody?.let { liveRegistrationStatus.value = String(it) }
                clearEditTexts()
            }

            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable?) {
                responseBody?.let { liveRegistrationStatus.value = "Failed! ${error?.message}" }
                error?.let { println("Throwable was ${error.message}") }
            }
        })
    }

    private fun clearEditTexts() {
        newUserUsername.value = ""
        newUserPassword.value = ""
        newUserEmail.value = ""
    }

}