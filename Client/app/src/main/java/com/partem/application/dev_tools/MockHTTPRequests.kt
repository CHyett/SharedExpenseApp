package com.partem.application.dev_tools

import kotlinx.coroutines.*

private const val HTTP_REQUEST_TIME = 3000L

fun makeHTTPRequest(successOrFailure: Boolean = true, callback: (Boolean) -> Unit) {
    CoroutineScope(Dispatchers.IO).launch {
        delay(HTTP_REQUEST_TIME)
        withContext(Dispatchers.Main) { callback(successOrFailure) }
    }
}