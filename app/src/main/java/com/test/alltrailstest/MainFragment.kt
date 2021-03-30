package com.test.alltrailstest

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.test.alltrailstest.databinding.MainFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private val viewModel by viewModels<MainViewModel>()

    private var _binding: MainFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var restaurantAdapter: RestaurantAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.main_fragment, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        restaurantAdapter = RestaurantAdapter(viewModel)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this@MainFragment
        setupRecyclerView()
        setupViewModelObservers()
        requestLocationPermissions()
    }


    private fun requestLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
            return
        } else {
            observeLocation()
        }


    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                observeLocation()
            } else {
                Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }


    private fun setupViewModelObservers() {
        lifecycleScope.launchWhenStarted {
            for (event in viewModel.eventChannel) {
                when (event) {
                    is MainViewModel.Event.StartQuery -> queryApi()
                }
            }
        }
    }

    private fun queryApi() {
        if (viewModel.latestLatitude == .0 && viewModel.latestLongitude == 0.0) {
            Toast.makeText(requireContext(), "No Location", Toast.LENGTH_SHORT).show()
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.queryRestaurants().collect {
                restaurantAdapter.restaurantInfos = it
                restaurantAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun observeLocation() {
        Timber.d("Observing Location")
        val utils = LocationUtils()
        utils.getInstance(requireContext())
        utils.getLocation().observe(requireActivity(), { loc: Location? ->
            Timber.d("Have Location $loc")
            if (loc != null) {
                viewModel.latestLatitude = loc.latitude
                viewModel.latestLongitude = loc.longitude
                // Yay! location recived. Do location related work here
                Timber.d("Location: ${loc.latitude}  ${loc.longitude}")
            }
        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRecyclerView() {
        binding.questionsView.layoutManager = LinearLayoutManager(requireContext())
        binding.questionsView.adapter = restaurantAdapter
    }
}