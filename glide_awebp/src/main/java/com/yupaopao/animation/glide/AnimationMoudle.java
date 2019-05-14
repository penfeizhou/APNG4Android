package com.yupaopao.animation.glide;

import android.content.Context;
import android.support.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.LibraryGlideModule;

/**
 * @Description: AnimationMoudle
 * @Author: pengfei.zhou
 * @CreateDate: 2019-05-14
 */
@GlideModule
public class AnimationMoudle extends LibraryGlideModule {
    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
        super.registerComponents(context, glide, registry);
        
    }
}
