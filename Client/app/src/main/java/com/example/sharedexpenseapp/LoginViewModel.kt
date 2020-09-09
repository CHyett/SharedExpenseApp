package com.example.sharedexpenseapp


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {
    internal val liveUsername = MutableLiveData<String>()

    internal val livePassword = MutableLiveData<String>()

    internal fun submit() {
        val user = User(liveUsername.value!!, livePassword.value!!, "", "")
        //TODO: Implement the rest of this function
    }

}
