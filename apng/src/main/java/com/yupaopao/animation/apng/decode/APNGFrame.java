package com.yupaopao.animation.apng.decode;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.yupaopao.animation.apng.io.APNGReader;
import com.yupaopao.animation.apng.io.APNGWriter;
import com.yupaopao.animation.decode.Frame;

import java.io.ByteArrayInputStream;
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
    List<Chunk> otherChunks = new ArrayList<>();
    private static final byte[] sPNGSignatures = {(byte) 137, 80, 78, 71, 13, 10, 26, 10};
    private static CRC32 crc32 = new CRC32();

    public APNGFrame(APNGReader reader, FCTLChunk fctlChunk) {
        super(reader);
        blend_op = fctlChunk.blend_op;
        dispose_op = fctlChunk.dispose_op;
        frameDuration = fctlChunk.delay_num * 1000 / (fctlChunk.delay_den == 0 ? 100 : fctlChunk.delay_den);
        frameWidth = fctlChunk.width;
        frameHeight = fctlChunk.height;
        frameX = fctlChunk.x_offset;
        frameY = fctlChunk.y_offset;
    }

    private int encode(APNGWriter apngWriter) throws IOException {
        int fileSize = 8 + 13 + 12;

        //otherChunks
        for (Chunk chunk : otherChunks) {
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

        apngWriter.reset(fileSize);
        apngWriter.putBytes(sPNGSignatures);
        //IHDR Chunk
        apngWriter.writeInt(13);
        int start = apngWriter.position();
        apngWriter.writeFourCC(IHDRChunk.ID);
        apngWriter.writeInt(frameWidth);
        apngWriter.writeInt(frameHeight);
        apngWriter.putBytes(ihdrData);
        crc32.reset();
        crc32.update(apngWriter.toByteArray(), start, 17);
        apngWriter.writeInt((int) crc32.getValue());

        //otherChunks
        for (Chunk chunk : otherChunks) {
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
                reader.reset();
                reader.skip(chunk.offset + 4);
                apngWriter.writeInt(chunk.length - 4);
                start = apngWriter.position();
                apngWriter.writeFourCC(IDATChunk.ID);
                reader.read(apngWriter.toByteArray(), apngWriter.position(), chunk.length - 4);
                apngWriter.skip(chunk.length - 4);
                crc32.reset();
                crc32.update(apngWriter.toByteArray(), start, chunk.length);
                apngWriter.writeInt((int) crc32.getValue());
            }
        }
        //endChunk
        for (Chunk chunk : otherChunks) {
            if (chunk instanceof IENDChunk) {
                reader.reset();
                reader.skip(chunk.offset);
                reader.read(apngWriter.toByteArray(), apngWriter.position(), chunk.length + 12);
                break;
            }
        }
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
            APNGReader apngReader = new APNGReader(new ByteArrayInputStream(bytes));
            APNGParser.parse(apngReader);
            try {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, length, options);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, length, options);
            assert bitmap != null;
            canvas.drawBitmap(bitmap, (float) frameX * 2 / sampleSize, (float) frameY * 2 / sampleSize, paint);
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
