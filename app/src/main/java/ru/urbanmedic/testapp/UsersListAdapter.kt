/**
 * © Panov Vitaly 2025 - All Rights Reserved
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Panov Vitaly 26 April 2025
 */

package ru.urbanmedic.testapp

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.recyclerview.widget.RecyclerView
import ru.urbanmedic.testapp.data.api.RetrofitBuilder
import ru.urbanmedic.testapp.utils.Pageable

class UsersListAdapter(
    private var users: MutableList<UserItem>,
    private val pageable: Pageable,
    private val onEditClickListener: View.OnClickListener
) : RecyclerView.Adapter<UsersListAdapter.UserViewHolder>(){

    inner class UserViewHolder(view: View) : RecyclerView.ViewHolder(view){
        var viewLayout: LinearLayout        = view.findViewById(R.id.viewLayout)
        var numTextView: TextView           = view.findViewById(R.id.numTextView)
        var emailTextView: TextView         = view.findViewById(R.id.emailTextView)
        var lastNameTextView: TextView      = view.findViewById(R.id.lastNameTextView)
        var editBtn: AppCompatImageButton   = view.findViewById(R.id.edit_btn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.list_item_user, parent, false
        )

        return UserViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return users.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        if(position+1 >= RetrofitBuilder.USER_API_RESULTS && position+1 == users.size){
            pageable.loadNextPage()
        }

        val user = users[position]
        user.let {
            holder.viewLayout.apply {
                val res = context!!.resources

                if (position % 2 == 0) {
                    setBackgroundColor(res.getColor(R.color.light_gray))
                } else {
                    setBackgroundColor(res.getColor(R.color.white))
                }
            }
            val num = position + 1
            holder.numTextView.text         = "$num"
            holder.emailTextView.text       = it.email
            holder.lastNameTextView.text    = it.lastname

            when(user.editable){
                true  ->  holder.editBtn.visibility = View.VISIBLE
                false -> holder.editBtn.visibility = View.GONE
            }

            holder.editBtn.tag = user
            holder.editBtn.setOnClickListener(onEditClickListener)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun appendDataset(dataset: List<UserItem>?) {
        if(dataset != null){
            users.addAll(dataset)
            notifyDataSetChanged()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun prependDataset(dataset: List<UserItem>?) {
        if(dataset != null){
            users.addAll(0, dataset)
            notifyDataSetChanged()
        }
    }

}