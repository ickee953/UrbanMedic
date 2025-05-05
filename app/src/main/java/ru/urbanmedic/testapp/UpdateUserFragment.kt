package ru.urbanmedic.testapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import ru.urbanmedic.testapp.databinding.FragmentUpdateUserBinding
import ru.urbanmedic.testapp.db.UrbanMedicDB
import ru.urbanmedic.testapp.db.UserDao
import ru.urbanmedic.testapp.model.User
import ru.urbanmedic.testapp.utils.DialogHelper.showDialog

class UpdateUserFragment: Fragment() {

    private var _binding : FragmentUpdateUserBinding? = null

    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdateUserBinding.inflate(inflater, container, false)

        (activity as MainActivity).setSupportActionBar(binding.toolbar)
        (activity as MainActivity).supportActionBar?.title = resources.getString(R.string.edit)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.saveBtn.setOnClickListener {
            val userDao: UserDao = UrbanMedicDB.getDatabase(requireActivity().application).userDao()
            val user = User(null, binding.email.text.toString(), binding.lastName.text.toString())

            lifecycleScope.launch {
                userDao.create(user)

                findNavController().navigateUp()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_edit_user, menu)

        val closeItem         : MenuItem? = menu.findItem(R.id.action_cancel)

        closeItem?.isVisible = true

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_cancel -> {
                findNavController().navigateUp()

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}