#ifndef BLE_SCANNER
#define BLE_SCANNER
#include "BleDevice.hpp"
#include <vector>
#include <string>

class BleScannerCallback
{
public:
    BleScannerCallback() {}
    virtual ~BleScannerCallback() {}
    virtual bool OnDeviceDiscovered(BleDevice &device, const std::vector<std::string> &rawdata, bool servicefound) = 0;
};

class BleScanner
{
public:
    BleScanner();
    BleScanner(const char* servicestr);
    virtual ~BleScanner();
    void SetScannerCallback(BleScannerCallback* callbackptr);
    void StartScanning();
    void StartScanning(const char* servicestr);
    void SetScanTarget(const char* servicestr);
    void SetScanTimeout(uint32_t tmout);
    void StopScanning();
    bool IsScanning();
private:
    BleScannerCallback* callback;
    char* serviceUuid;
    uint32_t scantime;
};
#endif
