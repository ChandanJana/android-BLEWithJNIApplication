#ifndef INTERFACE_TRANSPORT
#define INTERFACE_TRANSPORT

#include <stdint.h>
#include <stdbool.h>

typedef enum TransportType
{
	TCPSOCKET = 'T',
	BLEUART = 'B',
	RS232UART = 'R',
	OPTICALUART = 'O'
}TransportType;

typedef enum TransportState
{
	Startup,
	Connecting,
	Connected,
	DisConnected
}TransportState;

class ITransport
{
public:
	ITransport() { rstate = DisConnected; }
	virtual ~ITransport() {}
	virtual bool Initialize() = 0;
	virtual bool Open() = 0;
	virtual bool TransmitBuffer(const unsigned char* buffer, int len) = 0;
	virtual unsigned char* ReceiveBuffer(unsigned char* buffer, int* len, unsigned char eopbyte = 0x7E) = 0;
	virtual bool Close() = 0;
	virtual TransportState GetState() { return rstate; }
	TransportState rstate;
};

#endif