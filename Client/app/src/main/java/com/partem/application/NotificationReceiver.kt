package com.partem.application

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.partem.application.enums.Endpoints
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header

class NotificationReceiver: BroadcastReceiver() {

    /**
     * Specifies what happens when a push notification is reacted to.
     *
     * @param context The context on which to operate on.
     * @param intent Holds  information on what the user intends to do with the push notification.
     */
    override fun onReceive(context: Context?, intent: Intent?) {
        val client = AsyncHttpClient()
        val params = RequestParams()
        params.put("userFrom", intent!!.getStringExtra("userFrom"))
        params.put("userTo", intent.getStringExtra("userTo"))
        params.put("group", intent.getStringExtra("group"))
        when(intent.action) {
            "accept" -> params.put("response", true)
            "decline" -> params.put("response", false)
        }
        client.post(Endpoints.GROUP_INVITATION_REPLY, params, object: AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?) {
                println("Group reply success! Response is ${String(responseBody!!)}")
            }

            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable?) {
                error?.let { println("Group reply on failure called. Error is ${error.message}") }
            }
        })
    }

}