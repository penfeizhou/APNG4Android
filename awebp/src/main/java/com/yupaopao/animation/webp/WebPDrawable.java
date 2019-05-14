package com.yupaopao.animation.webp;


import com.yupaopao.animation.FrameAnimationDrawable;
import com.yupaopao.animation.decode.FrameSeqDecoder;
import com.yupaopao.animation.loader.StreamLoader;
import com.yupaopao.animation.webp.decode.WebPDecoder;

/**
 * @Description: Animated webp drawable
 * @Author: pengfei.zhou
 * @CreateDate: 2019/3/27
 */
public class WebPDrawable extends FrameAnimationDrawable {

    public WebPDrawable(StreamLoader provider) {
        super(provider);
    }

    @Override
    protected FrameSeqDecoder createFrameSeqDecoder(StreamLoader streamLoader, FrameSeqDecoder.RenderListener listener) {
        return new WebPDecoder(streamLoader, listener);
    }
}