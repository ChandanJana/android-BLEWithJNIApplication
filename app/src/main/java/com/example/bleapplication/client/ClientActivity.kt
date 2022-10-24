package com.example.bleapplication.client

import android.Manifest.permission.*
import android.annotation.SuppressLint
import android.bluetooth.*
import android.bluetooth.le.*
import android.companion.AssociationRequest
import android.companion.BluetoothLeDeviceFilter
import android.companion.CompanionDeviceManager
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.IntentSender.SendIntentException
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
import androidx.activity.result.IntentSenderRequest
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.bleapplication.Constants.SCAN_PERIOD
import com.example.bleapplication.R
import com.example.bleapplication.databinding.ActivityClientBinding
import com.example.bleapplication.databinding.ViewGattServerBinding
import com.example.bleapplication.utils.BluetoothUtils
import com.example.bleapplication.utils.StringUtils
import java.nio.charset.Charset
import java.util.*


class ClientActivity : AppCompatActivity() {
    private lateinit var binding: ActivityClientBinding
    private var isScanning = false
    private var isConnected = false
    private var timeInitialized = false
    private var echoInitialized = false
    private var deviceState = BluetoothProfile.STATE_DISCONNECTED

    private var handler: Handler? = null
    private var logHandler = Handler(Looper.getMainLooper())

    private var scanResults = HashMap<String, BluetoothDevice>()
    private var scanCallback: ScanCallback? = null

    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var bluetoothLeScanner: BluetoothLeScanner
    private lateinit var gatt: BluetoothGatt

    /*private val gattClientCallback: BluetoothGattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            deviceState = newState
            if (status == BluetoothGatt.GATT_SUCCESS) {
                val isServiceDiscovered = gatt.discoverServices()
                Log.e("isServiceDiscpvered", "" + isServiceDiscovered)
                isConnected = true
            }
            changeStatus(deviceState)
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            super.onServicesDiscovered(gatt, status)
            if (status == BluetoothGatt.GATT_SUCCESS) {
                // Get the read/notify characteristic
                tx = gatt.getService(UART_UUID)
                    .getCharacteristic(TX_UUID)
                rx = gatt.getService(UART_UUID)
                    .getCharacteristic(RX_UUID)
                // Setup notifications on RX characteristic changes (i.e. data received).
                // First call setCharacteristicNotification to enable notification.
                if (!gatt.setCharacteristicNotification(rx, true)) {
                    Toast.makeText(
                        this@ClientActivity,
                        "Couldn't set notifications for RX characteristic!",
                        Toast.LENGTH_SHORT
                    ).show()
                    //  writeLine("Couldn't set notifications for RX characteristic!");
                }
                // Next update the RX characteristic's client descriptor to enable notifications.
                if (rx?.getDescriptor(CLIENT_UUID) != null) {
                    val desc =
                        rx?.getDescriptor(CLIENT_UUID)
                    //desc.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
                    desc?.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                    if (!gatt.writeDescriptor(desc)) {
                        Toast.makeText(
                            this@ClientActivity,
                            "Couldn't write RX client descriptor value!",
                            Toast.LENGTH_SHORT
                        ).show()
                        // writeLine("Couldn't write RX client descriptor value!");
                    }
                } else {
                    Toast.makeText(
                        this@ClientActivity,
                        "Couldn't get RX client descriptor!",
                        Toast.LENGTH_SHORT
                    ).show()
                    // writeLine("Couldn't get RX client descriptor!");
                }
            }
        }

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            super.onCharacteristicWrite(gatt, characteristic, status)
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            super.onCharacteristicRead(gatt, characteristic, status)
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            super.onCharacteristicChanged(gatt, characteristic)
            val respdata = characteristic.value

            runOnUiThread {
                val buffer =
                    ByteBuffer.wrap(respdata).order(ByteOrder.LITTLE_ENDIAN)
                val data = String(buffer.array(), 0, buffer.position())
                Log.d("GXBLE", "Response Data=" + Arrays.toString(respdata))
                val byteArrayToStr = String(respdata, StandardCharsets.US_ASCII)
                Toast.makeText(
                    this@ClientActivity,
                    "$byteArrayToStr ,message received",
                    Toast.LENGTH_SHORT
                )
                    .show()
                Log.e("charactertics", "" + Arrays.toString(respdata))
            }

        }

        override fun onDescriptorWrite(
            gatt: BluetoothGatt,
            descriptor: BluetoothGattDescriptor,
            status: Int
        ) {
            super.onDescriptorWrite(gatt, descriptor, status)
        }
    }*/

    private fun changeStatus(newState: Int) {
        runOnUiThread {
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    val device: BluetoothDevice =
                        bluetoothAdapter.getRemoteDevice(gatt.device.address)
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

    private var tx: BluetoothGattCharacteristic? = null
    private var rx: BluetoothGattCharacteristic? = null

    // Lifecycle
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_client)
        if (!checkingPermissionIsEnabledOrNot()) {
            requestMultiplePermission()
        }
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
        @SuppressLint("HardwareIds")
        val deviceName = bluetoothAdapter.name
        val deviceAddress = bluetoothAdapter.address
        val deviceInfo = ("Device Info "
                + "\nName: " + deviceName
                + "\nAddress: " + deviceAddress)

        with(binding) {
            clientDeviceInfoTextView.text = deviceInfo
            startScanningButton.setOnClickListener {
                if (startScanningButton.text.equals(getString(R.string.start))) {
                    startScan()
                } else {
                    stopScan()
                    binding.scanProgress.visibility = View.GONE
                    startScanningButton.text = getString(R.string.start)
                }
            }
            sendMessageButton.setOnClickListener { sendMessage() }
            disconnectButton.setOnClickListener { disconnectGattServer() }
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
                    Toast.makeText(this@ClientActivity, "Permission Granted", Toast.LENGTH_LONG)
                        .show()
                } else {
                    Toast.makeText(this@ClientActivity, "Permission Denied", Toast.LENGTH_LONG)
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
        if (!hasPermissions() || isScanning) {
            return
        }
        binding.scanProgress.visibility = View.VISIBLE
        binding.startScanningButton.text = getString(R.string.stop)
        //doAction()
        disconnectGattServer()
        binding.serverListContainer.removeAllViews()
        scanResults = HashMap()
        scanCallback = BtleScanCallback(scanResults)
        bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
        // Note: Filtering does not work the same (or at all) on most devices. It also is unable to
        // search for a mask or anything less than a full UUID.
        // Unless the full UUID of the server is known, manual filtering may be necessary.
        // For example, when looking for a brand of device that contains a char sequence in the UUID
        val scanFilter = ScanFilter.Builder()
            //.setServiceUuid(ParcelUuid(SERVICE_UUID))
            .build()
        val filters = mutableListOf(scanFilter)
        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
            .build()
        bluetoothLeScanner.startScan(filters, settings, scanCallback)
        handler = Handler()
        handler!!.postDelayed({ stopScan() }, SCAN_PERIOD)
        isScanning = true
        log("Started scanning.")
    }

    private fun hasPermissions(): Boolean {
        if (!bluetoothAdapter.isEnabled) {
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

    private fun isVersion9_10(): Boolean {
        return if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O) {
            true
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.P) {
            true
        } else Build.VERSION.SDK_INT == Build.VERSION_CODES.Q
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    fun doAction() {
        var pairingRequest: AssociationRequest? = null
        val deviceManager = getSystemService(
            COMPANION_DEVICE_SERVICE
        ) as CompanionDeviceManager

        //  ParcelUuid BASE_UUID = ParcelUuid.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e");
        val scanFilter = ScanFilter.Builder() //  .setServiceUuid(BASE_UUID)
            .build()
        val deviceFilter = BluetoothLeDeviceFilter.Builder()
            .setScanFilter(scanFilter) // Match only Bluetooth devices whose name matches the pattern.
            //                .setNamePattern(Pattern.compile("My device"))
            // Match only Bluetooth devices whose service UUID matches this pattern.
            .build()
        pairingRequest = if (isVersion9_10()) {
            AssociationRequest.Builder() // .setSingleDevice(true)
                .build()
        } else {
            AssociationRequest.Builder()
                .addDeviceFilter(deviceFilter)
                .build()
        }
        deviceManager.associate(
            pairingRequest,
            object : CompanionDeviceManager.Callback() {
                override fun onDeviceFound(chooserLauncher: IntentSender) {
                    //isDeviceFound = true
                    //progressBar.setVisibility(View.GONE)
                    val launcher = IntentSenderRequest.Builder(chooserLauncher).build()
                    try {
                        this@ClientActivity.startIntentSenderForResult(
                            chooserLauncher,
                            SELECT_DEVICE_REQUEST_CODE,
                            null,
                            0,
                            0,
                            0
                        )
                    } catch (e: SendIntentException) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(error: CharSequence) {
                    //progressBar.setVisibility(View.GONE)
                    Log.e("No device found", "" + error.toString())
                    // handle failure to find the companion device
                }
            }, null
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == SELECT_DEVICE_REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                val scanResult =
                    data.getParcelableExtra<ScanResult>(CompanionDeviceManager.EXTRA_DEVICE)
                //val scanResult = data.getParcelableExtra<BluetoothDevice>(CompanionDeviceManager.EXTRA_DEVICE);
                if (scanResult != null) {
                    //progressBar.setVisibility(View.VISIBLE)
                    val device = scanResult.device
                    /*gatt =
                        device.connectGatt(
                            this,
                            false,
                            gattClientCallback,
                            BluetoothDevice.TRANSPORT_LE
                        )*/

                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun stopScan() {
        if (isScanning && bluetoothAdapter.isEnabled) {
            bluetoothLeScanner.stopScan(scanCallback)
            scanComplete()
        }
        scanCallback = null
        isScanning = false
        handler = null
        log("Stopped scanning.")
    }

    private fun scanComplete() {
        binding.scanProgress.visibility = View.GONE
        binding.startScanningButton.text = getString(R.string.start)
        if (scanResults.isEmpty()) {
            return
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
                //this.viewModel = GattServerViewModel(device)
                connectGattServerButton.setOnClickListener { connectDevice(device) }
            }
        }
    }

    // Gatt connection
    private fun connectDevice(device: BluetoothDevice?) {
        log("Connecting to " + device?.address)
        val gattClientCallback = GattClientCallback()
        gatt = device?.connectGatt(this, false, gattClientCallback, BluetoothDevice.TRANSPORT_LE)!!
    }

    // Gatt disconnected
    private fun disconnectGattServer() {
        log("Closing Gatt connection")
        clearLogs()
        isConnected = false
        echoInitialized = false
        timeInitialized = false

        if (this::gatt.isInitialized) {
            binding.txtDeviceStatus.text = "NO DEVICE CONNECTED"
            gatt.disconnect()
            gatt.close()
        }
    }

    // Messaging
    private fun sendMessage() {
        val message = binding.messageEditText.text.toString()
        log("Sending message: $message")
        if (tx == null || message.isEmpty()) {
            // Do nothing if there is no device or message to send.
            return
        }

        tx?.value = message.toByteArray(Charset.forName("UTF-8"))

        if (gatt.writeCharacteristic(tx)) {
            Log.e("MessageSent", "Sent: $message")
            log("Wrote: $message")
            binding.messageEditText.setText("")

        }
        /*if (!this::gatt.isInitialized || !isConnected || !echoInitialized) {
            return
        }
        val characteristic = BluetoothUtils.findEchoCharacteristic(gatt)
        if (characteristic == null) {
            logError("Unable to find echo characteristic.")
            disconnectGattServer()
            return
        }
        val message = binding.messageEditText.text.toString()
        log("Sending message: $message")
        val messageBytes = message.toByteArray()
        if (messageBytes.isEmpty()) {
            logError("Unable to convert message to bytes")
            return
        }
        characteristic.value = messageBytes

        val success = gatt.writeCharacteristic(characteristic)!!
        if (success) {
            log("Wrote: " + StringUtils.byteArrayInHexFormat(messageBytes))
        } else {
            logError("Failed to write data")
        }*/
    }

    // Logging
    private fun clearLogs() =
        logHandler.post { binding.viewClientLog.logTextView.text = "" }

    // Gat Client Actions
    private fun log(msg: String) {
        Log.d("ClientActivity", msg)
        logHandler.post {
            with(binding.viewClientLog) {
                logTextView.append(msg + "\n")
                logScrollView.post { logScrollView.fullScroll(View.FOCUS_DOWN) }
            }
        }
    }

    private fun logError(msg: String) =
        log("Error: $msg")

    // Scan Callbacks
    private inner class BtleScanCallback(private val mScanResults: MutableMap<String, BluetoothDevice>) :
        ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            addScanResult(result)
        }

        override fun onBatchScanResults(results: List<ScanResult>) =
            results.forEach { addScanResult(it) }

        override fun onScanFailed(errorCode: Int) =
            logError("BLE Scan Failed with code $errorCode")

        private fun addScanResult(result: ScanResult) =
            with(result.device) {
                mScanResults[address] = this
            }

    }


    // Connection callback
    private inner class GattClientCallback : BluetoothGattCallback() {

        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            //super.onConnectionStateChange(gatt, status, newState)
            //log("onConnectionStateChange newState: $newState")
            //log("onConnectionStateChange status: $status")
            deviceState = newState
            if (status == BluetoothGatt.GATT_SUCCESS) {
                val isServiceDiscovered = gatt.discoverServices()
                Log.e("isServiceDiscpvered", "" + isServiceDiscovered)
                isConnected = true

            }
            changeStatus(deviceState)

            /*if (newState == BluetoothProfile.STATE_CONNECTED) {
                log("Connected to device " + gatt.device.address)
                isConnected = true

                gatt.discoverServices()
            }
            if (status == BluetoothGatt.GATT_FAILURE) {
                logError("Connection Gatt failure status $status")
                disconnectGattServer()
                return
            } else if (status != BluetoothGatt.GATT_SUCCESS) { // handle anything not SUCCESS as failure
                logError("Connection not GATT success status $status")
                disconnectGattServer()
                return
            }
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                log("Connected to device " + gatt.device.address)
                isConnected = true
                gatt.discoverServices()
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                log("Disconnected from device")
                disconnectGattServer()
            }*/
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            super.onServicesDiscovered(gatt, status)
            if (status == BluetoothGatt.GATT_SUCCESS) {
                // Get the read/notify characteristic
                tx = gatt.getService(UART_UUID).getCharacteristic(TX_UUID)
                rx = gatt.getService(UART_UUID).getCharacteristic(RX_UUID)
                // Setup notifications on RX characteristic changes (i.e. data received).
                // First call setCharacteristicNotification to enable notification.
                if (!gatt.setCharacteristicNotification(rx, true)) {
                    logError("Couldn't set notifications for RX characteristic!")
                }
                // Next update the RX characteristic's client descriptor to enable notifications.
                if (rx?.getDescriptor(CLIENT_UUID) != null) {
                    val desc =
                        rx?.getDescriptor(CLIENT_UUID)
                    //desc.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
                    desc?.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                    if (!gatt.writeDescriptor(desc)) {
                        logError("Couldn't write RX client descriptor value!")
                    }
                } else {
                    logError("Couldn't get RX client descriptor!")
                }
            } else {
                //  Log.w(TAG, "onServicesDiscoveonConnectionStateChangered received: " + status);
            }
            /*if (status != BluetoothGatt.GATT_SUCCESS) {
                log("Device service discovery unsuccessful, status $status")
                return
            }

            val matchingCharacteristics = BluetoothUtils.findCharacteristics(gatt)
            if (matchingCharacteristics.isEmpty()) {
                logError("Unable to find characteristics.")
                return
            }
            log("Initializing: setting write type and enabling notification")
            matchingCharacteristics.forEach {
                it.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
                enableCharacteristicNotification(gatt, it)
            }*/

            /*
            gatt.services.find { it.uuid == SERVICE_UUID }
                    ?.characteristics?.find { it.uuid == CHARACTERISTIC_TIME_UUID }
                    ?.let {
                        if (gatt.setCharacteristicNotification(it, true)) {
                            log("Characteristic notification set successfully for " + it.uuid.toString())
                            enableCharacteristicConfigurationDescriptor(gatt, it)
                        }
                    }
             */
        }

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            super.onCharacteristicWrite(gatt, characteristic, status)
            if (status == BluetoothGatt.GATT_SUCCESS) {
                log("Characteristic written successfully")
            } else {
                logError("Characteristic write unsuccessful, status: $status")
                //disconnectGattServer()
            }
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            super.onCharacteristicRead(gatt, characteristic, status)
            if (status == BluetoothGatt.GATT_SUCCESS) {
                log("Characteristic read successfully")
                readCharacteristic(characteristic)
            } else {
                logError("Characteristic read unsuccessful, status: $status")
                // Trying to read from the Time Characteristic? It doesn't have the property or permissions
                // set to allow this. Normally this would be an error and you would want to call
                // disconnectGattServer()
            }
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            super.onCharacteristicChanged(gatt, characteristic)
            log("Characteristic changed, " + characteristic.uuid.toString())
            /*val respdata = characteristic.value

            runOnUiThread {
                val buffer =
                    ByteBuffer.wrap(respdata).order(ByteOrder.LITTLE_ENDIAN)
                val data = String(buffer.array(), 0, buffer.position())
                Log.d("GXBLE", "Response Data=" + Arrays.toString(respdata))
                val byteArrayToStr = String(respdata, StandardCharsets.US_ASCII)
                Toast.makeText(
                    this@ClientActivity,
                    "$byteArrayToStr ,message received",
                    Toast.LENGTH_SHORT
                )
                    .show()
                Log.e("charactertics", "" + Arrays.toString(respdata))
            }*/
            readCharacteristic(characteristic)
        }

        override fun onDescriptorWrite(
            gatt: BluetoothGatt,
            descriptor: BluetoothGattDescriptor,
            status: Int
        ) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                log("Descriptor written successfully: " + descriptor.uuid.toString())
                timeInitialized = true
            } else {
                logError("Descriptor write unsuccessful: " + descriptor.uuid.toString())
            }
        }

        private fun enableCharacteristicNotification(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {

            val characteristicWriteSuccess =
                gatt.setCharacteristicNotification(characteristic, true)
            if (characteristicWriteSuccess) {
                log("Characteristic notification set successfully for " + characteristic.uuid.toString())
                if (BluetoothUtils.isEchoCharacteristic(characteristic)) {
                    echoInitialized = true
                } else if (BluetoothUtils.isTimeCharacteristic(characteristic)) {
                    enableCharacteristicConfigurationDescriptor(gatt, characteristic)
                }
            } else {
                logError("Characteristic notification set failure for " + characteristic.uuid.toString())
            }
        }

        // Sometimes the Characteristic does not have permissions, and instead its Descriptor holds them
        // See https://www.bluetooth.com/specifications/gatt/viewer?attributeXmlFile=org.bluetooth.descriptor.gatt.client_characteristic_configuration.xml
        private fun enableCharacteristicConfigurationDescriptor(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            val descriptor =
                BluetoothUtils.findClientConfigurationDescriptor(characteristic.descriptors)
            if (descriptor == null) {
                logError("Unable to find Characteristic Configuration Descriptor")
                return
            }
            descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE

            val descriptorWriteInitiated = gatt.writeDescriptor(descriptor)
            if (descriptorWriteInitiated) {
                log("Characteristic Configuration Descriptor write initiated: " + descriptor.uuid.toString())
            } else {
                logError("Characteristic Configuration Descriptor write failed to initiate: " + descriptor.uuid.toString())
            }
        }

        private fun readCharacteristic(characteristic: BluetoothGattCharacteristic) {
            val messageBytes = characteristic.value
            log("Read: " + StringUtils.byteArrayInHexFormat(messageBytes))
            val message = String(messageBytes)
            log("Received message: $message")
        }
    }

    //external fun helloWorld(): String
    external fun connectPeripheral()
    external fun disconnectPeripheral()
    external fun isConnected(): Boolean
    external fun hasErrors(): Boolean
    external fun getTxCharacteristics(): String
    external fun getRxCharacteristics(): String
    external fun setTxCharacteristics(txUUID: String)
    external fun setRxCharacteristics(rxUUID: String)
    external fun transmitBuffer(buffer: String, length:Int): Int

    companion object {
        init {
            System.loadLibrary("BleAndroid")
        }
        private const val REQUEST_ENABLE_BT = 1
        private const val REQUEST_FINE_LOCATION = 2
        private const val SELECT_DEVICE_REQUEST_CODE = 0
        private const val REQUEST_PERMISSION_CODE = 7

        //private var UART_UUID = UUID.fromString("ED310001-C889-5D66-AE38-A7A01230635A")
        private var UART_UUID = UUID.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E")

        //private var TX_UUID = UUID.fromString("ED310002-C889-5D66-AE38-A7A01230635A")
        private var TX_UUID = UUID.fromString("6E400002-B5A3-F393-E0A9-E50E24DCCA9E")

        //private var RX_UUID = UUID.fromString("ED310003-C889-5D66-AE38-A7A01230635A")
        private var RX_UUID = UUID.fromString("6E400003-B5A3-F393-E0A9-E50E24DCCA9E")

        //private var CLIENT_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
        private var CLIENT_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
    }
}