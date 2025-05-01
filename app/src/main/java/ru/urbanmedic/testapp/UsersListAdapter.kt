/**
 * © Panov Vitaly 2025 - All Rights Reserved
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Panov Vitaly 26 April 2025
 */

package ru.urbanmedic.testapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class UsersListAdapter(
    private val users: List<UserItem>
) : RecyclerView.Adapter<UsersListAdapter.UserViewHolder>(){

    inner class UserViewHolder(view: View) : RecyclerView.ViewHolder(view){
        var viewLayout: LinearLayout    = view.findViewById(R.id.viewLayout)
        var numTextView: TextView       = view.findViewById(R.id.numTextView)
        var emailTextView: TextView     = view.findViewById(R.id.emailTextView)
        var lastNameTextView: TextView  = view.findViewById(R.id.lastNameTextView)
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
        val user = users[position]
        user.let {
            holder.itemView.tag = user

            if(position % 2 == 0){
                holder.viewLayout.apply {
                    val res = context!!.resources
                    setBackgroundColor(res.getColor(R.color.light_gray))
                }
            }
            val num = position + 1
            holder.numTextView.text         = "$num"
            holder.emailTextView.text       = it.email
            holder.lastNameTextView.text    = it.lastname
        }
    }

}