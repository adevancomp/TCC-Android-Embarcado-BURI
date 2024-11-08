package br.edu.uea.buri.screens.home.repository

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import br.edu.uea.buri.domain.measurement.Measurement
import br.edu.uea.buri.domain.measurement.RawMeasurement
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.math.BigDecimal
import java.util.UUID
import javax.inject.Inject

class BluetoothEsp32Repository @Inject constructor(@ApplicationContext private val context: Context) {
    private val bluetoothManager: BluetoothManager = context.getSystemService(BluetoothManager::class.java)
    private val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter
    private var bluetoothSocket: BluetoothSocket? = null
    private var esp32Device : BluetoothDevice? = null
    private var _isError : MutableLiveData<Boolean> = MutableLiveData(false)
    val isError : LiveData<Boolean> = _isError

    suspend fun createConnectionWithEsp32() : Boolean {
        var isSuccess = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                _isError.postValue(true)
                return false
            }
        }
        if(isEsp32connected()){
            esp32Device?.let { device : BluetoothDevice ->
                try {
                    bluetoothSocket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"))
                    bluetoothAdapter?.cancelDiscovery()
                    bluetoothSocket?.let {
                        if(!it.isConnected){
                            Log.i("BURI","Não tinha um blueetooth socket conectado ainda")
                            it.connect()
                        }else{
                            Log.i("BURI","o bluetooth socket já estava conectado")
                        }
                    }
                    isSuccess = true
                    _isError.postValue(false)
                    Log.i("BURI","Conectou no esp32")
                } catch (e: IOException){
                    Log.e("BURI","Não foi possível conectar ao ESP32 ${e.message}")
                    //_isError.postValue(true)
                    try {
                        bluetoothSocket?.close()
                    } catch (closeException: IOException) {
                        _isError.postValue(true)
                        Log.e("BURI", "Não foi possível fechar o socket após falha de conexão", closeException)
                    }
                }
            }
        }
        return isSuccess
    }

    private fun isEsp32connected() : Boolean {
        bluetoothAdapter?.let { adapter ->
            Log.i("BURI","Tem suporte bluetooth")
            if(adapter.isEnabled){
                Log.i("BURI","Habilitado")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    // Android 12+ (API 31+)
                    Log.i("BURI","Android 12+ (API 31+)")
                    if (ActivityCompat.checkSelfPermission(
                            context,
                            Manifest.permission.BLUETOOTH_CONNECT
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        Log.i("BURI","não tinha permissão")
                        _isError.postValue(true)
                        return false
                    }
                }
                val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter.bondedDevices
                Log.i("BURI","Quantidade : $pairedDevices")
                pairedDevices?.forEach { device: BluetoothDevice ->
                    Log.i("Buri","Address ${device.address}")
                    if(device.address == ESP32_MAC_ADDRESS){
                        Log.i("BURI","achou o Buri Hardware")
                        esp32Device = device
                        _isError.postValue(false)
                        return true
                    }
                }
            }
        }
        return false
    }

    suspend fun getMeasurement() : RawMeasurement? {
        var data : RawMeasurement? = null
        if(createConnectionWithEsp32()){
            try {
                Log.i("BURI","socket: $bluetoothSocket")
                bluetoothSocket?.inputStream?.let {
                        stream: InputStream ->

                    val buffer = ByteArray(100)
                    val bytes = stream.read(buffer)
                    val receivedMessage = String(buffer, 0, bytes)

                    Log.d("BURI", "Dados recebidos do ESP32: $receivedMessage")
                    try {
                        val jsonObject = JSONObject(receivedMessage)
                        data = RawMeasurement(
                            co = BigDecimal(jsonObject.getString("co")),
                            air = BigDecimal(jsonObject.getString("airH")),
                            tmp = BigDecimal(jsonObject.getString("temp")),
                            collectionDate = "",
                            equipmentId = ""
                        )
                    } catch (e: Exception){
                        Log.e("BURI", "Erro ao desserializar JSON recebido por bluetooth: ${e.message}")
                    }
                }
            } catch (e: IOException){
                _isError.postValue(true)
                Log.e("BURI", "Erro de leitura no InputStream: ${e.message}")
            }
        }
        return data
    }

    companion object {
        const val ESP32_MAC_ADDRESS = "C8:2E:18:67:4F:AA"
        const val ESP32_NAME = "Buri-Hardware"
    }
}