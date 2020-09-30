package com.example.sharedexpenseapp.registration

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.basgeekball.awesomevalidation.AwesomeValidation
import com.basgeekball.awesomevalidation.ValidationStyle
import com.bumptech.glide.Glide
import com.example.sharedexpenseapp.R
import com.example.sharedexpenseapp.RegisterViewModel
import com.example.sharedexpenseapp.databinding.RegisterFragmentBinding


private const val RESULT_LOAD_IMAGE = 20
private const val USERNAME_REGEX = "^[A-Z0-9a-z]{7,15}$"
private const val USERNAME_ERROR = "Please enter between 7-15 alphanumeric characters"
//private const val PASSWORD_REGEX = """^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[*.!@$%^&(){}[\]:;<>,.?\/~_+\-=|\\\\]).{8,32}$"""
private const val PASSWORD_ERROR = "Your password must be between 8-15 alphanumeric characters"
private const val EMAIL_ERROR = "Your eMail is invalid"

class RegisterFragment : Fragment() {

    private lateinit var binding: RegisterFragmentBinding
    private lateinit var navController: NavController
    private lateinit var viewModel: RegisterViewModel

    companion object { fun newInstance() = RegisterFragment() }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.register_fragment, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this

        //Make terms of service open in browser
        binding.registerFragmentAgreementTextview.movementMethod = LinkMovementMethod.getInstance()

        //Form validation
        val validation = AwesomeValidation(ValidationStyle.COLORATION)
        validation.addValidation(binding.registerUsernameEdittext, USERNAME_REGEX, USERNAME_ERROR)
        //validation.addValidation(binding.registerScreenPasswordEdittext, PASSWORD_REGEX, PASSWORD_ERROR)
        validation.addValidation(binding.registerEmailEdittext, Patterns.EMAIL_ADDRESS, EMAIL_ERROR)

        //LiveData observers
        viewModel.registrationStatus.observe(viewLifecycleOwner, Observer {
            Toast.makeText(activity, it, Toast.LENGTH_SHORT).show()
        })

        //Click listeners
        binding.registerFragmentImage.setOnClickListener {
            startActivityForResult(Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI), RESULT_LOAD_IMAGE)
        }
        binding.registerFragmentSubmitButton.setOnClickListener {
            if(validation.validate()) {
                viewModel.register()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data?.data != null) {
            Glide.with(this).load(data.data).into(binding.registerFragmentImage)
        }
    }

}


/*
*
* TODO:
*  Add regex to allow special characters for passwords (Regex is there but it doesn't work) ---Fix regex and add password validation because you omitted it
*  Password should be min: 8 characters and max: 50 characters
*
* */