package com.demo.bleconnection_compose.bluetoothclassic

import android.app.Application
import android.bluetooth.BluetoothDevice
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BluetoothViewModel(application: Application) : AndroidViewModel(application) {

    private val helper = BluetoothHelper(application)

    private val _isBluetoothOn = MutableStateFlow(helper.isBluetoothEnabled())
    val isBluetoothOn = _isBluetoothOn.asStateFlow()

    private val _pairedDevices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    val pairedDevices = _pairedDevices.asStateFlow()

    private val _nearbyDevices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    val nearbyDevices = _nearbyDevices.asStateFlow()

    init {
        refreshBluetoothState()
    }

    fun refreshBluetoothState() {
        _isBluetoothOn.value = helper.isBluetoothEnabled()
        _pairedDevices.value = helper.getPairedDevices()
    }

    fun startScan() {
        viewModelScope.launch {
            helper.startScanning { device ->
                _nearbyDevices.value = _nearbyDevices.value.toMutableSet().apply {
                    add(device)
                }.toList()
            }
        }
    }

    fun stopScan() {
        helper.stopScanning()
    }
}