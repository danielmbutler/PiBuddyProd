package com.dbtechprojects.pibuddy.ui.viewmodels

import androidx.lifecycle.*
import com.dbtechprojects.pibuddy.models.CommandResults
import com.dbtechprojects.pibuddy.models.PingResult
import com.dbtechprojects.pibuddy.repository.Repository
import com.dbtechprojects.pibuddy.utilities.Resource
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class MainViewModel : ViewModel() {
    private  val TAG = "MainViewModel"


    private val _pingTest = MutableLiveData<Resource<PingResult>>()
    val pingTest: LiveData<Resource<PingResult>>
        get() = _pingTest

    private val _commandResults = MutableLiveData<Resource<CommandResults>>()
    val commandResults: LiveData<Resource<CommandResults>>
        get() = _commandResults


    fun pingTest(ip: String) {
        viewModelScope.launch {
           _pingTest.postValue(Repository.pingTest(ip, viewModelScope))
        }
    }

    fun runPiCommand( ipAddress: String,
                      username: String,
                      password: String,
                      customCommand: String?
    ){
        viewModelScope.launch {
           _commandResults.postValue(
               Repository.runPiCommands(
                   username = username,
                   ipAddress = ipAddress,
                   password = password,
                   customCommand = customCommand,
                   scope = viewModelScope
               )
           )
        }
    }



}