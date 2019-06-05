package com.yupaopao.animation.glide;

import com.bumptech.glide.load.Option;

/**
 * @Description: AnimationDecoderOption
 * @Author: pengfei.zhou
 * @CreateDate: 2019-06-05
 */
public final class AnimationDecoderOption {

    /**
     * If set to {@code true}, disables the Frame Animation Decoder {@link com.yupaopao.animation.FrameAnimationDrawable}
     * Defaults to {@code false}.
     */
    public static final Option<Boolean> DISABLE_ANIMATION_DECODER = Option.memory(
            "com.yupaopao.animation.glide.AnimationDecoderOption.DISABLE_ANIMATION_DECODER", false);

    private AnimationDecoderOption() {
    }
}
