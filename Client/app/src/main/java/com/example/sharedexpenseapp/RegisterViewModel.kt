package com.example.sharedexpenseapp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RegisterViewModel : ViewModel() {

    internal val newUserUsername = MutableLiveData<String>()

    internal val newUserPassword = MutableLiveData<String>()

    internal val newUserDateOfBirth = MutableLiveData<String>()

    internal val newUserEmail = MutableLiveData<String>()

    internal fun register() {

    }

}
