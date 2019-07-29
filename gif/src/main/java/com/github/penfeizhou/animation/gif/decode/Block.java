package com.github.penfeizhou.animation.gif.decode;

import com.github.penfeizhou.animation.gif.io.GifReader;

import java.io.IOException;

/**
 * @Description: Block
 * @Author: pengfei.zhou
 * @CreateDate: 2019-05-16
 */
public interface Block {
    void receive(GifReader reader) throws IOException;

    int size();
}
