package com.example.sharedexpenseapp.mainactivity

import android.app.Application
import android.content.pm.ActivityInfo
import android.view.View
import androidx.lifecycle.*
import androidx.navigation.NavController
import com.example.sharedexpenseapp.ApplicationRepository
import com.example.sharedexpenseapp.enums.Endpoints
import com.example.sharedexpenseapp.database.ApplicationDatabase
import com.example.sharedexpenseapp.database.Entry
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import kotlinx.coroutines.*
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

    //LiveData to hide toolbar on login and registration
    private val liveHideToolbar = MutableLiveData<Int>(View.GONE)
    val hideToolbar: LiveData<Int>
        get() = liveHideToolbar

    //LiveData to lock or unlock nav drawer
    private val liveShowNavDrawer = MutableLiveData<Boolean>()
    internal val showNavDrawer: LiveData<Boolean>
        get() = liveShowNavDrawer

    //LiveData for app background
    private val liveAppBackgroundDrawable = MutableLiveData<Int>()
    internal val appBackgroundDrawable: LiveData<Int>
        get() = liveAppBackgroundDrawable

    //LiveData for nav drawer status
    private val liveIsNavDrawerOpen = MutableLiveData<Boolean>(false)
    internal val isNavDrawerOpen: LiveData<Boolean>
        get() = liveIsNavDrawerOpen

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
            repository.setUsername(Entry(Tags.USERNAME.tag, null, null))
            repository.setIsLoggedIn(Entry(Tags.LOGIN.tag, null, false))
        }
    }

    internal suspend fun saveLoginStatus(loginStatus: Boolean) {
        liveIsLoggedIn.postValue(loginStatus)
        repository.setIsLoggedIn(Entry(Tags.LOGIN.tag, null, loginStatus))
        println("login status saved")
    }

    internal suspend fun saveUsername(username: String?) {
        liveUser.postValue(username)
        username?.let {
            repository.setUsername(Entry(Tags.USERNAME.tag, username, null))
            println("username saved")
        }
    }

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

    fun lockNavDrawer(status: Boolean) {
        liveShowNavDrawer.value = status
    }

    fun hideToolbar(status: Boolean) {
        if(status) {
            liveHideToolbar.value = View.GONE
        } else {
            liveHideToolbar.value = View.VISIBLE
        }
    }

    fun setAppBackgroundDrawable(resID: Int) {
        liveAppBackgroundDrawable.value = resID
    }

    internal fun setNavDrawerStatus(status: Boolean) {
        liveIsNavDrawerOpen.value = status
    }

    override fun onCleared() {
        liveIsLoggedIn.value?.let { CoroutineScope(Dispatchers.IO).launch { saveLoginStatus(it) }}
        liveUser.value?.let { CoroutineScope(Dispatchers.IO).launch { saveUsername(it) }}
        super.onCleared()
    }

}

/*
*
* TODO:
*
*
* */