/**
 * © Panov Vitaly 2025 - All Rights Reserved
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Panov Vitaly 27 April 2025
 */

package ru.urbanmedic.testapp

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import ru.urbanmedic.testapp.data.api.ApiHelper
import ru.urbanmedic.testapp.data.api.GeoHelper
import ru.urbanmedic.testapp.data.api.RetrofitBuilder
import ru.urbanmedic.testapp.databinding.FragmentUsersBinding
import ru.urbanmedic.testapp.db.SeedDao
import ru.urbanmedic.testapp.db.UrbanMedicDB
import ru.urbanmedic.testapp.repository.GeoRepository
import ru.urbanmedic.testapp.repository.UserRepository
import ru.urbanmedic.testapp.vo.GeoVO
import java.util.LinkedList

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class UsersFragment : Fragment() {

    private var _binding: FragmentUsersBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var seedDao: SeedDao

    private var launcher: ActivityResultLauncher<Intent>? = null

    private var users: MutableList<UserItem> = LinkedList()

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(ACCESS_FINE_LOCATION, false) -> {
                getCurrentLocation()
            }
            permissions.getOrDefault(ACCESS_COARSE_LOCATION, false) -> {
                getCurrentLocation()
            } else -> {
            val builder = AlertDialog.Builder(requireActivity())
            builder.setMessage("No location access granted")
            builder.setPositiveButton(R.string.yes){ _, _ ->
                requireActivity().finish()
            }
            builder.show()
        }
        }
    }

    private fun getCurrentLocation() {
        val locationManager
                = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                loadCityByGeo(location.latitude, location.longitude)
            }

            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}

            override fun onProviderEnabled(provider: String) {}

            override fun onProviderDisabled(provider: String) {}
        }

        if( ActivityCompat.checkSelfPermission(requireActivity(), ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(requireActivity(), ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionRequest.launch(arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION))
        } else {
            locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        seedDao = UrbanMedicDB.getDatabase(requireActivity()).seedDao()

        launcher = registerForActivityResult<Intent, ActivityResult>(
            StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                init()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentUsersBinding.inflate(inflater, container, false)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).setSupportActionBar(binding.toolbar)

        binding.toolbar.isTitleCentered = true

        /*binding.addUserBtn.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }*/
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume(){
        super.onResume()

        init()
    }

    private fun init() {

        (activity as MainActivity).supportActionBar?.title = ""

        lifecycleScope.launch {
            val seed = seedDao.loggedIn()
            if (seed == null) {
                val intent = Intent(
                    requireActivity(),
                    LoginActivity::class.java
                )

                launcher?.launch(intent)
            } else {
                reloadUsers(seed.seed!!)
                getCurrentLocation()
            }

        }
    }

    private fun reloadUsers(seed: String) {
        users.clear()

        lifecycleScope.launch {

            binding.pullToRefresh.isRefreshing = true

            val userRepository = UserRepository(ApiHelper(RetrofitBuilder.apiService))

            try {
                val response = userRepository.allUsers(
                    "${RetrofitBuilder.URL}/${RetrofitBuilder.ALL_USERS_PATH}?seed=${seed}"
                )

                if( response.code() == 200 ){
                    val usersResponse = response.body()
                    /*usersResponse?.let { resp->
                        resp.forEach {
                            users.add(
                                UserItem(it.id, it.username)
                            )
                        }
                    }*/
                } else if( response.code() == 204 ){
                    binding.pullToRefresh.isRefreshing = false
                } else {
                    binding.pullToRefresh.isRefreshing = false
                }
            } catch (exception : Exception){
                exception.printStackTrace()
            } finally {
                binding.pullToRefresh.isRefreshing = false
            }

        }
    }

    private fun loadCityByGeo(lat: Double, lon: Double) {
        val geoRepository = GeoRepository(GeoHelper(RetrofitBuilder.geoService))

        lifecycleScope.launch {

            try {
                val response = geoRepository.getCity(GeoVO(lat, lon))
                //val response = geoRepository.getCity(GeoVO())

                if( response.code() == 200 ){
                    val suggestionsList = response.body()!!.suggestionsVO
                    if(suggestionsList.isNotEmpty()) {
                        (activity as MainActivity).supportActionBar?.title = suggestionsList[0].suggestionDataVO.city
                    }
                } else {
                    (activity as MainActivity).supportActionBar?.title = ""
                }
            } catch (exception : Exception){
                exception.printStackTrace()
            } finally {

            }

        }
    }
}