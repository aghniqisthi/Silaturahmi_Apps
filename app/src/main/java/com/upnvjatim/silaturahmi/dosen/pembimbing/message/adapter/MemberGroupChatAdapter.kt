package com.upnvjatim.silaturahmi.dosen.pembimbing.message.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.upnvjatim.silaturahmi.databinding.CardMemberGroupchatsBinding
import com.upnvjatim.silaturahmi.model.request.UserGroupChats
import com.upnvjatim.silaturahmi.model.response.DataLuaran

class MemberGroupChatAdapter(private val members: List<UserGroupChats?>)
    : RecyclerView.Adapter<MemberGroupChatAdapter.ViewHolder>() {

    class ViewHolder(val binding: CardMemberGroupchatsBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = CardMemberGroupchatsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.txtName.text = "${members[position]?.npm} - ${members[position]?.name}"
        holder.binding.txtDesc.text = members[position]?.description
        if (members[position]?.avatar != null && members[position]?.avatar != "null" && members[position]?.avatar  != "") {
            Log.d("cek avatar", members[position]?.avatar.toString())
            Glide.with(holder.itemView.context).load(members[position]?.avatar?.toUri()).into(holder.binding.imgAvatar)
        }
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<DataLuaran> = object : DiffUtil.ItemCallback<DataLuaran>() {
            override fun areItemsTheSame(oldUser: DataLuaran, newUser: DataLuaran): Boolean {
                return oldUser.id == newUser.id
            }
            override fun areContentsTheSame(oldUser: DataLuaran, newUser: DataLuaran): Boolean {
                return oldUser == newUser
            }
        }
    }

    override fun getItemCount(): Int = members.size
}