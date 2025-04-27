/**
 * © Panov Vitaly 2025 - All Rights Reserved
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Panov Vitaly 27 April 2025
 */

package ru.urbanmedic.testapp

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import ru.urbanmedic.testapp.data.api.ApiHelper
import ru.urbanmedic.testapp.data.api.RetrofitBuilder
import ru.urbanmedic.testapp.databinding.FragmentUsersBinding
import ru.urbanmedic.testapp.db.UrbanMedicDB
import ru.urbanmedic.testapp.repository.UserRepository
import java.util.LinkedList

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class UsersFragment : Fragment() {

    private var _binding: FragmentUsersBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var launcher: ActivityResultLauncher<Intent>? = null

    private var users: MutableList<UserItem> = LinkedList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        launcher = registerForActivityResult<Intent, ActivityResult>(
            StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                //todo login result processing
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

        val seedDao = UrbanMedicDB.getDatabase(requireActivity()).seedDao()
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
                    "${RetrofitBuilder.URL}/${RetrofitBuilder.ALL_USERS_PATH}", seed
                )

                if( response.code() == 200 ){
                    val usersResponse = response.body()
                    usersResponse?.let { resp->
                        resp.forEach {
                            users.add(
                                UserItem(it.id, it.username)
                            )
                        }
                    }
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
}