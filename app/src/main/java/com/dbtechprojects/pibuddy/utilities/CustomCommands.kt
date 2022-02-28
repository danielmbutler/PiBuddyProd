package com.dbtechprojects.pibuddy.utilities


object CustomCommands {

    val COMMAND_LIST = listOf(
        CustomButton(name = "Ping Server", command = "ping [server address]"),
        CustomButton(name = "Flush DNS", command = "sudo systemd-resolve --flush-caches"),
        CustomButton(name = "Take Picture", command = "raspistill"),
        CustomButton(name = "Remove File", command = "rm [filepath]"),
        CustomButton(name = "Copy File", command = "cp [from] [to]"),
        CustomButton(name = "Create Folder", command = "mkdir [folder]"),
        CustomButton(name = "Download File", command = "wget [uri] -P [filepath]"),
        CustomButton(name = "Update Packages", command = "sudo apt-get update"),
        CustomButton(name = "Upgrade Packages", command = "sudo apt-get upgrade"),
    )
}

data class CustomButton(
    val name : String,
    val command: String
)