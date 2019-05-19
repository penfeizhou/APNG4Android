//
// Created by pengfei.zhou on 2019-05-19.
//

#ifndef APNG4ANDROID_READER_H
#define APNG4ANDROID_READER_H

#include <jni.h>
#include "common.h"
#include <stdio.h>
#include <string.h>

class Reader {
public:
    Reader(JNIEnv* env,jobject reader,jbyteArray byteArray):
    mEnv(env),
    mJavaReader(reader),
    mByteArray(byteArray),
    mByteArrayLength(env->GetArrayLength(byteArray)) {}

    char peek();

    size_t read(char* in, size_t size);
private:
    JNIEnv* mEnv;
    const jobject mJavaReader;
    const jbyteArray mByteArray;
    const size_t mByteArrayLength;
};

jint JavaReader_OnLoad(JNIEnv* env);

#endif //APNG4ANDROID_READER_H
