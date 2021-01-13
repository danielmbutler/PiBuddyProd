package com.example.pibuddy.utilities

import com.google.common.net.InetAddresses
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.*
import java.util.regex.Matcher
import java.util.regex.Pattern

public   fun validate(ip: String): Boolean {
    val PATTERN = "^((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)$";

    return ip.matches(PATTERN.toRegex());
}

public fun isPortOpen(ip : String, port : Int, timeout : Int): String {

// validate if IP is properly formated


    val validationResult = validate(ip)

    if(!validationResult){
        return "false"
    }else {
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



