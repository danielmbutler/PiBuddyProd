package com.dbtechprojects.pibuddy.utilities

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan


object Constants {
    const val CONNECTION_ERROR = "Connection Failure Please Retry.."
    const val SESSION_ERROR = "Device Session failure, Please confirm username and password"
    const val REBOOT_MESSAGE = "Your device is now rebooting...."
    const val USERNAME_PASSWORD_ERROR = "error - Please check Username/Password"
    const val SHUTTING_DOWN_MESSAGE = "Your device is now shutting down.... "

    fun setColorForText(text: String, color: Int): SpannableString {
        val spannableString = SpannableString(text)
        val foregroundSpan = ForegroundColorSpan(color)
        spannableString.setSpan(
            foregroundSpan,
            0,
            spannableString.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return spannableString
    }
}