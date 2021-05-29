package com.partem.application.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.partem.application.R
import com.partem.application.dev_tools.makeHTTPRequest
import com.partem.application.mainactivity.MainActivityViewModel

class Splash : Fragment() {

    /**
     * Navigation controller used to navigate between fragments.
     */
    private lateinit var navController: NavController

    /**
     * The main ViewModel for the entire app. Stores values that are shared across multiple fragments.
     */
    private val sharedViewModel: MainActivityViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel.hideToolbar(true)
        handleInitialNavigation()
    }

    /**
     * Navigates the user to where they should be and shows splash screen if necessary.
     */
    private fun handleInitialNavigation() {
        navController = findNavController()
        val options = NavOptions.Builder().setPopUpTo(navController.graph.startDestination, true).build()
        if (MainActivityViewModel.isLoggedIn.value!!) {
            //Use this code in production
            /*if(sharedViewModel.hasShownSplashScreen) navController.navigate(R.id.home_fragment, null, options)
            else {
                sharedViewModel.hasShownSplashScreen = true
                sharedViewModel.cacheUserGroups { navController.navigate(R.id.home_fragment, null, options) }
            }*/
            if(sharedViewModel.hasShownSplashScreen) navController.navigate(R.id.home_fragment, null, options)
            else {
                sharedViewModel.hasShownSplashScreen = true
                makeHTTPRequest { navController.navigate(R.id.home_fragment, null, options) }
            }
        } else {
            sharedViewModel.hasShownSplashScreen = true
            navController.navigate(R.id.login_fragment, null, options)
        }
    }

}