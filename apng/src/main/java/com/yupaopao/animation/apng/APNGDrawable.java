package com.yupaopao.animation.apng;


import com.yupaopao.animation.FrameAnimationDrawable;
import com.yupaopao.animation.apng.decode.APNGDecoder;
import com.yupaopao.animation.decode.FrameSeqDecoder;
import com.yupaopao.animation.loader.StreamLoader;

/**
 * @Description: APNGDrawable
 * @Author: pengfei.zhou
 * @CreateDate: 2019/3/27
 */
public class APNGDrawable extends FrameAnimationDrawable {
    public APNGDrawable(StreamLoader provider) {
        super(provider);
    }

    @Override
    protected FrameSeqDecoder createFrameSeqDecoder(StreamLoader streamLoader, FrameSeqDecoder.RenderListener listener) {
        return new APNGDecoder(streamLoader, listener);
    }
}
