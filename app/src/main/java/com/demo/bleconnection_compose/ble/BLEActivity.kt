package com.demo.bleconnection_compose.ble

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.demo.bleconnection_compose.R

class BLEActivity : AppCompatActivity() {

val TAG = "BLEActivity"
    private lateinit var recyclerView: RecyclerView
    private val deviceList = mutableListOf<BluetoothDevice>()
    private lateinit var deviceAdapter: DeviceAdapter

    private lateinit var bluetoothAdapter: BluetoothAdapter
    private var bluetoothLeScanner: BluetoothLeScanner? = null
    private var bluetoothGatt: BluetoothGatt? = null

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            Log.d(TAG, "onScanResult: $result")
            val device = result.device
            if (!deviceList.contains(device)) {
                deviceList.add(device)
                deviceList.distinct()
                deviceAdapter.notifyItemInserted(deviceList.size - 1)
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_bleactivity)

        recyclerView = findViewById(R.id.recyclerView)
        deviceAdapter = DeviceAdapter(deviceList) { device ->
            connectToDevice(device)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = deviceAdapter

        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
        bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner

        requestPermissions()
    }

    private fun startScan() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission Required", Toast.LENGTH_SHORT).show()
            return
        }
        bluetoothLeScanner?.startScan(scanCallback)
    }

    private fun stopScan() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission Required", Toast.LENGTH_SHORT).show()
            return
        }
        bluetoothLeScanner?.stopScan(scanCallback)
    }

    private fun connectToDevice(device: BluetoothDevice) {
        stopScan()
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission Required", Toast.LENGTH_SHORT).show()
            return
        }
        bluetoothGatt = device.connectGatt(this, false, gattCallback)
    }

    private val gattCallback = object : BluetoothGattCallback() {

        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            Log.d(TAG, "status = $status")
            Log.d(TAG, "newState = $newState")
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.d(TAG, "Connected! Discovering services...")
                if (ActivityCompat.checkSelfPermission(this@BLEActivity, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this@BLEActivity, "Permission Required", Toast.LENGTH_SHORT).show()
                    return
                }
                gatt.discoverServices()
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.d(TAG, "Disconnected from GATT server")
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, "Services discovered")
                gatt.services.forEach { service ->
                    Log.d(TAG, "Service UUID: ${service.uuid}")
                }
            }
        }
    }

    private fun requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ActivityCompat.requestPermissions(this,
                arrayOf(
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT
                ), 1)
        } else {
            ActivityCompat.requestPermissions(this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION
                ), 1)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startScan()
        } else {
            Toast.makeText(this, "Permission needed for BLE", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission Required", Toast.LENGTH_SHORT).show()
            return
        }
        bluetoothGatt?.disconnect()
        bluetoothGatt?.close()
        bluetoothGatt = null
        stopScan()
    }
}