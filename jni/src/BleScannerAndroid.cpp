#include <jni.h>
//#include <iostream>
#include <stdlib.h>
#include <include\BleScanner.hpp>

//#if defined(unix) || defined(__unix) || defined(__unix__)

BleScanner::BleScanner() {
    using namespace std;

    JavaVM *jvm;                // Pointer to the JVM (Java Virtual Machine)
    JNIEnv *env;                // Pointer to native interface
    //==================== prepare loading of Java VM ============================
    jvm->AttachCurrentThread(&env, NULL);
    JavaVMInitArgs vm_args;                        // Initialization arguments
    JavaVMOption *options = new JavaVMOption[1];   // JVM invocation options
    options[0].optionString = "-Djava.class.path=C:\\Program Files\\Java\\jdk-18.0.2.1\\lib";   // where to find java .class
    vm_args.version = JNI_VERSION_1_6;             // minimum Java version
    vm_args.nOptions = 1;                          // number of options
    vm_args.options = options;
    vm_args.ignoreUnrecognized = false;     // invalid options make the JVM init fail

    //================= load and initialize Java VM and JNI interface ===============

    jint rc = JNI_CreateJavaVM(&jvm, reinterpret_cast<JNIEnv **>((void **) &env),
                               &vm_args);  // YES !!
    delete[] options;    // we then no longer need the initialisation options.
    //========================= analyse errors if any  ==============================
    // if process interuped before error is returned, it's because jvm.dll can't be
    // found, i.e.  its directory is not in the PATH.

    if (rc != JNI_OK) {
        if (rc == JNI_EVERSION)
            printf("FATAL ERROR: JVM is oudated and doesn't meet requirements");
        else if (rc == JNI_ENOMEM)
            printf("FATAL ERROR: not enough memory for JVM");
        else if (rc == JNI_EINVAL)
            printf("FATAL ERROR: invalid ragument for launching JVM");
        else if (rc == JNI_EEXIST)
            printf("FATAL ERROR: the process can only launch one JVM an not more");
        else
            printf("FATAL ERROR:  could not create the JVM instance");
        //cin.get();
        exit(EXIT_FAILURE);
    }

    jclass customBleClass = env->FindClass(
            "com/example/bleapplication/CustomBle2");  // try to find the class
    if (customBleClass == nullptr) {
        printf("ERROR: class not found !");
    } else {                                  // if class found, continue
        printf("Class MyTest found");
        jmethodID getInstance = env->GetStaticMethodID(customBleClass, "getInstance",
                                                       "(Landroid/content/Context;)Lcom/example/bleapplication/CustomBle2;");  // find method
        if (getInstance == nullptr)
            printf("ERROR: method CustomBle getInstance() not found !");
        else {
            jclass context = env->FindClass("android/content/Context");  // try to find the class
            if (context == nullptr) {
                printf("ERROR: class not found !");
            } else {
                jmethodID getApplicationContext = env->GetMethodID(context, "getApplicationContext",
                                                                   "()Landroid/content/Context;");  // find method
                jmethodID contextObject = env->GetMethodID(context, "<init>",
                                                           "()V"); // FIND AN OBJECT CONSTRUCTOR

                if (contextObject == nullptr) {
                    printf("ERROR: constructor not found !");
                } else {
                    printf("Object successfully constructed !");
                    jobject newContextObject = env->NewObject(context,
                                                              contextObject); // Create object of Context
                    jobject jobject1 = env->CallObjectMethod(newContextObject,
                                                             getApplicationContext); // call non static method
                    jobject customBleObject = env->CallStaticObjectMethod(customBleClass,
                                                                          getInstance,
                                                                          newContextObject); // call static method
                }

            }

        }
    }
    jvm->DetachCurrentThread();

}

BleScanner::BleScanner(const char *servicestr) {

}

BleScanner::~BleScanner() {

}

void BleScanner::SetScannerCallback(BleScannerCallback *callbackptr) {

}

void BleScanner::StartScanning() {

    using namespace std;

    JavaVM *jvm;                // Pointer to the JVM (Java Virtual Machine)
    JNIEnv *env;                // Pointer to native interface
    //==================== prepare loading of Java VM ============================
    jvm->AttachCurrentThread(&env, NULL);
    JavaVMInitArgs vm_args;                        // Initialization arguments
    JavaVMOption *options = new JavaVMOption[1];   // JVM invocation options
    options[0].optionString = "-Djava.class.path=C:\\Program Files\\Java\\jdk-18.0.2.1\\lib";   // where to find java .class
    vm_args.version = JNI_VERSION_1_6;               // minimum Java version
    vm_args.nOptions = 1;                          // number of options
    vm_args.options = options;
    vm_args.ignoreUnrecognized = false;     // invalid options make the JVM init fail

    //================= load and initialize Java VM and JNI interface ===============

    jint rc = JNI_CreateJavaVM(&jvm, reinterpret_cast<JNIEnv **>((void **) &env),
                               &vm_args);  // YES !!
    delete[] options;    // we then no longer need the initialisation options.
    //========================= analyse errors if any  ==============================
    // if process interuped before error is returned, it's because jvm.dll can't be
    // found, i.e.  its directory is not in the PATH.

    if (rc != JNI_OK) {
        if (rc == JNI_EVERSION)
            printf("FATAL ERROR: JVM is oudated and doesn't meet requirements");
        else if (rc == JNI_ENOMEM)
            printf("FATAL ERROR: not enough memory for JVM");
        else if (rc == JNI_EINVAL)
            printf("FATAL ERROR: invalid ragument for launching JVM");
        else if (rc == JNI_EEXIST)
            printf("FATAL ERROR: the process can only launch one JVM an not more");
        else
            printf("FATAL ERROR:  could not create the JVM instance");
        //cin.get();
        exit(EXIT_FAILURE);
    }

    jclass customBleClass = env->FindClass(
            "com/example/bleapplication/CustomBle2");  // try to find the class
    if (customBleClass == nullptr) {
        printf("ERROR: class not found !");
    } else {                                  // if class found, continue
        printf("Class MyTest found");
        jmethodID getInstance = env->GetStaticMethodID(customBleClass, "getInstance",
                                                       "(Landroid/content/Context;)Lcom/example/bleapplication/CustomBle2;");  // find method
        if (getInstance == nullptr)
            printf("ERROR: method CustomBle getInstance() not found !");
        else {
            jclass context = env->FindClass("android/content/Context");  // try to find the class
            if (context == nullptr) {
                printf("ERROR: class not found !");
            } else {
                jmethodID getApplicationContext = env->GetMethodID(context, "getApplicationContext",
                                                                   "()Landroid/content/Context;");  // find method
                jmethodID contextObject = env->GetMethodID(context, "<init>",
                                                           "()V"); // FIND AN OBJECT CONSTRUCTOR

                if (contextObject == nullptr) {
                    printf("ERROR: constructor not found !");
                } else {
                    printf("Object successfully constructed !");
                    jobject newContextObject = env->NewObject(context,
                                                              contextObject); // Create object of Context
                    jobject jobject1 = env->CallObjectMethod(newContextObject,
                                                             getApplicationContext); // call non static method
                    jobject customBleObject = env->CallStaticObjectMethod(customBleClass,
                                                                          getInstance,
                                                                          newContextObject); // call static method
                    jmethodID startScan = env->GetMethodID(customBleClass, "startScan", "()V");
                    env->CallVoidMethod(customBleObject, startScan);
                }

            }

        }
    }
    jvm->DetachCurrentThread();

}

void BleScanner::StartScanning(const char *servicestr) {

}

void BleScanner::StopScanning() {
    using namespace std;

    JavaVM *jvm;                // Pointer to the JVM (Java Virtual Machine)
    JNIEnv *env;                // Pointer to native interface
    //==================== prepare loading of Java VM ============================
    jvm->AttachCurrentThread(&env, NULL);
    JavaVMInitArgs vm_args;                        // Initialization arguments
    JavaVMOption *options = new JavaVMOption[1];   // JVM invocation options
    options[0].optionString = "-Djava.class.path=C:\\Program Files\\Java\\jdk-18.0.2.1\\lib";   // where to find java .class
    vm_args.version = JNI_VERSION_1_6;               // minimum Java version
    vm_args.nOptions = 1;                          // number of options
    vm_args.options = options;
    vm_args.ignoreUnrecognized = false;     // invalid options make the JVM init fail

    //================= load and initialize Java VM and JNI interface ===============

    jint rc = JNI_CreateJavaVM(&jvm, reinterpret_cast<JNIEnv **>((void **) &env), &vm_args);  // YES !!
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

    jclass customBleClass = env->FindClass(
            "com/example/bleapplication/CustomBle2");  // try to find the class
    if (customBleClass == nullptr) {
        printf("ERROR: class not found !");
    } else {                                  // if class found, continue
        printf("Class MyTest found");
        jmethodID getInstance = env->GetStaticMethodID(customBleClass, "getInstance",
                                                       "(Landroid/content/Context;)Lcom/example/bleapplication/CustomBle2;");  // find method
        if (getInstance == nullptr)
            printf("ERROR: method CustomBle getInstance() not found !");
        else {
            jclass context = env->FindClass("android/content/Context");  // try to find the class
            if (context == nullptr) {
                printf("ERROR: class not found !");
            } else {
                jmethodID getApplicationContext = env->GetMethodID(context, "getApplicationContext",
                                                                   "()Landroid/content/Context;");  // find method
                jmethodID contextObject = env->GetMethodID(context, "<init>",
                                                           "()V"); // FIND AN OBJECT CONSTRUCTOR

                if (contextObject == nullptr) {
                    printf("ERROR: constructor not found !");
                } else {
                    printf("Object successfully constructed !");
                    jobject newContextObject = env->NewObject(context,
                                                              contextObject); // Create object of Context
                    jobject jobject1 = env->CallObjectMethod(newContextObject,
                                                             getApplicationContext); // call method
                    jobject customBleObject = env->CallStaticObjectMethod(customBleClass,
                                                                          getInstance,
                                                                          newContextObject);
                    jmethodID stopScan = env->GetMethodID(customBleClass, "stopScan",
                                                          "()V");
                    env->CallVoidMethod(customBleObject, stopScan);
                }

            }

        }
    }
    jvm->DetachCurrentThread();

}

bool BleScanner::IsScanning() {

    using namespace std;

    JavaVM *jvm;                // Pointer to the JVM (Java Virtual Machine)
    JNIEnv *env;                // Pointer to native interface
    //==================== prepare loading of Java VM ============================
    jvm->AttachCurrentThread(&env, NULL);
    JavaVMInitArgs vm_args;                        // Initialization arguments
    JavaVMOption *options = new JavaVMOption[1];   // JVM invocation options
    options[0].optionString = "-Djava.class.path=C:\\Program Files\\Java\\jdk-18.0.2.1\\lib";   // where to find java .class
    vm_args.version = JNI_VERSION_1_6;               // minimum Java version
    vm_args.nOptions = 1;                          // number of options
    vm_args.options = options;
    vm_args.ignoreUnrecognized = false;     // invalid options make the JVM init fail

    //================= load and initialize Java VM and JNI interface ===============

    jint rc = JNI_CreateJavaVM(&jvm, reinterpret_cast<JNIEnv **>((void **) &env), &vm_args);  // YES !!
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

    jclass customBleClass = env->FindClass(
            "com/example/bleapplication/CustomBle2");  // try to find the class
    if (customBleClass == nullptr) {
        printf("ERROR: class not found !");
    } else {                                  // if class found, continue
        printf("Class MyTest found");
        jmethodID getInstance = env->GetStaticMethodID(customBleClass, "getInstance",
                                                       "(Landroid/content/Context;)Lcom/example/bleapplication/CustomBle2;");  // find method
        if (getInstance == nullptr)
            printf("ERROR: method CustomBle getInstance() not found !");
        else {
            jclass context = env->FindClass("android/content/Context");  // try to find the class
            if (context == nullptr) {
                printf("ERROR: class not found !");
            } else {
                jmethodID getApplicationContext = env->GetMethodID(context, "getApplicationContext",
                                                                   "()Landroid/content/Context;");  // find method
                jmethodID contextObject = env->GetMethodID(context, "<init>",
                                                           "()V"); // FIND AN OBJECT CONSTRUCTOR

                if (contextObject == nullptr) {
                    printf("ERROR: constructor not found !");
                } else {
                    printf("Object successfully constructed !");
                    jobject newContextObject = env->NewObject(context,
                                                              contextObject); // Create object of Context
                    jobject jobject1 = env->CallObjectMethod(newContextObject,
                                                             getApplicationContext); // call method
                    jobject customBleObject = env->CallStaticObjectMethod(customBleClass,
                                                                          getInstance,
                                                                          newContextObject);
                    jmethodID isScanning = env->GetMethodID(customBleClass, "isScanning", "()Z");
                    return env->CallBooleanMethod(customBleObject, isScanning);
                }

            }

        }
    }
    jvm->DetachCurrentThread();

    return false;
}

void BleScanner::SetScanTarget(const char *servicestr) {

}

void BleScanner::SetScanTimeout(uint32_t tmout) {
    using namespace std;

    JavaVM *jvm;                // Pointer to the JVM (Java Virtual Machine)
    JNIEnv *env;                // Pointer to native interface
    //==================== prepare loading of Java VM ============================
    jvm->AttachCurrentThread(&env, NULL);
    JavaVMInitArgs vm_args;                        // Initialization arguments
    JavaVMOption *options = new JavaVMOption[1];   // JVM invocation options
    options[0].optionString = "-Djava.class.path=C:\\Program Files\\Java\\jdk-18.0.2.1\\lib";   // where to find java .class
    vm_args.version = JNI_VERSION_1_6;              // minimum Java version
    vm_args.nOptions = 1;                          // number of options
    vm_args.options = options;
    vm_args.ignoreUnrecognized = false;     // invalid options make the JVM init fail

    //================= load and initialize Java VM and JNI interface ===============

    jint rc = JNI_CreateJavaVM(&jvm,  &env, &vm_args);  // YES !!
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

    jclass customBleClass = env->FindClass(
            "com/example/bleapplication/CustomBle2");  // try to find the class
    if (customBleClass == nullptr) {
        printf("ERROR: class not found !");
    } else {                                  // if class found, continue
        printf("Class MyTest found");
        jmethodID getInstance = env->GetStaticMethodID(customBleClass, "getInstance",
                                                       "(Landroid/content/Context;)Lcom/example/bleapplication/CustomBle2;");  // find method
        if (getInstance == nullptr)
            printf("ERROR: method CustomBle getInstance() not found !");
        else {
            jclass context = env->FindClass("android/content/Context");  // try to find the class
            if (context == nullptr) {
                printf("ERROR: class not found !");
            } else {
                jmethodID getApplicationContext = env->GetMethodID(context, "getApplicationContext",
                                                                   "()Landroid/content/Context;");  // find method
                jmethodID contextObject = env->GetMethodID(context, "<init>",
                                                           "()V"); // FIND AN OBJECT CONSTRUCTOR

                if (contextObject == nullptr) {
                    printf("ERROR: constructor not found !");
                } else {
                    printf("Object successfully constructed !");
                    jobject newContextObject = env->NewObject(context,
                                                              contextObject); // Create Object of Context
                    jobject customBleObject = env->CallStaticObjectMethod(customBleClass,
                                                                          getInstance,
                                                                          newContextObject); //
                    jmethodID setSCAN_PERIOD = env->GetMethodID(customBleClass, "setSCAN_PERIOD",
                                                                "(J)V");
                    env->CallVoidMethod(customBleObject, setSCAN_PERIOD, (jlong) tmout);
                }

            }

        }
    }
    jvm->DetachCurrentThread();

}

//#endif