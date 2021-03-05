package com.partem.application.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.partem.application.R
import com.partem.application.databinding.SettingsFragmentBinding
import com.partem.application.homepage.HomePageViewModel
import com.partem.application.mainactivity.MainActivityViewModel
import com.partem.application.util.BlurController


class SettingsFragment: Fragment() {

    private lateinit var binding: SettingsFragmentBinding

    private val sharedViewModel: MainActivityViewModel by activityViewModels()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.lifecycleOwner = this
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        sharedViewModel.setAppBackgroundDrawable(AppCompatResources.getDrawable(requireContext(), R.color.colorSecondary)!!)
        sharedViewModel.lockNavDrawer(false)
        sharedViewModel.hideToolbar(false)
        binding = DataBindingUtil.inflate(inflater, R.layout.settings_fragment, container, false)
        binding.lifecycleOwner = this
        BlurController.subjectView = binding.settingsFragmentRootLayout
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Click listeners

    }

}