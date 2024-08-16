package br.edu.uea.buri.screens

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.net.wifi.WifiManager
import android.os.Build
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class ConnectivityRepository @Inject constructor(@ApplicationContext private val context: Context) {
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    private val _isConnected = MutableStateFlow(false)
    val isConnected : Flow<Boolean> = _isConnected

    init {
        connectivityManager.registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback(){
            override fun onAvailable(network: Network) {
                _isConnected.value = true
                if(connectivityManager.activeNetworkInfo?.isConnected == true){
                    val ssid = getCurrentSsid() ?: ""
                }
            }

            override fun onLost(network: Network) {
                _isConnected.value = false
            }

            override fun onUnavailable() {
                _isConnected.value = false
            }
        })
    }

    private fun getCurrentSsid(): String? {

        var ssid = wifiManager.connectionInfo.ssid
        ssid = if (ssid != null && ssid.startsWith("\"") && ssid.endsWith("\"")) {
            ssid.substring(1, ssid.length - 1)
        } else {
            ssid
        }
        return  ssid
    }
}