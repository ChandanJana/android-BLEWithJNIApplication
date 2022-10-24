#include<jni.h>
#include<string.h>

jstring Java_com_example_bleapplication_client_ClientActivity_helloWorld(JNIEnv *env, jobject thiz) {
    return (*env)->NewStringUTF(env, "Hello World Chandan");
}