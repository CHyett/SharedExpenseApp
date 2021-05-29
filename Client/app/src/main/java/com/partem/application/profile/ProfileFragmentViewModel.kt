package com.partem.application.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.partem.application.R

/**
 * ViewModel for the profile fragment.
 */
class ProfileFragmentViewModel: ViewModel() {

    /**
     * LiveData containing the image data for the profile picture.
     */
    private val liveProfilePictureImage = MutableLiveData(R.drawable.blank_profile)
    val profilePictureImage: LiveData<Int>
        get() = liveProfilePictureImage

    /**
     * LiveData containing the number of groups that the user is a member of.
     */
    private val liveUserGroups = MutableLiveData("0")
    val userGroups: LiveData<String>
        get() = liveUserGroups

}