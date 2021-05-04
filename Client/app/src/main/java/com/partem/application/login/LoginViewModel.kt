package com.partem.application.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel: ViewModel() {

    val liveUsername = MutableLiveData<String>()

    val livePassword = MutableLiveData<String>()

    val liveIsChecked = MutableLiveData<Boolean>()

}