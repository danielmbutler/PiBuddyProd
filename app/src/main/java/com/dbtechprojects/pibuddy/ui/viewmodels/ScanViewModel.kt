package com.dbtechprojects.pibuddy.ui.viewmodels

import androidx.lifecycle.*
import com.dbtechprojects.pibuddy.models.PingResult
import com.dbtechprojects.pibuddy.repository.Repository
import com.dbtechprojects.pibuddy.utilities.Resource

class ScanViewModel : ViewModel() {

    private val TAG = "ScanViewModel"

    private val _ips = Repository._scanPingTest
    val ips: LiveData<Resource<PingResult>>
        get() = _ips.asLiveData()

    // descending count of IP Addresses
    private val _addressCount = Repository.addressCount
    val addressCount: LiveData<Int>
        get() = _addressCount

    fun scanIPs(netAddresses: Array<String>){
        Repository.scanIPs(netAddresses, viewModelScope)
    }

    fun cancelScan(){
        Repository.cancelScan()
    }
}