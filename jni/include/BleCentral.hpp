#ifndef BLE_CENTRAL
#define BLE_CENTRAL

#include <jni.h>
#include <stdint.h>
#include <stdbool.h>
#include <stdlib.h>
#include <string.h>

class BleCentral
{
public:
    BleCentral();
    BleCentral(uint64_t ibleaddress, const char* iserviceid);
    virtual ~BleCentral();

    void ConnectPeripheral();
    void ConnectPeripheral(uint64_t ibleaddress, const char* iaddressuuid, const char* iserviceid, const char* pin, size_t pinlen);
    void DisconnectPeripheral();
    void SetPeripheralDevice(uint64_t ibleaddress, const char* iaddressuuid, const char* iserviceid, const char* pin, size_t pinlen);
    uint32_t TransmitBuffer(const unsigned char* buffer, int len);
    unsigned char* ReceiveBuffer(unsigned char* buffer, int* len, unsigned char eopbyte = 0x7E);

    bool IsConnected();
    bool HasErrors();

    uint64_t Address();
    const char* ServiceUuid();

    void SetRxCharacteristics(const char* str);
    void SetTxCharacteristics(const char* str);

    const char* GetRxCharacteristics();
    const char* GetTxCharacteristics();
private:
    void DiscoverServices();
    uint64_t bleAddress;
    char serviceUUID[65];
    char addressUUID[65];
    char charxTxUUID[65];
    char charxRxUUID[65];
    bool peripheralconnected;
    char* pairingPIN;
    bool haserrors;
};

#endif
