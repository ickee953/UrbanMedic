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
import android.widget.FrameLayout
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
        val layoutNum: FrameLayout          = view.findViewById(R.id.layout_num)
        val layoutLastName: FrameLayout     = view.findViewById(R.id.layout_last_name)
        val layoutEmail: FrameLayout        = view.findViewById(R.id.layout_email)

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
            val res = holder.viewLayout.context!!.resources

            if (position % 2 == 0) {
                holder.layoutNum.setBackgroundColor(res.getColor(R.color.white))
                holder.layoutLastName.setBackgroundColor(res.getColor(R.color.white))
                holder.layoutEmail.setBackgroundColor(res.getColor(R.color.white))
            } else {
                holder.layoutNum.setBackgroundColor(res.getColor(R.color.cell_background))
                holder.layoutLastName.setBackgroundColor(res.getColor(R.color.cell_background))
                holder.layoutEmail.setBackgroundColor(res.getColor(R.color.cell_background))
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