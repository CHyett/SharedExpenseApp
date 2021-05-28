package com.partem.application.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel: ViewModel() {

    /**
     * LiveData to hold the user's username.
     */
    val liveUsername = MutableLiveData<String>()

    /**
     * LiveData to hold the user's password.
     */
    val livePassword = MutableLiveData<String>()

    /**
     * LiveData to hold the user's preference on whether they would like to be remembered on the next login.
     */
    val liveIsChecked = MutableLiveData<Boolean>()

}