package com.example.sharedexpenseapp.registration

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.method.LinkMovementMethod
import android.transition.ChangeBounds
import android.transition.TransitionManager
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.PathInterpolator
import android.view.animation.Transformation
import android.widget.ProgressBar
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintSet
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.basgeekball.awesomevalidation.AwesomeValidation
import com.basgeekball.awesomevalidation.ValidationStyle
import com.bumptech.glide.Glide
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.example.sharedexpenseapp.R
import com.example.sharedexpenseapp.databinding.RegisterFragmentBinding
import com.example.sharedexpenseapp.mainactivity.MainActivityViewModel
import kotlinx.coroutines.*


private const val RESULT_LOAD_IMAGE = 20
private const val USERNAME_REGEX = "^[A-Z0-9a-z]{7,15}$"
private const val USERNAME_ERROR = "Please enter between 7-15 alphanumeric characters"
//private const val PASSWORD_REGEX = """^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[*.!@$%^&(){}[\]:;<>,.?\/~_+\-=|\\\\]).{8,32}$"""
private const val PASSWORD_ERROR = "Your password must be between 8-15 alphanumeric characters"
private const val CONFIRM_PASSWORD_ERROR = "This must match your password"
private const val EMAIL_ERROR = "Your eMail is invalid"
private const val PROGRESS_BAR_ANIMATION_TIME = 750L

class RegisterFragment : Fragment() {

    //DataBinding object
    private lateinit var binding: RegisterFragmentBinding

    //App navcontroller
    private lateinit var navController: NavController

    //Fragment ViewModel
    private lateinit var viewModel: RegisterViewModel

    //MainActivityViewModel
    private val sharedViewModel: MainActivityViewModel by activityViewModels()

    //Registration regex matching criteria
    private val usernameRegex = Regex(USERNAME_REGEX)
    //private val passwordRegex = Regex(PASSWORD_REGEX)
    private val emailRegex = Patterns.EMAIL_ADDRESS.toRegex()

    //Animation ConstraintSets
    private val constraintSetHide = ConstraintSet()
    private val constraintSetShow = ConstraintSet()

    //Boolean to launch rocket only once
    private var hasRocketLaunched = false

    //Animation for registration ProgressBar
    private lateinit var progressAnimation: ProgressBarAnimation

    companion object { fun newInstance() = RegisterFragment() }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.register_fragment, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        //Initialize progress bar animation
        progressAnimation = ProgressBarAnimation(binding.registerFragmentProgressBarInner, 0, 0)
        progressAnimation.duration = PROGRESS_BAR_ANIMATION_TIME
        binding.registerFragmentProgressBarInner.interpolator = PathInterpolator(0f, 0f, 0.5f, 1f)

        //Make terms of service open in browser
        binding.registerFragmentAgreementTextview.movementMethod = LinkMovementMethod.getInstance()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this
        sharedViewModel.setAppBackgroundDrawable(R.drawable.start_bg)
        sharedViewModel.lockNavDrawer(true)
        sharedViewModel.hideToolbar(true)

        //Animation initialization
        constraintSetHide.clone(binding.registerFragmentRootConstraintLayout)
        constraintSetShow.clone(activity, R.layout.register_fragment_rocket_animation)

        //Form validation
        val validation = AwesomeValidation(ValidationStyle.COLORATION)
        validation.addValidation(binding.registerUsernameEdittext, USERNAME_REGEX, USERNAME_ERROR)
        //validation.addValidation(binding.registerScreenPasswordEdittext, PASSWORD_REGEX, PASSWORD_ERROR)
        validation.addValidation(binding.registerConfirmPasswordEdittext, { it == viewModel.newUserPassword.value }, CONFIRM_PASSWORD_ERROR)
        validation.addValidation(binding.registerEmailEdittext, Patterns.EMAIL_ADDRESS, EMAIL_ERROR)

        //LiveData observers
        viewModel.registrationStatus.observe(viewLifecycleOwner, Observer {
            Toast.makeText(activity, it, Toast.LENGTH_SHORT).show()
        })
        viewModel.newUserUsername.observe(viewLifecycleOwner, Observer {
            val progress = viewModel.liveProgress.value!!
            val matchStatus = it.matches(usernameRegex)
            if(matchStatus && viewModel.isInvalidUsername) {
                viewModel.liveProgress.value = progress + 34
                viewModel.isInvalidUsername = false
            } else if(!matchStatus && !viewModel.isInvalidUsername) {
                viewModel.liveProgress.value = progress - 34
                viewModel.isInvalidUsername = true
            }
        })
        viewModel.newUserPassword.observe(viewLifecycleOwner, Observer {
            val progress = viewModel.liveProgress.value!!
            //val matchStatus = it.matches(PasswordRegex)
            //This should be deleted when the password regex works
            val notEmpty = it.isNotEmpty()
            if(notEmpty && viewModel.isInvalidPassword) {
                viewModel.liveProgress.value = progress + 33
                viewModel.isInvalidPassword = false
            } else if(!notEmpty && !viewModel.isInvalidPassword) {
                viewModel.liveProgress.value = progress - 33
                viewModel.isInvalidPassword = true
            }
        })
        viewModel.newUserEmail.observe(viewLifecycleOwner, Observer {
            val progress = viewModel.liveProgress.value!!
            val matchStatus = it.matches(emailRegex)
            if(matchStatus && viewModel.isInvalidEmail) {
                viewModel.liveProgress.value = progress + 33
                viewModel.isInvalidEmail = false
            } else if(!matchStatus && !viewModel.isInvalidEmail) {
                viewModel.liveProgress.value = progress - 33
                viewModel.isInvalidEmail = true
            }
        })
        viewModel.liveProgress.observe(viewLifecycleOwner, Observer {
            progressAnimation.from = progressAnimation.to
            progressAnimation.to = it
            binding.registerFragmentProgressBarInner.startAnimation(progressAnimation)

            //Launch rocket only once
            if(it == 100 && !hasRocketLaunched) {
                viewModel.liveProgressAnimatable.value = R.drawable.rocketwithfire
                YoYo.with(Techniques.Shake).duration(700).onEnd {
                    val t = ChangeBounds()
                    t.interpolator = PathInterpolator(0.5f,-0.32f,0.9f,0.36f)
                    t.duration = 750L
                    TransitionManager.beginDelayedTransition(binding.registerFragmentRootConstraintLayout, t)
                    constraintSetShow.applyTo(binding.registerFragmentRootConstraintLayout)
                    hasRocketLaunched = true
                }.playOn(binding.registerFragmentProgressBarRightIcon)
            }
        })

        //Click listeners
        binding.registerFragmentImage.setOnClickListener {
            startActivityForResult(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), RESULT_LOAD_IMAGE)
        }
        binding.registerFragmentSubmitButton.setOnClickListener {
            if(validation.validate()) {
                viewModel.register {
                    CoroutineScope(Dispatchers.IO).launch {
                        coroutineScope {
                            sharedViewModel.saveUsername(it)
                            sharedViewModel.saveLoginStatus(true)
                        }
                        navController.popBackStack(R.id.loginFragment, true)
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data?.data != null) {
            viewModel.profilePicturePath = getRealPathFromURI(data.data!!)
            Glide.with(this).load(data.data).into(binding.registerFragmentImage)
        }
    }

    private fun getRealPathFromURI(contentURI: Uri): String {
        val result: String
        val cursor: Cursor? = activity?.contentResolver?.query(contentURI, null, null, null, null)
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.path!!
        } else {
            cursor.moveToFirst()
            val index: Int = cursor.getColumnIndexOrThrow("_data")
            result = cursor.getString(index)
            cursor.close()
        }
        return result
    }

}


/*
*
* TODO:
*  Add regex to allow special characters for passwords (Regex is there but it doesn't work) ---Fix regex and add password validation because you omitted it
*  Password should be min: 8 characters and max: 50 characters
*
* */

private class ProgressBarAnimation(val progBar: ProgressBar, var from: Int, var to: Int): Animation() {

    override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
        super.applyTransformation(interpolatedTime, t)
        val value = from + (to - from) * interpolatedTime
        progBar.progress = value.toInt()
    }

}