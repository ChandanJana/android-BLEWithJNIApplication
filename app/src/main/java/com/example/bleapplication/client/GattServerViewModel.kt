package com.example.bleapplication.client

import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanResult
import androidx.databinding.BaseObservable
import com.example.bleapplication.utils.StringUtils
import java.math.BigInteger

class GattServerViewModel(private val mBluetoothDevice: BluetoothDevice?, private val scanResult: ScanResult) : BaseObservable() {
    fun serverName(): String {
        return "Name: ".plus(mBluetoothDevice?.name)
            .plus("\nHexaDecimal: "+ toHex(mBluetoothDevice?.name) +"\n")
            .plus("\nByteArray: "+ StringUtils.byteArrayInHexFormat(toByteArray(mBluetoothDevice?.name)) +"\n")
            .plus("Address: " + mBluetoothDevice?.address)
            .plus("\n"+ StringUtils.byteArrayInHexFormat(scanResult.scanRecord?.bytes!!))
            .plus("\nRssi:"+ scanResult.rssi)
            //.plus("\n"+ StringUtils.byteArrayInHexFormat(scanResult.scanRecord?.getManufacturerSpecificData()))
    }

    private fun toHex(arg: String?): String? {
        return if (arg != null)
            java.lang.String.format("%040x", BigInteger(1, arg.toByteArray()))
        else
            ""
    }

    private fun toByteArray(name: String?): ByteArray{
       if (name != null)
           return name.toByteArray()
        else
            return byteArrayOf()

    }
}