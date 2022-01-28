package com.dbtechprojects.pibuddy.utilities


import android.content.SharedPreferences
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.appcompat.app.AlertDialog

object Constants {
    const val CONNECTION_ERROR = "Connection Failure Please Retry.."
    const val SESSION_ERROR = "Device Session failure, Please confirm username and password"
    const val REBOOT_MESSAGE = "Your device is now rebooting...."
    const val USERNAME_PASSWORD_ERROR = "error - Please check Username/Password"
    const val SHUTTING_DOWN_MESSAGE = "Your device is now shutting down.... "
    const val WAITING_MESSAGE = "waiting...."

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

    // saving for later

//    private fun showDeleteDialogSingleItem(ipAddress: String, index: Int) {
//        val builder = AlertDialog.Builder(this@MainActivity)
//        builder.setMessage("Are you sure you want to Delete?")
//            .setCancelable(false)
//            .setPositiveButton("Yes") { dialog, id ->
//
//                val editor: SharedPreferences.Editor = pref.edit()
//                editor.remove(ipAddress)
//                editor.apply()
//                navigationView.menu.removeItem(index)
//                drawer.closeDrawers()
//                setupDrawItems()
//
//            }
//            .setNegativeButton("No") { dialog, id ->
//                // Dismiss the dialog
//                dialog.dismiss()
//            }
//        val alert = builder.create()
//        alert.show()
//    }
}