package com.partem.application.registration

import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.partem.application.R
import com.partem.application.databinding.RegisterFragmentBinding
import com.partem.application.mainactivity.MainActivityViewModel


private const val RESULT_LOAD_IMAGE = 20
private const val USERNAME_REGEX = "^[A-Z0-9a-z]{7,15}$"
private const val USERNAME_ERROR = "Please enter between 7-15 alphanumeric characters"
//private const val PASSWORD_REGEX = """^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[*.!@$%^&(){}[\]:;<>,.?\/~_+\-=|\\\\]).{8,32}$"""
private const val PASSWORD_ERROR = "Your password must be between 8-15 alphanumeric characters"
private const val CONFIRM_PASSWORD_ERROR = "This must match your password"
private const val EMAIL_ERROR = "Your eMail is invalid"

class RegisterFragment : Fragment() {

    /**
     * DataBinding reference for register fragment.
     */
    private lateinit var binding: RegisterFragmentBinding

    /**
     * Nav controller to navigate between fragments.
     */
    private lateinit var navController: NavController

    /**
     * ViewModel for the register fragment
     */
    private lateinit var viewModel: RegisterViewModel

    /**
     * The main ViewModel for the entire app. Stores values that are shared across multiple fragments.
     */
    private val sharedViewModel: MainActivityViewModel by activityViewModels()

    /**
     * Registration regex matching criteria
     */
    private val usernameRegex = Regex(USERNAME_REGEX)
    //private val passwordRegex = Regex(PASSWORD_REGEX)
    private val emailRegex = Patterns.EMAIL_ADDRESS.toRegex()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.register_fragment, container, false)
        viewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this
        sharedViewModel.setAppBackgroundDrawable(AppCompatResources.getDrawable(requireContext(), R.color.colorSecondary)!!)
        sharedViewModel.lockNavDrawer(true)
        sharedViewModel.hideToolbar(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        //Form validation
        /*val validation = AwesomeValidation(ValidationStyle.COLORATION)
        validation.addValidation(binding.registerUsernameEdittext, USERNAME_REGEX, USERNAME_ERROR)
        //validation.addValidation(binding.registerScreenPasswordEdittext, PASSWORD_REGEX, PASSWORD_ERROR)
        validation.addValidation(binding.registerConfirmPasswordEdittext, { it == viewModel.newUserPassword.value }, CONFIRM_PASSWORD_ERROR)
        validation.addValidation(binding.registerEmailEdittext, Patterns.EMAIL_ADDRESS, EMAIL_ERROR)*/

        //LiveData observers
        viewModel.registrationStatus.observe(viewLifecycleOwner, {
            Toast.makeText(activity, it, Toast.LENGTH_SHORT).show()
        })
        viewModel.newUserUsername.observe(viewLifecycleOwner, {
            val matchStatus = it.matches(usernameRegex)
            if(matchStatus && viewModel.isInvalidUsername)
                viewModel.isInvalidUsername = false
            else if(!matchStatus && !viewModel.isInvalidUsername)
                viewModel.isInvalidUsername = true
        })
        viewModel.newUserPassword.observe(viewLifecycleOwner, {
            //val matchStatus = it.matches(PasswordRegex)
            //This should be deleted when the password regex works
            val notEmpty = it.isNotEmpty()
            if(notEmpty && viewModel.isInvalidPassword)
                viewModel.isInvalidPassword = false
            else if(!notEmpty && !viewModel.isInvalidPassword)
                viewModel.isInvalidPassword = true
        })
        viewModel.newUserEmail.observe(viewLifecycleOwner, {
            val matchStatus = it.matches(emailRegex)
            if(matchStatus && viewModel.isInvalidEmail)
                viewModel.isInvalidEmail = false
            else if(!matchStatus && !viewModel.isInvalidEmail)
                viewModel.isInvalidEmail = true
        })


        //Click listeners
        /*binding.registerFragmentImage.setOnClickListener {
            startActivityForResult(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), RESULT_LOAD_IMAGE)
        }*/
        /*binding.registerFragmentSubmitButton.setOnClickListener {
            if(validation.validate()) {
                if(isConnected(context as Application)) {
                    viewModel.register {
                        CoroutineScope(Dispatchers.IO).launch {
                            coroutineScope {
                                sharedViewModel.saveUsername(it)
                                sharedViewModel.saveLoginStatus(true)
                            }
                            navController.popBackStack(R.id.login_fragment, true)
                        }
                    }
                } else {
                    Toast.makeText(context, com.partem.application.util.NOT_CONNECTED_MESSAGE, Toast.LENGTH_SHORT).show()
                }
            }
        }*/
        //Add the next line of code when this fragment's layout is complete.
        //binding.registerFragmentAgreementTextview.movementMethod = LinkMovementMethod.getInstance()
    }

    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data?.data != null) {
            viewModel.profilePicturePath = getRealPathFromURI(data.data!!)
            Glide.with(this).load(data.data).into(binding.registerFragmentImage)
        }
    }*/

    /**
     * Finds the image path from a URI.
     *
     * @param contentURI The URI belonging to the image resource.
     *
     * @return The file path of the profile picture image.
     */
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
*/