#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_com_yupaopao_animation_gif_decode_GifFrame_nativeDecode(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
