package com.example.pibuddy.utilities


import android.text.Editable
import com.jcraft.jsch.ChannelExec
import com.jcraft.jsch.JSch
import com.jcraft.jsch.JSchException
import com.jcraft.jsch.Session
import java.io.ByteArrayOutputStream


suspend fun executeRemoteCommand(
    username: Editable,
    password: Editable,
    hostname: Editable, command: String,
    port: Int = 22): String {
    var session: Session? = null
    var channel: ChannelExec? = null
    try {
        session = JSch().getSession(username.toString(), hostname.toString(), port)
        session.setPassword(password.toString())
        session.setConfig("StrictHostKeyChecking", "no")
        session.connect()

        channel = session.openChannel("exec") as ChannelExec?
        channel!!.setCommand(command)
        val responseStream = ByteArrayOutputStream()

        channel.outputStream = responseStream
        channel.connect()

        while (channel.isConnected) {
            Thread.sleep(100)
        }
        val responseString = String(responseStream.toByteArray())
        return (responseString)
    } catch (ce: JSchException){
        return "error - Please check Username/Password"

    } finally {
        if (session != null) {
            session.disconnect()
        }
        channel?.disconnect()
    }
}



