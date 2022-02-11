package com.dbtechprojects.pibuddy.repository


import com.dbtechprojects.pibuddy.models.Connection
import com.dbtechprojects.pibuddy.utilities.NetworkUtils


object SecureShellRepo {

    suspend fun executeSSHSession(connection: Connection) = NetworkUtils.createSSHSession(connection.username, connection.password, connection.ipAddress, connection.port)
    suspend fun setCommand(command: String) = NetworkUtils.runCommandInSession(command)
    fun disconnect() = NetworkUtils.disconnectSession()
}