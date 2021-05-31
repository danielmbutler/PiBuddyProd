package com.dbtechprojects.pibuddy.ui.viewmodels

import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dbtechprojects.pibuddy.models.CommandResults
import com.dbtechprojects.pibuddy.utilities.NetworkUtils.executeRemoteCommand
import com.dbtechprojects.pibuddy.utilities.NetworkUtils.isPortOpen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class MainViewModel : ViewModel() {
    private  val TAG = "MainViewModel"

    private val _pingTest = MutableLiveData<Boolean>()
    val pingTest: LiveData<Boolean>
        get() = _pingTest

    private val _commandResults = MutableLiveData<CommandResults>()
    val commandResults: LiveData<CommandResults>
        get() = _commandResults

    fun pingTest(ip: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = isPortOpen(
                ip,
                22,
                3000
            )
            _pingTest.postValue(result)
            Log.d(TAG, "pingTest: $result ")
        }
    }

    fun runPiCommands(
        ipAddress: String,
        username: String,
        password: String,
        customCommand: String?
    ) {
        val resultsObject = CommandResults()
        viewModelScope.launch(Dispatchers.IO) {
            val testCommand = async {
                executeRemoteCommand(
                    username,
                    password,
                    ipAddress,
                    "echo hello"
                )
            }


            if (!testCommand.await().contains("hello")) {


                resultsObject.testCommand = false
                _commandResults.postValue(resultsObject)
                return@launch


            } else {

                resultsObject.testCommand = true
                Log.d(TAG, "runPiCommands: testCommand completed successfull ${testCommand.await()}")

                val LoggedInUsers = async {
                    executeRemoteCommand(
                        username,
                        password,
                        ipAddress,
                        "who | cut -d' ' -f1 | sort | uniq\n"
                    )
                }

                val DiskSpace = async {
                    executeRemoteCommand(
                        username,
                        password,
                        ipAddress,
                        "df -hl | grep \'root\' | awk \'BEGIN{print \"\"} {percent+=$5;} END{print percent}\' | column -t"
                    )
                }
                //
                val MemUsage = async {
                    executeRemoteCommand(
                        username,
                        password,
                        ipAddress,
                        "awk '/^Mem/ {printf(\"%u%%\", 100*\$3/\$2);}' <(free -m)"
                    )
                }
                val CpuUsage = async {
                    executeRemoteCommand(
                        username,
                        password,
                        ipAddress,
                        "cat <(grep 'cpu ' /proc/stat) <(sleep 1 && grep 'cpu ' /proc/stat) | awk -v RS=\"\" '{print (\$13-\$2+\$15-\$4)*100/(\$13-\$2+\$15-\$4+\$16-\$5)}'"

                    )
                }
                customCommand?.let {
                    val CustomCommandRun = async {
                        executeRemoteCommand(
                            username,
                            password,
                            ipAddress,
                           it
                        )

                    }
                    resultsObject.customCommand = CustomCommandRun.await()
                }

                resultsObject.cpuUsage = CpuUsage.await()
                resultsObject.diskSpace = DiskSpace.await()
                resultsObject.memUsage = MemUsage.await()
                resultsObject.loggedInUsers = LoggedInUsers.await()
                resultsObject.username = username
                resultsObject.password = password
                resultsObject.ipAddress = ipAddress
                _commandResults.postValue(resultsObject)


            }
        }

    }

}