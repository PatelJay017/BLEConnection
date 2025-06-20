package com.demo.bleconnection_compose.bluetoothclassic

import android.annotation.SuppressLint
import android.app.Application
import android.bluetooth.*
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

class BluetoothHelper(private val context: Application) {

    private val adapter: BluetoothAdapter? =
        (context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager)?.adapter

    private var scanner: BluetoothLeScanner? = adapter?.bluetoothLeScanner

    fun isBluetoothEnabled(): Boolean = adapter?.isEnabled == true

    @SuppressLint("MissingPermission")
    fun getPairedDevices(): List<BluetoothDevice> {
        return if (ActivityCompat.checkSelfPermission(
                context,
                android.Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            adapter?.bondedDevices?.toList() ?: emptyList()
        } else emptyList()
    }

    @SuppressLint("MissingPermission")
    fun startScanning(onDeviceFound: (BluetoothDevice) -> Unit) {
        scanner?.startScan(object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                result.device?.let(onDeviceFound)
            }
        })
    }

    fun stopScanning() {
        scanner?.stopScan(object : ScanCallback() {})
    }
}