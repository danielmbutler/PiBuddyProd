package com.dbtechprojects.pibuddy.ui.viewmodels

import android.util.Log
import androidx.lifecycle.*
import com.dbtechprojects.pibuddy.models.CommandResults
import com.dbtechprojects.pibuddy.models.Connection
import com.dbtechprojects.pibuddy.models.PingResult
import com.dbtechprojects.pibuddy.repository.Repository
import com.dbtechprojects.pibuddy.repository.SecureShellRepo
import com.dbtechprojects.pibuddy.utilities.Resource
import kotlinx.coroutines.launch

class DeploymentViewModel : ViewModel() {

    private val _ips = MutableLiveData<Resource<PingResult>>()
    val ips: LiveData<Resource<PingResult>>
        get() = _ips
    val commandResults: LiveData<Repository.CommandResult> = Repository.commandResults

    fun runCommand(connection: Connection, command: String, port: Int){
        viewModelScope.launch {
            Log.d("deployment", "runnning: $command")
            Repository.runCommand(this, connection, command, port)
        }
    }

    fun testDevice(ipAddress :String, port: Int){
        viewModelScope.launch {
            Repository.pingTest(ipAddress, viewModelScope, port ).let { pingResult ->
                Log.d("deployment", "command: ${pingResult.data}")
                _ips.postValue(pingResult)
            }
        }
    }

    fun cancelScan(){
        Repository.cancelScan()
    }
}
