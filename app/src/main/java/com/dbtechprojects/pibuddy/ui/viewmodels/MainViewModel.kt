package com.dbtechprojects.pibuddy.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dbtechprojects.pibuddy.utilities.NetworkUtils.isPortOpen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val _pingTest = MutableLiveData<Boolean>()
    val pingTest: LiveData<Boolean>
        get() = _pingTest

    fun pingTest(ip: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = isPortOpen(
                ip,
                22,
                3000
            )
            _pingTest.postValue(result)
        }
    }
}