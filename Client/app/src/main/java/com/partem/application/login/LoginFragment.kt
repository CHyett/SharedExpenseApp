package com.partem.application.login

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.partem.application.util.isConnected
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.basgeekball.awesomevalidation.AwesomeValidation
import com.basgeekball.awesomevalidation.ValidationStyle
import com.partem.application.R
import com.partem.application.databinding.LoginBinding
import com.partem.application.databinding.LoginFragmentBinding
import com.partem.application.databinding.LogoutBinding
import com.partem.application.mainactivity.MainActivityViewModel
import com.partem.application.util.BlurController


private const val USERNAME_REGEX = "^[A-Z0-9a-z]{7,15}$"
private const val USERNAME_ERROR = "Please enter between 7-15 alphanumeric characters"
private const val PASSWORD_REGEX = """^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,15}$"""
private const val PASSWORD_ERROR = "Your password must be between 8-15 alphanumeric characters"


class LoginFragment : Fragment() {

    /**
     * Data binding object used to reference login fragment views.
     */
    private lateinit var binding: LoginFragmentBinding

    /**
     * Navigation controller for the main activity. This is used to navigate through UI pages.
     */
    private lateinit var navController: NavController

    /**
     * The main ViewModel for the entire app. Stores values that are shared across multiple fragments.
     */
    private val sharedViewModel: MainActivityViewModel by activityViewModels()

    /**
     * ViewModel to hold data for the login fragment.
     */
    private lateinit var viewModel: LoginViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.login_fragment, container, false)
        BlurController.subjectView = binding.loginFragmentRoot
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        navController = findNavController()
        askForPermissions()
        binding.viewmodel = viewModel
        binding.onLogout = sharedViewModel.loggedOut
        binding.lifecycleOwner = this
        if(sharedViewModel.loggedOut) binding.logoutLayout.viewmodel = viewModel else binding.loginLayout.viewmodel = viewModel
        sharedViewModel.setAppBackgroundDrawable(AppCompatResources.getDrawable(requireContext(), R.color.colorSecondary)!!)
        sharedViewModel.lockNavDrawer(true)
        sharedViewModel.hideToolbar(true)

        //LiveData observers

        //Click listeners
        handleFormValidation()

    }

    /**
     * Function to handle assigning click listeners and form validation to the proper layout. (Login or logout)
     */
    private fun handleFormValidation() {
        val validation = AwesomeValidation(ValidationStyle.COLORATION)
        when(sharedViewModel.loggedOut) {
            true -> {
                validation.addValidation(binding.logoutLayout.logoutFragmentEmailInput, USERNAME_REGEX, USERNAME_ERROR)
                validation.addValidation(binding.logoutLayout.logoutFragmentPasswordInput, PASSWORD_REGEX, PASSWORD_ERROR)
                binding.logoutLayout.logoutFragmentSignUpButton.setOnClickListener { navController.navigate(R.id.action_loginFragment_to_registerFragment) }
                binding.logoutLayout.logoutFragmentSignInButton.setOnClickListener {
                    if (validation.validate()) {
                        if(isConnected(requireContext().applicationContext)) {
                            sharedViewModel.logIn(viewModel.liveUsername.value!!, viewModel.livePassword.value!!, viewModel.liveIsChecked.value!!) {
                                if(!it) Toast.makeText(activity, "Sorry, we don't recognize an account with those credentials.", Toast.LENGTH_LONG).show()
                            }
                        } else Toast.makeText(context, com.partem.application.util.NOT_CONNECTED_MESSAGE, Toast.LENGTH_SHORT).show()
                    }
                }
            }
            false -> {
                validation.addValidation(binding.loginLayout.loginFragmentEmailInput, USERNAME_REGEX, USERNAME_ERROR)
                validation.addValidation(binding.loginLayout.loginFragmentPasswordInput, PASSWORD_REGEX, PASSWORD_ERROR)
                binding.loginLayout.loginFragmentSignUpButton.setOnClickListener { navController.navigate(R.id.action_loginFragment_to_registerFragment) }
                binding.loginLayout.loginFragmentSignInButton.setOnClickListener {
                    if (validation.validate()) {
                        if(isConnected(requireContext().applicationContext)) {
                            sharedViewModel.logIn(viewModel.liveUsername.value!!, viewModel.livePassword.value!!, viewModel.liveIsChecked.value!!) {
                                if(!it) Toast.makeText(activity, "Sorry, we don't recognize an account with those credentials.", Toast.LENGTH_LONG).show()
                            }
                        } else Toast.makeText(context, com.partem.application.util.NOT_CONNECTED_MESSAGE, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    /**
     * This method asks the user for the permissions that this app needs to operate properly.
     */
    private fun askForPermissions() {
        val externalWriteCheck = ContextCompat.checkSelfPermission(requireActivity().applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val networkStateCheck = ContextCompat.checkSelfPermission(requireActivity().applicationContext, Manifest.permission.ACCESS_NETWORK_STATE)
        val internetCheck = ContextCompat.checkSelfPermission(requireActivity().applicationContext, Manifest.permission.INTERNET)
        val externalReadCheck = ContextCompat.checkSelfPermission(requireActivity().applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE)
        val storageWriteString = Manifest.permission.WRITE_EXTERNAL_STORAGE
        val internetString = Manifest.permission.INTERNET
        val networkString = Manifest.permission.ACCESS_NETWORK_STATE
        val storageReadString = Manifest.permission.READ_EXTERNAL_STORAGE
        when((-1 * externalWriteCheck) + (-2 * networkStateCheck) + (-4 * internetCheck) + (-8 * externalReadCheck)) {
            1 -> ActivityCompat.requestPermissions(requireActivity(), arrayOf(storageWriteString), 1)
            2 -> ActivityCompat.requestPermissions(requireActivity(), arrayOf(networkString), 2)
            3 -> ActivityCompat.requestPermissions(requireActivity(), arrayOf(storageWriteString, networkString), 3)
            4 -> ActivityCompat.requestPermissions(requireActivity(), arrayOf(internetString), 4)
            5 -> ActivityCompat.requestPermissions(requireActivity(), arrayOf(storageWriteString, internetString), 5)
            6 -> ActivityCompat.requestPermissions(requireActivity(), arrayOf(networkString, internetString), 6)
            7 -> ActivityCompat.requestPermissions(requireActivity(), arrayOf(storageWriteString, networkString, internetString), 7)
            8 -> ActivityCompat.requestPermissions(requireActivity(), arrayOf(storageReadString), 8)
            9 -> ActivityCompat.requestPermissions(requireActivity(), arrayOf(storageReadString, storageWriteString), 9)
            10 -> ActivityCompat.requestPermissions(requireActivity(), arrayOf(storageReadString, networkString), 10)
            11 -> ActivityCompat.requestPermissions(requireActivity(), arrayOf(storageReadString, storageWriteString, networkString), 11)
            12 -> ActivityCompat.requestPermissions(requireActivity(), arrayOf(storageReadString, internetString), 12)
            13 -> ActivityCompat.requestPermissions(requireActivity(), arrayOf(storageReadString, internetString, storageWriteString), 13)
            14 -> ActivityCompat.requestPermissions(requireActivity(), arrayOf(storageReadString, internetString, networkString), 14)
            15 -> ActivityCompat.requestPermissions(requireActivity(), arrayOf(storageReadString, internetString, networkString, storageWriteString), 15)
        }
    }

    //Implement what happens if the user rejects any permissions
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    }

}


/*
* Notes:
* Do I need to use "setContentView()" inside of onActivityCreated?
*
* TODO:
*  Set back stack so that users do not go back to login if they hit back button.
*  Change login status to type of string and pair if possible. (experimental idea. Haven't used pairs for live data)
*  Implement what happens if the user rejects permissions in onRequestPermissionsResult.
*  Get Better looking form validation.
*
* */