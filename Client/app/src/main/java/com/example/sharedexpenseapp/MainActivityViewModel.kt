package com.example.sharedexpenseapp

import android.app.Application
import android.content.pm.ActivityInfo
import androidx.lifecycle.*
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.example.sharedexpenseapp.database.ApplicationDatabase
import com.example.sharedexpenseapp.database.Entry
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
import com.loopj.android.http.TextHttpResponseHandler
import cz.msebera.android.httpclient.Header
import kotlinx.coroutines.*


const val LOGIN_TAG = "login_status"
const val USERNAME_TAG = "user_name"

class MainActivityViewModel(application: Application): AndroidViewModel(application) {

    //LiveData for HomePageFragment
    private val liveUser = MutableLiveData<String>()
    internal val user: LiveData<String>
        get() = liveUser

    private val liveIsLoggedIn = MutableLiveData<Boolean>()
    internal val isLoggedIn: LiveData<Boolean>
        get() = liveIsLoggedIn

    private val liveLoginStatus = MutableLiveData<String>()
    internal val loginStatus: LiveData<String>
        get() = liveLoginStatus

    //HttpClient for this ViewModel
    private val httpClient = AsyncHttpClient()

    internal val orientation = MutableLiveData<Int>()

    internal var navController: NavController? = null

    //LiveData for LoginFragment
    internal val liveUsername = MutableLiveData<String>()

    internal val livePassword = MutableLiveData<String>()

    private val repository: ApplicationRepository

    init {
        val entryDao = ApplicationDatabase.getDatabase(application, viewModelScope).entryDao()
        repository = ApplicationRepository(entryDao, viewModelScope)
        runBlocking {
            launch(Dispatchers.IO) {
                liveIsLoggedIn.postValue(repository.getIsLoggedIn())
                liveUser.postValue(repository.getUsername())
            }
        }
    }

    companion object {

        private val client = AsyncHttpClient()

        var firebaseToken: String? = null

        fun sendToServer(username: String?) {
            val token = firebaseToken
            if (token != null) {
                val params = RequestParams()
                params.put("token", token)
                params.put("username", username)
                client.post(Endpoints.FIREBASE_TOKEN_ENDPOINT.endpoint, params, object : AsyncHttpResponseHandler() {
                    override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?) {
                        println("firebase token post request status code: $statusCode")
                        responseBody?.let { println(String(it)) }
                    }

                    override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable?) {
                        println("There was an error posting firebase token to server")
                    }
                })
            }
        }

    }

    internal fun saveLoginStatus(loginStatus: Boolean) { liveIsLoggedIn.value = loginStatus
        viewModelScope.launch(Dispatchers.IO) {
            repository.setIsLoggedIn(Entry(LOGIN_TAG, null, loginStatus))
        }
    }

    //does username need to be nullable?
    internal fun saveUsername(username: String?) { liveUser.value = username; username?.let {
        viewModelScope.launch(Dispatchers.IO) {
            repository.setUsername(Entry(USERNAME_TAG, username, null))
        }
    }}

    internal fun logIn(callback: (loginStatus: Boolean) -> Unit) {
        //See if you can use the data class
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
    }

    internal fun logOut() {
        orientation.value = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        liveUser.value = null
        liveIsLoggedIn.value = false
        CoroutineScope(Dispatchers.IO).launch {
            repository.setIsLoggedIn(Entry(LOGIN_TAG, null, false))
            repository.setUsername(Entry(USERNAME_TAG, null, null))
        }
        val navOptions = NavOptions.Builder().setPopUpTo(R.id.homePageFragment, true).build()
        navController!!.navigate(R.id.homePageFragment, null, navOptions)
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
*  Use SQLite database for persistent data instead of SavedStateHandle.
*  Change user live data to be static
*  Change login status live satus to be static
*  Change device orientation to be static
*  Change nav controller to be static
*  Think about changing repository to be static
*
* */