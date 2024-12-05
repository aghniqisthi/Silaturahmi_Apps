package com.upnvjatim.silaturahmi.mahasiswa.notif

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.upnvjatim.silaturahmi.databinding.FragmentNotifAktivitasBinding
import com.upnvjatim.silaturahmi.getUser
import com.upnvjatim.silaturahmi.model.request.DataNotif
import com.upnvjatim.silaturahmi.viewmodel.adapter.NotifAktivitasAdapter
import com.google.firebase.firestore.FirebaseFirestore

class NotifAktivitasFragment : Fragment() {
    private var _binding: FragmentNotifAktivitasBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentNotifAktivitasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)

        FirebaseFirestore.getInstance()
            .collection("notification")
            .document(getUser(requireContext())?.user?.username.toString())
            .get()
            .addOnSuccessListener {
                if(it != null) {

                    val notifArray = (it.get("notif") as? MutableList<Map<String, String>> ?: mutableListOf()).asReversed()
                    val a = notifArray.map {
                        Log.d("cek notifarr", "${it.get("title").toString()} ; ${it.get("message").toString()}")
                        DataNotif(it.get("title").toString(), it.get("message").toString(), it.get("seen").toString().toBoolean())
                    }
                    val adapter = NotifAktivitasAdapter(a)
                    binding.rvListprogram.adapter = adapter
                    binding.rvListprogram.layoutManager =
                        LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

                    seenNotif(getUser(requireContext())?.user?.username.toString())

                } else {
                    Toast.makeText(requireContext(), "Program kosong!", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Error : ${it.message}", Toast.LENGTH_SHORT).show()
                Log.e("error get notif", "${it.message} ${it.cause} ${it.stackTrace}")
            }
    }

    private fun seenNotif(username:String) {
        val docRef = FirebaseFirestore.getInstance()
            .collection("notification")
            .document(username)

        docRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                var a = false
                val notifArray = document.get("notif") as? MutableList<Map<String, Any>> ?: mutableListOf()
                val updatedNotifArray = notifArray.map { notif ->
                    if (notif["seen"] == false) {
                        a = true
                        notif.toMutableMap().apply { this["seen"] = true }
                    } else notif
                }

                if(a) docRef.update("notif", updatedNotifArray)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Notifications updated successfully", Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener { e ->
                        Toast.makeText(context, "Error updating notifications: ${e.message}", Toast.LENGTH_SHORT).show()
                        Log.e("error notif", "${e.message} ${e.cause} ${e.stackTrace}")
                    }
            }
        }
    }
}