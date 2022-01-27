package com.dbtechprojects.pibuddy.ui.viewmodels

import androidx.lifecycle.*
import com.dbtechprojects.pibuddy.models.Connection
import com.dbtechprojects.pibuddy.repository.SecureShellRepo
import com.dbtechprojects.pibuddy.utilities.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ShellViewModel(connection: Connection) : ViewModel() {
    private val _commandOutput = MutableLiveData<Resource<String>>()
    val commandOutput: LiveData<Resource<String>>
        get() = _commandOutput

    fun sendCommand(command: String){
        viewModelScope.launch(Dispatchers.IO) {
            val output = SecureShellRepo.setCommand(command)
            withContext(Dispatchers.Main){_commandOutput.postValue(output)}
        }
    }

    init {
        startShellSession(connection)
    }

    private fun startShellSession(connection: Connection){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                SecureShellRepo.executeSSHSession(connection)
            }catch (e: Exception){
               withContext(Dispatchers.Main) {_commandOutput.postValue(Resource.Error("Session error connection lost"))}
            }
        }
    }

    fun disconnectSession(){
        SecureShellRepo.disconnect()
    }


}

class ShellViewModelFactory(
    private val connection: Connection
): ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ShellViewModel(connection) as T
    }
}