package com.upnvjatim.silaturahmi.mahasiswa.dialogpopup

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.upnvjatim.silaturahmi.R
import com.google.android.material.button.MaterialButton


class PendaftaranDialogFragment(
    private val back: Boolean, private val title: String?, private val subtitle: String?,
    private val isFragment: Boolean, private val funYes: (() -> Unit)?
) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.popup_konfirmasi)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val tvTitle: TextView = dialog.findViewById(R.id.txt_titlekonfirmasi)
        val tvMessage: TextView = dialog.findViewById(R.id.textView11)
        val btnYes: MaterialButton = dialog.findViewById(R.id.btn_y)
        val btnNo: MaterialButton = dialog.findViewById(R.id.btn_g)

        if (back) tvMessage.text = "Data belum tersimpan! Apakah Anda yakin ingin kembali?"
        else {
            if(!title.isNullOrBlank()) tvTitle.text = title
            if(!subtitle.isNullOrBlank()) tvMessage.text = subtitle
        }

        btnYes.setOnClickListener {
            if (back && !isFragment) activity?.finish()
            else if (back && isFragment) findNavController().popBackStack()
            else if(funYes != null) funYes.invoke()
            dismiss()
        }

        btnNo.setOnClickListener {
            dismiss()
        }

        return dialog
    }
}
