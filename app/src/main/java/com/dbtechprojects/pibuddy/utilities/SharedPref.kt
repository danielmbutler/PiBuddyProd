package com.dbtechprojects.pibuddy.utilities

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class SharedPref(context: Context) {


    // create shared preferences

    companion object {
        private var sharedPref: SharedPreferences? = null

        fun getSharedPref(context: Context) : SharedPreferences {
            if (sharedPref != null){
                return sharedPref!!
            }else {
                 val masterKey = MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build()
                sharedPref = EncryptedSharedPreferences.create(
                    context,
                    "Connections",
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                )
                return sharedPref as SharedPreferences
            }
        }
    }
}