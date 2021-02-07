package com.example.sharedexpenseapp.testchargingfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.example.sharedexpenseapp.R
import com.example.sharedexpenseapp.databinding.TestChargingFragmentBinding
import com.example.sharedexpenseapp.mainactivity.MainActivityViewModel

class TestChargingFragment: Fragment() {

    private lateinit var viewModel: TestChargingFragmentViewModel

    private lateinit var binding: TestChargingFragmentBinding

    private val sharedViewModel: MainActivityViewModel by activityViewModels()

    companion object { fun newInstance() = TestChargingFragment() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(TestChargingFragmentViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = DataBindingUtil.inflate(inflater, R.layout.test_charging_fragment, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this
        binding.testChargingFragmentSubmitButton.setOnClickListener {
            viewModel.addCharge(sharedViewModel.userGroups[viewModel.liveGroupName.value].toString())
        }
    }

}