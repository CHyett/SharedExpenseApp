package com.example.sharedexpenseapp

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.material.snackbar.Snackbar
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
import com.loopj.android.http.TextHttpResponseHandler
import cz.msebera.android.httpclient.Header

internal const val REQUEST_CODE = 26
private const val GET_TOKEN_ENDPOINT = "https://ourapp.live/client_token"
internal const val CHECKOUT_ENDPOINT = "https://ourapp.live/checkout"

class MainActivityViewModel: ViewModel() {

    private val liveClientToken = MutableLiveData<String>()
    internal val clientToken: LiveData<String>
        get() = liveClientToken

    private val liveResponseMessage = MutableLiveData<String>()
    internal val responseMessage: LiveData<String>
        get() = liveResponseMessage

    private val liveErrorMessage = MutableLiveData<String>()
    internal val errorMessage: LiveData<String>
        get() = liveErrorMessage

    internal val liveAmount = MutableLiveData<String>()

    private val httpClient = AsyncHttpClient()

    //Get client token, prepare drop-in ui, and collect device data for Braintree
    internal fun getClientToken(callback: () -> Unit) {
        httpClient.get(GET_TOKEN_ENDPOINT, object: TextHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: String?) {
                liveClientToken.value = responseBody ?: "" //Make some code for what to do if responseBody is null
                callback()
            }

            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: String?, error: Throwable?) {
                liveErrorMessage.value = error.toString()
            }
        })
    }

    internal fun sendPayment(amount: Float, nonce: String?, deviceData: String) {
        val client = AsyncHttpClient()
        val params = RequestParams()
        params.put("nonce", nonce)
        params.put("amount", amount)
        params.put("deviceData", deviceData)
        client.post(CHECKOUT_ENDPOINT, params, object: AsyncHttpResponseHandler(){
            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, response: ByteArray?) {
                if(response != null) liveResponseMessage.value = String(response) else println("No response")
            }
            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseString: ByteArray?, throwable: Throwable?) {
                liveErrorMessage.value = "Failed to send payment: status code $statusCode"
            }
        })
    }

    internal fun snackBarFunction(view: View) {
        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
            .setAction("Action", null).show()
    }

}