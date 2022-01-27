package com.dbtechprojects.pibuddy.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Connection(
    val ipAddress: String,
    val username: String,
    val password: String
): Parcelable