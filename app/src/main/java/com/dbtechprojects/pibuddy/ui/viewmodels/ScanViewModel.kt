package com.dbtechprojects.pibuddy.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dbtechprojects.pibuddy.utilities.NetworkUtils.isPortOpen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class ScanViewModel : ViewModel() {

    private  val TAG = "ScanViewModel"

    private val _ips = MutableLiveData<String>()
    val ips: LiveData<String>
        get() = _ips

    // descending count of IP Addresses
    private val _addressCount = MutableLiveData<Int>()
    val addressCount: LiveData<Int>
        get() = _addressCount

    // value to control whether scan should be running
    private var _scanRunning = true




    fun scanIPs(netAddresses: Array<String>) {
        // set scan to running
        _scanRunning = true
        var addresscount = netAddresses.count()


        viewModelScope.launch(Dispatchers.IO) {

                netAddresses.forEach {
                    Log.d(TAG, "loop runs")
                    if (_scanRunning){
                        Log.d(TAG, "scanning : $it")
                        Log.d(TAG, "scanning : scan status: $_scanRunning")

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
                            Log.d(TAG, "scanIPs: successful : $it, ips left: $addresscount")
                        } else {
                            // decrement address count
                            addresscount--
                            //post new address count value
                            _addressCount.postValue(addresscount)
                            Log.d(TAG, "scanIPs: unsuccessful : $it, ips left: $addresscount")
                        }
                    } else{
                        return@forEach
                    }
                }
            }
    }

        fun cancelScan() {
            _scanRunning = false
        }
    }