package com.example.sharedexpenseapp.homepage

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
            if(it == null)
                navController.navigate(R.id.loginFragment)
            else
                sharedViewModel.orientation.value = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
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

}