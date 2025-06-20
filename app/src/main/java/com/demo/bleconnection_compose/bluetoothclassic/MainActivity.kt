package com.demo.bleconnection_compose.bluetoothclassic

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import com.demo.bleconnection_compose.bluetoothclassic.ui.theme.BLEconnectioncomposeTheme

class MainActivity : ComponentActivity() {
    private val viewModel: BluetoothViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                "android.permission.BLUETOOTH_SCAN",
                "android.permission.BLUETOOTH_CONNECT",
                "android.permission.BLUETOOTH_ADVERTISE",
                "android.permission.ACCESS_FINE_LOCATION",
            ),
            100
        )

        val enableBluetoothLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                viewModel.refreshBluetoothState()
            }

        setContent {
            BLEconnectioncomposeTheme {
                BluetoothDeviceUI(
                    viewModel = viewModel,
                    onEnableBluetooth = {
                        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                        enableBluetoothLauncher.launch(enableBtIntent)
                    },
                    onMakeDiscoverable = {
                        val discoverableIntent =
                            Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
                                putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)
                            }
                        startActivity(discoverableIntent)
                    }
                )
            }
        }
    }
}