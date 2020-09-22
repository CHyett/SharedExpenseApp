package com.example.sharedexpenseapp

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.sharedexpenseapp.databinding.HomePageFragmentBinding
import com.github.nkzawa.socketio.client.IO
import com.google.android.material.snackbar.Snackbar
import java.lang.Exception
import com.github.nkzawa.socketio.client.Socket

class HomePageFragment : Fragment() {

    companion object { fun newInstance() = LoginFragment() }

    private lateinit var viewModel: HomePageViewModel

    private lateinit var binding: HomePageFragmentBinding

    private lateinit var navController: NavController

    private lateinit var socket: Socket

    private val sharedViewModel: MainActivityViewModel by activityViewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(HomePageViewModel::class.java)
        if(sharedViewModel.navController == null) {
            navController = findNavController()
            sharedViewModel.navController = navController
        } else {
            navController = sharedViewModel.navController!!
        }
        sharedViewModel.orientation.value = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
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

        //Test code that should be removed at some point
        try {
            socket = IO.socket("https://ourapp.live/")
            socket.connect()
        } catch(e: Exception) {
            Toast.makeText(activity, e.message, Toast.LENGTH_LONG).show()
        }


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