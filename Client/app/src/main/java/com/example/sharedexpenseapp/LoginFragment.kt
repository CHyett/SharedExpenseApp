package com.example.sharedexpenseapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.sharedexpenseapp.databinding.LoginFragmentBinding


class LoginFragment : Fragment() {

    companion object { fun newInstance() = LoginFragment() }

    private lateinit var viewModel: LoginViewModel
    private lateinit var binding: LoginFragmentBinding
    private lateinit var navController: NavController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.login_fragment, container, false)
        binding.lifecycleOwner = this
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        binding.viewmodel = viewModel
        return binding.root
        //return inflater.inflate(R.layout.login_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.submitButton.setOnClickListener { viewModel.submit() }
        binding.registrationLink.setOnClickListener { navController.navigate(R.id.action_loginFragment_to_registerFragment) }
    }

}


/*
* Notes:
* Do I need to use "setContentView()" inside of onActivityCreated?
*
* TODO "binding" not working. Click listener for register isn't working. Maybe it's because you declared binding wrong
* */