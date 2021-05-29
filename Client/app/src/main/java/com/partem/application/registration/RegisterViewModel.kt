package com.partem.application.registration

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.partem.application.R
import com.partem.application.enums.Endpoints
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.DataOutputStream
import java.io.File
import java.io.FileInputStream
import java.net.HttpURLConnection
import java.net.MalformedURLException


class RegisterViewModel : ViewModel() {

    /**
     * 2-way data binding for username EditText
     */
    val newUserUsername = MutableLiveData<String>()

    /**
     * 2-way data binding for password EditText
     */
    val newUserPassword = MutableLiveData<String>()

    /**
     * 2-way data binding for email EditText
     */
    val newUserEmail = MutableLiveData<String>()

    //Registration status LiveData
    private val liveRegistrationStatus = MutableLiveData<String>()
    internal val registrationStatus: LiveData<String>
        get() = liveRegistrationStatus

    /**
     * LiveData to disable registration button
     */
    private val liveIsRegistrationButtonEnabled = MutableLiveData<Boolean>(true)
    val isRegistrationButtonEnabled: LiveData<Boolean>
        get() = liveIsRegistrationButtonEnabled

    /**
     * Object to make async HTTP requests to the server.
     */
    private val httpClient = AsyncHttpClient()

    /**
     * Username validity flag.
     */
    internal var isInvalidUsername = true

    /**
     * Password validity flag.
     */
    internal var isInvalidPassword = true

    /**
     * EMail validity flag.
     */
    internal var isInvalidEmail = true

    /**
     * File system path to profile picture image.
     */
    internal var profilePicturePath: String? = null

    /**
     * Sends the client's eMail, username, and password to the server to be processed and registered.
     *
     * @param callback A callback function that will be called on success or on failure. (callback(true) if registration succeeded, callback(false) otherwise)
     */
    internal fun register(callback: (name: String) -> Unit) {
        liveIsRegistrationButtonEnabled.value = false
        val username = newUserUsername.value!!
        val params = RequestParams()
        params.put("username", newUserUsername.value)
        params.put("password", newUserPassword.value)
        params.put("email", newUserEmail.value)
        httpClient.post(Endpoints.REGISTER_ENDPOINT, params, object : AsyncHttpResponseHandler() {
                override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?) {
                    responseBody?.let { liveRegistrationStatus.value = String(it) }
                    profilePicturePath?.let { uploadProfilePicture(username) }
                    callback(username)
                }

                override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable?) {
                    liveIsRegistrationButtonEnabled.value = true
                    responseBody?.let { liveRegistrationStatus.value = "Failed! ${error?.message}" }
                    error?.let { println("Throwable was ${error.message}") }
                }
            })
    }

    /**
     * Clears any text inside of the username, password, and eMail text fields.
     */
    private fun clearEditTexts() {
        newUserUsername.postValue("")
        newUserPassword.postValue("")
        newUserEmail.postValue("")
    }

    /**
     * Sends the user's optional profile picture to the server.
     *
     * @param username The username of the user that this image belongs to.
     */
    private fun uploadProfilePicture(username: String) {
        val maxBufferSize = 1024 * 1024
        val sourceFile = File(profilePicturePath!! )
        val fileName = "$username.${profilePicturePath!!.substringAfterLast('.')}"

        //Check if the file is available and then upload picture
        if (!sourceFile.isFile) {
            println("Source File not exist: $profilePicturePath")
        } else {
            try {
                val inStream = FileInputStream(sourceFile)
                val uploadUrl = java.net.URL(Endpoints.PROFILE_PIC_ENDPOINT)
                val conn = uploadUrl.openConnection() as HttpURLConnection
                conn.doInput = true
                conn.doOutput = true
                conn.useCaches = false
                conn.requestMethod = "POST"
                conn.setRequestProperty("Connection", "Keep-Alive")
                conn.setRequestProperty("ENCTYPE", "multipart/form-data")
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=*****")
                conn.setRequestProperty("uploaded_file", username)

                CoroutineScope(Dispatchers.IO).launch {
                    val dos = DataOutputStream(conn.outputStream)
                    dos.writeBytes("--*****\r\n")
                    dos.writeBytes("Content-Disposition: form-data; name=image;filename=$fileName\r\n")
                    dos.writeBytes("\r\n")

                    var bufferSize = inStream.available().coerceAtMost(maxBufferSize)
                    val buffer = ByteArray(bufferSize)

                    // read file and write it into form...
                    var bytesRead = inStream.read(buffer, 0, bufferSize)

                    while (bytesRead > 0) {
                        dos.write(buffer, 0, bufferSize)
                        bufferSize = inStream.available().coerceAtMost(maxBufferSize)
                        bytesRead = inStream.read(buffer, 0, bufferSize)
                    }

                    // send multipart form data necesssary after file data...
                    dos.writeBytes("\r\n")

                    dos.writeBytes("--*****--\r\n")

                    println("Uploaded file => HTTP Response is: ${conn.responseMessage}: ${conn.responseCode}")

                    inStream.close();
                    dos.flush()
                    dos.close()
                    conn.disconnect()
                    clearEditTexts()
                }
            } catch (error: MalformedURLException) {
                println("MalformedURLException: ")
                error.printStackTrace()
            } catch (error: Exception) {
                println("Other extraneous error: ")
                error.printStackTrace()
            }
        }
    }

}