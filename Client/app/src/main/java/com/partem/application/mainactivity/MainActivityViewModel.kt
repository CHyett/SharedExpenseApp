package com.partem.application.mainactivity

import android.app.Application
import android.graphics.drawable.Drawable
import android.view.View
import androidx.lifecycle.*
import com.partem.application.ApplicationRepository
import com.partem.application.enums.Endpoints
import com.partem.application.database.ApplicationDatabase
import com.partem.application.database.Entry
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import kotlinx.coroutines.*
import com.partem.application.enums.Tags
import java.time.LocalDateTime

private const val REFRESH_GROUPS_THRESHOLD = 5L

class MainActivityViewModel(application: Application): AndroidViewModel(application) {

    //LiveData to hide toolbar on login and registration
    private val liveHideToolbar = MutableLiveData(View.GONE)
    val hideToolbar: LiveData<Int>
        get() = liveHideToolbar

    //LiveData to lock or unlock nav drawer
    private val liveShowNavDrawer = MutableLiveData<Boolean>()
    internal val showNavDrawer: LiveData<Boolean>
        get() = liveShowNavDrawer

    //LiveData for app background
    private val liveAppBackgroundDrawable = MutableLiveData<Drawable>()
    val appBackgroundDrawable: LiveData<Drawable>
        get() = liveAppBackgroundDrawable

    //LiveData for nav drawer status
    private val liveIsNavDrawerOpen = MutableLiveData(false)
    internal val isNavDrawerOpen: LiveData<Boolean>
        get() = liveIsNavDrawerOpen

    //Gateway to access persistent data
    private val repository: ApplicationRepository = ApplicationRepository(ApplicationDatabase.getDatabase(application, viewModelScope).entryDao(), viewModelScope)

    //HttpClient for this ViewModel
    private val client = AsyncHttpClient()

    //Array of groups that the user is a part of
    val userGroups = HashMap<String, Int>()

    //Time stamp for data caching
    private val timeStamp = LocalDateTime.now().minusMinutes(5L)

    //Boolean to indicate whether username gas been loaded from local database
    var isDatabaseLoaded = false

    //Boolean to keep track of splash screen status
    var hasShownSplashScreen = false

    //List of callbacks fired after database fetches complete
    private val onDatabaseLoadedListeners = arrayListOf<() -> Unit>()

    companion object {

        //Firebase token for push notifications
        var firebaseToken: String? = null

        //LiveData for whether the user is logged in or not
        private val liveIsLoggedIn = MutableLiveData<Boolean>()
        internal val isLoggedIn: LiveData<Boolean>
            get() = liveIsLoggedIn

        //LiveData for current user username
        private val liveUser = MutableLiveData<String?>()
        internal val user: LiveData<String?>
            get() = liveUser

    }

    init {
        var loginStatus = false
        var name: String? = ""
        CoroutineScope(Dispatchers.IO).launch {
            coroutineScope {
                launch { loginStatus = repository.getIsLoggedIn() }
                launch { name = repository.getUsername() }
            }
            withContext(Dispatchers.Main) {
                liveIsLoggedIn.value = loginStatus
                liveUser.value = name
                isDatabaseLoaded = true
                for (listener in onDatabaseLoadedListeners) listener()
            }
        }
    }

    fun addOnDatabaseLoadedListener(callback: () -> Unit) = onDatabaseLoadedListeners.add(callback)

    fun lockNavDrawer(status: Boolean) { liveShowNavDrawer.value = status }

    fun hideToolbar(status: Boolean) { if(status) liveHideToolbar.value = View.GONE else liveHideToolbar.value = View.VISIBLE }

    fun setAppBackgroundDrawable(drawable: Drawable) { liveAppBackgroundDrawable.value = drawable }

    internal fun setNavDrawerStatus(status: Boolean) { liveIsNavDrawerOpen.value = status }

    internal fun logIn(username: String, password: String, callback: (status: Boolean) -> Unit) {
        val params = RequestParams()
        params.put("username", username)
        params.put("password", password)
        client.get(Endpoints.LOGIN_ENDPOINT.endpoint, params, object: AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?) {
                //Ask cy what is returned from the server here. You might need to assign it to the livedata
                //responseBody?.let { liveLoginStatus.value = String(responseBody) }
                if(statusCode == 200) {
                    saveLoginStatus(true)
                    saveUsername(username)
                    callback(true)
                } else callback(false)
            }

            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable?) {
                //responseBody?.let { liveLoginStatus.value = "Failed! ${String(it)}" }
                error?.let { println("Throwable was ${error.message}") }
            }
        })
    }

    internal fun logOut() {
        liveUser.value = null
        liveIsLoggedIn.value = false
        CoroutineScope(Dispatchers.IO).launch {
            repository.setUsername(Entry(Tags.USERNAME.tag, null, null))
            repository.setIsLoggedIn(Entry(Tags.LOGIN.tag, null, false))
        }
    }

    internal fun saveLoginStatus(loginStatus: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            repository.setIsLoggedIn(Entry(Tags.LOGIN.tag, null, loginStatus))
            println("login status saved")
            withContext(Dispatchers.Main) { liveIsLoggedIn.value = loginStatus }
        }
    }

    internal fun saveUsername(username: String?) {
        username?.let {
            CoroutineScope(Dispatchers.IO).launch {
                repository.setUsername(Entry(Tags.USERNAME.tag, it, null))
                println("username saved")
                withContext(Dispatchers.Main) { liveUser.value = it }
            }
        }
    }

    fun cacheUserGroups(callback: ((Boolean) -> Unit)? = null) {
        println("Request made and username is $isDatabaseLoaded")
        /*if(ChronoUnit.MINUTES.between(timeStamp, LocalDateTime.now()) >= REFRESH_GROUPS_THRESHOLD) {
            for(group in TEST_DATA)
                userGroups[group.getString("name")] = group.getInt("id")
            timeStamp = LocalDateTime.now()
        }*/

        //Use this code in production
        /*val params = RequestParams()
        params.add("username", user.value)
        if(user.value != "") {
            client.get(Endpoints.USER_GROUP_LIST.endpoint, params, object: JsonHttpResponseHandler() {
                override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: JSONArray) {
                    for(i in 0 until responseBody.length()) {
                        val item = responseBody.getJSONObject(i)
                        userGroups[item.getString("name")] = item.getInt("id")
                    }
                    println("Response is: $responseBody")
                }

                override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseString: String?, throwable: Throwable?) {
                    super.onFailure(statusCode, headers, responseString, throwable)
                    println("Error was: ${throwable.toString()}\nResponse string was: $responseString")
                }
            })
        }*/
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

    override fun onCleared() {
        liveIsLoggedIn.value?.let { CoroutineScope(Dispatchers.IO).launch { saveLoginStatus(it) }}
        liveUser.value?.let { CoroutineScope(Dispatchers.IO).launch { saveUsername(it) }}
        super.onCleared()
    }

}

/*
*
* TODO:
*   group list cache in sending requests with no username present on init. Possibly because it hasn't been read from the database yet.
*   Get real name from server in caching call. Tell cy to send you the extra information.
*
* */