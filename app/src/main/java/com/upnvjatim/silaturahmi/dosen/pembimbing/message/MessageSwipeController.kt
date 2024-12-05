package com.upnvjatim.silaturahmi.dosen.pembimbing.message

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_IDLE
import androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_SWIPE
import androidx.recyclerview.widget.ItemTouchHelper.RIGHT
import androidx.recyclerview.widget.RecyclerView
import com.upnvjatim.silaturahmi.R
import com.upnvjatim.silaturahmi.dosen.pembimbing.message.adapter.MessageAdapter
import com.upnvjatim.silaturahmi.dosen.pembimbing.message.paging.MessageItem

interface SwipeControllerActions {
    fun showReplyUI(position: Int)
}

class MessageSwipeController(private val swipeControllerActions: SwipeControllerActions) :
    ItemTouchHelper.Callback() {

    private var currentItemViewHolder: RecyclerView.ViewHolder? = null
    private lateinit var mView: View
    private var dX = 0f
    private var swipeBack = false
    private var startTracking = false

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        mView = viewHolder.itemView
//        imageDrawable = context.getDrawable(R.drawable.rounded_send_24)!!
//        shareRound = context.getDrawable(R.drawable.shape_button_circle)!!
        return makeMovementFlags(ACTION_STATE_IDLE, RIGHT)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

    override fun convertToAbsoluteDirection(flags: Int, layoutDirection: Int): Int {
        if (swipeBack) {
            swipeBack = false
            return 0
        }
        return super.convertToAbsoluteDirection(flags, layoutDirection)
    }

    override fun onChildDraw(
        c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
        dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
    ) {
        if (actionState == ACTION_STATE_SWIPE) {
            setTouchListener(recyclerView, viewHolder)
        }

        if (mView.translationX < 80 || dX < this.dX) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            this.dX = dX
            startTracking = true
        }
        currentItemViewHolder = viewHolder
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setTouchListener(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        recyclerView.setOnTouchListener { _, event ->
            swipeBack = event.action == MotionEvent.ACTION_CANCEL || event.action == MotionEvent.ACTION_UP
            if (swipeBack) {
                if (Math.abs(mView.translationX) >= 50) {
                    val position = viewHolder.adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
//                        val messageItem = viewHolder.itemId
//                        (recyclerView.adapter as? MessageAdapter)?.getMessageItem(position)
                        Log.d("cek settouchlistener", "$position")
                        swipeControllerActions.showReplyUI(position)
                    }
                }
            }
            false
        }
    }
}