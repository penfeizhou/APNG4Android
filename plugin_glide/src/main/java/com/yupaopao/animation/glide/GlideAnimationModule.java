package com.yupaopao.animation.glide;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.LibraryGlideModule;

import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * @Description: GlideAnimationModule
 * @Author: pengfei.zhou
 * @CreateDate: 2019-05-14
 */
@GlideModule
public class GlideAnimationModule extends LibraryGlideModule {
    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
        super.registerComponents(context, glide, registry);
        ByteBufferAnimationDecoder byteBufferAnimationDecoder = new ByteBufferAnimationDecoder();
        StreamAnimationDecoder streamAnimationDecoder = new StreamAnimationDecoder(byteBufferAnimationDecoder);
        registry.prepend(InputStream.class, Drawable.class, streamAnimationDecoder);
        registry.prepend(ByteBuffer.class, Drawable.class, byteBufferAnimationDecoder);
    }
}
