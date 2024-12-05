package com.upnvjatim.silaturahmi.dosen.kaprodi

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import android.widget.RadioButton
import android.widget.RadioGroup
import com.upnvjatim.silaturahmi.R
import com.upnvjatim.silaturahmi.databinding.FragmentPlottingBinding
import com.upnvjatim.silaturahmi.getUser
import com.upnvjatim.silaturahmi.model.response.ItemPeriode
import com.upnvjatim.silaturahmi.semester
import com.upnvjatim.silaturahmi.viewmodel.PendaftarProgramViewModel
import com.upnvjatim.silaturahmi.viewmodel.PeriodeViewModel
import com.upnvjatim.silaturahmi.viewmodel.adapter.PlottingAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import android.os.Handler
import android.os.Looper

class PlottingFragment : Fragment() {
    private var _binding: FragmentPlottingBinding? = null
    private val binding get() = _binding!!

    var selectedPeriode: ItemPeriode? = null
    var listPeriode: List<ItemPeriode?>? = null
    var selectedStatus: String = ""
    private var query: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlottingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("cek periode", selectedPeriode?.id.toString())
        requireActivity().enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, 0)
            insets
        }

        if(getUser(requireContext())?.user?.role?.id == "HA07"){
            binding.toolbar.visibility = View.VISIBLE
        } else {
            Log.d("cek idd", getUser(requireContext())?.user?.role?.id.toString())
            binding.toolbar.visibility = View.GONE
        }

        val viewmodel = ViewModelProvider(this)[PendaftarProgramViewModel::class.java]
        viewmodel.callApiPendaftarProgram(
            authHeader = "bearer ${getUser(requireContext())?.token}",

            /// TODO : CHECK AGAIN
            idProgramProdi = getUser(requireContext())?.user?.idProgramProdi.toString(),
            idJenisMbkm = null, // getUser(requireContext())?.user?.idJenisMbkm.toString(),
            idProgram = null, // getUser(requireContext())?.user?.idProgram.toString(),
            statusMbkm = null, // "MSIB,MANDIRI",
            sortByStatus = null, // "PEGAWAI",
            idPegawai = null, sortBy = null, sortType = null, status = selectedStatus, idPeriode = selectedPeriode?.id,
            pageNumber = 1, pageSize = 100, query = null
        )
        viewmodel.liveDataPendaftarProgram.observe(viewLifecycleOwner) {
//        viewmodel.getliveDataPendaftarProgram().observe(viewLifecycleOwner) {
            if (it != null) {
                binding.recyclerView.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                binding.recyclerView.adapter = PlottingAdapter(it.data?.items!!)
            } else {
                Toast.makeText(requireContext(), "ItemPeriode kosong!", Toast.LENGTH_SHORT).show()
            }
        }
        viewmodel.isLoading.observe(viewLifecycleOwner) {
            if (it) binding.progressBar.visibility = View.VISIBLE
            else binding.progressBar.visibility = View.INVISIBLE
        }
        viewmodel.isError.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        }

        binding.btnFilter.setOnClickListener {
            val dialog = BottomSheetDialog(requireContext())
            val views = layoutInflater.inflate(R.layout.bottomsheet_filterplotting, null)

//          btnClose.setOnClickListener {
//             dialog.dismiss() // calling a dismiss method to close dialog.
//          }

            val viewModelPeriode = ViewModelProvider(this)[PeriodeViewModel::class.java]

            viewModelPeriode.getliveDataPeriode(
                "bearer ${getUser(requireContext())?.token?.AccessToken}"
            ).observe(viewLifecycleOwner) {
                val radioGroup = views.findViewById<RadioGroup>(R.id.radioGroupPeriode)
                radioGroup.removeAllViews()

                var i = 0
                it.data?.map { item ->
                    if (radioGroup != null) {
                        val radioBtn = LayoutInflater.from(requireContext())
                            .inflate(R.layout.radiobutton_periode, radioGroup, false) as RadioButton
                        radioBtn.text = "Tahun ${item?.tahun} Semester ${semester(item?.semester.toString())}"
                        radioBtn.id = i
                        radioGroup.addView(radioBtn)
                        i++
                    }
                }

                listPeriode = it.data

                radioGroup.setOnCheckedChangeListener { group, checkedId ->
                    val selectedRadioButton = group.findViewById<RadioButton>(checkedId)
                    Log.d("cek periode id",
                        "${listPeriode!![selectedRadioButton.id]} ; ${selectedRadioButton.id}")
                    selectedPeriode = listPeriode!![selectedRadioButton.id]
                    // status 5 : belum ; status 6 : sudah
                    // idPeriode=0715ab25-a20e-40bb-8832-2be87f03beab
                }
            }

            val btnPerbarui = views.findViewById<MaterialButton>(R.id.btn_perbarui)
            val btnBatal = views.findViewById<MaterialButton>(R.id.btn_batal)

            views.findViewById<RadioButton>(R.id.radioBtnBelum).setOnClickListener{
                Log.d("cek status id", "5")
                selectedStatus = "5"
                // status 5 : belum ; status 6 : sudah
                // idPeriode=0715ab25-a20e-40bb-8832-2be87f03beab
            }

            views.findViewById<RadioButton>(R.id.radioBtnSudah).setOnClickListener {
                Log.d("cek status id",
                    "6")
                selectedStatus = "6"
            }

            btnPerbarui?.setOnClickListener {
                viewmodel.callApiPendaftarProgram(
                    authHeader = "bearer ${getUser(requireContext())?.token}",
                    idProgramProdi = null, // getUser(requireContext())?.user?.idProgramProdi.toString(),
                    idJenisMbkm = null, // getUser(requireContext())?.user?.idJenisMbkm.toString(),
                    idProgram = null, // getUser(requireContext())?.user?.idProgram.toString(),
                    statusMbkm = null, // "MSIB,MANDIRI",
                    sortByStatus = null, // "PEGAWAI",
                    idPegawai = null, sortBy = null, sortType = null, status = selectedStatus, idPeriode = selectedPeriode?.id,
                    pageNumber = 1, pageSize = 100, query = query
                )
                viewmodel.liveDataPendaftarProgram.observe(viewLifecycleOwner) {
//                viewmodel.getliveDataPendaftarProgram().observe(viewLifecycleOwner) {
                    if (it != null) {
                        binding.recyclerView.layoutManager =
                            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                        binding.recyclerView.adapter = PlottingAdapter(it.data?.items!!)
                    } else {
                        Toast.makeText(requireContext(), "ItemPeriode kosong!", Toast.LENGTH_SHORT).show()
                    }
                    dialog.dismiss()
                }
            }

            btnBatal?.setOnClickListener {
                dialog.dismiss()
            }

//            dialog.setCancelable(false)     // set cancelable to avoid closing dialog box when clicking screen.
            dialog.setContentView(views)     // set content view to our view.
            dialog.show()       // calling a show method to display a dialog.
        }

        binding.editSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                query = s.toString()

                // Remove the previous callback if the user is still typing
                searchRunnable?.let { searchHandler.removeCallbacks(it) }

                // Schedule a new task to run after the specified delay
                searchRunnable = Runnable {
                    viewmodel.callApiPendaftarProgram(
                        authHeader = "bearer ${getUser(requireContext())?.token}",
                        idProgramProdi = null, // getUser(requireContext())?.user?.idProgramProdi.toString(),
                        idJenisMbkm = null, // getUser(requireContext())?.user?.idJenisMbkm.toString(),
                        idProgram = null, // getUser(requireContext())?.user?.idProgram.toString(),
                        statusMbkm = null, // "MSIB,MANDIRI",
                        sortByStatus = null, // "PEGAWAI",
                        idPegawai = null, sortBy = null, sortType = null, status = selectedStatus, idPeriode = selectedPeriode?.id,
                        pageNumber = 1, pageSize = 100, query = query
                    )
                }

                // Schedule the task to run after the delay
                searchHandler.postDelayed(searchRunnable!!, SEARCH_DELAY)
            }
        })
    }

    private var searchHandler: Handler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null
    private val SEARCH_DELAY = 500L // 500 milliseconds debounce
}