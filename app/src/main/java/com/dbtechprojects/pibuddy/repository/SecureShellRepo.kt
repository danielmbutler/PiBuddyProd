package com.dbtechprojects.pibuddy.repository


import com.dbtechprojects.pibuddy.utilities.NetworkUtils


object SecureShellRepo {

    suspend fun executeSSHSession(username: String, password: String, hostname: String) = NetworkUtils.createSSHSession(username, password, hostname)
    suspend fun setCommand(command: String) = NetworkUtils.runCommandInSession(command)
    fun disconnect() = NetworkUtils.disconnectSession()
}