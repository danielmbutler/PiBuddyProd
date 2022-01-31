package com.dbtechprojects.pibuddy.utilities

sealed class Resource<T>(
    val data: T? = null,
    val error: String? = null
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Loading<T>(data: T? = null) : Resource<T>(data)
    class Initial<T>(data: T? = null) : Resource<T>(data)
    class Error<T>(throwable: String? = null, data: T? = null, message: String? = null) : Resource<T>(data, throwable)
}
