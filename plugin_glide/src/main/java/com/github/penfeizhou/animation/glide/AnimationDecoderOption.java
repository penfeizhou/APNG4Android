package com.github.penfeizhou.animation.glide;

import com.bumptech.glide.load.Option;

/**
 * @Description: AnimationDecoderOption
 * @Author: pengfei.zhou
 * @CreateDate: 2019-06-05
 */
public final class AnimationDecoderOption {

    /**
     * If set to {@code true}, disables the Frame Animation Decoder {@link com.github.penfeizhou.animation.gif.GifDrawable}
     * Defaults to {@code false}.
     */
    public static final Option<Boolean> DISABLE_ANIMATION_GIF_DECODER = Option.memory(
            "com.github.penfeizhou.animation.glide.AnimationDecoderOption.DISABLE_ANIMATION_GIF_DECODER", false);
    /**
     * If set to {@code true}, disables the Frame Animation Decoder {@link com.github.penfeizhou.animation.webp.WebPDrawable}
     * Defaults to {@code false}.
     */
    public static final Option<Boolean> DISABLE_ANIMATION_WEBP_DECODER = Option.memory(
            "com.github.penfeizhou.animation.glide.AnimationDecoderOption.DISABLE_ANIMATION_WEBP_DECODER", false);
    /**
     * If set to {@code true}, disables the Frame Animation Decoder {@link com.github.penfeizhou.animation.apng.APNGDrawable}
     * Defaults to {@code false}.
     */
    public static final Option<Boolean> DISABLE_ANIMATION_APNG_DECODER = Option.memory(
            "com.github.penfeizhou.animation.glide.AnimationDecoderOption.DISABLE_ANIMATION_APNG_DECODER", false);

    /**
     * If set to {@code true},  call {@link com.github.penfeizhou.animation.FrameAnimationDrawable#setNoMeasure(boolean)}
     * Defaults to {@code false}.
     */
    public static final Option<Boolean> NO_ANIMATION_BOUNDS_MEASURE = Option.memory(
            "com.github.penfeizhou.animation.glide.AnimationDecoderOption.DISABLE_ANIMATION_BOUNDS_MEASURE", false);


    /**
     * If set to {@code true}, disables the Frame Animation Decoder {@link com.github.penfeizhou.animation.avif.AVIFDrawable}
     * Defaults to {@code false}.
     */
    public static final Option<Boolean> DISABLE_ANIMATION_AVIF_DECODER = Option.memory(
            "com.github.penfeizhou.animation.glide.AnimationDecoderOption.DISABLE_AVIF_DECODER", false);

    private AnimationDecoderOption() {
    }
}
