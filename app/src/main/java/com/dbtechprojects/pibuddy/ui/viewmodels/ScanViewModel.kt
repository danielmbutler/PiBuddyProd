package com.dbtechprojects.pibuddy.ui.viewmodels

import android.util.Log
import androidx.lifecycle.*
import com.dbtechprojects.pibuddy.models.PingResult
import com.dbtechprojects.pibuddy.repository.repository
import com.dbtechprojects.pibuddy.utilities.NetworkUtils.isPortOpen
import com.dbtechprojects.pibuddy.utilities.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ScanViewModel : ViewModel() {

    private val TAG = "ScanViewModel"

    private val _ips = repository._pingTest
    val ips: LiveData<Resource<PingResult>>
        get() = _ips.asLiveData()

    // descending count of IP Addresses
    private val _addressCount = repository.addressCount
    val addressCount: LiveData<Int>
        get() = _addressCount

    fun scanIPs(netAddresses: Array<String>){
        repository.scanIPs(netAddresses, viewModelScope)
    }

    fun cancelScan(){
        repository.cancelScan()
    }
}