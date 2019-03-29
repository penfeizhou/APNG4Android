package com.yupaopao.animation.apng.chunk;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.CRC32;

/**
 * @Description: 帧每次绘制时实时从流种读取APNG原始信息并解码，速度最慢，java内存及memory占用内存都低
 * @Author: pengfei.zhou
 * @CreateDate: 2019/3/29
 */
class LowMemoryFrame extends AbstractFrame {

    LowMemoryFrame(IHDRChunk ihdrChunk, FCTLChunk fctlChunk, List<Chunk> otherChunks, int sampleSize, APNGStreamLoader streamLoader) {
        super(ihdrChunk, fctlChunk, otherChunks, sampleSize, streamLoader);
    }

    @Override
    List<IDATChunk> getChunkChain() {
        List<IDATChunk> chunkChain = new ArrayList<>();
        InputStream inputStream = null;
        try {
            inputStream = streamLoader.getInputStream();
            inputStream.skip(startPos);
            Chunk chunk;
            while ((chunk = Chunk.read(inputStream, false)) != null) {
                if (chunk instanceof IENDChunk) {
                    break;
                } else if (chunk instanceof FCTLChunk) {
                    break;
                } else if (chunk instanceof FDATChunk) {
                    chunkChain.add(new FakedIDATChunk((FDATChunk) chunk));
                } else if (chunk instanceof IDATChunk) {
                    chunkChain.add((IDATChunk) chunk);
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
        return chunkChain;
    }

    InputStream toInputStream(byte[] byteBuff) {
        List<InputStream> inputStreams = new ArrayList<>();
        InputStream signatureStream = new ByteArrayInputStream(sPNGSignatures);
        inputStreams.add(signatureStream);
        inputStreams.add(ihdrChunk.toInputStream());
        for (Chunk chunk : otherChunks) {
            inputStreams.add(chunk.toInputStream());
        }
        inputStreams.add(idatStream(byteBuff));
        inputStreams.add(new ByteArrayInputStream(sPNGEndChunk));
        return new ChainInputStream(inputStreams.toArray(new InputStream[0]));
    }

    private InputStream idatStream(byte[] byteBuff) {
        InputStream inputStream = null;
        int offset = 0;
        try {
            inputStream = streamLoader.getInputStream();
            inputStream.skip(startPos);
            while (offset < endPos - startPos) {
                inputStream.read(byteBuff, offset, 8);
                int length = byteBuff[offset + 3] & 0xFF |
                        (byteBuff[offset + 2] & 0xFF) << 8 |
                        (byteBuff[offset + 1] & 0xFF) << 16 |
                        (byteBuff[offset] & 0xFF) << 24;
                offset += 4;
                String typeCode = new String(byteBuff, offset, 4);
                offset += 4;
                if (FDATChunk.ID.equals(typeCode)) {
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

                    CRC32 crc32 = new CRC32();
                    crc32.update(byteBuff, offset - 4, 4);
                    crc32.update(byteBuff, offset, length);
                    int crc = (int) crc32.getValue();
                    offset += length;
                    byteBuff[offset++] = Chunk.readIntByByte(crc, 0);
                    byteBuff[offset++] = Chunk.readIntByByte(crc, 1);
                    byteBuff[offset++] = Chunk.readIntByByte(crc, 2);
                    byteBuff[offset++] = Chunk.readIntByByte(crc, 3);
                } else if (IDATChunk.ID.equals(typeCode)) {
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
        return new ByteArrayInputStream(byteBuff, 0, offset);
    }

    @Override
    void draw(Canvas canvas, Paint paint, Bitmap reusedBitmap, byte[] byteBuff) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inSampleSize = sampleSize;
        options.inMutable = true;
        if (reusedBitmap != null
                && !reusedBitmap.isRecycled()) {
            reusedBitmap.reconfigure(srcRect.width(), srcRect.height(), Bitmap.Config.ARGB_8888);
            reusedBitmap.eraseColor(0);
        }
        options.inBitmap = reusedBitmap;
        Bitmap bitmap = BitmapFactory.decodeStream(toInputStream(byteBuff), null, options);
        assert bitmap != null;
        canvas.drawBitmap(bitmap, srcRect, dstRect, paint);
        if (reusedBitmap != bitmap) {
            bitmap.recycle();
        }
    }
}
