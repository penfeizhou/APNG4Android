package com.github.pengfeizhou.animation.glide;

import com.bumptech.glide.load.Option;

/**
 * @Description: AnimationDecoderOption
 * @Author: pengfei.zhou
 * @CreateDate: 2019-06-05
 */
public final class AnimationDecoderOption {

    /**
     * If set to {@code true}, disables the Frame Animation Decoder {@link com.github.pengfeizhou.animation.gif.GifDrawable}
     * Defaults to {@code true}.
     */
    public static final Option<Boolean> DISABLE_ANIMATION_GIF_DECODER = Option.memory(
            "com.github.pengfeizhou.animation.glide.AnimationDecoderOption.DISABLE_ANIMATION_GIF_DECODER", true);
    /**
     * If set to {@code true}, disables the Frame Animation Decoder {@link com.github.pengfeizhou.animation.webp.WebPDrawable}
     * Defaults to {@code false}.
     */
    public static final Option<Boolean> DISABLE_ANIMATION_WEBP_DECODER = Option.memory(
            "com.github.pengfeizhou.animation.glide.AnimationDecoderOption.DISABLE_ANIMATION_WEBP_DECODER", false);
    /**
     * If set to {@code true}, disables the Frame Animation Decoder {@link com.github.pengfeizhou.animation.apng.APNGDrawable}
     * Defaults to {@code false}.
     */
    public static final Option<Boolean> DISABLE_ANIMATION_APNG_DECODER = Option.memory(
            "com.github.pengfeizhou.animation.glide.AnimationDecoderOption.DISABLE_ANIMATION_APNG_DECODER", false);


    private AnimationDecoderOption() {
    }
}
