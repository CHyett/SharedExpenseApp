package com.partem.application.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.partem.application.R

class ProfileFragmentViewModel: ViewModel() {

    private val liveProfilePictureImage = MutableLiveData(R.drawable.blank_profile)
    val profilePictureImage: LiveData<Int>
        get() = liveProfilePictureImage

    private val liveUserGroups = MutableLiveData("0")
    val userGroups: LiveData<String>
        get() = liveUserGroups

}