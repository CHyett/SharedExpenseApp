package com.example.sharedexpenseapp.registration

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sharedexpenseapp.R
import com.example.sharedexpenseapp.enums.Endpoints
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

    //2-way data binding for username EditText
    val newUserUsername = MutableLiveData<String>()

    //2-way data binding for password EditText
    val newUserPassword = MutableLiveData<String>()

    //2-way data binding for email EditText
    val newUserEmail = MutableLiveData<String>()

    //2-way data binding for progress bar
    internal val liveProgress = MutableLiveData(0)

    //2-way data binding for rocket ImageView
    val liveProgressAnimatable = MutableLiveData(R.drawable.rocketnofire)

    //Registration status LiveData
    private val liveRegistrationStatus = MutableLiveData<String>()
    internal val registrationStatus: LiveData<String>
        get() = liveRegistrationStatus

    //LiveData to disable registration button
    private val liveIsRegistrationButtonEnabled = MutableLiveData<Boolean>(true)
    val isRegistrationButtonEnabled: LiveData<Boolean>
        get() = liveIsRegistrationButtonEnabled

    private val httpClient = AsyncHttpClient()

    internal var isInvalidUsername = true
    internal var isInvalidPassword = true
    internal var isInvalidEmail = true

    internal var profilePicturePath: String? = null

    internal fun register(callback: (name: String) -> Unit) {
        liveIsRegistrationButtonEnabled.value = false
        val username = newUserUsername.value!!
        val params = RequestParams()
        params.put("username", newUserUsername.value)
        params.put("password", newUserPassword.value)
        params.put("email", newUserEmail.value)
        httpClient.post(Endpoints.REGISTER_ENDPOINT.endpoint, params, object : AsyncHttpResponseHandler() {
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

    private fun clearEditTexts() {
        newUserUsername.postValue("")
        newUserPassword.postValue("")
        newUserEmail.postValue("")
    }

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
                val uploadUrl = java.net.URL(Endpoints.PROFILE_PIC_ENDPOINT.endpoint)
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