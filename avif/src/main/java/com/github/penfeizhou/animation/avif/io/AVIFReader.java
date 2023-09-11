package com.github.penfeizhou.animation.avif.io;

import com.github.penfeizhou.animation.io.FilterReader;
import com.github.penfeizhou.animation.io.Reader;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @Author: pengfei.zhou
 * @CreateDate: 2023-09-07
 */
public class AVIFReader extends FilterReader {

    public AVIFReader(Reader reader) {
        super(reader);
    }

    private ByteBuffer cachedBuffer = null;

    public ByteBuffer toDirectByteBuffer() throws IOException {
        if (cachedBuffer == null) {
            int count = available();
            byte[] buf = new byte[count];
            read(buf, 0, count);
            cachedBuffer = ByteBuffer.allocateDirect(count);
            cachedBuffer.put(buf);
        }
        cachedBuffer.flip();
        return cachedBuffer;
    }
}
