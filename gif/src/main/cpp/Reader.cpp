//
// Created by pengfei.zhou on 2019-05-19.
//

#include "Reader.h"

#define READER_CLASS_PATH "com/github/penfeizhou/animation/io/Reader"

#define min(a, b) \
   ({ __typeof__ (a) _a = (a); \
       __typeof__ (b) _b = (b); \
     _a < _b ? _a : _b; })

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

size_t Reader::read(char *in, size_t size) {
    size_t totalBytesRead = 0;

    do {
        size_t requested = min(size, mByteArrayLength);

        jint bytesRead = mEnv->CallIntMethod(mJavaReader,
                                             gReaderClassInfo.readMethodId, mByteArray, 0,
                                             requested);
        if (mEnv->ExceptionCheck() || bytesRead < 0) {
            return 0;
        }

        mEnv->GetByteArrayRegion(mByteArray, 0, bytesRead, (jbyte *) in);
        in = in + bytesRead;
        totalBytesRead += bytesRead;
        size -= bytesRead;
    } while (size > 0);

    return totalBytesRead;
}

char Reader::peek() {
    jbyte bytesRead = mEnv->CallByteMethod(mJavaReader,
                                           gReaderClassInfo.peekMethodId);
    return bytesRead;
}

