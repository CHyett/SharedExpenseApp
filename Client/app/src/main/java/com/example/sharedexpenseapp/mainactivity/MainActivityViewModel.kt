package com.example.sharedexpenseapp.mainactivity

import android.app.Application
import android.content.pm.ActivityInfo
import androidx.lifecycle.*
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.example.sharedexpenseapp.ApplicationRepository
import com.example.sharedexpenseapp.enums.Endpoints
import com.example.sharedexpenseapp.database.ApplicationDatabase
import com.example.sharedexpenseapp.database.Entry
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import kotlinx.coroutines.*
import com.example.sharedexpenseapp.R
import com.example.sharedexpenseapp.enums.Tags


class MainActivityViewModel(application: Application): AndroidViewModel(application) {

    //LiveData for whether the user is logged in or not
    private val liveIsLoggedIn = MutableLiveData<Boolean>()
    internal val isLoggedIn: LiveData<Boolean>
        get() = liveIsLoggedIn

    //LiveData for current user username
    private val liveUser = MutableLiveData<String>()
    internal val user: LiveData<String>
        get() = liveUser

    //Screen orientation (forced portrait mode or free to rotate)
    internal val orientation = MutableLiveData<Int>()

    //App nav controller
    internal var navController: NavController? = null

    //Gateway to access persistent data
    private val repository: ApplicationRepository = ApplicationRepository(ApplicationDatabase.getDatabase(application, viewModelScope).entryDao(), viewModelScope)

    //HttpClient for this ViewModel
    private val client = AsyncHttpClient()

    init {
        runBlocking {
            val job = CoroutineScope(Dispatchers.IO).launch {
                liveIsLoggedIn.postValue(repository.getIsLoggedIn())
                liveUser.postValue(repository.getUsername())
            }
            job.join()
        }
    }

    companion object {

        //Firebase token for push notifications
        internal var firebaseToken: String? = null

    }

    internal fun logOut() {
        orientation.value = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        liveUser.value = null
        liveIsLoggedIn.value = false
        CoroutineScope(Dispatchers.IO).launch {
            repository.setIsLoggedIn(Entry(Tags.LOGIN.tag, null, false))
            repository.setUsername(Entry(Tags.USERNAME.tag, null, null))
        }
        val navOptions = NavOptions.Builder().setPopUpTo(R.id.homePageFragment, true).build()
        navController!!.navigate(R.id.homePageFragment, null, navOptions)
    }

    internal fun saveLoginStatus(loginStatus: Boolean) {
        liveIsLoggedIn.value = loginStatus
        viewModelScope.launch(Dispatchers.IO) {
            repository.setIsLoggedIn(Entry(Tags.LOGIN.tag, null, loginStatus))
        }
    }

    internal fun saveUsername(username: String?) {
        liveUser.value = username
        username?.let {
        viewModelScope.launch(Dispatchers.IO) {
            repository.setUsername(Entry(Tags.USERNAME.tag, username, null))
        }
    }}

    fun sendToServer() {
        if (firebaseToken != null) {
            val params = RequestParams()
            params.put("token", firebaseToken)
            params.put("username", user.value!!)
            client.post(Endpoints.FIREBASE_TOKEN_ENDPOINT.endpoint, params, object : AsyncHttpResponseHandler() {
                override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?) {
                    println("firebase token post request status code: $statusCode")
                    responseBody?.let { println(String(it)) }
                }

                override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable?) {
                    println("There was an error posting firebase token to server => error is \n${error?.message}")
                    println("Stack trace is: \n${error?.printStackTrace()}")
                }
            })
        }
    }

    override fun onCleared() {
        liveIsLoggedIn.value?.let { saveLoginStatus(it) }
        liveUser.value?.let { saveUsername(it) }
        super.onCleared()
    }

}

/*
*
* TODO:
*
*
* */