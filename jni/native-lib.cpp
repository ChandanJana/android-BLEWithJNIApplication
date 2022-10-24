#include<jni.h>
#include<string>
#include<include\BleCentral.hpp>
#include<src\BleCentralAndroid.cpp>
#include<src\BleScannerAndroid.cpp>

extern "C"
{

JNIEXPORT void JNICALL
Java_com_example_bleapplication_client_ClientActivity2_connectPeripheral(JNIEnv *env, jobject thiz) {
    BleCentral bleCentral = BleCentral();
    bleCentral.ConnectPeripheral();
}

JNIEXPORT void JNICALL
Java_com_example_bleapplication_client_ClientActivity2_disconnectPeripheral(JNIEnv *env,jobject thiz) {
    BleCentral bleCentral = BleCentral();
    bleCentral.DisconnectPeripheral();
}

JNIEXPORT jboolean JNICALL
Java_com_example_bleapplication_client_ClientActivity2_isConnected(JNIEnv *env, jobject thiz) {
    BleCentral bleCentral = BleCentral();
    return bleCentral.IsConnected();
}

JNIEXPORT jboolean JNICALL
Java_com_example_bleapplication_client_ClientActivity2_hasErrors(JNIEnv *env, jobject thiz) {
    BleCentral bleCentral = BleCentral();
    return bleCentral.HasErrors();
}

JNIEXPORT jstring JNICALL
Java_com_example_bleapplication_client_ClientActivity2_getTxCharacteristics(JNIEnv *env,jobject thiz) {
    BleCentral bleCentral = BleCentral();
    return (jstring) bleCentral.GetTxCharacteristics();
}

JNIEXPORT jstring JNICALL
Java_com_example_bleapplication_client_ClientActivity2_getRxCharacteristics(JNIEnv *env,jobject thiz) {
    BleCentral bleCentral = BleCentral();
    return (jstring) bleCentral.GetRxCharacteristics();
}

JNIEXPORT void JNICALL
Java_com_example_bleapplication_client_ClientActivity2_setRxCharacteristics(JNIEnv *env,jobject thiz,jstring rx_uuid) {
    BleCentral bleCentral = BleCentral();
    bleCentral.SetRxCharacteristics((const char *) rx_uuid);
}

JNIEXPORT void JNICALL
Java_com_example_bleapplication_client_ClientActivity2_setTxCharacteristics(JNIEnv *env,jobject thiz,jstring tx_uuid) {
    BleCentral bleCentral = BleCentral();
    bleCentral.SetTxCharacteristics((const char *) tx_uuid);
}

JNIEXPORT jint JNICALL
Java_com_example_bleapplication_client_ClientActivity2_transmitBuffer(JNIEnv *env, jobject thiz,jstring buffer, jint length) {
    BleCentral bleCentral = BleCentral();
    return bleCentral.TransmitBuffer(reinterpret_cast<const unsigned char *>(buffer), length);
}

JNIEXPORT void JNICALL
Java_com_example_bleapplication_client_ClientActivity2_startScanning(JNIEnv *env, jobject thiz) {
    BleScanner bleScanner = BleScanner();
    bleScanner.StartScanning();
}
JNIEXPORT void JNICALL
Java_com_example_bleapplication_client_ClientActivity2_stopScanning(JNIEnv *env, jobject thiz) {
    BleScanner bleScanner = BleScanner();
    bleScanner.StopScanning();
}
JNIEXPORT void JNICALL
Java_com_example_bleapplication_client_ClientActivity2_setScanTimeout(JNIEnv *env, jobject thiz,
                                                                     jlong time) {
    BleScanner bleScanner = BleScanner();
    bleScanner.SetScanTimeout(time);
}

JNIEXPORT jboolean JNICALL
Java_com_example_bleapplication_client_ClientActivity2_isScanning(JNIEnv *env, jobject thiz) {
    BleScanner bleScanner = BleScanner();
    return bleScanner.IsScanning();
}

}











