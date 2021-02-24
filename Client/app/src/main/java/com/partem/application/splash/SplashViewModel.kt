package com.partem.application.splash

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SplashViewModel: ViewModel() {

    val liveTransitionToApp = MutableLiveData(false)

}