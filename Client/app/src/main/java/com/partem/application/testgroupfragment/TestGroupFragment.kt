package com.partem.application.testgroupfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.partem.application.R
import com.partem.application.databinding.TestGroupFragmentBinding
import com.partem.application.mainactivity.MainActivityViewModel
import com.partem.application.util.isConnected

class TestGroupFragment : Fragment() {

    companion object { fun newInstance() = TestGroupFragment() }

    //Data binding object
    private lateinit var binding: TestGroupFragmentBinding

    //Fragment ViewModel
    private lateinit var viewModel: TestGroupFragmentViewModel

    //MainActivityViewModel
    private val sharedViewModel: MainActivityViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.test_group_fragment, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(TestGroupFragmentViewModel::class.java)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this
        sharedViewModel.setAppBackgroundDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.home_screen_bg)!!)
        sharedViewModel.lockNavDrawer(false)

        //LiveData observers


        //Click listeners
        binding.testGroupFragmentSubmitButton.setOnClickListener { _ ->
            if(activity?.application?.let { it -> isConnected(it) } == true) {
                viewModel.submitGroupInfo(MainActivityViewModel.user.value!!) { failure, message ->
                    if (failure)
                        Toast.makeText(requireActivity(), "onFailure was called", Toast.LENGTH_LONG).show()
                    else
                        Toast.makeText(requireActivity(), message, Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(context, com.partem.application.util.NOT_CONNECTED_MESSAGE, Toast.LENGTH_SHORT).show()
            }
        }

    }

}