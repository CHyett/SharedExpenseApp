package com.example.sharedexpenseapp

import android.os.Bundle
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
import com.example.sharedexpenseapp.databinding.LoginFragmentBinding
import com.google.android.material.snackbar.Snackbar


private const val USERNAME_REGEX = "^[A-Z0-9a-z]{7,15}$"
private const val USERNAME_ERROR = "Please enter between 7-15 alphanumeric characters"
//private const val PASSWORD_REGEX = """^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[*.!@$%^&(){}[\]:;<>,.?\/~_+\-=|\\\\]).{8,32}$"""
private const val PASSWORD_ERROR = "Your password must be between 8-15 alphanumeric characters"


class LoginFragment : Fragment() {

    companion object { fun newInstance() = LoginFragment() }

    private lateinit var viewModel: LoginViewModel
    private lateinit var binding: LoginFragmentBinding
    private lateinit var navController: NavController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.login_fragment, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this
        val validation = AwesomeValidation(ValidationStyle.COLORATION)
        validation.addValidation(binding.loginUsernameEddittext, USERNAME_REGEX, USERNAME_ERROR)
        //validation.addValidation(binding.loginPasswordEdittext, PASSWORD_REGEX, PASSWORD_ERROR)
        viewModel.loginStatus.observe(viewLifecycleOwner, Observer {
            if(it == "you ain't registered biatch.")
                Snackbar.make(binding.submitButton, it, Snackbar.LENGTH_LONG).setAction("Action", null).show()
            else
                Toast.makeText(activity, it, Toast.LENGTH_LONG).show()
        })
        binding.submitButton.setOnClickListener {
            if(validation.validate())
                viewModel.submit()}
        binding.registrationLink.setOnClickListener { navController.navigate(R.id.action_loginFragment_to_registerFragment) }
    }

}


/*
* Notes:
* Do I need to use "setContentView()" inside of onActivityCreated?
*
* TODO:
*  Should I validate username and password fields before sending?
* */