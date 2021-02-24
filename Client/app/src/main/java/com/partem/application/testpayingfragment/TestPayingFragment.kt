package com.partem.application.testpayingfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.partem.application.R
import com.partem.application.databinding.TestPayingFragmentBinding
import com.partem.application.mainactivity.MainActivityViewModel

class TestPayingFragment: Fragment() {

    private lateinit var binding: TestPayingFragmentBinding

    private lateinit var viewModel: TestPayingFragmentViewModel

    private val sharedViewModel: MainActivityViewModel by activityViewModels()

    companion object { fun newInstance() = TestPayingFragment() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(TestPayingFragmentViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = DataBindingUtil.inflate(inflater, R.layout.test_paying_fragment, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this

        binding.testPayingFragmentSubmitButton.setOnClickListener {
            viewModel.payCharge(sharedViewModel.userGroups[viewModel.liveGroupName.value].toString())
        }
    }

}