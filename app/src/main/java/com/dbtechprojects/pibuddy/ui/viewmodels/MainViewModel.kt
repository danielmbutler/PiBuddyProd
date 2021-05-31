package com.dbtechprojects.pibuddy.ui.viewmodels

import android.util.Log
import androidx.lifecycle.*
import com.dbtechprojects.pibuddy.models.CommandResults
import com.dbtechprojects.pibuddy.models.PingResult
import com.dbtechprojects.pibuddy.repository.repository
import com.dbtechprojects.pibuddy.utilities.NetworkUtils.executeRemoteCommand
import com.dbtechprojects.pibuddy.utilities.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class MainViewModel : ViewModel() {
    private  val TAG = "MainViewModel"


    private val _pingTest = repository._pingTest
    val pingTest: LiveData<Resource<PingResult>>
        get() = _pingTest.asLiveData()

    private val _commandResults = repository._commandResults
    val commandResults: LiveData<Resource<CommandResults>>
        get() = _commandResults.asLiveData()


    fun pingTest(ip: String) {
        repository.pingTest(ip, viewModelScope)
    }

    fun runPiCommand( ipAddress: String,
                      username: String,
                      password: String,
                      customCommand: String?
    ){
        viewModelScope.launch {
            repository.runPiCommands(
                username = username,
                ipAddress = ipAddress,
                password = password,
                customCommand = customCommand,
                scope = viewModelScope
            )
        }
    }

}