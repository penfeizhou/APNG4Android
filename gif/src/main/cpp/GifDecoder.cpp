#include <jni.h>
#include <string>

#include "common.h"
#include "Reader.h"

jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env;
    if (vm->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION_1_6) != JNI_OK) {
        return -1;
    }
    if (JavaReader_OnLoad(env)) {
        LOGE("Failed to load JavaReader");
        return -1;
    }

    return JNI_VERSION_1_6;
}


extern "C" {
JNIEXPORT jstring JNICALL
Java_com_yupaopao_animation_gif_decode_GifFrame_nativeDecode(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

JNIEXPORT void JNICALL
Java_com_yupaopao_animation_gif_decode_GifFrame_uncompressLZW(
        JNIEnv *env,
        jobject /* this */,
        jobject jReader,
        jbyteArray pixels,
        jint lzwMinCodeSize,
        jbyteArray buffer) {
    LOGE("exec here");
    Reader reader(env, jReader, buffer);
    int blockSize;
    char buf[0xff];
    while ((blockSize = reader.peek() & 0xff) != 0) {
        reader.read(buf, blockSize);
    }
}
}
