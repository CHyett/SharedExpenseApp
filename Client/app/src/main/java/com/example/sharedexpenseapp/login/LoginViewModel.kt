package com.example.sharedexpenseapp.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sharedexpenseapp.enums.Endpoints
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header

class LoginViewModel: ViewModel() {

    private val liveLoginStatus = MutableLiveData<String>()
    internal val loginStatus: LiveData<String>
        get() = liveLoginStatus

    val liveUsername = MutableLiveData<String>()

    val livePassword = MutableLiveData<String>()

    private val client = AsyncHttpClient()

    internal fun logIn(callback: (status: Boolean) -> Unit) {
        val params = RequestParams()
        params.put("username", liveUsername.value)
        params.put("password", livePassword.value)
        client.get(Endpoints.LOGIN_ENDPOINT.endpoint, params, object: AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?) {
                responseBody?.let { liveLoginStatus.value = String(responseBody) }
                if(statusCode == 200) callback(true) else callback(false)
            }

            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable?) {
                responseBody?.let { liveLoginStatus.value = "Failed! ${String(it)}" }
                error?.let { println("Throwable was ${error.message}") }
            }
        })
    }

}