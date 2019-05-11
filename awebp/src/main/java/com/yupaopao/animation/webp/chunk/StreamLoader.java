package com.yupaopao.animation.webp.chunk;

import android.graphics.Rect;

import java.io.IOException;
import java.io.InputStream;

/**
 * @Description: APNG 流加载器，可实现该接口加载APNG文件
 * @Author: pengfei.zhou
 * @CreateDate: 2019/3/28
 */
public abstract class StreamLoader {
    /**
     * 打开APNG的文件流，读取完后会自动close
     */
    public abstract InputStream getInputStream() throws IOException;

    /**
     * 获取图像尺寸信息
     */
    public Rect getBounds() {
        IHDRChunk ihdrChunk = getChunk(IHDRChunk.class);
        if (ihdrChunk == null) {
            return new Rect(0, 0, 0, 0);
        } else {
            return new Rect(0, 0, ihdrChunk.width, ihdrChunk.height);
        }
    }

    /**
     * @return is webp format
     * @link {https://developers.google.com/speed/webp/docs/riff_container#webp_file_header}
     */
    public boolean isWebp() {
        InputStream inputStream = null;
        try {
            inputStream = getInputStream();
            return parseWebpHeader(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * @return is animated webp format
     * @link {https://developers.google.com/speed/webp/docs/riff_container#extended_file_format}
     */
    public boolean isAnimatedWebp() {
        InputStream inputStream = null;
        try {
            inputStream = getInputStream();
            return parseWebpHeader(inputStream) && parseAnimationHeader(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    private boolean parseAnimationHeader(InputStream inputStream) throws IOException {
        return inputStream.read() == 'V'
                && inputStream.read() == 'P'
                && inputStream.read() == '8'
                && inputStream.read() == 'X'
                && (inputStream.read() & 0x2) == 0x2;
    }

    private boolean parseWebpHeader(InputStream inputStream) throws IOException {
        return inputStream.read() == 'R'
                && inputStream.read() == 'I'
                && inputStream.read() == 'F'
                && inputStream.read() == 'F'
                && inputStream.skip(4) == 4
                && inputStream.read() == 'W'
                && inputStream.read() == 'E'
                && inputStream.read() == 'B'
                && inputStream.read() == 'P';
    }

    private <T> T getChunk(Class<? extends T> clz) {
        InputStream inputStream = null;
        try {
            inputStream = getInputStream();
            byte[] sigBytes = new byte[8];
            inputStream.read(sigBytes);
            if (sigBytes[0] != (byte) 0x89 || sigBytes[1] != (byte) 0x50
                    || sigBytes[2] != (byte) 0x4E || sigBytes[3] != (byte) 0x47
                    || sigBytes[4] != (byte) 0x0D || sigBytes[5] != (byte) 0x0A
                    || sigBytes[6] != (byte) 0x1A || sigBytes[7] != (byte) 0x0A) {
                return null;
            }
            Chunk chunk;
            while ((chunk = Chunk.read(inputStream, true)) != null) {
                if (chunk.getClass() == clz) {
                    return (T) chunk;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
