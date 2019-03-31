package com.yupaopao.animation.apng.chunk;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.CRC32;

/**
 * @Description: 帧每次绘制时实时从流种读取APNG原始信息并解码，速度最慢，java内存及memory占用内存都低
 * @Author: pengfei.zhou
 * @CreateDate: 2019/3/29
 */
class LowMemoryFrame extends Frame {
    private CRC32 crc32 = new CRC32();
    private BitmapFactory.Options options = new BitmapFactory.Options();

    LowMemoryFrame(IHDRChunk ihdrChunk, FCTLChunk fctlChunk, List<Chunk> otherChunks, int sampleSize, APNGStreamLoader streamLoader) {
        super(ihdrChunk, fctlChunk, otherChunks, sampleSize, streamLoader);
    }

    private int generatePNGRaw(byte[] byteBuff) {
        InputStream inputStream = null;
        int offset = 0;
        System.arraycopy(sPNGSignatures, 0, byteBuff, offset, sPNGSignatures.length);
        offset += sPNGSignatures.length;

        ihdrChunk.copy(byteBuff, offset);
        offset += ihdrChunk.getRawDataLength();

        for (Chunk chunk : otherChunks) {
            chunk.copy(byteBuff, offset);
            offset += chunk.getRawDataLength();
        }

        try {
            inputStream = streamLoader.getInputStream();
            inputStream.skip(startPos);
            while (offset < endPos) {
                inputStream.read(byteBuff, offset, 8);
                int length = byteBuff[offset + 3] & 0xFF |
                        (byteBuff[offset + 2] & 0xFF) << 8 |
                        (byteBuff[offset + 1] & 0xFF) << 16 |
                        (byteBuff[offset] & 0xFF) << 24;
                offset += 4;
                int type = byteBuff[offset + 3] & 0xFF |
                        (byteBuff[offset + 2] & 0xFF) << 8 |
                        (byteBuff[offset + 1] & 0xFF) << 16 |
                        (byteBuff[offset] & 0xFF) << 24;
                offset += 4;
                if (FDATChunk.ID == type) {
                    length -= 4;
                    byteBuff[offset - 8] = Chunk.readIntByByte(length, 0);
                    byteBuff[offset - 7] = Chunk.readIntByByte(length, 1);
                    byteBuff[offset - 6] = Chunk.readIntByByte(length, 2);
                    byteBuff[offset - 5] = Chunk.readIntByByte(length, 3);
                    byteBuff[offset - 4] = 'I';
                    byteBuff[offset - 3] = 'D';
                    inputStream.skip(4);
                    inputStream.read(byteBuff, offset, length);
                    inputStream.skip(4);

                    crc32.reset();
                    crc32.update(byteBuff, offset - 4, 4);
                    crc32.update(byteBuff, offset, length);
                    int crc = (int) crc32.getValue();
                    offset += length;
                    byteBuff[offset++] = Chunk.readIntByByte(crc, 0);
                    byteBuff[offset++] = Chunk.readIntByByte(crc, 1);
                    byteBuff[offset++] = Chunk.readIntByByte(crc, 2);
                    byteBuff[offset++] = Chunk.readIntByByte(crc, 3);
                } else if (IDATChunk.ID == type) {
                    inputStream.read(byteBuff, offset, length + 4);
                    offset += length + 4;
                } else {
                    offset -= 8;
                    break;
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
        System.arraycopy(sPNGEndChunk, 0, byteBuff, offset, sPNGEndChunk.length);
        offset += sPNGEndChunk.length;
        return offset;
    }

    @Override
    Bitmap draw(Canvas canvas, Paint paint, Bitmap reusedBitmap, byte[] byteBuff) {
        options.inJustDecodeBounds = false;
        options.inSampleSize = sampleSize;
        options.inMutable = true;
        options.inBitmap = reusedBitmap;
        int length = generatePNGRaw(byteBuff);
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteBuff, 0, length, options);
        assert bitmap != null;
        canvas.drawBitmap(bitmap, srcRect, dstRect, paint);
        return bitmap;
    }
}
