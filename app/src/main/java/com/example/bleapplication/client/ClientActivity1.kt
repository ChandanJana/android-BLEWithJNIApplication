package com.example.bleapplication.client

import android.Manifest.permission.*
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothProfile
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.bleapplication.Constants.CLIENT_UUID
import com.example.bleapplication.Constants.REQUEST_ENABLE_BT
import com.example.bleapplication.Constants.REQUEST_FINE_LOCATION
import com.example.bleapplication.Constants.REQUEST_PERMISSION_CODE
import com.example.bleapplication.Constants.RX_UUID
import com.example.bleapplication.Constants.SCAN_PERIOD
import com.example.bleapplication.Constants.TX_UUID
import com.example.bleapplication.Constants.UART_UUID
import com.example.bleapplication.CustomBle1
import com.example.bleapplication.R
import com.example.bleapplication.databinding.ActivityClientBinding
import com.example.bleapplication.databinding.ViewGattServerBinding
import com.example.bleapplication.utils.StringUtils.byteArrayInHexFormat
import java.io.UnsupportedEncodingException
import java.util.*
import kotlin.experimental.and
import kotlin.text.Charsets.UTF_8


/**
 * Created by Chandan Jana on 22-09-2022.
 * Company name: Mindset
 * Email: chandan.jana@mindteck.com
 */
class ClientActivity1 : AppCompatActivity(), CustomBle1.ConnectionChangeListener {

    private lateinit var binding: ActivityClientBinding
    private var handler: Handler? = null
    private var logHandler = Handler(Looper.getMainLooper())
    private lateinit var customBle: CustomBle1

    private fun changeStatus(newState: Int) {
        runOnUiThread {
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    val device: BluetoothDevice =
                        customBle.bluetoothAdapter.getRemoteDevice(CustomBle1.mGatt.device.address)
                    val name = "Device connected to ${device.name} Address: ${device.address}"
                    binding.txtDeviceStatus.setText(name)
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    binding.txtDeviceStatus.text = "NO DEVICE CONNECTED"
                }
                else -> {
                    setAlert("Unable to connect. Please try again.")
                }
            }
        }
    }

    private fun setAlert(text: String) {
        AlertDialog.Builder(this)
            .setTitle("Alert")
            .setPositiveButton("Ok") { dialogInterface, i ->
                if (!text.equals("No device found", ignoreCase = true)) {
                    val intent = Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", packageName, null)
                    )
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
            }
            .setMessage(text)
            .show()
    }

    // Lifecycle
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_client)

        if (!checkingPermissionIsEnabledOrNot()) {
            requestMultiplePermission()
        } else {
            customBle = CustomBle1.getInstance(this, this)
            //customBle.setRx(RX_UUID)
            //customBle.setTx(TX_UUID)
            //customBle.setUartUuid(UART_UUID)
            //customBle.setClientUuid(CLIENT_UUID)
            @SuppressLint("HardwareIds")
            val deviceName = customBle.bluetoothAdapter?.name
            val deviceAddress = customBle.bluetoothAdapter?.address
            val deviceInfo = ("Device Info"
                    + "\nName: " + deviceName
                    + "\nAddress: " + deviceAddress)
            binding.clientDeviceInfoTextView.text = deviceInfo
            if (binding.startScanningButton.text.equals(getString(R.string.start))) {
                clearLogs()
                startScan()
            }
        }

        with(binding) {
            startScanningButton.setOnClickListener {
                if (startScanningButton.text.equals(getString(R.string.start))) {
                    clearLogs()
                    startScan()
                } else {
                    stopScan()
                    binding.scanProgress.visibility = View.GONE
                    startScanningButton.text = getString(R.string.start)
                }
            }
            sendMessageButton.setOnClickListener {
                val message = binding.messageEditText.text.toString()
                log("Sending message")
                customBle.sendMessage(message)
            }
            disconnectButton.setOnClickListener {
                log("Closing Gatt connection")
                clearLogs()
                binding.txtDeviceStatus.text = "NO DEVICE CONNECTED"
                customBle.disconnectGattServer()
            }
            viewClientLog.clearLogButton.setOnClickListener { clearLogs() }
        }
    }

    override fun onResume() {
        super.onResume()
        // Check low energy support
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) { // Get a newer device
            logError("No LE Support.")
            finish()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_PERMISSION_CODE -> if (grantResults.isNotEmpty()) {
                val fineLocation = grantResults[0] == PackageManager.PERMISSION_GRANTED
                val coarseLocation = grantResults[1] == PackageManager.PERMISSION_GRANTED
                val bluetooth = grantResults[2] == PackageManager.PERMISSION_GRANTED
                val bluetoothAdmin = grantResults[3] == PackageManager.PERMISSION_GRANTED
                if (fineLocation && coarseLocation && bluetooth && bluetoothAdmin) {
                    Toast.makeText(this@ClientActivity1, "Permission Granted", Toast.LENGTH_LONG)
                        .show()
                } else {
                    Toast.makeText(this@ClientActivity1, "Permission Denied", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }

    private fun checkingPermissionIsEnabledOrNot(): Boolean {
        val FirstPermissionResult =
            ContextCompat.checkSelfPermission(applicationContext, ACCESS_FINE_LOCATION)
        val SecondPermissionResult =
            ContextCompat.checkSelfPermission(applicationContext, ACCESS_COARSE_LOCATION)
        val ThirdPermissionResult = ContextCompat.checkSelfPermission(applicationContext, BLUETOOTH)
        val ForthPermissionResult =
            ContextCompat.checkSelfPermission(applicationContext, BLUETOOTH_ADMIN)
        return FirstPermissionResult == PackageManager.PERMISSION_GRANTED &&
                SecondPermissionResult == PackageManager.PERMISSION_GRANTED &&
                ThirdPermissionResult == PackageManager.PERMISSION_GRANTED &&
                ForthPermissionResult == PackageManager.PERMISSION_GRANTED
    }

    private fun requestMultiplePermission() {

        // Creating String Array with Permissions.
        ActivityCompat.requestPermissions(
            this, arrayOf(
                ACCESS_FINE_LOCATION,
                ACCESS_COARSE_LOCATION,
                BLUETOOTH,
                BLUETOOTH_ADMIN
            ), REQUEST_PERMISSION_CODE
        )
    }

    // Scanning
    @RequiresApi(Build.VERSION_CODES.O)
    private fun startScan() {
        if (!hasPermissions()) {
            return
        }
        binding.scanProgress.visibility = View.VISIBLE
        binding.startScanningButton.text = getString(R.string.stop)
        binding.serverListContainer.removeAllViews()
        handler = Handler()
        handler!!.postDelayed({ stopScan() }, SCAN_PERIOD)
    }

    private fun hasPermissions(): Boolean {
        if (!customBle.bluetoothAdapter.isEnabled) {
            requestBluetoothEnable()
            return false
        } else if (!hasLocationPermissions()) {
            requestLocationPermission()
            return false
        }
        return true
    }

    private fun requestBluetoothEnable() {
        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        log("Requested user enables Bluetooth. Try starting the scan again.")
    }

    private fun hasLocationPermissions() =
        checkSelfPermission(ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

    private fun requestLocationPermission() {
        requestPermissions(arrayOf(ACCESS_FINE_LOCATION), REQUEST_FINE_LOCATION)
        log("Requested user enable Location. Try starting the scan again.")
    }

    private fun stopScan() {
        binding.scanProgress.visibility = View.GONE
        if (customBle.bluetoothAdapter.isEnabled) {
            scanComplete()
        }
    }

    fun toHexString(byteArray: ByteArray?, size: Int): String? {
        require(!(byteArray == null || byteArray.size < 1)) { "this byteArray must not be null or empty" }
        val hexString = java.lang.StringBuilder(size * 2)
        for (i in 0 until size) {
            if (byteArray[i] and 255.toByte() < 16) {
                hexString.append("0")
            }
            hexString.append(Integer.toHexString((byteArray[i] and 255.toByte()).toInt()))
            if (i != byteArray.size - 1) {
                hexString.append(" ")
            }
        }
        return hexString.toString().uppercase(Locale.getDefault())
    }


    private fun scanComplete() {
        binding.scanProgress.visibility = View.GONE
        binding.startScanningButton.text = getString(R.string.start)
        var scanResults = customBle.scanResults;
        var sab = customBle.scanResultAndBluetoothDeviceList
        if (scanResults.isEmpty()) {
            return
        }
        customBle.listScanResults.forEach {
            //log("Advertising data " + byteArrayInHexFormat(it.scanRecord?.bytes!!))
            //log("Device name "+ it.scanRecord?.deviceName)
            //log("Advertising data " + toHexString(it.scanRecord?.bytes!!, it.scanRecord?.bytes?.size!!))
            var index = 0
            /*while (index < it.scanRecord?.bytes?.size!!) {
                val length = it.scanRecord?.bytes!![index++].toInt()
                //Done once we run out of records
                if (length == 0)
                    break
                val type = it.scanRecord?.bytes!![index].toInt()
                //Done if our record isn't a valid type
                if (type == 0)
                    break
                val dd : ByteArray = byteArrayOf(0x45.toByte(),0x44.toByte(),0x4D.toByte(), 0x67.toByte(), 0x12.toByte(), 0x18.toByte(),
                    0xEF.toByte(), 0xBF.toByte(), 0xBD.toByte(), 0x4E.toByte(),)
                val dd1: ByteArray = byteArrayOf(0x45.toByte(), 0x44.toByte(), 0x4D.toByte(), 0x67.toByte(), 0x12.toByte(), 0x18.toByte(), 0xC7.toByte(), 0x4E.toByte())
                val dd2: ByteArray = byteArrayOf(0x45.toByte(), 0x44.toByte(), 0x4D.toByte(), 0x67.toByte(), 0x12.toByte(), 0x18.toByte(),
                    0xEF.toByte(), 0xBF.toByte(), 0xBD.toByte(), 0x4E.toByte())
                log("dd1 ${String(dd1)}")
                log("dd2 ${String(dd2)}")
                val dd3: ByteArray = byteArrayOf(0xC7.toByte())
                val dd4: ByteArray = byteArrayOf(0xEF.toByte(), 0xBF.toByte(), 0xBD.toByte())
                log("dd3 ${String(dd3)}")
                log("dd4 ${String(dd4)}")
                val data: ByteArray = Arrays.copyOfRange(it.scanRecord?.bytes, index + 1, index + length)
                log("Length $length")
                log("Type $type")
                //log("Meter Data "+  byteArrayInHexFormat(dd))
                //log("Meter String Data "+  String(dd))
                log("Data "+  byteArrayInHexFormat(data))
                log("String Data "+  String(data))
                //log("Advertising data "+  toHexString(data, data.size))
                index += length
            }*/

        }

        scanResults.keys.forEach { deviceAddress ->
            with(
                DataBindingUtil.inflate(
                    LayoutInflater.from(this),
                    R.layout.view_gatt_server,
                    binding.serverListContainer,
                    true
                ) as ViewGattServerBinding
            ) {
                val device = scanResults[deviceAddress]
                val serviceResult = sab[deviceAddress]?.serviceResult!!
                this.viewModel = GattServerViewModel(device, serviceResult)
                connectGattServerButton.setOnClickListener {
                    log("Connecting to " + device?.address)
                    customBle.connectDevice(device)
                }
            }
        }
    }

    // Logging
    private fun clearLogs() =
        logHandler.post { binding.viewClientLog.logTextView.text = "" }

    // Gat Client Actions
    private fun log(msg: String) {
        Log.d("ClientActivity ", msg)
        logHandler.post {
            with(binding.viewClientLog) {
                logTextView.append(msg + "\n")
                logScrollView.post { logScrollView.fullScroll(View.FOCUS_DOWN) }
            }
        }
    }

    private fun logError(msg: String) =
        log("Error: $msg")

    override fun onConnected(isConnected: Boolean) {
        if (isConnected) {
            changeStatus(BluetoothProfile.STATE_CONNECTED)
        }

    }

    override fun onDisconnected(isDisconnected: Boolean) {
        if (isDisconnected) {
            changeStatus(BluetoothProfile.STATE_DISCONNECTED)
        }
    }

    override fun onScanStart(isScanStarted: Boolean) {
        log("Scanning has been Started")
    }

    override fun onScanStop(isScanStop: Boolean) {
        log("Scanning has Stopped")
    }

    override fun onSentMessage(message: String?) {
        log("Sent message: $message")
        binding.messageEditText.setText("")
    }

    override fun onReceivedMessage(message: String?) {
        log("Received message: $message")
    }

    companion object{

        fun ByteArrayToString(ba: ByteArray): String? {
            val hex = StringBuilder(ba.size * 2)
            for (b in ba) hex.append("$b ")
            return hex.toString()
        }
        fun parseScanRecord(scanRecord: ByteArray): List<AdRecord>? {
            val records: MutableList<AdRecord> = ArrayList()
            var index = 0
            while (index < scanRecord.size) {
                val length = scanRecord[index++].toInt()
                //Done once we run out of records
                if (length == 0) break
                val type = scanRecord[index].toInt()
                //Done if our record isn't a valid type
                if (type == 0) break
                val data: ByteArray = Arrays.copyOfRange(scanRecord, index + 1, index + length)
                records.add(AdRecord(length, type, data))
                //Advance
                index += length
            }
            return records
        }

        class AdRecord {

            constructor(length: Int, type: Int, data: ByteArray?) {
                var decodedRecord = ""
                try {
                    decodedRecord = String(data!!, UTF_8)
                } catch (e: UnsupportedEncodingException) {
                    e.printStackTrace()
                }
                Log.d("TAGG", "DecodedRecord Length: $length Type : $type Data : " + decodedRecord)

                Log.d("TAGG","Length: $length Type : $type Data : " + ByteArrayToString(
                    data!!
                )
                )
            }
        }
    }
}