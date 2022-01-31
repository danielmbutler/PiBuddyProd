package com.dbtechprojects.pibuddy.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dbtechprojects.pibuddy.repository.SecureShellRepo
import com.dbtechprojects.pibuddy.utilities.Constants
import com.dbtechprojects.pibuddy.utilities.NetworkUtils
import com.dbtechprojects.pibuddy.utilities.NetworkUtils.executeRemoteCommand
import com.dbtechprojects.pibuddy.utilities.NetworkUtils.isPortOpen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class ResultViewModel : ViewModel() {

    private val _restartAttemptMessage = MutableLiveData<String>()
    val restartAttemptMessage: LiveData<String>
        get() = _restartAttemptMessage

    private val _powerOffAttemptMessage = MutableLiveData<String>()
    val powerOffAttemptMessage: LiveData<String>
        get() = _powerOffAttemptMessage



    fun restartButtonClick(ipaddress: String, username: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {

            //pingtest
            val pingtest = async {
                isPortOpen(
                    ipaddress,
                    22,
                    3000
                )
            }
            Log.d("pingtest", pingtest.await().toString() + ipaddress)

            if (!pingtest.await()) {
                _restartAttemptMessage.postValue(Constants.CONNECTION_ERROR)

            } else {
                // run command

                val testcommand = async {
                    executeRemoteCommand(
                        username,
                        password,
                        ipaddress, "echo hello"
                    )
                }

                //Log.d("testcommand", testcommand.await())

                if (!testcommand.await().contains("hello")) {

                    _restartAttemptMessage.postValue(Constants.SESSION_ERROR)

                } else {

                    //run real command

                    val RestartCommand = async {
                        executeRemoteCommand(
                            username,
                            password,
                            ipaddress, "sudo systemctl start reboot.target"
                        )
                    }

                    _restartAttemptMessage.postValue(Constants.REBOOT_MESSAGE)

                }
            }

        }
    }

    fun powerOffButtonClicked(username: String, password: String, IPAddress: String) {
        viewModelScope.launch(Dispatchers.IO) {

            //pingtest
            val pingtest = async {
                isPortOpen(
                    IPAddress.toString(),
                    22,
                    3000
                )
            }
            //Log.d("pingtest", pingtest.await())

            if (!pingtest.await()) {
                _powerOffAttemptMessage.postValue(Constants.CONNECTION_ERROR)

            } else {
                // run command

                val testcommand = async {
                    executeRemoteCommand(
                        username,
                        password,
                        IPAddress, "echo hello"
                    )
                }

                //Log.d("testcommand", testcommand.await())

                if (!testcommand.await().contains("hello")) {
                    _powerOffAttemptMessage.postValue(Constants.SESSION_ERROR)

                } else {

                    //run real command

                    val ShutdownCommand = async {
                        executeRemoteCommand(
                            username,
                            password,
                            IPAddress, "sudo shutdown -P now"
                        )
                    }
                    _powerOffAttemptMessage.postValue(Constants.SHUTTING_DOWN_MESSAGE)

                }
            }
        }
    }

    fun sshClick(ipaddress: String, username: String, password: String){
        viewModelScope.launch(Dispatchers.IO) {

            SecureShellRepo.setCommand("echo hello")
        }
    }

}