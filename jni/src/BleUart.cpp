#include "BleUart.hpp"
#include <include\BleCentral.hpp>

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include <algorithm>
#include <iomanip>
#include <set>
#include <sstream>
#include <vector>
#include <thread>
#include <functional>
#include <iostream>
#include <chrono>
#include <mutex>
#include <queue>
#include <list>

BleCentral blecentral;

BleUart::BleUart()
{
	bleAddress = 0;
	memset(serviceUUID, 0, sizeof(serviceUUID));
    memset(addressUUID, 0, sizeof(addressUUID));
    memset(name, 0, sizeof(name));
    pairingPIN = nullptr;
}

BleUart::~BleUart()
{
	if (blecentral.IsConnected())
	{
		Close();
	}
}

bool BleUart::Initialize()
{
	return true;
}

bool BleUart::Open()
{
    blecentral.SetPeripheralDevice(bleAddress, addressUUID, serviceUUID, pairingPIN, strlen(pairingPIN));
    blecentral.ConnectPeripheral();

    bool hasconnected = false;

    while (true)
    {
        std::this_thread::sleep_for(std::chrono::milliseconds(100));

        if (blecentral.HasErrors())
        {
            break;
        }

        if (blecentral.IsConnected())
        {
            hasconnected = true;
            break;
        }
    }

    return hasconnected;
}

bool BleUart::TransmitBuffer(const unsigned char* buffer, int len)
{
    if (!blecentral.IsConnected())
    {
        return false;
    }

    unsigned long bytessent = blecentral.TransmitBuffer(buffer, len);

    if (bytessent == len)
    {
        return true;
    }

    return false;
}

unsigned char* BleUart::ReceiveBuffer(unsigned char* buffer, int* len, unsigned char eopbyte)
{
    if (!blecentral.IsConnected())
    {
        return nullptr;
    }

    buffer = blecentral.ReceiveBuffer(buffer, len, eopbyte);

    return buffer;
}

bool BleUart::Close()
{
    blecentral.DisconnectPeripheral();
    return true;
}

uint64_t BleUart::GetActiveDevice()
{
	return bleAddress;
}

bool BleUart::SetParameters(uint64_t ibleaddress, const char *iaddressuuid, const char* iserviceid, const char* iname, const char *pin, size_t pinlen)
{
    if (iserviceid == nullptr || ibleaddress < 1)
    {
        return false;
    }

    bleAddress = ibleaddress;

    memset(addressUUID, 0, sizeof(addressUUID));
    memset(serviceUUID, 0, sizeof(serviceUUID));
    memset(name, 0, sizeof(name));
    strncpy(serviceUUID, iserviceid, 64);

    if (iaddressuuid)
    {
        strncpy(addressUUID, iaddressuuid, 64);
    }

    if (iname)
    {
        strncpy(name, iname, 64);
    }

    if(pairingPIN)
    {
        delete [] pairingPIN;
        pairingPIN = nullptr;
    }

    if(pin != nullptr && pinlen > 0)
    {
        pairingPIN = new char[pinlen+1];
        memset(pairingPIN, 0, pinlen+1);
        memcpy(pairingPIN, pin, pinlen);
    }

    return true;
}
