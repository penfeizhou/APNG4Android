//
// Created by pengfei.zhou on 2019-05-19.
//

#ifndef APNG4ANDROID_COMMON_H
#define APNG4ANDROID_COMMON_H

#include <jni.h>

#include <android/log.h>

#define  ADB_LOG_TAG    "GifDecoder"
#ifdef DEBUG
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG, ADB_LOG_TAG, __VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR, ADB_LOG_TAG, __VA_ARGS__)
#else
#define  LOGD(...)
#define  LOGE(...)
#endif

#endif //APNG4ANDROID_COMMON_H
