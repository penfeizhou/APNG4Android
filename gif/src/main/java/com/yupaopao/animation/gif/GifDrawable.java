package com.yupaopao.animation.gif;

import com.yupaopao.animation.FrameAnimationDrawable;
import com.yupaopao.animation.decode.FrameSeqDecoder;
import com.yupaopao.animation.loader.Loader;

/**
 * @Description: GifDrawable
 * @Author: pengfei.zhou
 * @CreateDate: 2019-05-16
 */
public class GifDrawable extends FrameAnimationDrawable {
    public GifDrawable(Loader provider) {
        super(provider);
    }

    @Override
    protected FrameSeqDecoder createFrameSeqDecoder(Loader streamLoader, FrameSeqDecoder.RenderListener listener) {
        return null;
    }
}
