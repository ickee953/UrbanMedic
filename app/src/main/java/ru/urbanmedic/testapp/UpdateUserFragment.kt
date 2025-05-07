/**
 * © Panov Vitaly 2025 - All Rights Reserved
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Panov Vitaly 5 May 2025
 */

package ru.urbanmedic.testapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
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
import ru.urbanmedic.testapp.utils.Utils

class UpdateUserFragment: Fragment() {

    companion object {
        const val UPDATE_USER_PARAM = "updateUser"
        const val ARG_ID            = "id"
        const val ARG_EMAIL         = "email"
        const val ARG_LAST_NAME     = "lastName"
    }

    private var _binding : FragmentUpdateUserBinding? = null

    private val binding get() = _binding!!

    private var userId: Long = 0

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

        return binding.root
    }

    private fun fieldsChecked(): Boolean {
        val email = binding.email.text.toString().trim()
        val lastName = binding.lastName.text.toString().trim()

        return !(TextUtils.isEmpty(email) || !Utils.isValidEmail(email)
                || TextUtils.isEmpty(lastName) || !Utils.isValidText(lastName))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).setSupportActionBar(binding.toolbar)

        binding.email.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                @SuppressLint("UseCompatLoadingForColorStateLists", "UseCompatLoadingForDrawables")
                override fun afterTextChanged(s: Editable?) {
                    val email = s.toString().trim()
                    if (TextUtils.isEmpty(email) || !Utils.isValidEmail(email)) {
                        //binding.email.error = "Please enter a valid email address"
                        binding.email.background = resources.getDrawable(R.drawable.background_edit_text_selector_error)
                        binding.email.setTextColor(resources.getColor(R.color.warning))
                        binding.saveBtn.isEnabled = false
                    } else {
                        //binding.email.error = null
                        binding.email.background = resources.getDrawable(R.drawable.background_edit_text_selector)
                        binding.email.setTextColor(resources.getColor(R.color.text_primary))
                        if( fieldsChecked() ) binding.saveBtn.isEnabled = true
                    }
                }
        })

        binding.lastName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            @SuppressLint("UseCompatLoadingForColorStateLists", "UseCompatLoadingForDrawables")
            override fun afterTextChanged(s: Editable?) {
                val lastName = s.toString().trim()
                if (TextUtils.isEmpty(lastName) || !Utils.isValidText(lastName)) {
                    //binding.email.error = "Please enter a valid last name"
                    binding.lastName.background = resources.getDrawable(R.drawable.background_edit_text_selector_error)
                    binding.lastName.setTextColor(resources.getColor(R.color.warning))
                    binding.saveBtn.isEnabled = false
                } else {
                    //binding.email.error = null
                    binding.lastName.background = resources.getDrawable(R.drawable.background_edit_text_selector)
                    binding.lastName.setTextColor(resources.getColor(R.color.text_primary))
                    if( fieldsChecked() ) binding.saveBtn.isEnabled = true
                }
            }
        })

        binding.saveBtn.setOnClickListener {
            val userDao: UserDao = UrbanMedicDB.getDatabase(requireActivity().application).userDao()

            val user = User(
                id = userId,
                email = binding.email.text.toString(),
                lastName = binding.lastName.text.toString()
            )

            lifecycleScope.launch {
                if(userId == 0.toLong()){
                    userDao.create(user)
                } else {
                    userDao.update(user)
                }

                findNavController().previousBackStackEntry?.savedStateHandle
                    ?.set(UPDATE_USER_PARAM, user)
                findNavController().navigateUp()
            }
        }

        if(arguments != null){
            if(requireArguments().containsKey(ARG_ID)){
                userId = requireArguments().getLong(ARG_ID)
            }
            if(requireArguments().containsKey(ARG_EMAIL)){
                binding.email.setText(requireArguments().getString(ARG_EMAIL))
            }
            if(requireArguments().containsKey(ARG_LAST_NAME)){
                binding.lastName.setText(requireArguments().getString(ARG_LAST_NAME))
            }

            (activity as MainActivity).supportActionBar?.title = resources.getString(R.string.edit)
            binding.saveBtn.text = resources.getString(R.string.edit)
            binding.saveBtn.isEnabled = true
        } else {
            (activity as MainActivity).supportActionBar?.title = resources.getString(R.string.new_contact)
            binding.saveBtn.text = resources.getString(R.string.save)
            binding.saveBtn.isEnabled = false
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
                showDialog(
                    requireContext(),
                    resources.getString(R.string.are_you_sure),
                    resources.getString(R.string.information_will_not_be_saved),
                    R.string.dialog_btn_yes,
                    R.string.dialog_btn_no,
                    {
                        findNavController().navigateUp()
                    },{})

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}