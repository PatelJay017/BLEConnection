package com.demo.bleconnection_compose.ble

import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.demo.bleconnection_compose.R

class DeviceAdapter(
    private val devices: List<BluetoothDevice>,
    private val onDeviceClick: (BluetoothDevice) -> Unit
) : RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder>() {

    inner class DeviceViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(device: BluetoothDevice) {
            val deviceName = itemView.findViewById<TextView>(R.id.deviceName)
            val deviceAddress = itemView.findViewById<TextView>(R.id.deviceAddress)
            val deviceAlias = itemView.findViewById<TextView>(R.id.deviceAlias)
            val deviceType = itemView.findViewById<TextView>(R.id.deviceType)
            val deviceBondState = itemView.findViewById<TextView>(R.id.deviceBondState)

            deviceName.text = "Name: ${device.name ?: "Unknown"}"
            deviceAddress.text = "Address: ${device.address}"
            deviceAlias.text = "Alias: ${device.alias ?: "N/A"}"
            deviceType.text = "Type: ${getDeviceType(device.type)}"
            deviceBondState.text = "Bond State: ${getBondState(device.bondState)}"

            itemView.setOnClickListener {
                onDeviceClick.invoke(device)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_device, parent, false)
        return DeviceViewHolder(view)
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        holder.bind(devices[position])
    }

    override fun getItemCount() = devices.size

    private fun getDeviceType(type: Int): String {
        return when (type) {
            BluetoothDevice.DEVICE_TYPE_CLASSIC -> "Classic"
            BluetoothDevice.DEVICE_TYPE_LE -> "Low Energy (LE)"
            BluetoothDevice.DEVICE_TYPE_DUAL -> "Dual Mode"
            BluetoothDevice.DEVICE_TYPE_UNKNOWN -> "Unknown"
            else -> "Other"
        }
    }

    private fun getBondState(bondState: Int): String {
        return when (bondState) {
            BluetoothDevice.BOND_BONDED -> "Bonded"
            BluetoothDevice.BOND_BONDING -> "Bonding"
            BluetoothDevice.BOND_NONE -> "Not Bonded"
            else -> "Unknown"
        }
    }
}