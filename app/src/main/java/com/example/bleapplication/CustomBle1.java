package com.example.bleapplication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Chandan Jana on 22-09-2022.
 * Company name: Mindteck
 * Email: chandan.jana@mindteck.com
 */
public class CustomBle1 {

    public interface ConnectionChangeListener {

        void onConnected(boolean isConnected);

        void onDisconnected(boolean isDisconnected);

        void onScanStart(boolean isScanStarted);

        void onScanStop(boolean isScanStop);

        void onSentMessage(String message);

        void onReceivedMessage(String message);

    }

    private final String TAGG = CustomBle1.class.getSimpleName();
    private long SCAN_PERIOD = 10000;
    private BluetoothManager bluetoothManager = null;
    private static BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner mBluetoothLeScanner = null;
    private ScanCallback mScanCallback = null;
    public static BluetoothGatt mGatt = null;
    private Map<String, BluetoothDevice> scanResults;
    private Map<String, CustomBleDevice> scanResultAndBluetoothDeviceList;
    private List<ScanResult> listScanResults;
    private static boolean mScanning = false;
    private Handler mHandler;
    private static boolean mConnected = false;
    private static CustomBle1 instance = null;
    private BluetoothGattCharacteristic tx = null;
    private BluetoothGattCharacteristic rx = null;
    private Context context;
    private static ConnectionChangeListener connectionChangeListener;

    //private static UUID UART_UUID = UUID.fromString("ED310001-C889-5D66-AE38-A7A01230635A");
    static UUID UART_UUID = UUID.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E");

    //private static UUID TX_UUID = UUID.fromString("ED310002-C889-5D66-AE38-A7A01230635A");
    static UUID TX_UUID = UUID.fromString("6E400002-B5A3-F393-E0A9-E50E24DCCA9E");

    //private static UUID RX_UUID = UUID.fromString("ED310003-C889-5D66-AE38-A7A01230635A");
    static UUID RX_UUID = UUID.fromString("6E400003-B5A3-F393-E0A9-E50E24DCCA9E");

    //private static UUID CLIENT_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    static UUID CLIENT_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {

                }
            };



    private CustomBle1(Context context, ConnectionChangeListener connectionChangeListener) {
        if (this.connectionChangeListener == null)
            this.connectionChangeListener = connectionChangeListener;
        if (this.context == null)
            this.context = context;
        if (bluetoothManager == null) {
            bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        }
        if (bluetoothAdapter == null) {
            bluetoothAdapter = bluetoothManager.getAdapter();
        }
        startScan();
    }

    public static CustomBle1 getInstance(Context context, ConnectionChangeListener connectionChangeListener) {
        //synchronized (instance) {
        if (instance == null)
            return new CustomBle1(context, connectionChangeListener);
        else
            return instance;
        //}

    }

    public void setTx(UUID tx) {
        TX_UUID = tx;
    }

    public void setRx(UUID rx) {
        RX_UUID = rx;
    }

    public UUID getTx() {
        return TX_UUID;
    }

    public UUID getRx() {
        return RX_UUID;
    }

    public UUID getUartUuid() {
        return UART_UUID;
    }

    public void setUartUuid(UUID uartUuid) {
        UART_UUID = uartUuid;
    }

    public UUID getClientUuid() {
        return CLIENT_UUID;
    }

    public void setClientUuid(UUID clientUuid) {
        CLIENT_UUID = clientUuid;
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return bluetoothAdapter;

    }

    public Map<String, BluetoothDevice> getScanResults() {
        return scanResults;
    }

    public Map<String, CustomBleDevice> getScanResultAndBluetoothDeviceList() {
        return scanResultAndBluetoothDeviceList;
    }

    public List<ScanResult> getListScanResults() {
        return listScanResults;
    }

    public void setListScanResults(List<ScanResult> listScanResults) {
        this.listScanResults = listScanResults;
    }

    public boolean isConnected() {
        return mConnected;
    }

    public boolean isScanning() {
        return mScanning;
    }

    public void setScanning(boolean mScanning) {
        this.mScanning = mScanning;
    }

    public long getSCAN_PERIOD() {
        return SCAN_PERIOD;
    }

    public void setSCAN_PERIOD(long SCAN_PERIOD) {
        this.SCAN_PERIOD = SCAN_PERIOD;
    }

    public void startScan() {

        if (!mScanning) {
            disconnectGattServer();
            scanResults = new HashMap<>();
            listScanResults = new ArrayList<>();
            scanResultAndBluetoothDeviceList = new HashMap<>();
            mScanCallback = new BtleScanCallback(scanResults, listScanResults);
            mBluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
            // Note: Filtering does not work the same (or at all) on most devices. It also is unable to
            // search for a mask or anything less than a full UUID.
            // Unless the full UUID of the server is known, manual filtering may be necessary.
            // For example, when looking for a brand of device that contains a char sequence in the UUID
            ScanFilter scanFilter = new ScanFilter.Builder()
                    //.setServiceUuid(new ParcelUuid(SERVICE_UUID))
                    .build();
            List<ScanFilter> filters = new ArrayList<>();
            filters.add(scanFilter);

            ScanSettings settings = new ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                    .build();

            mBluetoothLeScanner.startScan(filters, settings, mScanCallback);

            mHandler = new Handler();
            mHandler.postDelayed(this::stopScan, SCAN_PERIOD);

            connectionChangeListener.onScanStart(true);
            mScanning = true;
        }
    }

    public void stopScan() {
        if (mScanning && bluetoothAdapter != null && bluetoothAdapter.isEnabled() && mBluetoothLeScanner != null) {
            mBluetoothLeScanner.stopScan(mScanCallback);
            //scanComplete();
        }
        connectionChangeListener.onScanStop(true);
        mScanCallback = null;
        mScanning = false;
        mHandler = null;
    }

    public void connectDevice(BluetoothDevice device) {
        GattClientCallback gattClientCallback = new GattClientCallback();
        mGatt = device.connectGatt(context, false, gattClientCallback);
    }

    public void setConnected(boolean connected) {
        mConnected = connected;
    }

    public void disconnectGattServer() {
        if (mGatt != null) {
            mGatt.disconnect();
            mGatt.close();
        }
        connectionChangeListener.onDisconnected(true);
        setConnected(false);
    }

    // Callbacks

    private class BtleScanCallback extends ScanCallback {

        private Map<String, BluetoothDevice> mScanResults;
        private List<ScanResult> mListScanResults;

        BtleScanCallback(Map<String, BluetoothDevice> scanResults, List<ScanResult> listScanResults) {
            mScanResults = scanResults;
            mListScanResults = listScanResults;
        }

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            mListScanResults.add(result);
            addScanResult(result);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            mListScanResults.addAll(results);
            for (ScanResult result : results) {
                addScanResult(result);
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
        }

        private void addScanResult(ScanResult result) {
            BluetoothDevice device = result.getDevice();
            String deviceAddress = device.getAddress();
            scanResultAndBluetoothDeviceList.put(deviceAddress, new CustomBleDevice(device, result));
            mScanResults.put(deviceAddress, device);
        }
    }

    private class GattClientCallback extends BluetoothGattCallback {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                gatt.discoverServices();
                setConnected(true);
                connectionChangeListener.onConnected(true);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                // Get the read/notify characteristic
                tx = gatt.getService(UART_UUID).getCharacteristic(TX_UUID);
                rx = gatt.getService(UART_UUID).getCharacteristic(RX_UUID);
                // Setup notifications on RX characteristic changes (i.e. data received).
                // First call setCharacteristicNotification to enable notification.
                if (!gatt.setCharacteristicNotification(rx, true)) {
                    Log.e(TAGG, "Couldn't set notifications for RX characteristic!");
                }
                // Next update the RX characteristic's client descriptor to enable notifications.
                if (rx.getDescriptor(CLIENT_UUID) != null) {
                    BluetoothGattDescriptor desc = rx.getDescriptor(CLIENT_UUID);
                    //desc.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
                    desc.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    if (!gatt.writeDescriptor(desc)) {
                        Log.e(TAGG, "Couldn't write RX client descriptor value!");
                    }
                } else {
                    Log.e(TAGG, "Couldn't get RX client descriptor!");
                }
            } else {
                //  Log.w(TAG, "onServicesDiscoveonConnectionStateChangered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            readCharacteristic(characteristic);
        }

        private void readCharacteristic(BluetoothGattCharacteristic characteristic) {
            byte[] messageBytes = characteristic.getValue();
            //Log.d("TAGG","Read: " + StringUtils.byteArrayInHexFormat(messageBytes));
            String message = new String(messageBytes);
            Log.d(TAGG, "Received message: " + message);
            connectionChangeListener.onReceivedMessage(message);
        }
    }

    // Messaging
    public void sendMessage(String message) {
        //val message = binding.messageEditText.text.toString()
        Log.d(TAGG, "Sending message: " + message);
        if (tx == null || message.isEmpty()) {
            // Do nothing if there is no device or message to send.
            return;
        }
        tx.setValue(message.getBytes(Charset.forName("UTF-8")));

        if (mGatt.writeCharacteristic(tx)) {
            Log.e(TAGG, "Sent: " + message);
            Log.d(TAGG, "Wrote: " + message);
            connectionChangeListener.onSentMessage(message);
        }
    }

}
