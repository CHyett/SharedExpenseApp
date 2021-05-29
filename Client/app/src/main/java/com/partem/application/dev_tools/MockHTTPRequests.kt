package com.partem.application.dev_tools

import kotlinx.coroutines.*

/**
 * Constant value representing how long this request should take.
 */
private const val HTTP_REQUEST_TIME = 3000L

/**
 * Mock HTTP request that will call back after the predefined HTTP_REQUEST_TIME.
 *
 * @param successOrFailure Whether this request should fail or succeed. Default value is true. (success)
 * @param callback A callback function that will be called after the request completes. It will be passed a boolean. (True for success, false for failure)
 */
fun makeHTTPRequest(successOrFailure: Boolean = true, callback: (Boolean) -> Unit) {
    CoroutineScope(Dispatchers.IO).launch {
        delay(HTTP_REQUEST_TIME)
        withContext(Dispatchers.Main) { callback(successOrFailure) }
    }
}