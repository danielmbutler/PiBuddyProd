package com.dbtechprojects.pibuddy.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CommandResults(
    var results: String? = null, //LoggedInUsers
    var diskSpace: String? = null,
    var memUsage: String? = null,
    var cpuUsage: String? = null,
    var testCommand: Boolean? = null,
    var customCommand: String? = null
) : Parcelable