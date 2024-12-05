package com.upnvjatim.silaturahmi.viewmodel.adapter

import android.animation.ValueAnimator
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.upnvjatim.silaturahmi.databinding.CardNotifBinding
import com.upnvjatim.silaturahmi.model.request.DataNotif

class NotifAktivitasAdapter(private var notif: List<DataNotif>)
    : ListAdapter<DataNotif, NotifAktivitasAdapter.ViewHolder>(DIFF_CALLBACK) {

    class ViewHolder(var binding: CardNotifBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = CardNotifBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val timestamp = groupChats[position].data?.get("timestamp") as Timestamp
//        val milliseconds = timestamp.seconds * 1000 + timestamp.nanoseconds / 1000000
//        val netDate = Date(milliseconds)
//
//        val currentDate = Calendar.getInstance().time
//        val dateFormat = SimpleDateFormat("dd MMMM yyyy")
//        val isToday = dateFormat.format(netDate) == dateFormat.format(currentDate)
//
//        Log.d("cek timestamp", "$netDate ; ${dateFormat.format(currentDate)} $isToday")
//
//        if (isToday) holder.binding.txtTimestamp.setText(SimpleDateFormat("HH:mm").format(netDate))
//        else holder.binding.txtTimestamp.setText(dateFormat.format(netDate))

        holder.binding.txtDuration.visibility = View.GONE
        holder.binding.txtTitle.text = notif[position].title
        holder.binding.txtLastMessage.text = notif[position].message

        if(!notif[position].seen){
            val startColor = Color.parseColor("#E3F2B2")
            val endColor = Color.WHITE

            // ValueAnimator to animate between the two colors
            val colorAnimation = ValueAnimator.ofArgb(startColor, endColor)

            colorAnimation.duration = 3000 // duration in milliseconds
            colorAnimation.interpolator = DecelerateInterpolator()

            // Update the background color during the animation
            colorAnimation.addUpdateListener { animator ->
                holder.binding.cardNotif.setCardBackgroundColor(animator.animatedValue as Int)
            }

            // Start the animation
            colorAnimation.start()
        } else holder.binding.cardNotif.setCardBackgroundColor(Color.WHITE)

    }

    override fun getItemCount() = notif.size

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<DataNotif> =
            object : DiffUtil.ItemCallback<DataNotif>() {
                override fun areItemsTheSame(
                    oldUser: DataNotif,
                    newUser: DataNotif
                ): Boolean {
                    return oldUser.title == newUser.title
                }

                override fun areContentsTheSame(
                    oldUser: DataNotif,
                    newUser: DataNotif
                ): Boolean {
                    return oldUser == newUser
                }
            }
    }
}