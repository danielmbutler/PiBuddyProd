package com.dbtechprojects.pibuddy.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dbtechprojects.pibuddy.utilities.NetworkUtils.executeRemoteCommand
import com.dbtechprojects.pibuddy.utilities.NetworkUtils.isPortOpen
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
        viewModelScope.launch {

            //pingtest
            val pingtest = async {
                isPortOpen(
                    ipaddress,
                    22,
                    3000
                )
            }
            //Log.d("pingtest", pingtest.await())

            if (!pingtest.await()) {
                _restartAttemptMessage.postValue("Connection Failure Please Retry..")

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

                    _restartAttemptMessage.postValue("Device Session failure, Please confirm username and password")

                } else {

                    //run real command

                    val RestartCommand = async {
                        executeRemoteCommand(
                            username,
                            password,
                            ipaddress, "sudo systemctl start reboot.target"
                        )
                    }

                    _restartAttemptMessage.postValue("Your device is now rebooting....")

                }
            }

        }
    }

    fun powerOffButtonClicked(username: String, password: String, IPAddress: String) {
        viewModelScope.launch {

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
                _powerOffAttemptMessage.postValue("Connection Failure Please Retry..")

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
                    _powerOffAttemptMessage.postValue("Device Session failure, Please confirm username and password")

                } else {

                    //run real command

                    val ShutdownCommand = async {
                        executeRemoteCommand(
                            username,
                            password,
                            IPAddress, "sudo shutdown -P now"
                        )
                    }
                    _powerOffAttemptMessage.postValue("Your device is now shutting down....")

                }
            }
        }
    }
}