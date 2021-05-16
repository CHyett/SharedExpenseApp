package com.partem.application.profile

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.partem.application.R
import com.partem.application.databinding.ProfileFragmentBinding
import com.partem.application.enums.RECYCLER_DATA
import com.partem.application.mainactivity.MainActivityViewModel
import com.partem.application.util.BlurController

class ProfileFragment: Fragment() {

    private lateinit var viewModel: ProfileFragmentViewModel

    private lateinit var binding: ProfileFragmentBinding

    private val sharedViewModel: MainActivityViewModel by activityViewModels()

    private val groupAdapter = GroupRecyclerAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.profile_fragment, container, false)
        BlurController.subjectView = binding.profileFragmentRootLayout
        initRecyclerView()
        addDataSet()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(ProfileFragmentViewModel::class.java)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this

        sharedViewModel.setAppBackgroundDrawable(AppCompatResources.getDrawable(requireContext(), R.color.colorSecondary)!!)
        sharedViewModel.lockNavDrawer(false)
        sharedViewModel.hideToolbar(false)

        //TODO: This will have to be changed to dynamically get group size from server cache
        binding.profileFragmentNumGroupsText.text = getString(R.string.profile_fragment_groups, RECYCLER_DATA.size)

        //LiveData observers
        MainActivityViewModel.user.observe(viewLifecycleOwner, { binding.profileFragmentUsernameText.text = getString(R.string.profile_fragment_username, it) })

        //Click listeners


    }

    private fun initRecyclerView() {
        binding.profileFragmentGroupsRecyclerView.layoutManager = object: LinearLayoutManager(requireContext()) {
            override fun checkLayoutParams(lp: RecyclerView.LayoutParams?): Boolean {
                lp?.height = binding.profileFragmentRootLayout.height / 10
                return true
            }
        }
        val divider = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        divider.setDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.recycler_divider)!!)
        binding.profileFragmentGroupsRecyclerView.addItemDecoration(divider)
        binding.profileFragmentGroupsRecyclerView.adapter = groupAdapter
    }

    private fun addDataSet() = groupAdapter.submitList(RECYCLER_DATA)

}

/*
*
* TODO:
*  RecyclerView dividers need to have a left indent
*
* */