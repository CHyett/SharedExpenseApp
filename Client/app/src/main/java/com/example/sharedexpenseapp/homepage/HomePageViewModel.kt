package com.example.sharedexpenseapp.homepage

import androidx.lifecycle.ViewModel
import com.example.sharedexpenseapp.enums.Endpoints
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header


class HomePageViewModel: ViewModel() {

    private val client = AsyncHttpClient()

    //Boolean to represent state of dual buttton motion layout
    internal var isExpensesClicked = true

    fun getGroupExpenses() {
        val params = RequestParams()
        client.post(Endpoints.GLOBAL_GROUP_EXPENSE.endpoint, params, object: AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?) {

            }

            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable?) {
                println("There was an error: ${error?.message}")
            }
        })
    }

    /*
    *
    *TODO:
    *   Make the profile picture functionality work and change LiveData types
    *
    */

}