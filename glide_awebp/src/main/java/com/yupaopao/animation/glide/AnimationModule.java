package com.yupaopao.animation.glide;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.LibraryGlideModule;

import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * @Description: AnimationMoudle
 * @Author: pengfei.zhou
 * @CreateDate: 2019-05-14
 */
@GlideModule
public class AnimationModule extends LibraryGlideModule {
    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
        super.registerComponents(context, glide, registry);
        StreamAnimationDecoder streamAnimationDecoder = new StreamAnimationDecoder();
        ByteBufferWebPDecoder byteBufferWebPDecoder = new ByteBufferWebPDecoder();
        registry.prepend(InputStream.class, Drawable.class, streamAnimationDecoder);
        registry.prepend(ByteBuffer.class, Drawable.class, byteBufferWebPDecoder);
    }
}
