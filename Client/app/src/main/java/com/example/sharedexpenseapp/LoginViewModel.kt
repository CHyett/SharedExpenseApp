package com.example.sharedexpenseapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.RequestParams
import com.loopj.android.http.TextHttpResponseHandler
import cz.msebera.android.httpclient.Header


private const val LOGIN_ENDPOINT = "https://ourapp.live/login"


class LoginViewModel : ViewModel() {

    internal val liveUsername = MutableLiveData<String>()

    internal val livePassword = MutableLiveData<String>()

    private val liveLoginStatus = MutableLiveData<String>()
    internal val loginStatus: LiveData<String>
        get() = liveLoginStatus

    internal fun submit() {
        //See if you can use the data class
        //val user = User(liveUsername.value!!, livePassword.value!!)
        val params = RequestParams()
        params.put("username", liveUsername.value)
        params.put("password", livePassword.value)
        val client = AsyncHttpClient()
        client.get("$LOGIN_ENDPOINT?username=${liveUsername.value}&password=${livePassword.value}", object: TextHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseString: String?) {
                responseString?.let { liveLoginStatus.value = it }
            }

            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseString: String?, error: Throwable?) {
                responseString?.let { liveLoginStatus.value = "Failed! $it" }
                error?.let { println("Throwable was ${error.message}") }
            }
        })
        //TODO: Implement the rest of this function
    }

}
