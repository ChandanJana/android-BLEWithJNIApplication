#ifndef BLE_UART
#define BLE_UART

#include <stdint.h>
#include <stdbool.h>
#include "ITransport.hpp"

class BleUart : public ITransport
{
protected:
	uint64_t bleAddress;
	char serviceUUID[65];
    char addressUUID[65];
	char name[65];
    char* pairingPIN;
    bool haserrors;
public:
	BleUart();
	virtual ~BleUart();
	bool Initialize();
	bool Open();
	bool TransmitBuffer(const unsigned char* buffer, int len);
	unsigned char* ReceiveBuffer(unsigned char* buffer, int* len, unsigned char eopbyte = 0x7E);
	bool Close();
	uint64_t GetActiveDevice();
    bool SetParameters(uint64_t ibleaddress, const char* iaddressuuid, const char* iserviceid, const char* name, const char* pin, size_t pinlen);
};

#endif
