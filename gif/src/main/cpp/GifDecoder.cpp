#include <jni.h>
#include <string>
#include <vector>

#include "common.h"
#include "Reader.h"

struct Slice {
    int *ptr_data;
    size_t len_data;
};

void uncompressLZW(
        JNIEnv *env,
        jobject /* this */,
        jobject jReader,
        jintArray colorTable,
        jint transparentColorIndex,
        jintArray pixels,
        jint width,
        jint height,
        jint lzwMinCodeSize,
        jboolean interlace,
        jbyteArray buffer) {
    Reader reader(env, jReader, buffer);
    char buf[0xff];
    jboolean b = JNI_FALSE;
    int *pixelsBuffer = env->GetIntArrayElements(pixels, &b);
    size_t idx_pixel = 0;
    size_t offset_data = 0;
    size_t idx_data = 0;
    size_t bits = 0;
    size_t code_size = lzwMinCodeSize + 1;
    size_t pixelsSize = width * height;
    int datum = 0;
    int code_clear = 1 << lzwMinCodeSize;
    int code_end = code_clear + 1;
    int code;

    std::vector<Slice> table_string;

    Slice prefix;
    prefix.len_data = 0;
    prefix.ptr_data = nullptr;
    int table_max_size = (1 << 12) - code_end - 1;
    while (idx_pixel < pixelsSize) {
        if (offset_data == 0) {
            offset_data = reader.peek() & 0xff;
            if (offset_data <= 0) {
                // DECODE ERROR
                break;
            }
            reader.read(buf, offset_data);
            idx_data = 0;
        }
        datum += (buf[idx_data] & 0xff) << bits;
        bits += 8;
        idx_data++;
        offset_data--;
        while (bits >= code_size) {
            code = datum & ((1 << code_size) - 1);
            datum >>= code_size;
            bits -= code_size;
            if (code == code_clear) {
                table_string.clear();
                code_size = lzwMinCodeSize + 1;
                prefix.len_data = 0;
                prefix.ptr_data = nullptr;
                continue;
            } else if (code == code_end) {
                break;
            } else {
                if (prefix.len_data > 0 && prefix.ptr_data) {
                    //Add to String Table
                    Slice slice;
                    int sufix;
                    // Find suffix
                    if (code > code_end) {
                        if (code - code_end > table_string.size()) {
                            sufix = *prefix.ptr_data;
                            //output current slice to buffer
                            memcpy(pixelsBuffer + idx_pixel, prefix.ptr_data,
                                   prefix.len_data * sizeof(int));
                            //update slice ptr,so that this continious memory includes sufix
                            slice.ptr_data = pixelsBuffer + idx_pixel;
                            idx_pixel += prefix.len_data;
                            pixelsBuffer[idx_pixel++] = sufix;
                            slice.len_data = prefix.len_data + 1;
                            prefix.ptr_data = slice.ptr_data;
                            prefix.len_data = slice.len_data;
                        } else {
                            // Get Prefix's first char as sufix
                            Slice current = table_string.at(
                                    code - code_end - 1);
                            // sufix = *current.ptr_data;
                            // update ptr so that new table item contain sufix
                            slice.ptr_data = pixelsBuffer + idx_pixel - prefix.len_data;
                            slice.len_data = prefix.len_data + 1;
                            memcpy(pixelsBuffer + idx_pixel, current.ptr_data,
                                   current.len_data *
                                   sizeof(int));
                            idx_pixel += current.len_data;

                            prefix.ptr_data = current.ptr_data;
                            prefix.len_data = current.len_data;
                        }
                    } else {
                        sufix = code;
                        pixelsBuffer[idx_pixel] = sufix;
                        // It's been copied to pixelsBuffer,so just move forward so that sufix can be contained
                        slice.len_data = prefix.len_data + 1;
                        slice.ptr_data = pixelsBuffer + idx_pixel - prefix.len_data;
                        // Set prefix to just one code
                        prefix.len_data = 1;
                        prefix.ptr_data = pixelsBuffer + idx_pixel;
                        idx_pixel++;
                    }
                    if (table_string.size() < table_max_size) {
                        //Add to string table
                        table_string.push_back(slice);
                        if (table_string.size() >= (1 << code_size) - code_end - 1
                            && table_string.size() < table_max_size) {
                            code_size++;
                        }
                    }
                } else {
                    pixelsBuffer[idx_pixel] = code & 0xff;
                    prefix.ptr_data = pixelsBuffer + idx_pixel;
                    prefix.len_data = 1;
                    idx_pixel++;
                }
            }
        }
    }

    int *colors = env->GetIntArrayElements(colorTable, &b);
    int idx;
    for (int loop = 0; loop < idx_pixel; loop++) {
        idx = pixelsBuffer[loop] & 0xff;
        if (idx == transparentColorIndex) {
            pixelsBuffer[loop] = 0;
        } else {
            pixelsBuffer[loop] = colors[idx];
        }
    }

    while (idx_pixel < pixelsSize) {
        pixelsBuffer[idx_pixel++] = 0;
    }
    if (interlace) {
        // interlace flag
        size_t i = 0;
        int *pixels_copy = static_cast<int *>(malloc(sizeof(int) * pixelsSize));
        size_t src_row = 0;
        size_t pack = 1;
        size_t step = 8;
        size_t start = 8;
        for (; pack <= 4 & src_row < height;) {
            switch (pack) {
                case 1:
                    step = 8;
                    start = 0;
                    break;
                case 2:
                    step = 8;
                    start = 4;
                    break;
                case 3:
                    step = 4;
                    start = 2;
                    break;
                case 4:
                    step = 2;
                    start = 1;
                    break;
            }
            i = start;
            do {
                // copy
                memcpy(pixels_copy + i * width, pixelsBuffer + src_row * width,
                       width * (sizeof(int)));
                src_row++;
                i += step;
            } while (i < height);
            pack++;
        }
        memcpy(pixelsBuffer, pixels_copy, pixelsSize * sizeof(int));
        free(pixels_copy);
    }
    env->ReleaseIntArrayElements(pixels, pixelsBuffer, 0);
    env->ReleaseIntArrayElements(colorTable, colors, JNI_ABORT);
}


static JNINativeMethod methods[] = {
        {"uncompressLZW", "(Lcom/github/penfeizhou/animation/gif/io/GifReader;[II[IIIIZ[B)V", (void *) &uncompressLZW},
};

int jniRegisterNativeMethods(JNIEnv *env, const char *className, const JNINativeMethod *gMethods,
                             int numMethods) {
    jclass clazz;
    int tmp;
    clazz = env->FindClass(className);
    if (clazz == nullptr) {
        return -1;
    }
    if ((tmp = env->RegisterNatives(clazz, gMethods, numMethods)) < 0) {
        return -1;
    }
    return 0;
}

int registerNativeMethods(JNIEnv *env) {
    return jniRegisterNativeMethods(env, "com/github/penfeizhou/animation/gif/decode/GifFrame",
                                    methods,
                                    sizeof(methods) / sizeof(methods[0]));
}

jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env;
    if (vm->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION_1_6) != JNI_OK) {
        return -1;
    }
    if (JavaReader_OnLoad(env)) {
        LOGE("Failed to load JavaReader");
        return -1;
    }
    if (registerNativeMethods(env) != JNI_OK) {
        return -1;
    }
    return JNI_VERSION_1_6;
}
