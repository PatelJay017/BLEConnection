package com.demo.bleconnection_compose.bluetoothclassic

import android.bluetooth.BluetoothDevice
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BluetoothDeviceUI(
    viewModel: BluetoothViewModel,
    onEnableBluetooth: () -> Unit,
    onMakeDiscoverable: () -> Unit
) {
    val isBluetoothOn by viewModel.isBluetoothOn.collectAsState()
    val pairedDevices by viewModel.pairedDevices.collectAsState()
    val nearbyDevices by viewModel.nearbyDevices.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Top Action Buttons
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(onClick = onEnableBluetooth, modifier = Modifier.weight(1f)) {
                Text("Enable Bluetooth")
            }
            Button(onClick = onMakeDiscoverable, modifier = Modifier.weight(1f)) {
                Text("Discoverable")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(onClick = viewModel::startScan, modifier = Modifier.weight(1f)) {
                Text("Start Scan")
            }
            Button(onClick = viewModel::stopScan, modifier = Modifier.weight(1f)) {
                Text("Stop Scan")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text("Bluetooth ON: $isBluetoothOn", style = MaterialTheme.typography.bodyLarge)

        Spacer(modifier = Modifier.height(12.dp))

        Text("Paired Devices:", style = MaterialTheme.typography.titleMedium)
        DeviceList(devices = pairedDevices)

        Spacer(modifier = Modifier.height(16.dp))

        Text("Nearby Devices:", style = MaterialTheme.typography.titleMedium)
        DeviceList(devices = nearbyDevices)
    }
}

@Composable
fun DeviceList(devices: List<BluetoothDevice>) {
    LazyColumn {
        items(devices) { device ->
            Text(
                text = "${device.name ?: "Unknown"} (${device.address})",
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
}