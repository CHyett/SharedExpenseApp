package com.example.sharedexpenseapp.registration

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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
    internal val newUserUsername = MutableLiveData<String>()

    //2-way data binding for password EditText
    internal val newUserPassword = MutableLiveData<String>()

    //2-way data binding for email EditText
    internal val newUserEmail = MutableLiveData<String>()

    //2-way data binding for progress bar
    internal val liveProgress = MutableLiveData(0)

    //2-way data binding for rocket ImageView
    internal val liveProgressAnimatable = MutableLiveData(R.drawable.rocketnofire)

    //Registration status LiveData
    private val liveRegistrationStatus = MutableLiveData<String>()
    internal val registrationStatus: LiveData<String>
        get() = liveRegistrationStatus

    //LiveData to disable registration button
    private val liveIsRegistrationButtonEnabled = MutableLiveData<Boolean>(true)
    internal val isRegistrationButtonEnabled: LiveData<Boolean>
        get() = liveIsRegistrationButtonEnabled

    private val httpClient = AsyncHttpClient()

    internal var isInvalidUsername = true
    internal var isInvalidPassword = true
    internal var isInvalidEmail = true

    internal lateinit var profilePicturePath: String

    internal fun register(callback: () -> Unit) {
        liveIsRegistrationButtonEnabled.value = false
        val params = RequestParams()
        params.put("username", newUserUsername.value)
        params.put("password", newUserPassword.value)
        params.put("email", newUserEmail.value)
        httpClient.post(Endpoints.REGISTER_ENDPOINT.endpoint, params, object : AsyncHttpResponseHandler() {
                override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?) {
                    responseBody?.let { liveRegistrationStatus.value = String(it) }
                    clearEditTexts()
                    callback()
                    uploadProfilePicture()
                }

                override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable?) {
                    liveIsRegistrationButtonEnabled.value = true
                    responseBody?.let { liveRegistrationStatus.value = "Failed! ${error?.message}" }
                    error?.let { println("Throwable was ${error.message}") }
                }
            })
    }

    private fun clearEditTexts() {
        newUserUsername.value = ""
        newUserPassword.value = ""
        newUserEmail.value = ""
    }

    private fun uploadProfilePicture() {
        val maxBufferSize = 1024 * 1024
        val sourceFile = File(profilePicturePath)

        //Print profilePictureUri to see what it looks like (delete this code later)
        println("profilePictureUri is: $profilePicturePath")

        //Check if the file is available and then upload picture
        if (!sourceFile.isFile) {
            println("Source File not exist: $profilePicturePath")
        } else {
            try {
                val inStream = FileInputStream(sourceFile)
                val uploadUrl = java.net.URL(Endpoints.PROFILE_PIC_ENDPOINT.endpoint)
                val conn = uploadUrl.openConnection() as HttpURLConnection
                val uploadedFile = "${newUserUsername.value}"
                conn.doInput = true
                conn.doOutput = true
                conn.useCaches = false
                conn.requestMethod = "POST"
                conn.setRequestProperty("Connection", "Keep-Alive")
                conn.setRequestProperty("ENCTYPE", "multipart/form-data")
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=*****")
                conn.setRequestProperty("uploaded_file", "${newUserUsername.value}")
                CoroutineScope(Dispatchers.IO).launch {
                    val dos = DataOutputStream(conn.outputStream)
                    dos.writeBytes("--*****\r\n")
                    dos.writeBytes("Content-Disposition: form-data; name=image;filename=${uploadedFile}\r\n")
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
                    dos.flush();
                    dos.close();
                }
            } catch (error: MalformedURLException) {
                println("MalformedURLException: ")
                error.printStackTrace()
            } catch (error: Exception) {
                println("Other extraneous error:")
                error.printStackTrace()
            }
        }
    }

}