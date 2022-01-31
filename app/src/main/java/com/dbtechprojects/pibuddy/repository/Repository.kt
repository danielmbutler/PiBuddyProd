package com.dbtechprojects.pibuddy.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dbtechprojects.pibuddy.models.CommandResults
import com.dbtechprojects.pibuddy.models.Connection
import com.dbtechprojects.pibuddy.models.PingResult
import com.dbtechprojects.pibuddy.utilities.NetworkUtils
import com.dbtechprojects.pibuddy.utilities.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


object Repository {
    private const val TAG = "repository"
    //val _pingTest = MutableStateFlow<Resource<PingResult>>(Resource.Initial())
    val _scanPingTest = MutableStateFlow<Resource<PingResult>>(Resource.Initial())
    //val _commandResults = MutableStateFlow<Resource<CommandResults>>(Resource.Initial())

    // value to control whether scan should be running
    private var _scanRunning = true

    private val _addressCount = MutableLiveData<Int>()
    val addressCount: LiveData<Int>
        get() = _addressCount

    private val _commandResults = MutableLiveData<Repository.CommandResult>()
    val commandResults: LiveData<Repository.CommandResult>
        get() = _commandResults

    suspend fun pingTest(ip: String, scope: CoroutineScope): Resource<PingResult> {
       return suspendCoroutine<Resource<PingResult>> { Pingresult ->
            scope.launch(Dispatchers.IO){
                val result = NetworkUtils.isPortOpen(ip, 22, 5000)


                Pingresult.resume(Resource.Success(PingResult(ip,result)))

//                _pingTest.value = Resource.Success(
//                    PingResult(
//                        ip,
//                        result = result.await()
//                    )
//                )
                Log.d(TAG, "pingTestRepository: $result ")
            }
        }

    }


    suspend fun runPiCommands(
        ipAddress: String,
        username: String,
        password: String,
        customCommand: String?,
        scope: CoroutineScope
    ): Resource<CommandResults> {
        Log.d("customCommand", "custom command $customCommand")

        return suspendCoroutine<Resource<CommandResults>> {commandResult ->
            val resultsObject = CommandResults()
            scope.launch(Dispatchers.IO) {
                val testCommand = async {
                    NetworkUtils.executeRemoteCommand(
                        username,
                        password,
                        ipAddress,
                        "echo hello"
                    )
                }


                if (!testCommand.await().contains("hello")) {

                    resultsObject.testCommand = false
                    commandResult.resume(Resource.Error(message = "failed"))
                    return@launch


                } else {

                    resultsObject.testCommand = true
                    Log.d(
                        TAG,
                        "runPiCommands: testCommand completed successfull ${testCommand.await()}"
                    )

                    val LoggedInUsers = async {
                        NetworkUtils.executeRemoteCommand(
                            username,
                            password,
                            ipAddress,
                            "who | cut -d' ' -f1 | sort | uniq\n"
                        )
                    }

                    val DiskSpace = async {
                        NetworkUtils.executeRemoteCommand(
                            username,
                            password,
                            ipAddress,
                            "df -hl | grep \'root\' | awk \'BEGIN{print \"\"} {percent+=$5;} END{print percent}\' | column -t"
                        )
                    }
                    //
                    val MemUsage = async {
                        NetworkUtils.executeRemoteCommand(
                            username,
                            password,
                            ipAddress,
                            "awk '/^Mem/ {printf(\"%u%%\", 100*\$3/\$2);}' <(free -m)"
                        )
                    }
                    val CpuUsage = async {
                        NetworkUtils.executeRemoteCommand(
                            username,
                            password,
                            ipAddress,
                            "mpstat | awk '\$12 ~ /[0-9.]+/ { print 100 - \$12\"%\" }'"

                        )
                    }
                    customCommand?.let {
                        val CustomCommandRun = async {
                            NetworkUtils.executeRemoteCommand(
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
                   // Log.d(TAG, "runPiCommands: $resultsObject")
                    commandResult.resume(Resource.Success(resultsObject))
                }
            }
        }


    }

    fun scanIPs(netAddresses: Array<String>, scope: CoroutineScope) {
        // set scan to running
        _scanRunning = true
        var addresscount = netAddresses.count()


        scope.launch(Dispatchers.IO) {

            netAddresses.forEach {
                Log.d(TAG, "loop runs")
                if (_scanRunning) {
                    Log.d(TAG, "scanning : $it")
                    Log.d(TAG, "scanning : scan status: $_scanRunning")

                    val pingtest = async {
                        NetworkUtils.isPortOpen(
                            it,
                            22,
                            1000
                        )

                    }

                    if (pingtest.await()) {
                        _scanPingTest.value = Resource.Initial() // clear state
                        _scanPingTest.value = Resource.Success(PingResult(ipAddress = it, true))
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
                } else {
                    return@forEach
                }
            }
        }
    }

    suspend fun runCommand(scope: CoroutineScope, connection: Connection, command: String) {

        scope.launch(Dispatchers.IO) {
                val result = async {
                    NetworkUtils.executeRemoteCommand(
                        connection.username,
                        connection.password,
                        connection.ipAddress,
                        command
                    )
                }
                Log.d("result" , "result : ${result.await()}")
                _commandResults.postValue(CommandResult(connection.ipAddress, result = result.await()))
            }
    }

    fun cancelScan() {
        _scanRunning = false
    }

    data class CommandResult(
        val ip: String,
        val result: String,
    )
}

