package com.partem.application.history

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.viewpager.widget.ViewPager
import com.partem.application.R
import com.partem.application.databinding.HistoryFragmentBinding
import com.partem.application.mainactivity.MainActivityViewModel
import com.partem.application.util.BlurController

class HistoryFragment : Fragment() {

    /**
     * ViewModel for the history fragment.
     */
    private lateinit var viewModel: HistoryFragmentViewModel

    /**
     * DataBinding reference for history fragment.
     */
    private lateinit var binding: HistoryFragmentBinding

    /**
     * The main ViewModel for the entire app. Stores values that are shared across multiple fragments.
     */
    private val sharedViewModel: MainActivityViewModel by activityViewModels()

    /**
     * Active tab drawable width.
     */
    private var indicatorWidth = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.history_fragment, container, false)
        viewModel = ViewModelProvider(this).get(HistoryFragmentViewModel::class.java)
        BlurController.subjectView = binding.historyFragmentRootLayout
        binding.historyFragmentViewPager.adapter = TabsPagerAdapter()
        binding.historyFragmentTab.setupWithViewPager(binding.historyFragmentViewPager)
        binding.historyFragmentTab.apply { post {
            val indicatorParams = binding.historyFragmentTabIndicator.layoutParams as FrameLayout.LayoutParams
            indicatorWidth = width / tabCount
            indicatorParams.width =  indicatorWidth
            binding.historyFragmentTabIndicator.layoutParams = indicatorParams
        }}
        binding.historyFragmentViewPager.addOnPageChangeListener(object: ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                val newParams = binding.historyFragmentTabIndicator.layoutParams as FrameLayout.LayoutParams
                newParams.leftMargin = ((positionOffset + position) * indicatorWidth).toInt()
                binding.historyFragmentTabIndicator.layoutParams = newParams
            }

            override fun onPageScrollStateChanged(state: Int) {}    //noop

            override fun onPageSelected(position: Int) {}           //noop
        })
        return binding.root
    }

}