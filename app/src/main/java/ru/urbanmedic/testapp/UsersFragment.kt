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
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import ru.urbanmedic.testapp.UpdateUserFragment.Companion.UPDATE_USER_PARAM
import ru.urbanmedic.testapp.data.api.ApiHelper
import ru.urbanmedic.testapp.data.api.GeoHelper
import ru.urbanmedic.testapp.data.api.RetrofitBuilder
import ru.urbanmedic.testapp.databinding.FragmentUsersBinding
import ru.urbanmedic.testapp.db.SeedDao
import ru.urbanmedic.testapp.db.UrbanMedicDB
import ru.urbanmedic.testapp.db.UserDao
import ru.urbanmedic.testapp.model.Seed
import ru.urbanmedic.testapp.model.User
import ru.urbanmedic.testapp.repository.GeoRepository
import ru.urbanmedic.testapp.repository.UserLocalRepository
import ru.urbanmedic.testapp.repository.UserNetworkRepository
import ru.urbanmedic.testapp.repository.UserRepository
import ru.urbanmedic.testapp.utils.DialogHelper.showDialog
import ru.urbanmedic.testapp.utils.Pageable
import ru.urbanmedic.testapp.utils.RefreshableUI
import ru.urbanmedic.testapp.utils.Utils.getLanguagePref
import ru.urbanmedic.testapp.utils.Utils.setLanguagePref
import ru.urbanmedic.testapp.utils.Utils.setLocale
import ru.urbanmedic.testapp.vo.GeoVO
import java.util.LinkedList

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class UsersFragment : Fragment(), RefreshableUI, Pageable {

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
                showDialog(
                    requireContext(),
                    R.string.permission_required,
                    R.string.no_localtion_permission,
                    R.string.yes
                )
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

        if( ActivityCompat.checkSelfPermission(requireActivity(), ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(requireActivity(), ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionRequest.launch(arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION))
        } else {
            requestCurrentLocation()
        }

        if(users.isEmpty()) init()

        launcher = registerForActivityResult(
            StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                init()
            } else {
                activity?.finish()
            }
        }

        usersAdapter = UsersListAdapter(users, this) { itemView ->
            val item = itemView.tag as UserItem
            val bundle = Bundle()
            item.id?.let {
                bundle.putLong(UpdateUserFragment.ARG_ID, it)
            }
            bundle.putString(UpdateUserFragment.ARG_EMAIL, item.email)
            bundle.putString(UpdateUserFragment.ARG_LAST_NAME, item.lastname)

            itemView.findNavController().navigate(R.id.action_update_user, bundle)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentUsersBinding.inflate(inflater, container, false)

        val recyclerView = binding.recyclerItemsView
        recyclerView.adapter = usersAdapter

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).setSupportActionBar(binding.toolbar)
        (activity as MainActivity).supportActionBar?.title = ""
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
            lifecycleScope.launch {
                reloadUsersList()
            }
        }

        binding.newContactBtn.setOnClickListener {
            findNavController().navigate(R.id.action_update_user)
        }

        findNavController().currentBackStackEntry?.savedStateHandle
            ?.getLiveData<User>(UPDATE_USER_PARAM)?.observe(viewLifecycleOwner) {
                reloadUsersList()
            }
    }

    override fun onResume() {
        super.onResume()

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
                showDialog(
                    requireContext(),
                    resources.getString(R.string.are_you_sure),
                    resources.getString(R.string.information_will_be_deleted),
                    R.string.dialog_btn_yes,
                    R.string.dialog_btn_no,
                    {
                        lifecycleScope.launch {
                            if(seedDao.loggedIn() != null) {
                                seedDao.logout()
                                val userRepository = UserLocalRepository(requireContext())
                                userRepository.clear()

                                val intent = Intent(activity, LoginActivity::class.java)
                                launcher?.launch(intent)
                            }
                        }
                    },{})

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun init() {

        lifecycleScope.launch {
            seed = seedDao.loggedIn()
            if (seed == null) {
                val intent = Intent(
                    activity,
                    LoginActivity::class.java
                )

                launcher?.launch(intent)
            } else {
                reloadUsersList()
            }

        }
    }

    private fun reloadUsersList(){
        currentPage = 0
        users.clear()

        lifecycleScope.launch {
            loadUsersListLocal()
        }
        lifecycleScope.launch {
            loadUsersListByNetwork()
        }
    }

    private suspend fun loadUsersListLocal(){
        val localUsers = loadLocalUsers().await()

        synchronized(usersAdapter){
            usersAdapter.appendFirstDataset(localUsers)
        }
    }

    private suspend fun loadUsersListByNetwork(){
        val loadedUsersByNetwork = loadNetworkUsers(seed!!.value!!, currentPage).await()

        synchronized(usersAdapter){
            usersAdapter.appendDataset(loadedUsersByNetwork)
        }
    }

    private fun loadLocalUsers(): Deferred<MutableList<UserItem>?> =
        lifecycleScope.async {
            val userLocalRepository = UserLocalRepository(requireContext())
            val localUsersList = userLocalRepository.allUsers()
            val resultList: ArrayList<UserItem> = ArrayList(localUsersList.size)

            localUsersList.forEach{
                resultList.add(
                    UserItem(it.id, it.email, it.lastName, true)
                )
            }

            return@async resultList
        }

    private fun loadNetworkUsers(seed: String, page: Int): Deferred<MutableList<UserItem>?> =

        lifecycleScope.async {

                var resultList: ArrayList<UserItem>? = null

                binding.pullToRefresh.isRefreshing = true

                val userRepository = UserNetworkRepository(
                    ApiHelper(RetrofitBuilder.apiService),
                    seed
                )

                try {
                    val response = userRepository.loadPage(page)

                    if( response.code() == 200 ){
                        val usersResponse = response.body()

                        resultList = ArrayList(usersResponse!!.results.size)

                        usersResponse.results.forEach{
                            resultList.add(
                                UserItem(null, it.email, it.userName?.lastName)
                            )
                        }

                        //usersAdapter.updateDataset(resultList)
                    } else if( response.code() == 502 ) {
                        showDialog(
                            requireContext(), "Bad Gateway",
                            "HTTP 502 - Unable to Connect to the Origin Server: ${RetrofitBuilder.USER_BASE_URL}",
                            R.string.yes, null
                        )
                    } else {
                        showDialog(
                            requireContext(), R.string.network_error,
                            "HTTP ${response.code()} - Can't fetch data from Server: ${RetrofitBuilder.USER_BASE_URL}",
                            R.string.yes, null
                        )
                    }
                } catch (exception : Exception){
                    exception.printStackTrace()
                    exception.message?.let {
                        showDialog(
                            requireContext(),
                            R.string.network_error,
                            it,
                            R.string.yes, null
                        )
                    }
                } finally {
                    binding.pullToRefresh.isRefreshing = false
                }

                return@async resultList
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

    override fun loadNextPage() {
        currentPage++
        lifecycleScope.launch {
            loadUsersListByNetwork()
        }
    }
}