package com.upnvjatim.silaturahmi.mahasiswa.homenavbar

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.upnvjatim.silaturahmi.DATAPASS
import com.upnvjatim.silaturahmi.DATAUSER
import com.upnvjatim.silaturahmi.DETAILUSER
import com.upnvjatim.silaturahmi.IDPENDAFTARPROGRAM
import com.upnvjatim.silaturahmi.LISTROLE
import com.upnvjatim.silaturahmi.LoginActivity
import com.upnvjatim.silaturahmi.databinding.FragmentProfileBinding
import com.upnvjatim.silaturahmi.fakultas
import com.upnvjatim.silaturahmi.getUser
import com.upnvjatim.silaturahmi.model.request.UsersData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.soundcloud.android.crop.Crop
import java.io.File
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.util.UUID

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private var avatar: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, 0)
            insets
        }

        (activity as AppCompatActivity?)!!.setSupportActionBar(binding.toolbar)
        binding.toolbar.setTitle("Profil")

        binding.apply {
            txtNama.text = getUser(requireContext())?.user?.name
            txtNpm.text = getUser(requireContext())?.user?.username
            txtProdi.text = getUser(requireContext())?.user?.prodi?.namaProdi ?: "-"
            txtFakultas.text = fakultas(getUser(requireContext())?.user?.prodi?.kdFakjur.toString())
            txtAkses.text = getUser(requireContext())?.user?.role?.name
        }

        context?.let { context ->  // if (isAdded && context != null) {
            FirebaseFirestore.getInstance()
                .collection("users")
                .document(getUser(context)?.user?.username.toString())
                .get()
                .addOnSuccessListener {
                    val temp = it.getString("avatar").toString()
                    if (it.exists() && temp != null && temp != "") {
                        avatar = temp
                        Glide.with(requireContext()).load(avatar).into(binding.imgAvatar)
                    } else {
                        Log.d("cek data users not found", "${it.data?.size} ; $temp")
                    }
                }.addOnFailureListener {
                    Toast.makeText(
                        requireContext(),
                        "Error getAvatar : ${it.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("error getavatar", "${it.message} ${it.cause} ${it.stackTrace}")
                }
        }

        binding.cardAvatar.setOnClickListener {
            val minTiramisu = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
            val permission = if (minTiramisu) Manifest.permission.READ_MEDIA_IMAGES
                else Manifest.permission.READ_EXTERNAL_STORAGE

            if (checkSelfPermission(requireContext(), permission) == PERMISSION_GRANTED == true) {
                val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                pickImageAvatarLauncher.launch(galleryIntent)
            } else {
                requestPermissionLauncher.launch(permission)
            }
        }

        binding.btnEditProfile.setOnClickListener {
            val minTiramisu = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
            val permission = if (minTiramisu) Manifest.permission.READ_MEDIA_IMAGES
                else Manifest.permission.READ_EXTERNAL_STORAGE

            if (checkSelfPermission(requireContext(), permission) == PERMISSION_GRANTED == true) {
                val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                pickImageAvatarLauncher.launch(galleryIntent)
            } else {
                requestPermissionLauncher.launch(permission)
            }
        }

        binding.btnKeluar.setOnClickListener {
            val sharedPrefUser = requireActivity().getSharedPreferences(DATAUSER, Context.MODE_PRIVATE)
            sharedPrefUser.edit().clear().apply()

            val sharedPrefPendaftar = requireActivity().getSharedPreferences(IDPENDAFTARPROGRAM, Context.MODE_PRIVATE)
            sharedPrefPendaftar.edit().clear().apply()

            val sharedPrefDetailUser = requireActivity().getSharedPreferences(DETAILUSER, Context.MODE_PRIVATE)
            sharedPrefDetailUser.edit().clear().apply()

            val sharedPrefPass = requireActivity().getSharedPreferences(DATAPASS, Context.MODE_PRIVATE)
            sharedPrefPass.edit().clear().apply()

            val sharedPrefRole = requireActivity().getSharedPreferences(LISTROLE, Context.MODE_PRIVATE)
            sharedPrefRole.edit().clear().apply()

            val intent = Intent(requireActivity(), LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) Log.d("Permission", "Granted")
            else Log.d("Permission", "Denied")
        }

    private val cropImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            val croppedUri = Crop.getOutput(result.data)
            Log.d("cek croppeduri - 3", "$croppedUri")

            croppedUri?.let { imageUri ->
//                val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, it)
//                imageView.setImageBitmap(bitmap)

                val username = getUser(requireContext())?.user?.username.toString()
                val nip = getUser(requireContext())?.user?.nip

                val firestore = FirebaseFirestore.getInstance()
                firestore.collection("users")
                    .document(if (nip != "" && nip != null && nip != "null") nip.toString() else username)
                    .get()
                    .addOnSuccessListener {
                        if(it != null && it.data != null){
                            Log.d("cek data users - 2", "${it.data?.values}")
                            uploadAvatar(imageUri)
                        } else {
                            Log.d("cek else - 2", "${it.data?.size}")
                            firestore.collection("users")
                                .document(getUser(requireContext())?.user?.username.toString())
                                .set(
                                    UsersData(
                                        avatar = "",
//                                        idPegawai = getUser(requireContext())?.user?.idPegawai.toString(),
//                                        idProdi = getUser(requireContext())?.user?.prodi?.id.toString(),
//                                        idRole = getUser(requireContext())?.user?.role?.id.toString(),
//                                        idSiamikMahasiswa = getUser(requireContext())?.user?.idSiamikMahasiswa.toString(),
//                                        idUser = getUser(requireContext())?.user?.id.toString(),
//                                        kdFakjur = getUser(requireContext())?.user?.prodi?.kdFakjur.toString(),
//                                        namaProdi = getUser(requireContext())?.user?.prodi?.namaProdi.toString(),
//                                        name = getUser(requireContext())?.user?.name.toString(),
//                                        userAccessToken = "",
//                                        username = getUser(requireContext())?.user?.username.toString()
                                    )
                                ).addOnSuccessListener {
                                    Toast.makeText(
                                        requireContext(), "Data pengguna berhasil dibuat", Toast.LENGTH_SHORT).show()
                                    uploadAvatar(imageUri)
                                }.addOnFailureListener { e ->
                                    Toast.makeText(context, "Failed set users: ${e.message}", Toast.LENGTH_SHORT).show()
                                    Log.e("error saveusersdata", "${e.message} ${e.cause} ${e.stackTrace}")
                                }
                        }
                    }.addOnFailureListener {
                        Toast.makeText(requireContext(), "Error : ${it.message}", Toast.LENGTH_SHORT).show()
                        Log.e("error getUser change avatr", "${it.message} ${it.cause} ${it.stackTrace}")
                    }
            }
        }
    }

    private val pickImageAvatarLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                val selectedImageUri = result.data?.data
                Log.d("cek resultcode ${result.resultCode} - 1", "${selectedImageUri.toString()}")

                selectedImageUri?.let { imageUri ->
                    val destinationUri = Uri.fromFile(File(requireContext().cacheDir, "cropped"))
                    cropImageLauncher.launch(Crop.of(imageUri, destinationUri).asSquare().getIntent(requireActivity()))
                }
            }
        }

    private fun uploadAvatar(imageUri: Uri) {
        val auth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()
        val storageRef = FirebaseStorage.getInstance().reference
        val imageRef = storageRef.child("avatars/${UUID.randomUUID()}")
        val uploadTask = imageRef.putFile(imageUri)

        val userID = auth.currentUser?.uid
        userID?.let {
            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    Toast.makeText(
                        requireContext(),
                        "Error ${task.exception?.cause} : ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e(
                        "failed upload avatar",
                        "${task.exception?.message} ${task.exception?.cause} ${task.exception?.stackTrace}"
                    )
                }
                imageRef.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result

                    Log.d("cek task success", "${task.isSuccessful} : $downloadUri")
//                    avatar = downloadUri.toString()

                    firestore.collection("users")
                        .document(getUser(requireContext())?.user?.username.toString())
                        .update("avatar", downloadUri)
                        .addOnSuccessListener {
                            avatar?.let { deleteAvatar(it) }
                            Toast.makeText(requireContext(), "Avatar berhasil diubah", Toast.LENGTH_SHORT).show()
                            Glide.with(this).load(downloadUri).into(binding.imgAvatar)
                        }.addOnFailureListener { e ->
                            Toast.makeText(
                                requireContext(),
                                "Failed to update avatar: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Failed ${task.exception?.cause}: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e(
                        "error update avatar",
                        "${
                            task.exception?.message
                        } ${
                            task.exception?.cause
                        } ${task.exception?.stackTrace}"
                    )
                }
            }
        }
    }

    private fun deleteAvatar(fullUrl: String){
        try {
            // Extract the path part after '/o/' and before '?'
            val encodedPath = fullUrl.substringAfter("/o/").substringBefore("?")
            // Decode the URL-encoded path to get the original image path
            val imagePath = URLDecoder.decode(encodedPath, StandardCharsets.UTF_8.name())
            val storageReference = FirebaseStorage.getInstance().getReference(imagePath)

            // Delete the file
            storageReference.delete()
                .addOnSuccessListener {
                    // File deleted successfully
                    Log.d("FirebaseStorage", "Image deleted successfully")
                }
                .addOnFailureListener { e ->
                    // Uh-oh, an error occurred!
                    Log.e("FirebaseStorage", "Failed to delete image: ${e.message}")
                }
        } catch (e: Exception) {
            e.printStackTrace()
            null // Return null if URL format is invalid
        }

    }
}