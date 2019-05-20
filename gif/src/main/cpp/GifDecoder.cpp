#include <jni.h>
#include <string>
#include <vector>

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
struct Slice {
    char *ptr_data;
    size_t len_data;
};

JNIEXPORT void JNICALL
Java_com_yupaopao_animation_gif_decode_GifFrame_uncompressLZW(
        JNIEnv *env,
        jobject /* this */,
        jobject jReader,
        jbyteArray pixels,
        jint pixelsSize,
        jint lzwMinCodeSize,
        jbyteArray buffer) {
    Reader reader(env, jReader, buffer);
    char buf[0xff];
    char *pixelsBuffer = (char *) malloc(pixelsSize * sizeof(char));
    size_t idx_pixel = 0;
    size_t offset_data = 0;
    size_t idx_data = 0;
    size_t bits = 0;
    size_t code_size = lzwMinCodeSize + 1;

    int datum = 0;
    int code_clear = 1 << lzwMinCodeSize;
    int code_end = code_clear + 1;
    int code;

    std::vector<Slice> table_string;

    Slice prefix;
    while (idx_pixel < pixelsSize) {
        if (offset_data == 0) {
            offset_data = reader.peek() & 0xff;
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
                    char sufix;
                    // Find suffix
                    if (code > code_end) {
                        if (code - code_end > table_string.size()) {
                            sufix = *prefix.ptr_data;
                            //output current slice to buffer
                            memcpy(pixelsBuffer + idx_pixel, prefix.ptr_data,
                                   prefix.len_data);
                            //update slice ptr,so that this continious memory includes sufix
                            slice.ptr_data = pixelsBuffer + idx_pixel;
                            idx_pixel += prefix.len_data;
                            pixelsBuffer[idx_pixel++] = sufix;
                            slice.len_data = prefix.len_data + 1;
                            //Add to string table
                            table_string.push_back(slice);
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
                            memcpy(pixelsBuffer + idx_pixel, current.ptr_data, current.len_data);
                            idx_pixel += current.len_data;
                            //Add to string table
                            table_string.push_back(slice);
                            prefix.ptr_data = current.ptr_data;
                            prefix.len_data = current.len_data;
                        }
                    } else {
                        sufix = (char) (code & 0xff);
                        pixelsBuffer[idx_pixel] = sufix;
                        // It's been copied to pixelsBuffer,so just move forward so that sufix can be contained
                        slice.len_data = prefix.len_data + 1;
                        slice.ptr_data = pixelsBuffer + idx_pixel - prefix.len_data;
                        //Add to string table
                        table_string.push_back(slice);

                        // Set prefix to just one code
                        prefix.len_data = 1;
                        prefix.ptr_data = pixelsBuffer + idx_pixel;
                        idx_pixel++;
                    }
                    if (table_string.size() >= (1 << code_size) - code_end - 1) {
                        code_size++;
                    }
                } else {
                    pixelsBuffer[idx_pixel] = (char) (code & 0xff);
                    prefix.ptr_data = pixelsBuffer + idx_pixel;
                    prefix.len_data = 1;
                    idx_pixel++;
                }
            }
        }
    }
    env->SetByteArrayRegion(pixels, 0, pixelsSize, (jbyte *) pixelsBuffer);
    free(pixelsBuffer);
}
}
