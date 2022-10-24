#include <jni.h>
//#include <iostream>
#include <stdlib.h>
#include <stdio.h>
#include <string>
#include <include\BleCentral.hpp>

//#if defined(unix) || defined(__unix) || defined(__unix__)

jstring charTojstring(JNIEnv* env, const char* pat);

BleCentral::BleCentral()
{
    using namespace std;

    JavaVM *jvm;				// Pointer to the JVM (Java Virtual Machine)
    JNIEnv *env;				// Pointer to native interface
    //==================== prepare loading of Java VM ============================
    jvm->AttachCurrentThread(&env, NULL);

    JavaVMInitArgs vm_args;                        // Initialization arguments
    JavaVMOption* options = new JavaVMOption[1];   // JVM invocation options
    //options[0].optionString = "-Djava.class.path=C:\\Program Files\\Java\\jdk-18.0.2.1";   // where to find java .class
    options[0].optionString = "-Djava.class.path=C:\\Program Files\\Java\\jdk-18.0.2.1\\lib";   // where to find java .class
    vm_args.version = JNI_VERSION_1_6;              // minimum Java version
    vm_args.nOptions = 1;                          // number of options
    vm_args.options = options;
    vm_args.ignoreUnrecognized = false;     // invalid options make the JVM init fail

    //================= load and initialize Java VM and JNI interface ===============
    //HINSTANCE hVM = LoadLibrary("");
    //if (hVM == NULL)
    //{
    //    DWORD dwe = GetLastError();
    //    return -1;
    //}
    //typedef jint	(CALLBACK *fpCJV)(JavaVM**, void**, JavaVMInitArgs*);
    //fpCJV CreateJavaVM = (fpCJV)::GetProcAddress(hVM, "JNI_CreateJavaVM");
    //res = CreateJavaVM(&jvm, (void**)&env, &vm_args);

    jint rc = JNI_CreateJavaVM(&jvm, &env, &vm_args);  // YES !!
    delete[] options;    // we then no longer need the initialisation options.
    //========================= analyse errors if any  ==============================
    // if process interuped before error is returned, it's because jvm.dll can't be
    // found, i.e.  its directory is not in the PATH.

    if(rc != JNI_OK) {
        if(rc == JNI_EVERSION)
            printf("FATAL ERROR: JVM is oudated and doesn't meet requirements");
        else if(rc == JNI_ENOMEM)
            printf("FATAL ERROR: not enough memory for JVM");
        else if(rc == JNI_EINVAL)
            printf("FATAL ERROR: invalid ragument for launching JVM");
        else if(rc == JNI_EEXIST)
            printf("FATAL ERROR: the process can only launch one JVM an not more");
        else
            printf("FATAL ERROR:  could not create the JVM instance");
        //cin.get();
        exit(0);
    }

    jclass customBleClass = env->FindClass("com/example/bleapplication/CustomBle2");  // try to find the class
    if(customBleClass == nullptr) {
        printf("ERROR: class not found !");
    }else {                                  // if class found, continue
        printf("Class MyTest found");
        jmethodID getInstance = env->GetStaticMethodID(customBleClass, "getInstance",
                                                       "(Landroid/content/Context;)Lcom/example/bleapplication/CustomBle2;");  // find method
        if(getInstance == nullptr)
            printf("ERROR: method CustomBle getInstance() not found !");
        else {
            jclass context = env->FindClass("android/content/Context");  // try to find the class
            if(context == nullptr) {
                printf("ERROR: class not found !");
            } else{
                jmethodID getApplicationContext = env->GetMethodID(context, "getApplicationContext",
                                                                   "()Landroid/content/Context;");  // find method
                jmethodID contextObject = env->GetMethodID(context, "<init>", "()V"); // FIND AN OBJECT CONSTRUCTOR

                if(contextObject == nullptr) {
                    printf("ERROR: constructor not found !");
                }
                else {
                    printf("Object successfully constructed !");
                    jobject newContextObject = env->NewObject(context, contextObject); // Create object of Context
                    jobject jobject1 = env->CallObjectMethod(newContextObject, getApplicationContext); // call non static method
                    jobject customBleObject = env->CallStaticObjectMethod(customBleClass, getInstance, newContextObject); // call static method
                }
                printf("");
            }

        }
    }
    jvm->DetachCurrentThread();

}

BleCentral::BleCentral(uint64_t ibleaddress, const char* iserviceid)
{

}

BleCentral::~BleCentral()
{

}

void BleCentral::ConnectPeripheral()
{

}

void BleCentral::ConnectPeripheral(uint64_t ibleaddress, const char* iaddressuuid, const char* iserviceid, const char* pin, size_t pinlen)
{

}

void BleCentral::DisconnectPeripheral()
{
    using namespace std;

    JavaVM *jvm;				// Pointer to the JVM (Java Virtual Machine)
    JNIEnv *env;				// Pointer to native interface
    //==================== prepare loading of Java VM ============================
    jvm->AttachCurrentThread(&env, NULL);
    JavaVMInitArgs vm_args;                        // Initialization arguments
    JavaVMOption* options = new JavaVMOption[1];   // JVM invocation options
    options[0].optionString = "-Djava.class.path=C:\\Program Files\\Java\\jdk-18.0.2.1\\lib";   // where to find java .class
    vm_args.version = JNI_VERSION_1_6;               // minimum Java version
    vm_args.nOptions = 1;                         // number of options
    vm_args.options = options;
    vm_args.ignoreUnrecognized = false;     // invalid options make the JVM init fail

    //================= load and initialize Java VM and JNI interface ===============

    jint rc = JNI_CreateJavaVM(&jvm, &env, &vm_args);  // YES !!
    delete[] options;    // we then no longer need the initialisation options.
    //========================= analyse errors if any  ==============================
    // if process interuped before error is returned, it's because jvm.dll can't be
    // found, i.e.  its directory is not in the PATH.

    if(rc != JNI_OK) {
        if(rc == JNI_EVERSION)
            printf("FATAL ERROR: JVM is oudated and doesn't meet requirements");
        else if(rc == JNI_ENOMEM)
            printf("FATAL ERROR: not enough memory for JVM");
        else if(rc == JNI_EINVAL)
            printf("FATAL ERROR: invalid ragument for launching JVM");
        else if(rc == JNI_EEXIST)
            printf("FATAL ERROR: the process can only launch one JVM an not more");
        else
            printf("FATAL ERROR:  could not create the JVM instance");
        //cin.get();
        exit(EXIT_FAILURE);
    }

    jclass customBleClass = env->FindClass("com/example/bleapplication/CustomBle2");  // try to find the class
    if(customBleClass == nullptr) {
        printf("ERROR: class not found !");
    }else {                                  // if class found, continue
        printf("Class MyTest found");
        jmethodID getInstance = env->GetStaticMethodID(customBleClass, "getInstance",
                                                       "(Landroid/content/Context;)Lcom/example/bleapplication/CustomBle2;");  // find method
        if(getInstance == nullptr)
            printf("ERROR: method CustomBle getInstance() not found !");
        else {
            jclass context = env->FindClass("android/content/Context");  // try to find the class
            if(context == nullptr) {
                printf("ERROR: class not found !");
            } else{
                jmethodID getApplicationContext = env->GetMethodID(context, "getApplicationContext",
                                                                   "()Landroid/content/Context;");  // find method
                jmethodID contextObject = env->GetMethodID(context, "<init>", "()V"); // FIND AN OBJECT CONSTRUCTOR

                if(contextObject == nullptr) {
                    printf("ERROR: constructor not found !");
                }
                else {
                    printf("Object successfully constructed !");
                    jobject newContextObject = env->NewObject(context, contextObject); // Create object of Context
                    jobject jobject1 = env->CallObjectMethod(newContextObject, getApplicationContext); // call non static method
                    jobject customBleObject = env->CallStaticObjectMethod(customBleClass, getInstance, newContextObject); // call static method
                    jmethodID disconnectGattServer = env->GetMethodID(customBleClass, "disconnectGattServer",
                                                                      "()V");
                    env->CallVoidMethod(customBleObject, disconnectGattServer);
                }
                printf("");
            }

        }
    }
    jvm->DetachCurrentThread();

}

void BleCentral::SetPeripheralDevice(uint64_t ibleaddress, const char* iaddressuuid, const char* iserviceid, const char* pin, size_t pinlen)
{

}

uint32_t BleCentral::TransmitBuffer(const unsigned char* buffer, int len)
{
    return 0;
}

unsigned char* BleCentral::ReceiveBuffer(unsigned char* buffer, int* len, unsigned char eopbyte)
{
    return nullptr;
}

bool BleCentral::IsConnected()
{
    using namespace std;

    JavaVM *jvm;				// Pointer to the JVM (Java Virtual Machine)
    JNIEnv *env;				// Pointer to native interface
    //==================== prepare loading of Java VM ============================
    jvm->AttachCurrentThread(&env, NULL);
    JavaVMInitArgs vm_args;                        // Initialization arguments
    JavaVMOption* options = new JavaVMOption[1];   // JVM invocation options
    options[0].optionString = "-Djava.class.path=C:\\Program Files\\Java\\jdk-18.0.2.1\\lib";   // where to find java .class
    vm_args.version = JNI_VERSION_1_6;               // minimum Java version
    vm_args.nOptions = 1;                          // number of options
    vm_args.options = options;
    vm_args.ignoreUnrecognized = false;     // invalid options make the JVM init fail

    //================= load and initialize Java VM and JNI interface ===============

    jint rc = JNI_CreateJavaVM(&jvm, &env, &vm_args);  // YES !!
    delete[] options;    // we then no longer need the initialisation options.
    //========================= analyse errors if any  ==============================
    // if process interuped before error is returned, it's because jvm.dll can't be
    // found, i.e.  its directory is not in the PATH.

    if(rc != JNI_OK) {
        if(rc == JNI_EVERSION)
            printf("FATAL ERROR: JVM is oudated and doesn't meet requirements");
        else if(rc == JNI_ENOMEM)
            printf("FATAL ERROR: not enough memory for JVM");
        else if(rc == JNI_EINVAL)
            printf("FATAL ERROR: invalid ragument for launching JVM");
        else if(rc == JNI_EEXIST)
            printf("FATAL ERROR: the process can only launch one JVM an not more");
        else
            printf("FATAL ERROR:  could not create the JVM instance");
        //cin.get();
        exit(EXIT_FAILURE);
    }

    jclass customBleClass = env->FindClass("com/example/bleapplication/CustomBle2");  // try to find the class
    if(customBleClass == nullptr) {
        printf("ERROR: class not found !");
    }else {                                  // if class found, continue
        printf("Class MyTest found");
        jmethodID getInstance = env->GetStaticMethodID(customBleClass, "getInstance",
                                                       "(Landroid/content/Context;)Lcom/example/bleapplication/CustomBle2;");  // find method
        if(getInstance == nullptr)
            printf("ERROR: method CustomBle getInstance() not found !");
        else {
            jclass context = env->FindClass("android/content/Context");  // try to find the class
            if(context == nullptr) {
                printf("ERROR: class not found !");
            } else{
                jmethodID getApplicationContext = env->GetMethodID(context, "getApplicationContext",
                                                                   "()Landroid/content/Context;");  // find method
                jmethodID contextObject = env->GetMethodID(context, "<init>", "()V"); // FIND AN OBJECT CONSTRUCTOR

                if(contextObject == nullptr) {
                    printf("ERROR: constructor not found !");
                }
                else {
                    printf("Object successfully constructed !");
                    jobject newContextObject = env->NewObject(context, contextObject); // Create object of Context
                    jobject jobject1 = env->CallObjectMethod(newContextObject, getApplicationContext); // call non static method
                    jobject customBleObject = env->CallStaticObjectMethod(customBleClass, getInstance, newContextObject); // call static method
                    jmethodID isConnected = env->GetMethodID(customBleClass, "isConnected",
                                                             "()Z");
                    return env->CallBooleanMethod(customBleObject, isConnected);
                }
                printf("");
            }

        }
    }
    jvm->DetachCurrentThread();
    return false;
}

bool BleCentral::HasErrors()
{
    return false;
}

uint64_t BleCentral::Address()
{
    return -1;
}

const char* BleCentral::ServiceUuid()
{
    return nullptr;
}

void BleCentral::SetRxCharacteristics(const char* str)
{
    using namespace std;

    JavaVM *jvm;				// Pointer to the JVM (Java Virtual Machine)
    JNIEnv *env;				// Pointer to native interface
    //==================== prepare loading of Java VM ============================
    jvm->AttachCurrentThread(&env, NULL);
    JavaVMInitArgs vm_args;                        // Initialization arguments
    JavaVMOption* options = new JavaVMOption[1];   // JVM invocation options
    options[0].optionString = "-Djava.class.path=C:\\Program Files\\Java\\jdk-18.0.2.1\\lib";   // where to find java .class
    vm_args.version = JNI_VERSION_1_6;               // minimum Java version
    vm_args.nOptions = 1;                          // number of options
    vm_args.options = options;
    vm_args.ignoreUnrecognized = false;     // invalid options make the JVM init fail

    //================= load and initialize Java VM and JNI interface ===============

    jint rc = JNI_CreateJavaVM(&jvm, &env, &vm_args);  // YES !!
    delete[] options;    // we then no longer need the initialisation options.
    //========================= analyse errors if any  ==============================
    // if process interuped before error is returned, it's because jvm.dll can't be
    // found, i.e.  its directory is not in the PATH.

    if(rc != JNI_OK) {
        if(rc == JNI_EVERSION)
            printf("FATAL ERROR: JVM is oudated and doesn't meet requirements");
        else if(rc == JNI_ENOMEM)
            printf("FATAL ERROR: not enough memory for JVM");
        else if(rc == JNI_EINVAL)
            printf("FATAL ERROR: invalid ragument for launching JVM");
        else if(rc == JNI_EEXIST)
            printf("FATAL ERROR: the process can only launch one JVM an not more");
        else
            printf("FATAL ERROR:  could not create the JVM instance");
        //cin.get();
        exit(EXIT_FAILURE);
    }

    jclass customBleClass = env->FindClass("com/example/bleapplication/CustomBle2");  // try to find the class
    if(customBleClass == nullptr) {
        printf("ERROR: class not found !");
    }else {                                  // if class found, continue
        printf("Class MyTest found");
        jmethodID getInstance = env->GetStaticMethodID(customBleClass, "getInstance",
                                                       "(Landroid/content/Context;)Lcom/example/bleapplication/CustomBle2;");  // find method
        if(getInstance == nullptr)
            printf("ERROR: method CustomBle getInstance() not found !");
        else {
            jclass context = env->FindClass("android/content/Context");  // try to find the class
            if(context == nullptr) {
                printf("ERROR: class not found !");
            } else{
                jmethodID getApplicationContext = env->GetMethodID(context, "getApplicationContext",
                                                                   "()Landroid/content/Context;");  // find method
                jmethodID contextObject = env->GetMethodID(context, "<init>", "()V"); // FIND AN OBJECT CONSTRUCTOR

                if(contextObject == nullptr) {
                    printf("ERROR: constructor not found !");
                }
                else {
                    printf("Object successfully constructed !");
                    jobject newContextObject = env->NewObject(context, contextObject); // Create object of Context
                    jobject jobject1 = env->CallObjectMethod(newContextObject, getApplicationContext); // call non static method
                    jobject customBleObject = env->CallStaticObjectMethod(customBleClass, getInstance, newContextObject); // call static method
                    jmethodID setRx = env->GetMethodID(customBleClass, "setRx",
                                                       "(Ljava/util/UUID;)V");
                    jclass uuidClass = env->FindClass("java/util/UUID"); // Find class
                    jmethodID fromString = env->GetStaticMethodID(uuidClass, "fromString",
                                                                  "(Ljava/lang/String;)Ljava/util/UUID;"); // find static method

                    jobject uuidObject = env->CallStaticObjectMethod(uuidClass, fromString, charTojstring(env, str)); // call static method
                    env->CallVoidMethod(customBleObject, setRx, uuidObject);
                }
                printf("");
            }

        }
    }
    jvm->DetachCurrentThread();

}

jstring charTojstring(JNIEnv* env, const char* pat)
{
   //Define the java String class strClass
    jclass strClass = (env)->FindClass("Ljava/lang/String;");
   //Get the constructor of String(byte[],String) to convert the local byte[] array into a new String
    jmethodID ctorID = (env)->GetMethodID(strClass, "<init>", "([BLjava/lang/String;)V");
   //Create byte array
    jbyteArray bytes = (env)->NewByteArray(strlen(pat));
   //Convert char* to byte array
    (env)->SetByteArrayRegion(bytes, 0, strlen(pat), (jbyte*) pat);
   //Set the String, save the language type, used for the parameters when the byte array is converted to String
    jstring encoding = (env)->NewStringUTF("GB2312");
   //Convert the byte array to java String and output
    return (jstring) (env)->NewObject(strClass, ctorID, bytes, encoding);
}

void BleCentral::SetTxCharacteristics(const char* str)
{
    using namespace std;

    JavaVM *jvm;				// Pointer to the JVM (Java Virtual Machine)
    JNIEnv *env;				// Pointer to native interface
    //==================== prepare loading of Java VM ============================
    jvm->AttachCurrentThread(&env, NULL);
    JavaVMInitArgs vm_args;                        // Initialization arguments
    JavaVMOption* options = new JavaVMOption[1];   // JVM invocation options
    options[0].optionString = "-Djava.class.path=C:\\Program Files\\Java\\jdk-18.0.2.1\\lib";   // where to find java .class
    vm_args.version = JNI_VERSION_1_6;               // minimum Java version
    vm_args.nOptions = 1;                          // number of options
    vm_args.options = options;
    vm_args.ignoreUnrecognized = false;     // invalid options make the JVM init fail

    //================= load and initialize Java VM and JNI interface ===============

    jint rc = JNI_CreateJavaVM(&jvm, &env, &vm_args);  // YES !!
    delete[] options;    // we then no longer need the initialisation options.
    //========================= analyse errors if any  ==============================
    // if process interuped before error is returned, it's because jvm.dll can't be
    // found, i.e.  its directory is not in the PATH.

    if(rc != JNI_OK) {
        if(rc == JNI_EVERSION)
            printf("FATAL ERROR: JVM is oudated and doesn't meet requirements");
        else if(rc == JNI_ENOMEM)
            printf("FATAL ERROR: not enough memory for JVM");
        else if(rc == JNI_EINVAL)
            printf("FATAL ERROR: invalid ragument for launching JVM");
        else if(rc == JNI_EEXIST)
            printf("FATAL ERROR: the process can only launch one JVM an not more");
        else
            printf("FATAL ERROR:  could not create the JVM instance");
        //cin.get();
        exit(EXIT_FAILURE);
    }

    jclass customBleClass = env->FindClass("com/example/bleapplication/CustomBle2");  // try to find the class
    if(customBleClass == nullptr) {
        printf("ERROR: class not found !");
    }else {                                  // if class found, continue
        printf("Class MyTest found");
        jmethodID getInstance = env->GetStaticMethodID(customBleClass, "getInstance",
                                                       "(Landroid/content/Context;)Lcom/example/bleapplication/CustomBle2;");  // find method
        if(getInstance == nullptr)
            printf("ERROR: method CustomBle getInstance() not found !");
        else {
            jclass context = env->FindClass("android/content/Context");  // try to find the class
            if(context == nullptr) {
                printf("ERROR: class not found !");
            } else{
                jmethodID getApplicationContext = env->GetMethodID(context, "getApplicationContext",
                                                                   "()Landroid/content/Context;");  // find method
                jmethodID contextObject = env->GetMethodID(context, "<init>", "()V"); // FIND AN OBJECT CONSTRUCTOR

                if(contextObject == nullptr) {
                    printf("ERROR: constructor not found !");
                }
                else {
                    printf("Object successfully constructed !");
                    jobject newContextObject = env->NewObject(context, contextObject); // Create of object of Context
                    jobject jobject1 = env->CallObjectMethod(newContextObject, getApplicationContext); // call non static method
                    jobject customBleObject = env->CallStaticObjectMethod(customBleClass, getInstance, newContextObject); // call static method
                    jmethodID setTx = env->GetMethodID(customBleClass, "setTx",
                                                       "(Ljava/util/UUID;)V");
                    jclass uuidClass = env->FindClass("java/util/UUID");
                    jmethodID fromString = env->GetStaticMethodID(uuidClass, "fromString",
                                                                  "(Ljava/lang/String;)Ljava/util/UUID;");

                    jobject uuidObject = env->CallStaticObjectMethod(uuidClass, fromString, charTojstring(env, str));
                    env->CallVoidMethod(customBleObject, setTx, uuidObject);
                }
                printf("");
            }

        }
    }
    jvm->DetachCurrentThread();
}

const char* BleCentral::GetRxCharacteristics()
{
    using namespace std;

    JavaVM *jvm;				// Pointer to the JVM (Java Virtual Machine)
    JNIEnv *env;				// Pointer to native interface
    //==================== prepare loading of Java VM ============================
    jvm->AttachCurrentThread(&env, NULL);
    JavaVMInitArgs vm_args;                        // Initialization arguments
    JavaVMOption* options = new JavaVMOption[1];   // JVM invocation options
    options[0].optionString = "-Djava.class.path=C:\\Program Files\\Java\\jdk-18.0.2.1\\lib";   // where to find java .class
    vm_args.version = JNI_VERSION_1_6;               // minimum Java version
    vm_args.nOptions = 1;                          // number of options
    vm_args.options = options;
    vm_args.ignoreUnrecognized = false;     // invalid options make the JVM init fail

    //================= load and initialize Java VM and JNI interface ===============

    jint rc = JNI_CreateJavaVM(&jvm, &env, &vm_args);  // YES !!
    delete[] options;    // we then no longer need the initialisation options.
    //========================= analyse errors if any  ==============================
    // if process interuped before error is returned, it's because jvm.dll can't be
    // found, i.e.  its directory is not in the PATH.

    if(rc != JNI_OK) {
        if(rc == JNI_EVERSION)
            printf("FATAL ERROR: JVM is oudated and doesn't meet requirements");
        else if(rc == JNI_ENOMEM)
            printf("FATAL ERROR: not enough memory for JVM");
        else if(rc == JNI_EINVAL)
            printf("FATAL ERROR: invalid ragument for launching JVM");
        else if(rc == JNI_EEXIST)
            printf("FATAL ERROR: the process can only launch one JVM an not more");
        else
            printf("FATAL ERROR:  could not create the JVM instance");
        //cin.get();
        exit(EXIT_FAILURE);
    }

    jclass customBleClass = env->FindClass("com/example/bleapplication/CustomBle2");  // try to find the class
    if(customBleClass == nullptr) {
        printf("ERROR: class not found !");
    }else {                                  // if class found, continue
        printf("Class MyTest found");
        jmethodID getInstance = env->GetStaticMethodID(customBleClass, "getInstance",
                                                       "(Landroid/content/Context;)Lcom/example/bleapplication/CustomBle2;");  // find method
        if(getInstance == nullptr)
            printf("ERROR: method CustomBle getInstance() not found !");
        else {
            jclass context = env->FindClass("android/content/Context");  // try to find the class
            if(context == nullptr) {
                printf("ERROR: class not found !");
            } else{
                jmethodID getApplicationContext = env->GetMethodID(context, "getApplicationContext",
                                                                   "()Landroid/content/Context;");  // find method
                jmethodID contextObject = env->GetMethodID(context, "<init>", "()V"); // FIND AN OBJECT CONSTRUCTOR

                if(contextObject == nullptr) {
                    printf("ERROR: constructor not found !");
                }
                else {
                    printf("Object successfully constructed !");
                    jobject newContextObject = env->NewObject(context, contextObject); // Create object of Context
                    jobject jobject1 = env->CallObjectMethod(newContextObject, getApplicationContext); // call non static method
                    jobject customBleObject = env->CallStaticObjectMethod(customBleClass, getInstance, newContextObject); // call static method
                    jmethodID getRx = env->GetMethodID(customBleClass, "getRx",
                                                       "()Ljava/util/UUID;");
                    return reinterpret_cast<const char *>(env->CallObjectMethod(customBleObject, getRx));
                }
                printf("");
            }

        }
    }
    jvm->DetachCurrentThread();
    return nullptr;
}
const char* BleCentral::GetTxCharacteristics()
{
    using namespace std;

    JavaVM *jvm;				// Pointer to the JVM (Java Virtual Machine)
    JNIEnv *env;				// Pointer to native interface
    //==================== prepare loading of Java VM ============================

    jvm->AttachCurrentThread(&env, NULL);

    JavaVMInitArgs vm_args;                        // Initialization arguments
    JavaVMOption* options = new JavaVMOption[1];   // JVM invocation options
    options[0].optionString = "-Djava.class.path=C:\\Program Files\\Java\\jdk-18.0.2.1\\lib";   // where to find java .class
    vm_args.version = JNI_VERSION_1_6;              // minimum Java version
    vm_args.nOptions = 1;                          // number of options
    vm_args.options = options;
    vm_args.ignoreUnrecognized = false;     // invalid options make the JVM init fail

    //================= load and initialize Java VM and JNI interface ===============

    jint rc = JNI_CreateJavaVM(&jvm, &env, &vm_args);  // YES !!
    delete[] options;    // we then no longer need the initialisation options.
    //========================= analyse errors if any  ==============================
    // if process interuped before error is returned, it's because jvm.dll can't be
    // found, i.e.  its directory is not in the PATH.

    if(rc != JNI_OK) {
            if(rc == JNI_EVERSION)
                printf("FATAL ERROR: JVM is oudated and doesn't meet requirements");
            else if(rc == JNI_ENOMEM)
                printf("FATAL ERROR: not enough memory for JVM");
            else if(rc == JNI_EINVAL)
                printf("FATAL ERROR: invalid ragument for launching JVM");
            else if(rc == JNI_EEXIST)
                printf("FATAL ERROR: the process can only launch one JVM an not more");
            else
                printf("FATAL ERROR:  could not create the JVM instance");
            //cin.get();
            exit(EXIT_FAILURE);
        }

    jclass customBleClass = env->FindClass("com/example/bleapplication/CustomBle2");  // try to find the class
    if(customBleClass == nullptr) {
        printf("ERROR: class not found !");
    }else {                                  // if class found, continue
        printf("Class MyTest found");
        jmethodID getInstance = env->GetStaticMethodID(customBleClass, "getInstance",
                                                       "(Landroid/content/Context;)Lcom/example/bleapplication/CustomBle2;");  // find method
        if(getInstance == nullptr)
            printf("ERROR: method CustomBle getInstance() not found !");
        else {
            jclass context = env->FindClass("android/content/Context");  // try to find the class
            if(context == nullptr) {
                printf("ERROR: class not found !");
            } else{
                jmethodID getApplicationContext = env->GetMethodID(context, "getApplicationContext",
                                                                   "()Landroid/content/Context;");  // find method
                jmethodID contextObject = env->GetMethodID(context, "<init>", "()V"); // FIND AN OBJECT CONSTRUCTOR

                if(contextObject == nullptr) {
                    printf("ERROR: constructor not found !");
                }
                else {
                    printf("Object successfully constructed !");
                    jobject newContextObject = env->NewObject(context, contextObject); // Create object of Context
                    jobject jobject1 = env->CallObjectMethod(newContextObject, getApplicationContext); // call non static method
                    jobject customBleObject = env->CallStaticObjectMethod(customBleClass, getInstance, newContextObject); // call static method
                    jmethodID getTx = env->GetMethodID(customBleClass, "getTx",
                                                       "()Ljava/util/UUID;");

                    return reinterpret_cast<const char *>(env->CallObjectMethod(customBleObject, getTx));
                }
                //print("");
            }

        }
    }
    jvm->DetachCurrentThread();
    return nullptr;
}

void BleCentral::DiscoverServices()
{
    return;
}
//#endif