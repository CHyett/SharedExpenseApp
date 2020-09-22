package com.example.sharedexpenseapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.basgeekball.awesomevalidation.AwesomeValidation
import com.basgeekball.awesomevalidation.ValidationStyle
import com.example.sharedexpenseapp.databinding.LoginFragmentBinding
import com.google.android.material.snackbar.Snackbar


private const val USERNAME_REGEX = "^[A-Z0-9a-z]{7,15}$"
private const val USERNAME_ERROR = "Please enter between 7-15 alphanumeric characters"
//private const val PASSWORD_REGEX = """^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[*.!@$%^&(){}[\]:;<>,.?\/~_+\-=|\\\\]).{8,32}$"""
private const val PASSWORD_ERROR = "Your password must be between 8-15 alphanumeric characters"


class LoginFragment : Fragment() {

    companion object {
        fun newInstance() = LoginFragment()
    }

    private lateinit var binding: LoginFragmentBinding

    private lateinit var navController: NavController

    private val sharedViewModel: MainActivityViewModel by activityViewModels() //Shared by this fragment and HomePageFragment


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.login_fragment, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
        sharedViewModel.saveLoginStatus(false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.viewmodel = sharedViewModel
        binding.lifecycleOwner = this

        //Form validation
        val validation = AwesomeValidation(ValidationStyle.COLORATION)
        validation.addValidation(binding.loginUsernameEddittext, USERNAME_REGEX, USERNAME_ERROR)
        //validation.addValidation(binding.loginPasswordEdittext, PASSWORD_REGEX, PASSWORD_ERROR)

        //LiveData observers
        sharedViewModel.loginStatus.observe(viewLifecycleOwner, Observer {
                Snackbar.make(binding.submitButton, it, Snackbar.LENGTH_LONG).setAction("Action", null).show()
        })

        //Click listeners
        binding.registrationLink.setOnClickListener { navController.navigate(R.id.action_loginFragment_to_registerFragment) }
        binding.submitButton.setOnClickListener {
            if (validation.validate()) {
                sharedViewModel.logIn {
                    if(it) {
                        sharedViewModel.livePassword.value = ""
                        sharedViewModel.liveUsername.value = ""
                        sharedViewModel.saveLoginStatus(true)
                        sharedViewModel.saveUsername(sharedViewModel.liveUsername.value!!)
                        navController.popBackStack()
                    }
                }
            }
        }
    }

}


/*
* Notes:
* Do I need to use "setContentView()" inside of onActivityCreated?
*
* TODO:
*  Set back stack so that users do not go back to login if they hit back button.
*  Change login status to type of string and pair if possible (experimental idea. Haven't used pairs for live data)
*
* */