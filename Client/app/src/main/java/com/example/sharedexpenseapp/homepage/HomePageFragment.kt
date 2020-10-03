package com.example.sharedexpenseapp.homepage

import android.Manifest
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.sharedexpenseapp.login.LoginFragment
import com.example.sharedexpenseapp.R
import com.example.sharedexpenseapp.databinding.HomePageFragmentBinding
import com.example.sharedexpenseapp.mainactivity.MainActivityViewModel
import com.google.android.material.snackbar.Snackbar

class HomePageFragment : Fragment() {

    companion object { fun newInstance() = LoginFragment() }

    //Exclusive ViewModel for HomePageFragment
    private lateinit var viewModel: HomePageViewModel

    //App nav controller
    private lateinit var navController: NavController

    //Binding for interacting with ui components
    private lateinit var binding: HomePageFragmentBinding

    //Main ViewModel for the whole app
    private val sharedViewModel: MainActivityViewModel by activityViewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(HomePageViewModel::class.java)

        //Prevent can't find nav controller in onCreate error
        if(sharedViewModel.navController == null) {
            navController = findNavController()
            sharedViewModel.navController = navController
        } else {
            navController = sharedViewModel.navController!!
        }

        //Lock screen to prevent glitches. You should re-simulate them and log what they do
        sharedViewModel.orientation.value = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        //LiveData observers
        sharedViewModel.isLoggedIn.observe(this, Observer {
            if (!it) {
                val navOptions = NavOptions.Builder().setPopUpTo(R.id.homePageFragment, false).build()
                navController.navigate(R.id.loginFragment, null, navOptions)
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.home_page_fragment, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //If username is present, unlock screen, otherwise send user to login fragment
        sharedViewModel.user.observe(viewLifecycleOwner, Observer {
            if(it == null) {
                navController.navigate(R.id.loginFragment)
            } else {
                askForPermissions()
                sharedViewModel.orientation.value = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
            }
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this

        //LiveData observers
        viewModel.liveGroupName.observe(viewLifecycleOwner, Observer {
            viewModel.liveIsClickable.value = it.isNotEmpty()
        })

        //Click listeners
        binding.homeFragmentCreateGroupButton.setOnClickListener { viewModel.showCreateGroupForm() }
        binding.homeFragmentSubmitGroupNameButton.setOnClickListener { view ->
            viewModel.createGroup(sharedViewModel.user.value!!) {
                Snackbar.make(view, it, Snackbar.LENGTH_LONG).show()
            }
        }
        binding.homeFragmentLogOutButton.setOnClickListener {
            sharedViewModel.logOut()
        }

        //Listeners for testing. Should be deleted at some point
        binding.homeFragmentPayButton.setOnClickListener {
            Toast.makeText(activity, sharedViewModel.isLoggedIn.value.toString(), Toast.LENGTH_LONG).show()
        }
        binding.homeFragmentChargeButton.setOnClickListener {
            Toast.makeText(activity, sharedViewModel.user.value, Toast.LENGTH_LONG).show()
        }

    }

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
*
* TODO:
*  Implement what happens if the user rejects permissions in onRequestPermissionsResult.
*
* */