package com.example.sharedexpenseapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.sharedexpenseapp.databinding.RegisterFragmentBinding


class RegisterFragment : Fragment() {

    private lateinit var binding: RegisterFragmentBinding
    private lateinit var navController: NavController
    private lateinit var viewModel: RegisterViewModel

    companion object {
        fun newInstance() = RegisterFragment()
    }


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
        binding.registerFragmentRegisterButton.setOnClickListener {
            Toast.makeText(activity, User(viewModel.newUserUsername.value!!, viewModel.newUserPassword.value!!, viewModel.newUserEmail.value!!, viewModel.newUserDateOfBirth.value!!).toString(), Toast.LENGTH_LONG).show()
            viewModel.register() }
    }

}
