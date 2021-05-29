package com.dbtechprojects.pibuddy.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dbtechprojects.pibuddy.utilities.NetworkUtils.isPortOpen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class ScanViewModel : ViewModel() {

    private val _ips = MutableLiveData<String>()
    val ips: LiveData<String>
        get() = _ips

    // descending count of IP Addresses
    private val _addressCount = MutableLiveData<Int>()
    val addressCount: LiveData<Int>
        get() = _addressCount

    // value to control whether scan should be running
    private var _scanRunning = false




    fun scanIPs(netAddresses: Array<String>) {
        // set scan to running
        _scanRunning = true
        var addresscount = netAddresses.count()

        // do this while scan is running is set to true
        while (_scanRunning) {
            viewModelScope.launch(Dispatchers.IO) {
                netAddresses.forEach {
                    //Log.d(TAG, cancelled)

                    val pingtest = async {
                        isPortOpen(
                            it,
                            22,
                            1000
                        )

                    }

                    if (pingtest.await()) {
                        _ips.postValue(it)
                        // decrement address count
                        addresscount--
                        //post new address count value
                        _addressCount.postValue(addresscount)

                    }
                }
            }
        }
    }

        fun cancelScan() {
            _scanRunning = false
        }
    }