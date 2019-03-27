package com.yupaopao.animation.apng;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import ar.com.hjg.pngj.PngReaderApng;

/**
 * @author jiahongyu
 * @date 18-6-19
 */
public class ApngImageUtil {

    public static int DECODE_MEMORY_RETRY_COUNT = 3;


    public static boolean isApng(File file) {
        PngReaderApng reader;
        try {
            reader = new PngReaderApng(file);
            return reader.isApng();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Png decode error : ", e.getMessage());
        }
        return false;
    }

    public static boolean isApng(InputStream inputStream) {
        PngReaderApng reader;
        try {
            reader = new PngReaderApng(inputStream);
            return reader.isApng();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Png decode error : ", e.getMessage());
        }
        return false;
    }

    public static Drawable bitmapToDrawable(String imageUri, ImageView view, Bitmap loadedBitmap) {
        ApngDrawable apngDrawable = ApngImageUtil.translateToApng(imageUri, view.getScaleType(), loadedBitmap);
        if (apngDrawable != null) {
            apngDrawable.decodePrepare();
            return apngDrawable;
        } else if (loadedBitmap != null) {
            return new BitmapDrawable(ApngLoader.getAppContext().getResources(), loadedBitmap);
        } else {
            return null;
        }
    }

    public static Drawable bitmapToDrawable(String imageUri, Bitmap loadedBitmap) {
        ApngDrawable apngDrawable = ApngImageUtil.translateToApng(imageUri, ImageView.ScaleType.CENTER_CROP, loadedBitmap);
        if (apngDrawable != null) {
            apngDrawable.decodePrepare();
            return apngDrawable;
        } else if (loadedBitmap != null) {
            return new BitmapDrawable(ApngLoader.getAppContext().getResources(), loadedBitmap);
        } else {
            return null;
        }
    }

    public static ApngDrawable translateToApng(String imageUri, ImageView.ScaleType scaleType, Bitmap loadedBitmap) {
        boolean isApng = false;
        File cacheFile = processApngFile(imageUri);
        if (cacheFile != null && cacheFile.exists()) {
            isApng = isApng(cacheFile);
        }
        if (isApng) {
            ApngDrawable drawable = new ApngDrawable(loadedBitmap, Uri.fromFile(cacheFile), scaleType);
            return drawable;
        }
        return null;
    }

    /**
     * 把apng文件拷贝到图片缓存目录, 解决assert文件的播放
     *
     * @param imageUri
     * @return
     */
    protected static File processApngFile(final String imageUri) {
        File cacheFile = new File(getFileCachePath(ApngLoader.getAppContext(), imageUri));
        if (!cacheFile.exists()) {
            Scheme scheme = Scheme.ofUri(imageUri);
            if (scheme == Scheme.ASSETS) {
                // asset资源
                try {
                    InputStream input = getStreamFromAssets(ApngLoader.getAppContext(), imageUri);
                    copyInputStreamToFile(input, cacheFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                URL source = null;
                try {
                    source = new URL(imageUri);
                    InputStream input = source.openStream();
                    copyInputStreamToFile(input, cacheFile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return cacheFile;
    }

    /**
     * 把流数据拷贝到文件里
     *
     * @param inputStream
     * @param destination
     */
    public static boolean copyInputStreamToFile(InputStream inputStream, File destination) {
        try {
            FileOutputStream outputStream = new FileOutputStream(destination, false);
            byte bt[] = new byte[1024];
            int c;
            while ((c = inputStream.read(bt)) > 0) {
                outputStream.write(bt, 0, c); // 将内容写到新文件当中
            }
            // 关闭数据流
            outputStream.close();
            inputStream.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Retrieves {@link InputStream} of image by URI (image is located in assets
     * of application).
     *
     * @param imageUri Image URI
     * @return {@link InputStream} of image
     * @throws IOException if some I/O error occurs file reading
     */
    public static InputStream getStreamFromAssets(Context appContext, String imageUri)
            throws IOException {
        String filePath = Scheme.ASSETS.crop(imageUri);
        return appContext.getAssets().open(filePath);
    }

    /**
     * 获取图片缓存目录
     *
     * @return
     */
    public static String getImageCachePath(Context appContext) {
        File externalCache = appContext.getExternalFilesDir(null);
        if (externalCache != null) {
            File cacheImg = new File(externalCache, "img");
            if (cacheImg.exists() && cacheImg.canRead() && cacheImg.canWrite()) {
                return cacheImg.getAbsolutePath();
            }
        }
        File cache = appContext.getCacheDir();
        if (cache != null) {
            File cacheImg = new File(cache, "img");
            if (cacheImg.exists()) {
                return cacheImg.getAbsolutePath();
            }
        }
        return null;
    }

    /**
     * 根据网络地址获取本地文件缓存路径
     */
    public static String getFileCachePath(Context appContext, String uri) {
        File externalCache = appContext.getExternalFilesDir(null);
        if (externalCache != null) {
            File cacheImg = new File(externalCache, "img");
            if (cacheImg.exists() || cacheImg.mkdirs()) {
                File file = new File(cacheImg, Md5.toMD5(uri.toLowerCase().trim()));
                return file.getAbsolutePath();
            }
        }

        File cache = appContext.getFilesDir();
        if (cache != null) {
            File cacheImg = new File(cache, "img");
            if (cacheImg.exists() || cacheImg.mkdirs()) {
                File file = new File(cacheImg, Md5.toMD5(uri.toLowerCase().trim()));
                return file.getAbsolutePath();
            }
        }

        return null;
    }

    /**
     * 拷贝文件
     *
     * @param fromFilePath
     * @param toFilePath
     * @param deleteExist
     * @return
     */
    public static boolean copyFile(String fromFilePath, String toFilePath,
                                   boolean deleteExist) {
        File toFile = new File(toFilePath);
        if (toFile.exists()) {
            if (deleteExist) {
                toFile.delete();
            } else {
                return true;
            }
        }
        try {
            File fromFile = new File(fromFilePath);
            if (!fromFile.exists()) {
                return false;
            }
            if (!fromFile.canRead()) {
                return false;
            }

            FileInputStream fosfrom = new FileInputStream(
                    fromFilePath);
            FileOutputStream fosto = new FileOutputStream(
                    toFilePath);
            byte bt[] = new byte[1024];
            int c;
            while ((c = fosfrom.read(bt)) > 0) {
                // 将内容写到新文件当中
                fosto.write(bt, 0, c);
            }
            // 关闭数据流
            fosfrom.close();
            fosto.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 解析文件
     *
     * @return
     * @throws Throwable
     * @throws Exception
     */
    public static Bitmap decodeFileToDrawable(String uri, Bitmap reuseBitmap) {
        String filePath = Scheme.FILE.crop(uri);
        if (TextUtils.isEmpty(filePath)) {
            return null;
        }

        // 计算图片大小
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        options.inSampleSize = 1;
        if (ApngDrawable.enableDebugLog) {
            Log.v(ApngDrawable.TAG, "current frame size:" + options.outWidth + "-" + options.outHeight);
        }

        // 复用图片
        if (reuseBitmap != null && !reuseBitmap.isRecycled() && reuseBitmap.isMutable()) {
            if (!options.inPurgeable) {
                options.inMutable = true;
            }
            int reuseSize = options.outWidth * options.outHeight * 4;
            if (reuseBitmap != null && reuseBitmap.getAllocationByteCount() >= reuseSize) {
                if (reuseBitmap.getWidth() != options.outWidth || reuseBitmap.getHeight() != options.outHeight) {
                    reuseBitmap.reconfigure(options.outWidth, options.outHeight, Bitmap.Config.ARGB_8888);
                }
                reuseBitmap.eraseColor(0);
                options.inBitmap = reuseBitmap;
            }
        }

        options.inJustDecodeBounds = false;
        options.inMutable = true;
        Bitmap decodeBitmap = null;
        int retryCount = 0;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(filePath);
            while (retryCount <= DECODE_MEMORY_RETRY_COUNT) {
                retryCount++;
                try {
                    decodeBitmap = BitmapFactory.decodeFileDescriptor(fis.getFD(), null, options);
                } catch (OutOfMemoryError e) {
                    Log.e(ApngDrawable.TAG, "OutOfMemoryError, try to decrease inSampleSize");
                    options.inSampleSize *= 2;
                    continue;
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                    // 如果是由于inBitmap选项导致的异常, 则把inBitmap选项清除后再重试
                    if (options.inBitmap != null) {
                        options.inBitmap = null;
                        continue;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                fis = null;
            }
        }
        return decodeBitmap;
    }

    public enum Scheme {
        FILE("file"),
        URL("https"),
        ASSETS("assets"),
        DRAWABLE("drawable"),
        UNKNOWN("");

        private String scheme;
        private String uriPrefix;

        Scheme(String scheme) {
            this.scheme = scheme;
            uriPrefix = scheme + "://";
        }

        /**
         * Defines scheme of incoming URI
         *
         * @param uri URI for scheme detection
         * @return Scheme of incoming URI
         */
        public static Scheme ofUri(String uri) {
            if (uri != null) {
                for (Scheme s : values()) {
                    if (s.belongsTo(uri)) {
                        return s;
                    }
                }
            }
            return UNKNOWN;
        }

        private boolean belongsTo(String uri) {
            return uri.startsWith(uriPrefix);
        }

        /**
         * Appends scheme to incoming path
         */
        public String wrap(String path) {
            return uriPrefix + path;
        }

        /**
         * Removed scheme part ("scheme://") from incoming URI
         */
        public String crop(String uri) {
            if (!belongsTo(uri)) {
                throw new IllegalArgumentException(String.format(
                        "URI [%1$s] doesn't have expected scheme [%2$s]", uri,
                        scheme));
            }
            return uri.substring(uriPrefix.length());
        }
    }
}
