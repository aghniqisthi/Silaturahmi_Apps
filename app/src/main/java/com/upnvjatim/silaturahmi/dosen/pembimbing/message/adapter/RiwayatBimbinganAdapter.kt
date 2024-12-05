package com.upnvjatim.silaturahmi.dosen.pembimbing.message.adapter

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.upnvjatim.silaturahmi.R
import com.upnvjatim.silaturahmi.databinding.CardRiwayatBimbinganBinding
import com.upnvjatim.silaturahmi.model.request.GroupChats
import java.text.SimpleDateFormat
import java.util.Calendar

class RiwayatBimbinganAdapter(
    private var groupChats: List<GroupChats?>,
//    private var messagesMap: Map<String, List<Message>> // Map groupId to its messages
) : RecyclerView.Adapter<RiwayatBimbinganAdapter.ViewHolder>() {

    class ViewHolder(var binding: CardRiwayatBimbinganBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            CardRiwayatBimbinganBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val timestamp = groupChats[position].data?.get("timestamp") as Timestamp
//        val milliseconds = timestamp.seconds * 1000 + timestamp.nanoseconds / 1000000
//        val netDate = Date(milliseconds)

        if (groupChats[position] != null) {
            val timestamp = groupChats[position]?.timestamp
            val currentDate = Calendar.getInstance().time
            val dateFormat = SimpleDateFormat("dd MMMM yyyy")
            val isToday = dateFormat.format(timestamp) == dateFormat.format(currentDate)

            Log.d("cek timestamp", "$timestamp ; ${dateFormat.format(currentDate)} $isToday")

            if (isToday) {
                holder.binding.txtTimestamp.text = SimpleDateFormat("HH:mm").format(timestamp)
            } else {
                holder.binding.txtTimestamp.text = dateFormat.format(timestamp)
            }

            holder.binding.txtTitle.text = groupChats[position]?.groupName
            holder.binding.txtLastMessage.text = groupChats[position]?.lastMessage

//        val messages = messagesMap[groupChats[position].groupId] ?: emptyList()
//        val unreadCount = messages.count { !it.seen }
//
//        if (unreadCount == 0) {
//            holder.binding.circleNotif.visibility = View.GONE
//        } else {
//            holder.binding.circleNotif.visibility = View.VISIBLE
//            holder.binding.txtTotalunread.text = unreadCount.toString()
//        }

//        messages.forEach {
//            if (!it.seen) numb = numb + 1
//        }
//        if (numb == 0) holder.binding.circleNotif.visibility = View.GONE
//        else {
//            holder.binding.circleNotif.visibility = View.VISIBLE
//            holder.binding.txtTotalunread.text = numb.toString()
//        }
            holder.binding.cardMessage.setOnClickListener {
                val bundle = Bundle()
                bundle.putParcelable("gc", groupChats[position])

//                GroupChats(
//                    groupId = groupChats[position].id,
//                    groupName = groupChats[position].data?.get("groupName").toString(),
//                    lastMessage = groupChats[position].data?.get("lastMessage").toString(),
//                    members = groupChats[position].data?.get("members") as List<String>,
//                    timestamp = (groupChats[position].data?.get("timestamp") as Timestamp).toDate(),
//                    semester = groupChats[position].data?.get("semester").toString(),
//                    tahun = groupChats[position].data?.get("tahun").toString()
//                )

                Log.d("cek current nav", "${it.id
                } ; ${Navigation.findNavController(it).currentDestination
                } ; ${Navigation.findNavController(it).graph}")

                Navigation.findNavController(it).navigate(
                    R.id.action_riwayatBimbinganFragment_to_messageRiwayatFragment, bundle)
            }
        }
    }

    override fun getItemCount() = groupChats.size

}