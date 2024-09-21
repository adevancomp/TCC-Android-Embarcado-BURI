package br.edu.uea.buri.screens.home.repository

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.startActivityForResult
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import java.io.InputStream
import java.util.UUID
import javax.inject.Inject

class BluetoothEsp32Repository @Inject constructor(@ApplicationContext private val context: Context) {
    private val bluetoothManager: BluetoothManager = context.getSystemService(BluetoothManager::class.java)
    private val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter
    private var bluetoothSocket: BluetoothSocket? = null
    private var esp32Device : BluetoothDevice? = null

    suspend fun createConnectionWithEsp32() : Boolean {
        var isSuccess: Boolean = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Android 12+ (API 31+)
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        if(isEsp32connected()){
            esp32Device?.let { device : BluetoothDevice ->
                try {

                    val buffer = ByteArray(1024)  // Buffer para armazenar os dados recebidos
                    var bytes: Int  // Número de bytes lidos
                    var inputStream: InputStream

                    bluetoothSocket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"))
                    bluetoothAdapter?.cancelDiscovery()
                    bluetoothSocket?.connect()
                    inputStream = bluetoothSocket!!.inputStream
                    isSuccess = true
                    Log.d("BURI", "Conexão com ESP32 bem-sucedida!")
                    bytes = inputStream.read(buffer)
                    val receivedMessage = String(buffer, 0, bytes)

                    // Loga a mensagem recebida
                    Log.d("BURI", "Dados recebidos do ESP32: $receivedMessage")
                } catch (e: IOException){
                    Log.e("BURI","Não foi possível conectar ao ESP32")
                    try {
                        bluetoothSocket?.close()
                    } catch (closeException: IOException) {
                        Log.e("BURI", "Não foi possível fechar o socket após falha de conexão", closeException)
                    }
                }
            }
        }
        return isSuccess
    }

    fun isEsp32connected() : Boolean {
        //Responde se o esp32 está conectado no bluetooth
        bluetoothAdapter?.let { adapter ->
            Log.i("BURI","Ta ai")
            //Dispositivo suporta bluetooth
            if(adapter.isEnabled){
                //Bluetooth está ligado
                Log.i("BURI","Habilitado")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    // Android 12+ (API 31+)
                    Log.i("BURI","Android 12+ (API 31+)")
                    if (ActivityCompat.checkSelfPermission(
                            context,
                            Manifest.permission.BLUETOOTH_CONNECT
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        Log.i("BURI","não tinha pessoal")
                        return false
                    }
                }
                Log.i("BURI","seguiu a vida")
                val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter.bondedDevices
                Log.i("BURI","Quantidade : $pairedDevices")
                pairedDevices?.forEach { device: BluetoothDevice ->
                    Log.i("Buri","Address ${device.address}")
                    if(device.address == ESP32_MAC_ADDRESS){
                        Log.i("BURI","achou o Buri esp32")
                        esp32Device = device
                        return true
                    }
                }
            }
        }
        return false
    }

    companion object {
        //const val ESP32_MAC_ADDRESS = "C8:2E:18:67:4F:A8"
        //C8:2E:18:67:4F:AA MAC ADDRESS que o android fisico me mostrou
        const val ESP32_MAC_ADDRESS = "C8:2E:18:67:4F:AA"
        const val ESP32_NAME = "Buri-Hardware"
    }
}