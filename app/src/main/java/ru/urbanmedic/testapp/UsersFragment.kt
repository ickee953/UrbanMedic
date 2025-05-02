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
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
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
import ru.urbanmedic.testapp.model.Seed
import ru.urbanmedic.testapp.repository.GeoRepository
import ru.urbanmedic.testapp.repository.UserRepository
import ru.urbanmedic.testapp.utils.RefreshableUI
import ru.urbanmedic.testapp.utils.Utils
import ru.urbanmedic.testapp.utils.Utils.getLanguagePref
import ru.urbanmedic.testapp.utils.Utils.setLanguagePref
import ru.urbanmedic.testapp.utils.Utils.setLocale
import ru.urbanmedic.testapp.vo.GeoVO
import java.util.LinkedList

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class UsersFragment : Fragment(), RefreshableUI {

    private var _binding: FragmentUsersBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var locationManager: LocationManager

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            loadCityByGeo(location.latitude, location.longitude)
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}

        override fun onProviderEnabled(provider: String) {}

        override fun onProviderDisabled(provider: String) {}
    }

    private lateinit var seedDao: SeedDao

    private var launcher: ActivityResultLauncher<Intent>? = null

    private var users: MutableList<UserItem> = LinkedList()
    private lateinit var usersAdapter: UsersListAdapter

    private var currentPage: Int = 0
    private var seed: Seed? = null

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(ACCESS_FINE_LOCATION, false) -> {
                requestCurrentLocation()
            }
            permissions.getOrDefault(ACCESS_COARSE_LOCATION, false) -> {
                requestCurrentLocation()
            } else -> {
                val builder = activity?.let{AlertDialog.Builder(it)}
                builder?.setMessage("No location access granted")
                builder?.setPositiveButton(R.string.yes){ _, _ ->

                }
                builder?.show()
            }
        }
    }

    private fun requestCurrentLocation() {
        if( ActivityCompat.checkSelfPermission(requireActivity(), ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(requireActivity(), ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        } else {
            locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)

        locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        seedDao = UrbanMedicDB.getDatabase(requireActivity()).seedDao()

        init()

        if( ActivityCompat.checkSelfPermission(requireActivity(), ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(requireActivity(), ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionRequest.launch(arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION))
        } else {
            requestCurrentLocation()
        }

        launcher = registerForActivityResult<Intent, ActivityResult>(
            StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                init()
            } else {
                activity?.finish()
            }
        }

        usersAdapter = UsersListAdapter(users)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentUsersBinding.inflate(inflater, container, false)

        var recyclerView = binding.recyclerItemsView
        recyclerView.adapter = usersAdapter

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).setSupportActionBar(binding.toolbar)

        binding.toolbar.isTitleCentered = true

        binding.toggleButton.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if(isChecked)
                when(checkedId) {
                    R.id.ru -> {
                        setLocale(this, activity,"ru")
                        setLanguagePref(activity, "ru")
                    }
                    R.id.en -> {
                        setLocale(this, activity,"en")
                        setLanguagePref(activity, "en")
                    }
                }
        }

        binding.pullToRefresh.setOnRefreshListener {
            reloadUsers(seed!!.value!!)
        }

        /*binding.addUserBtn.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }*/
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).supportActionBar?.title = ""

        val locale = getLanguagePref(activity?.applicationContext)
        setLocale(this, activity,locale)

        when(locale){
            "ru" -> binding.toggleButton.check(R.id.ru)
            "en" -> binding.toggleButton.check(R.id.en)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)

        val exitItem         : MenuItem? = menu.findItem(R.id.action_exit)

        exitItem?.isVisible = true

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_exit -> {
                Utils.showDialog(
                    activity, resources.getString(R.string.are_you_sure),
                    resources.getString(R.string.information_will_be_deleted),
                    R.string.dialog_btn_yes, R.string.dialog_btn_no){
                    lifecycleScope.launch {
                        if(seedDao.loggedIn() != null) {
                            seedDao.logout()
                            init()
                        }
                    }
                }

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun init() {

        (activity as MainActivity).supportActionBar?.title = ""

        lifecycleScope.launch {
            seed = seedDao.loggedIn()
            if (seed == null) {
                val intent = Intent(
                    activity,
                    LoginActivity::class.java
                )

                launcher?.launch(intent)
            } else {
                reloadUsers(seed!!.value!!)
            }

        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun reloadUsers(seed: String) {
        users.clear()
        
        lifecycleScope.launch {

            binding.pullToRefresh.isRefreshing = true

            val userRepository = UserRepository(ApiHelper(RetrofitBuilder.apiService))

            try {
                val response = userRepository.allUsers(
                    "${RetrofitBuilder.USER_BASE_URL}/${RetrofitBuilder.USER_API_PATH}" +
                            "?page=${currentPage}&results=${RetrofitBuilder.USER_API_RESULTS}&seed=${seed}"
                )

                if( response.code() == 200 ){
                    val usersResponse = response.body()
                    usersResponse!!.results.forEach{
                        users.add(
                            UserItem(it.email, it.userName?.lastName)
                        )
                    }
                } else if( response.code() == 502 ) {
                    Utils.showDialog(
                        activity, "Bad Gateway",
                        "HTTP 502 - Unable to Connect to the Origin Server: ${RetrofitBuilder.USER_BASE_URL}",
                        R.string.yes, null
                    ) {}
                } else {
                    activity?.let {
                        Utils.showDialog(
                            it, it.getString(R.string.network_error),
                            "HTTP ${response.code()} - Can't fetch data from Server: ${RetrofitBuilder.USER_BASE_URL}",
                            R.string.yes, null
                        ) {}
                    }
                }
            } catch (exception : Exception){
                exception.printStackTrace()
                activity?.let {
                    Utils.showDialog(
                        it, it.getString(R.string.network_error),
                        exception.message,
                        R.string.yes, null
                    ) {}
                }
            } finally {
                binding.pullToRefresh.isRefreshing = false
            }
            usersAdapter.notifyDataSetChanged()
        }
    }

    private fun loadCityByGeo(lat: Double, lon: Double) {
        val geoRepository = GeoRepository(GeoHelper(RetrofitBuilder.geoService))

        lifecycleScope.launch {

            try {
                val response = geoRepository.getCity(GeoVO(lat, lon))

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

    override fun refreshUI() {
        binding.newContactBtn.setText(R.string.new_contact)
    }
}