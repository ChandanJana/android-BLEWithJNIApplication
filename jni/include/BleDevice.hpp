#ifndef BLE_DEVICE
#define BLE_DEVICE

#include<stdbool.h>
#include<stdint.h>
#include<string.h>

class BleDevice
{
public:
	uint64_t bleaddress;
	char uuidserviceid[65];
	char name[65];
    char uuidaddress[65];

	BleDevice()
	{
		memset(uuidserviceid, 0, 65);
		memset(name, 0, 65);
        memset(uuidaddress, 0, 65);
    }

	BleDevice(BleDevice& t)
	{
		memset(uuidserviceid, 0, 65);
		memset(name, 0, 65);
        memset(uuidaddress, 0, 65);

		bleaddress = t.bleaddress;

		strncpy(uuidserviceid, t.uuidserviceid, 64);
		strncpy(name, t.name, 64);
        strncpy(uuidaddress, t.uuidaddress, 64);
    }

	void operator = (const BleDevice& t)
	{
		memset(uuidserviceid, 0, 65);
		memset(name, 0, 65);
        memset(uuidaddress, 0, 65);

		bleaddress = t.bleaddress;

		strncpy(uuidserviceid, t.uuidserviceid, 64);
		strncpy(name, t.name, 64);
        strncpy(uuidaddress, t.uuidaddress, 64);
    }
};

#endif
