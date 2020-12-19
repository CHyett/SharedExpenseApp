package com.example.sharedexpenseapp.testgroupfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.example.sharedexpenseapp.R
import com.example.sharedexpenseapp.databinding.TestGroupFragmentBinding
import com.example.sharedexpenseapp.mainactivity.MainActivityViewModel

class TestGroupFragment : Fragment() {

    companion object { fun newInstance() = TestGroupFragment() }

    //Data binding object
    private lateinit var binding: TestGroupFragmentBinding

    //Fragment ViewModel
    private lateinit var viewModel: TestGroupFragmentViewModel

    //MainActivityViewModel
    private val sharedViewModel: MainActivityViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.test_group_fragment, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(TestGroupFragmentViewModel::class.java)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this
        sharedViewModel.setAppBackgroundDrawable(R.drawable.home_screen_bg)
        sharedViewModel.lockNavDrawer(false)

        //LiveData observers


        //Click listeners
        binding.testGroupFragmentSubmitButton.setOnClickListener {
            viewModel.submitGroupInfo(MainActivityViewModel.user.value!!) { failure, message ->
                if(failure)
                    Toast.makeText(requireActivity(), "onFailure was called", Toast.LENGTH_LONG).show()
                else
                    Toast.makeText(requireActivity(), message, Toast.LENGTH_LONG).show()
            }
        }

    }

}