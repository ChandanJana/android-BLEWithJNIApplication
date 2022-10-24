package com.example.bleapplication

import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanResult

/**
 * Created by Chandan Jana on 21-10-2022.
 * Company name: Mindteck
 * Email: chandan.jana@mindteck.com
 */
data class CustomBleDevice(
    var bluetoothDevice: BluetoothDevice,
    var serviceResult: ScanResult
)
