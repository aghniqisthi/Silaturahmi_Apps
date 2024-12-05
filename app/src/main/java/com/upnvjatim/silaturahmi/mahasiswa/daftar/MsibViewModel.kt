package com.upnvjatim.silaturahmi.mahasiswa.daftar

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.upnvjatim.silaturahmi.model.response.ItemLookupProgram
import com.upnvjatim.silaturahmi.model.response.ItemLookupPaketKonversi
import java.io.File

class MsibViewModel : ViewModel() {
    val jenisMsib: MutableLiveData<String?> = MutableLiveData()
    val file: MutableLiveData<File?> = MutableLiveData()
    val fileName: MutableLiveData<String?> = MutableLiveData()
    val programData: MutableLiveData<ItemLookupProgram?> = MutableLiveData()
    var paketData: MutableLiveData<ItemLookupPaketKonversi?> = MutableLiveData()
}

