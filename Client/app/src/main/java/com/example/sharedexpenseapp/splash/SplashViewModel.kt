package com.example.sharedexpenseapp.splash

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SplashViewModel: ViewModel() {

    val liveTransitionToApp = MutableLiveData(false)

}