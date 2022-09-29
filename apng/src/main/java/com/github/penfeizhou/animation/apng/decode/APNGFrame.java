package com.github.penfeizhou.animation.apng.decode;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.github.penfeizhou.animation.apng.io.APNGReader;
import com.github.penfeizhou.animation.apng.io.APNGWriter;
import com.github.penfeizhou.animation.decode.Frame;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.CRC32;

/**
 * @Description: APNG4Android
 * @Author: pengfei.zhou
 * @CreateDate: 2019-05-13
 */
public class APNGFrame extends Frame<APNGReader, APNGWriter> {
    public final byte blend_op;
    public final byte dispose_op;
    byte[] ihdrData;
    List<Chunk> imageChunks = new ArrayList<>();
    List<Chunk> prefixChunks = new ArrayList<>();
    private static final byte[] sPNGSignatures = {(byte) 137, 80, 78, 71, 13, 10, 26, 10};
    private static final byte[] sPNGEndChunk = {0, 0, 0, 0, 0x49, 0x45, 0x4E, 0x44, (byte) 0xAE, 0x42, 0x60, (byte) 0x82};

    private static final ThreadLocal<CRC32> sCRC32 = new ThreadLocal<>();

    private CRC32 getCRC32() {
        CRC32 crc32 = sCRC32.get();
        if (crc32 == null) {
            crc32 = new CRC32();
            sCRC32.set(crc32);
        }
        return crc32;
    }

    public APNGFrame(APNGReader reader, FCTLChunk fctlChunk) {
        super(reader);
        blend_op = fctlChunk.blend_op;
        dispose_op = fctlChunk.dispose_op;
        frameDuration = fctlChunk.delay_num * 1000 / (fctlChunk.delay_den == 0 ? 100 : fctlChunk.delay_den);
        if (frameDuration < 10) {
            /*  Many annoying ads specify a 0 duration to make an image flash as quickly as  possible.
            We follow Safari and Firefox's behavior and use a duration of 100 ms for any frames that specify a duration of <= 10 ms.
            See <rdar://problem/7689300> and <http://webkit.org/b/36082> for more information.
            See also: http://nullsleep.tumblr.com/post/16524517190/animated-gif-minimum-frame-delay-browser.
            */
            frameDuration = 100;
        }
        frameWidth = fctlChunk.width;
        frameHeight = fctlChunk.height;
        frameX = fctlChunk.x_offset;
        frameY = fctlChunk.y_offset;
    }

    private int encode(APNGWriter apngWriter) throws IOException {
        int fileSize = 8 + 13 + 12;

        //prefixChunks
        for (Chunk chunk : prefixChunks) {
            fileSize += chunk.length + 12;
        }

        //imageChunks
        for (Chunk chunk : imageChunks) {
            if (chunk instanceof IDATChunk) {
                fileSize += chunk.length + 12;
            } else if (chunk instanceof FDATChunk) {
                fileSize += chunk.length + 8;
            }
        }
        fileSize += sPNGEndChunk.length;
        apngWriter.reset(fileSize);
        apngWriter.putBytes(sPNGSignatures);
        //IHDR Chunk
        apngWriter.writeInt(13);
        int start = apngWriter.position();
        apngWriter.writeFourCC(IHDRChunk.ID);
        apngWriter.writeInt(frameWidth);
        apngWriter.writeInt(frameHeight);
        apngWriter.putBytes(ihdrData);
        CRC32 crc32 = getCRC32();
        crc32.reset();
        crc32.update(apngWriter.toByteArray(), start, 17);
        apngWriter.writeInt((int) crc32.getValue());

        //prefixChunks
        for (Chunk chunk : prefixChunks) {
            if (chunk instanceof IENDChunk) {
                continue;
            }
            reader.reset();
            reader.skip(chunk.offset);
            reader.read(apngWriter.toByteArray(), apngWriter.position(), chunk.length + 12);
            apngWriter.skip(chunk.length + 12);
        }
        //imageChunks
        for (Chunk chunk : imageChunks) {
            if (chunk instanceof IDATChunk) {
                reader.reset();
                reader.skip(chunk.offset);
                reader.read(apngWriter.toByteArray(), apngWriter.position(), chunk.length + 12);
                apngWriter.skip(chunk.length + 12);
            } else if (chunk instanceof FDATChunk) {
                apngWriter.writeInt(chunk.length - 4);
                start = apngWriter.position();
                apngWriter.writeFourCC(IDATChunk.ID);

                reader.reset();
                // skip to fdat data position
                reader.skip(chunk.offset + 4 + 4 + 4);
                reader.read(apngWriter.toByteArray(), apngWriter.position(), chunk.length - 4);

                apngWriter.skip(chunk.length - 4);
                crc32.reset();
                crc32.update(apngWriter.toByteArray(), start, chunk.length);
                apngWriter.writeInt((int) crc32.getValue());
            }
        }
        //endChunk
        apngWriter.putBytes(sPNGEndChunk);
        return fileSize;
    }


    @Override
    public Bitmap draw(Canvas canvas, Paint paint, int sampleSize, Bitmap reusedBitmap, APNGWriter writer) {
        try {
            int length = encode(writer);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            options.inSampleSize = sampleSize;
            options.inMutable = true;
            options.inBitmap = reusedBitmap;
            byte[] bytes = writer.toByteArray();

            Bitmap bitmap;
            try {
                bitmap = BitmapFactory.decodeByteArray(bytes, 0, length, options);
            } catch (IllegalArgumentException e) {
                // Problem decoding into existing bitmap when on Android 4.2.2 & 4.3
                BitmapFactory.Options optionsFixed = new BitmapFactory.Options();
                optionsFixed.inJustDecodeBounds = false;
                optionsFixed.inSampleSize = sampleSize;
                optionsFixed.inMutable = true;
                bitmap = BitmapFactory.decodeByteArray(bytes, 0, length, optionsFixed);
            }

            assert bitmap != null;
            srcRect.left = 0;
            srcRect.top = 0;
            srcRect.right = bitmap.getWidth();
            srcRect.bottom = bitmap.getHeight();
            dstRect.left = (int) ((float) frameX / sampleSize);
            dstRect.top = (int) ((float) frameY / sampleSize);
            dstRect.right = (int) ((float) frameX / sampleSize + bitmap.getWidth());
            dstRect.bottom = (int) ((float) frameY / sampleSize + bitmap.getHeight());

            canvas.drawBitmap(bitmap, srcRect, dstRect, paint);
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
