package com.partem.application.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.partem.application.R
import com.partem.application.mainactivity.MainActivityViewModel
import kotlinx.coroutines.*

class Splash : Fragment() {

    private lateinit var viewModel: SplashViewModel

    private val sharedViewModel: MainActivityViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(SplashViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel.hideToolbar(true)
        viewModel.liveTransitionToApp.observe(viewLifecycleOwner, {
            if(it)
                findNavController().navigate(R.id.homePageFragment)
        })
        CoroutineScope(Dispatchers.Default).launch {
            delay(3000)
            withContext(Dispatchers.Main) {
                viewModel.liveTransitionToApp.value = true
                sharedViewModel.hideToolbar(false)
            }
        }
    }

}