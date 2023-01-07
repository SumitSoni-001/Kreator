package com.blog.kreator.utils

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission

/** Check Internet Connectivity */
object MonitorConnectivity {
    private val IMPL = if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
        Marshmellow
    }else{
        BaseImpl
    }

    fun isConnected(connectivityManager: ConnectivityManager):Boolean{
        return IMPL.isConnected(connectivityManager)
    }
}

interface ConnectedCompat {
    fun isConnected(connectivityManager: ConnectivityManager): Boolean
}

/** Getting network info for version greater or equal to Marshmallow */
object Marshmellow : ConnectedCompat {
    override fun isConnected(connectivityManager: ConnectivityManager): Boolean {
        return connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)?.hasCapability(
            NetworkCapabilities.NET_CAPABILITY_INTERNET
        ) == true
    }
}

/** Getting network info for version below Marshmallow */
object BaseImpl : ConnectedCompat {
//    @RequiresPermission(value = "android.permission.ACCESS_NETWORK_STATE")
    override fun isConnected(connectivityManager: ConnectivityManager): Boolean {
        return connectivityManager.activeNetworkInfo?.isConnected ?: false
    }
}