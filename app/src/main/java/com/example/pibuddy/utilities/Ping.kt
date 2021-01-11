package com.example.pibuddy.utilities

import com.google.common.net.InetAddresses
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.*
import java.util.regex.Matcher
import java.util.regex.Pattern


fun isPortOpen(ip : String, port : Int, timeout : Int): String {

    val check = InetAddresses.isInetAddress(ip)

    if (check == false){
        return "false"
    } else {

        try {
            val socket = Socket()
            socket.connect(InetSocketAddress(ip, port), timeout)

            socket.close()
            return "connection successfull"

        } catch (ce: ConnectException) {
            ce.printStackTrace()
            return "false"
        } catch (ce: SocketTimeoutException) {

            return "false"
        } catch (ex: Exception) {
            ex.printStackTrace()
            return "false"
        }
    }
}


