//
// Created by pengfei.zhou on 2019-05-19.
//

#include "Reader.h"

#define READER_CLASS_PATH "com/yupaopao/animation/io/Reader"


static struct {
    jclass clazz;
    jmethodID readMethodId;
    jmethodID peekMethodId;
} gReaderClassInfo;

jint JavaReader_OnLoad(JNIEnv *env) {
    // Get jclass with env->FindClass.
    // Register methods with env->RegisterNatives.
    gReaderClassInfo.clazz = env->FindClass(READER_CLASS_PATH);
    if (!gReaderClassInfo.clazz) {
        LOGE("Failed to find "
                     READER_CLASS_PATH);
        return -1;
    }
    gReaderClassInfo.clazz = (jclass) env->NewGlobalRef(gReaderClassInfo.clazz);

    gReaderClassInfo.readMethodId = env->GetMethodID(gReaderClassInfo.clazz, "read", "([BII)I");
    gReaderClassInfo.peekMethodId = env->GetMethodID(gReaderClassInfo.clazz, "peek", "()B");
    if (!gReaderClassInfo.readMethodId) {
        LOGE("Failed to find read for Reader - was it stripped?");
        return -1;
    }
    if (!gReaderClassInfo.peekMethodId) {
        LOGE("Failed to find peek for Reader - was it stripped?");
        return -1;
    }
    return 0;
}