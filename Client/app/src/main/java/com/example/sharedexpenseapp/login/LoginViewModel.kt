package com.example.sharedexpenseapp.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sharedexpenseapp.enums.Endpoints
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.TextHttpResponseHandler
import cz.msebera.android.httpclient.Header

class LoginViewModel: ViewModel() {

    private val liveLoginStatus = MutableLiveData<String>()
    internal val loginStatus: LiveData<String>
        get() = liveLoginStatus

    internal val liveUsername = MutableLiveData<String>()

    internal val livePassword = MutableLiveData<String>()

    private val client = AsyncHttpClient()

    internal fun logIn(callback: (status: Boolean) -> Unit) {
        //TODO: Implement this function
    }

    //This is the old code from the main viewmodel
    /*internal fun logIn(callback: (loginStatus: Boolean) -> Unit) {
        httpClient.get("${Endpoints.LOGIN_ENDPOINT.endpoint}?username=${liveUsername.value}&password=${livePassword.value}", object: TextHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseString: String?) {
                responseString?.let { liveLoginStatus.value = it }
                if(statusCode == 200) {
                    liveUser.value = liveUsername.value!!
                    callback(true)
                } else
                    callback(false)
            }

            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseString: String?, error: Throwable?) {
                responseString?.let { liveLoginStatus.value = "Failed! $it" }
                error?.let { println("Throwable was ${error.message}") }
            }
        })
    }*/

}