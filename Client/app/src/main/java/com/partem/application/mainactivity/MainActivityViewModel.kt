package com.partem.application.mainactivity

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.view.View
import androidx.lifecycle.*
import com.partem.application.enums.Endpoints
import com.partem.application.enums.Tags
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import java.time.LocalDateTime

private const val REFRESH_GROUPS_THRESHOLD = 5L

class MainActivityViewModel(application: Application): AndroidViewModel(application) {

    /**
     * LiveData to hide toolbar on login and registration.
     */
    private val liveHideToolbar = MutableLiveData(View.GONE)
    val hideToolbar: LiveData<Int>
        get() = liveHideToolbar

    /**
     * LiveData to lock or unlock nav drawer.
     */
    private val liveShowNavDrawer = MutableLiveData<Boolean>()
    internal val showNavDrawer: LiveData<Boolean>
        get() = liveShowNavDrawer

    /**
     * LiveData for app background.
     */
    private val liveAppBackgroundDrawable = MutableLiveData<Drawable>()
    val appBackgroundDrawable: LiveData<Drawable>
        get() = liveAppBackgroundDrawable

    /**
     * LiveData for nav drawer status.
     */
    private val liveIsNavDrawerOpen = MutableLiveData(false)
    internal val isNavDrawerOpen: LiveData<Boolean>
        get() = liveIsNavDrawerOpen

    /**
     * Gateway to access persistent data.
     */
    private val sharedPrefs: SharedPreferences = application.getSharedPreferences(Tags.SHARED_PREFS_NAME, Context.MODE_PRIVATE)

    /**
     * HttpClient for this ViewModel.
     */
    private val client = AsyncHttpClient()

    /**
     * Array of groups that the user is a part of.
     */
    val userGroups = HashMap<String, Int>()

    /**
     * Time stamp for data caching.
     */
    private val timeStamp = LocalDateTime.now().minusMinutes(5L)

    /**
     * Boolean to keep track of splash screen status.
     */
    var hasShownSplashScreen = false

    /**
     * Boolean to keep track of when the user logs out.
     */
    var loggedOut = false

    companion object {

        /**
         * Firebase token for push notifications.
         */
        var firebaseToken: String? = null

        /**
         * LiveData for whether the user is logged in or not.
         */
        private val liveIsLoggedIn = MutableLiveData<Boolean>()
        internal val isLoggedIn: LiveData<Boolean>
            get() = liveIsLoggedIn

        /**
         * LiveData for current user username.
         */
        private val liveUser = MutableLiveData<String?>()
        internal val user: LiveData<String?>
            get() = liveUser

        /**
         * LiveData for whether user would like to be remembered or not.
         */
        private val liveRememberUser = MutableLiveData<Boolean>()
        internal val rememberUser: LiveData<Boolean>
            get() = liveRememberUser

    }

    init {
        var loginStatus = false
        var name: String? = ""
        var remember = false
        //Dev
        autoLogin()
        val editor = sharedPrefs.edit()
        if(sharedPrefs.contains(Tags.PREFS_LOGIN_HANDLE)) loginStatus = sharedPrefs.getBoolean(Tags.PREFS_LOGIN_HANDLE, false)
        else editor.putBoolean(Tags.PREFS_LOGIN_HANDLE, false)
        if(sharedPrefs.contains(Tags.PREFS_USERNAME_HANDLE)) name = sharedPrefs.getString(Tags.PREFS_USERNAME_HANDLE, "")
        else editor.putString(Tags.PREFS_USERNAME_HANDLE, "")
        if(sharedPrefs.contains(Tags.PREFS_REMEMBER_HANDLE)) remember = sharedPrefs.getBoolean(Tags.PREFS_REMEMBER_HANDLE, false)
        else editor.putBoolean(Tags.PREFS_REMEMBER_HANDLE, false)
        if(!editor.commit()) println("Could not commit changes to shared prefs.")
        liveIsLoggedIn.value = loginStatus
        liveUser.value = name
        liveRememberUser.value = remember
    }

    /**
     * Locks and hides the nav drawer.
     *
     * @param status True if the nav drawer should be locked.
     */
    fun lockNavDrawer(status: Boolean) { liveShowNavDrawer.value = status }

    /**
     * Hides the top action/tool bar.
     *
     * @param status True if the action/tool bar should be hidden from the user.
     */
    fun hideToolbar(status: Boolean) { if(status) liveHideToolbar.value = View.GONE else liveHideToolbar.value = View.VISIBLE }

    /**
     * Sets the background for the app.
     *
     * @param drawable The drawable resource to be drawn on the screen.
     */
    fun setAppBackgroundDrawable(drawable: Drawable) { liveAppBackgroundDrawable.value = drawable }

    /**
     * Sets the nav drawer state. (Open or closed)
     *
     * @param status True if the nav drawer is open
     */
    internal fun setNavDrawerStatus(status: Boolean) { liveIsNavDrawerOpen.value = status }

    /**
     * Send username and password to server for login verification.
     *
     * @param username The user's username.
     * @param password The user's password.
     * @param rememberUser User's preference on credentials being remembered on next login.
     * @param callback The callback function to be invoked. (callback(true) if login succeeded)
     */
    internal fun logIn(username: String, password: String, rememberUser: Boolean, callback: (status: Boolean) -> Unit) {
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
                    saveRememberUser(rememberUser)
                    callback(true)
                } else callback(false)
            }

            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable?) {
                //responseBody?.let { liveLoginStatus.value = "Failed! ${String(it)}" }
                error?.let { println("Throwable was ${error.message}") }
            }
        })
    }

    //TODO: How does this change when we find out what the remember checkbox does?
    /**
     * Save user preferences to persistent storage.
     */
    internal fun logOut() {
        loggedOut = true
        liveUser.value = null
        liveIsLoggedIn.value = false
        sharedPrefs.edit().apply {
            putBoolean(Tags.PREFS_LOGIN_HANDLE, false)
            putString(Tags.PREFS_USERNAME_HANDLE, null)
            putBoolean(Tags.PREFS_REMEMBER_HANDLE, false)
            if(!commit()) println("Could not commit changes to shared prefs.")
        }
    }

    /**
     * Save the current login status to persistent storage.
     *
     * @param loginStatus The current login status of the user.
     */
    internal fun saveLoginStatus(loginStatus: Boolean) {
        sharedPrefs.edit().apply {
            putBoolean(Tags.PREFS_LOGIN_HANDLE, loginStatus)
            if(!commit()) println("Could not commit changes to shared prefs.")
        }
        println("login status saved")
        liveIsLoggedIn.value = loginStatus
    }

    /**
     * Save username to persistent storage.
     *
     * @param username The user's username.
     */
    internal fun saveUsername(username: String?) {
        username?.let {
            sharedPrefs.edit().apply {
                putString(Tags.PREFS_USERNAME_HANDLE, it)
                if(!commit()) println("Could not commit changes to shared prefs.")
            }
            println("username saved")
            liveUser.value = it
        }
    }

    /**
     * Save rememberUser parameter to persistent storage.
     *
     * @param rememberUser User's preference on whether they'd like to be remembered on the next log in or not.
     */
    internal fun saveRememberUser(rememberUser: Boolean) {
        sharedPrefs.edit().apply {
            putBoolean(Tags.PREFS_REMEMBER_HANDLE, rememberUser)
            if(!commit()) println("Could not commit changes to shared prefs.")
        }
        println("remember user saved")
        liveRememberUser.value = rememberUser
    }

    //Document this if and when it starts being used
    fun cacheUserGroups(callback: ((Boolean) -> Unit)? = null) {
        println("Request made and username is true")
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

    /**
     * Sets current username's firebase token on the backend server.
     */
    fun updateFirebaseToken() {
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

    //Save important data to SharedPrefs for next login
    override fun onCleared() {
        liveIsLoggedIn.value?.let { saveLoginStatus(it) }
        liveUser.value?.let { saveUsername(it) }
        liveRememberUser.value?.let { saveRememberUser(it) }
        super.onCleared()
    }

    //Dev tools

    /**
     * Automatically log a user in with the following username:
     *
     * username: tnoah122
     */
    private fun autoLogin() {
        sharedPrefs.edit().apply {
            putBoolean(Tags.PREFS_LOGIN_HANDLE, true)
            putString(Tags.PREFS_USERNAME_HANDLE, "tnoah122")
            putBoolean(Tags.PREFS_REMEMBER_HANDLE, true)
            if(!commit()) println("Could not commit changes to shared preferences.")
        }
    }

}

/*
*
* TODO:
*   group list cache in sending requests with no username present on init. Possibly because it hasn't been read from the database yet.
*   Get real name from server in caching call. Tell cy to send you the extra information.
*
* */