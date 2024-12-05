package com.upnvjatim.silaturahmi.dosen.pembimbing.message.adapter

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import androidx.core.view.marginTop
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DiffUtil.DiffResult.NO_POSITION
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.upnvjatim.silaturahmi.databinding.TextReceiveBinding
import com.upnvjatim.silaturahmi.dosen.pembimbing.message.paging.MessageItem
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.inappmessaging.model.MessageType
import com.upnvjatim.silaturahmi.model.request.MessagesList
import com.upnvjatim.silaturahmi.model.response.DataValidasiLogin
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class MessageAdapter(private val context: Context, private val username: String)
    : PagingDataAdapter<MessageItem, MessageAdapter.ViewHolder>(DIFF_CALLBACK) {

    fun getMessageItem(position: Int): MessageItem? {
        Log.d("cek getmessageitem adapter", "${position} ; ${getItem(position)?.message}")
        return getItem(position) // Access the protected `getItem` here
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = TextReceiveBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    inner class ViewHolder(private val binding: TextReceiveBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: MessageItem, username: String) {
            val timestamp = data.timestamp?.let { Timestamp(it / 1000, ((it % 1000) * 1_000_000).toInt()) }
            val milliseconds = (timestamp?.seconds?:0) * 1000 + (timestamp?.nanoseconds?:0) / 1000000
            val netDate = Date(milliseconds)
            val currentDate = Calendar.getInstance().time
            val dateFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm")
            val isToday = currentDate.date == netDate.date

            if (isToday) binding.txtDatetime.text = SimpleDateFormat("HH:mm").format(netDate)
            else binding.txtDatetime.text = dateFormat.format(netDate)

            if(data.username == username){
                binding.txtSender.visibility = View.GONE
                binding.cardView5.setCardBackgroundColor(Color.parseColor("#C5D9CA"))
                binding.linear.gravity = Gravity.END
            }
            else {
                binding.txtSender.visibility = View.VISIBLE
                binding.txtSender.text = data.name // viewModel.fetchSender(messages[position].sender) ?: user
                binding.cardView5.setCardBackgroundColor(Color.WHITE)
                binding.linear.gravity = Gravity.START
            }
            binding.txtMessageReceive.text = data.message

            binding.cardView5.setOnLongClickListener {
                val rootView = binding.root.rootView
                val clipboard = rootView.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

                val textContent = binding.txtMessageReceive.text.toString()
                val clip = ClipData.newPlainText(textContent, textContent)
                clipboard.setPrimaryClip(clip)

                Snackbar
                    .make(rootView, "Copied $textContent to the Clipboard", Snackbar.LENGTH_SHORT).show()

                return@setOnLongClickListener true
            }

            binding.includeUserInput.txtMessageReply.setOnClickListener {
                Log.d("cek includeuserinput click bind", "${data.message}")
                onItemClickListener?.onClick(data)
            }

            binding.txtSender.text = data.name
            binding.txtMessageReceive.text = data.message
            binding.includeUserInput.txtMessageReply.text = data.reply
            binding.includeUserInput.txtSenderReply.text = data.senderReplay

//            if (blinkItem == position) {
//                val anim: Animation = AlphaAnimation(0.0f, 0.5f)
//                android.os.Handler().postDelayed({
//                    anim.duration = 200
//
////                    holder.itemView.startAnimation(anim)
//                    anim.setAnimationListener(object : Animation.AnimationListener {
//                        override fun onAnimationStart(animation: Animation?) {
//                        }
//
//                        override fun onAnimationEnd(animation: Animation?) {
//                            blinkItem = NO_POSITION
//                        }
//
//                        override fun onAnimationRepeat(animation: Animation?) {
//                        }
//                    })
//                }, 100)
//
//            }
            itemView.setOnClickListener {
                Log.d("cek itemview bind", "${data.message}")
                onItemClickListener?.onClick(data)
            }

            Log.d("cek datareply", "${data.reply} ; ${data.senderReplay}")

            if(data.reply != null && data.senderReplay != null){
                binding.includeUserInput.cardBorder.visibility = View.VISIBLE
                binding.includeUserInput.txtSenderReply.text = data.senderReplay
                binding.includeUserInput.txtMessageReply.text = data.reply
                if(data.username == username)
                    binding.includeUserInput.root.setPadding(0,4,0,0)

            } else {
                binding.includeUserInput.cardBorder.visibility = View.GONE
            }

        }
    }


    private val additionalMessages = mutableListOf<MessageItem>()

    // Override to present a combination of paginated data and new real-time messages
    override fun getItemCount(): Int = super.getItemCount() + additionalMessages.size

    private var blinkItem = NO_POSITION

//    private var mQuoteClickListener: QuoteClickListener? = null

//    fun setQuoteClickListener(listener: QuoteClickListener) {
//        mQuoteClickListener = listener
//    }

    private var onItemClickListener: OnItemClickListener? = null

    fun interface OnItemClickListener {
        fun onClick(alarm: MessageItem)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val paginatedDataCount = super.getItemCount() + additionalMessages.size
        if (position < paginatedDataCount) {
            val data = getItem(position)
            if (data != null) {
                holder.bind(data, username)
            }
        } else {
            val realTimeMessageIndex = position - paginatedDataCount
            Log.d("cek else chat", "${realTimeMessageIndex} ; ${additionalMessages.size}")
            additionalMessages.getOrNull(realTimeMessageIndex)?.let { realTimeMessage ->
                holder.bind(realTimeMessage, username)
            }
        }
    }

//    override fun getItemViewType(position: Int): Int {
//        return if (message.type == MessageType.SEND)
//            if (message.quotePos == -1) R.layout.send_message_row
//            else R.layout.send_message_quoted_row
//        else
//            if (message.quotePos == -1) R.layout.received_message_row
//            else R.layout.received_message_quoted_row
//    }

    fun setMessages(messageList: List<MessagesList>) {
//        this.messageList = messageList as ArrayList<MessageItem>
        notifyDataSetChanged()
    }


    fun blinkItem(position: Int) {
        blinkItem = position
        notifyItemChanged(position)
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<MessageItem>() {
            override fun areItemsTheSame(oldItem: MessageItem, newItem: MessageItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: MessageItem, newItem: MessageItem): Boolean {
                return oldItem.chatId == newItem.chatId
            }
        }
        const val ANIMATION_DURATION: Long = 300

    }
}

interface QuoteClickListener {
    fun onQuoteClick(messageItem: MessageItem)
}
