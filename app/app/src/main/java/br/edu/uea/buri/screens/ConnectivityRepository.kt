package br.edu.uea.buri.screens

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class ConnectivityRepository @Inject constructor(@ApplicationContext private val context: Context) {
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val _isConnected = MutableStateFlow(false)
    val isConnected : Flow<Boolean> = _isConnected

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback(){
                override fun onAvailable(network: Network) {
                    _isConnected.value = true
                }

                override fun onLost(network: Network) {
                    _isConnected.value = false
                }

                override fun onUnavailable() {
                    _isConnected.value = false
                }
            })
        } else {
            //val activeNetworkInfo = connectivityManager.activeNetworkInfo
            //_isConnected.value = activeNetworkInfo?.isConnected ?: false
            val networkReceiver = object: BroadcastReceiver(){
                override fun onReceive(p0: Context?, p1: Intent?) {
                    val activeNetworkInfo = connectivityManager.activeNetworkInfo
                    _isConnected.value = activeNetworkInfo?.isConnected ?: false
                }
            }
            val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
            context.registerReceiver(networkReceiver,intentFilter)
        }
    }
}