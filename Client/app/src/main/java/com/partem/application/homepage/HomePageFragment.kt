package com.partem.application.homepage

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.partem.application.R
import com.partem.application.databinding.HomePageFragmentBinding
import com.partem.application.mainactivity.MainActivityViewModel
import com.partem.application.enums.RECYCLER_DATA
import com.partem.application.util.BlurController


class HomePageFragment: Fragment(), SwipeRefreshLayout.OnRefreshListener {

    //Exclusive ViewModel for HomePageFragment
    private lateinit var viewModel: HomePageViewModel

    //App nav controller
    private lateinit var navController: NavController

    //Binding for interacting with ui components
    private lateinit var binding: HomePageFragmentBinding

    //Main ViewModel for the whole app
    private val sharedViewModel: MainActivityViewModel by activityViewModels()

    //Home fragment recycler view adapter
    private lateinit var groupAdapter: GroupRecyclerAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Prevent can't find nav controller in onCreate error
        if(sharedViewModel.navController == null) {
            navController = findNavController()
            sharedViewModel.navController = navController
        } else {
            navController = sharedViewModel.navController!!
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        if (sharedViewModel.isDatabaseLoaded)
            sharedViewModel.cacheUserGroups()
        else
            sharedViewModel.addOnDatabaseLoadedListener { sharedViewModel.cacheUserGroups() }
        binding = DataBindingUtil.inflate(inflater, R.layout.home_page_fragment, container, false)
        BlurController.subjectView = binding.homeFragmentRootLinearLayout
        initRecyclerView()
        addDataSet()
        binding.homeFragmentSwipeRefreshRoot.setOnRefreshListener(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //If username is present, unlock screen, otherwise send user to login fragment
        MainActivityViewModel.isLoggedIn.observe(viewLifecycleOwner, {
            if (!it) {
                navController.navigate(R.id.loginFragment)
            } else {
                askForPermissions()
            }
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(HomePageViewModel::class.java)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this

        sharedViewModel.setAppBackgroundDrawable(AppCompatResources.getDrawable(requireContext(), R.color.colorSecondary)!!)
        sharedViewModel.lockNavDrawer(false)
        sharedViewModel.hideToolbar(false)

        //Click listeners

    }

    private fun askForPermissions() {
        val externalWriteCheck = ContextCompat.checkSelfPermission(
            requireActivity().applicationContext,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val networkStateCheck = ContextCompat.checkSelfPermission(
            requireActivity().applicationContext,
            Manifest.permission.ACCESS_NETWORK_STATE
        )
        val internetCheck = ContextCompat.checkSelfPermission(
            requireActivity().applicationContext,
            Manifest.permission.INTERNET
        )
        val externalReadCheck = ContextCompat.checkSelfPermission(
            requireActivity().applicationContext,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        val storageWriteString = Manifest.permission.WRITE_EXTERNAL_STORAGE
        val internetString = Manifest.permission.INTERNET
        val networkString = Manifest.permission.ACCESS_NETWORK_STATE
        val storageReadString = Manifest.permission.READ_EXTERNAL_STORAGE
        when((-1 * externalWriteCheck) + (-2 * networkStateCheck) + (-4 * internetCheck) + (-8 * externalReadCheck)) {
            1 -> ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(storageWriteString),
                1
            )
            2 -> ActivityCompat.requestPermissions(requireActivity(), arrayOf(networkString), 2)
            3 -> ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(
                    storageWriteString,
                    networkString
                ), 3
            )
            4 -> ActivityCompat.requestPermissions(requireActivity(), arrayOf(internetString), 4)
            5 -> ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(
                    storageWriteString,
                    internetString
                ), 5
            )
            6 -> ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(
                    networkString,
                    internetString
                ), 6
            )
            7 -> ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(
                    storageWriteString,
                    networkString,
                    internetString
                ), 7
            )
            8 -> ActivityCompat.requestPermissions(requireActivity(), arrayOf(storageReadString), 8)
            9 -> ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(
                    storageReadString,
                    storageWriteString
                ), 9
            )
            10 -> ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(
                    storageReadString,
                    networkString
                ), 10
            )
            11 -> ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(
                    storageReadString,
                    storageWriteString,
                    networkString
                ), 11
            )
            12 -> ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(
                    storageReadString,
                    internetString
                ), 12
            )
            13 -> ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(
                    storageReadString,
                    internetString,
                    storageWriteString
                ), 13
            )
            14 -> ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(
                    storageReadString,
                    internetString,
                    networkString
                ), 14
            )
            15 -> ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(
                    storageReadString,
                    internetString,
                    networkString,
                    storageWriteString), 15
            )
        }
    }

    private fun initRecyclerView() {
        binding.homeFragmentGroupsList.layoutManager = object: LinearLayoutManager(requireContext()) {
            override fun checkLayoutParams(lp: RecyclerView.LayoutParams?): Boolean {
                lp?.height = binding.homeFragmentRootLinearLayout.height / 10
                return true
            }
        }
        binding.homeFragmentGroupsList.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        groupAdapter = GroupRecyclerAdapter()
        binding.homeFragmentGroupsList.adapter = groupAdapter
    }

    private fun addDataSet() {
        groupAdapter.submitList(RECYCLER_DATA)
    }

    //SwipeRefreshLayout onRefresh interface listener
    override fun onRefresh() {
        Toast.makeText(context, "You refreshed the page!", Toast.LENGTH_SHORT).show()
        binding.homeFragmentSwipeRefreshRoot.isRefreshing = false
    }

    //Implement what happens if the user rejects any permissions
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    }

}

/*
*
* TODO:
*  Implement what happens if the user rejects permissions in onRequestPermissionsResult.
*  BlurLayout leaves navdrawer residue behind.
*  LottieAnimationView and BlurLayout activate only after nav drawer settles. (They should animate in flow with nav drawer)
*  Add onSwipe for expenses/charges (In addition to the already existing onClick).
*
* */
