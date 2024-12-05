package com.upnvjatim.silaturahmi.mahasiswa.dialogpopup

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.upnvjatim.silaturahmi.R
import com.upnvjatim.silaturahmi.model.request.EditNilaiMk
import com.upnvjatim.silaturahmi.model.response.ItemPendaftarProgram
import com.upnvjatim.silaturahmi.model.response.MatkulKonversi
import com.upnvjatim.silaturahmi.viewmodel.NilaiViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText


class EditNilaiDialogFragment(
    private val data: MatkulKonversi,
    private val dataPendaftar: ItemPendaftarProgram
) : DialogFragment() {

    private val nilaiViewmodel: NilaiViewModel by activityViewModels()
    var listener: DialogListener? = null

    interface DialogListener {
        fun onDialogDismissed(getData: ItemPendaftarProgram)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.popup_ubahnilai)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val tvMk: TextView = dialog.findViewById(R.id.txt_matkul)
        val txtNilai: TextInputEditText = dialog.findViewById(R.id.edit_nilai)
        val progressBar: ProgressBar = dialog.findViewById(R.id.progress_bar)
        val btnYes: MaterialButton = dialog.findViewById(R.id.btn_y)
        val btnNo: MaterialButton = dialog.findViewById(R.id.btn_g)

        tvMk.text = data.namaMatkul
        btnYes.setOnClickListener {
            /// TODO : save data nilai to nilai final
            val datass = EditNilaiMk(
                id = data.id.toString(),
                idMatkul = data.idMatkul.toString(),
                idPaketKonversi = data.idPaketKonversi.toString(),
                idPendaftarMbkm = data.idPendaftarMbkm.toString(),
                nilaiAngka = txtNilai.text.toString(),
                nilaiHuruf = null,
                sks = data.sks.toString()
            )
            nilaiViewmodel.editNilai(datass, requireContext())
            var a = nilaiViewmodel.liveDataEditNilai.value // observe(viewLifecycleOwner){
            if(a != null)
                Toast.makeText(requireContext(), "Nilai ${data.namaMatkul} berhasil diubah", Toast.LENGTH_SHORT).show()

            var load = nilaiViewmodel.isLoading.value // .observe(viewLifecycleOwner) {
            if (load != null && load == true) {
                progressBar.visibility = View.VISIBLE
                btnYes.text = ""
            }
            else {
                progressBar.visibility = View.INVISIBLE
                btnYes.text = "Simpan"
            }

            var err = nilaiViewmodel.isError.value // .observe(viewLifecycleOwner) {
            if(err != null) Toast.makeText(requireContext(), err, Toast.LENGTH_LONG).show()

            dismiss()
            listener?.onDialogDismissed(dataPendaftar)
        }

        btnNo.setOnClickListener {
            dismiss()
        }

        return dialog
    }
}
