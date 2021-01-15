package com.dbtechprojects.pibuddy.utilities


import android.os.Handler
import android.os.Looper
import android.text.Editable
import com.jcraft.jsch.*
import java.io.ByteArrayOutputStream
import java.net.Socket
import java.util.*
import kotlin.concurrent.schedule


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
        session.timeout = 15000
        session.connect()

        channel = session.openChannel("exec") as ChannelExec?
        channel!!.setCommand(command)

        val responseStream = ByteArrayOutputStream()

        channel.outputStream = responseStream
        channel.connect(15000) //set session timeout



        while (channel.isConnected) {
            Thread.sleep(100)
            Timer().schedule(15000) {
                channel.disconnect() //disconnect channel if command output lasts longer than 15secs
            }
        }



        val responseString = String(responseStream.toByteArray())
        return (responseString)
    }
    catch (ce: JSchException){


        return "error - Please check Username/Password"

   }

    finally {
        if (session != null) {
            session.disconnect()
        }
        channel?.disconnect()
    }
}



