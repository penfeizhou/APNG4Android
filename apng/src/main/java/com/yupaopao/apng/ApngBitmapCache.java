package com.yupaopao.apng;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author jiahongyu
 * @description * 专属于apng解码使用的Bitmap缓存, 极度的内存控制。缓存池＋复用池一般在4帧左右
 * 1. 有效控制缓存大小. 只存储解码所需的缓存帧(在apng解包时根据op类型, 可以计算出解码过程中最多需要的缓存数), 理论上最少缓存帧数为1帧, 一般2-3帧就可以了
 * 2. 复用Bitmap. 所有使用Bitmap的地方均从bitmap复用池中获取, 不再需要的Bitmap也都放入复用池中。从目前的测试结果来看, 复用池的大小一般在1-2之间变化
 */

public class ApngBitmapCache {

    private final static String TAG = ApngDrawable.TAG + "_cache";

    private final ReentrantLock lock = new ReentrantLock();

    private int maxCacheSize = 2;
    private Map<Integer, Bitmap> bitmapCache = new HashMap<>();
    private Set<Bitmap> bitmapReuse = new HashSet<>();

    public void clear() {
        bitmapCache.clear();
        bitmapReuse.clear();
    }

    public int getMaxCacheSize() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return maxCacheSize;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 设置最大的缓存池大小, 动态计算缓存池的大小, 以便刚好满足当前apng解码需要的使用
     */
    void setMaxCacheSize(int maxCacheSize) {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            this.maxCacheSize = maxCacheSize;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 缓存图片帧
     */
    void cacheBitmap(int frameIndex, Bitmap bitmap) {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            if (bitmap == null) {
                return;
            }
            if (ApngDrawable.enableDebugLog) {
                Log.v(TAG, "save cache " + frameIndex);
            }

            if (frameIndex == 0) {
                bitmapCache.clear();
            } else if (bitmapCache.size() >= maxCacheSize) {
                Iterator<Integer> indexIter = bitmapCache.keySet().iterator();
                while (indexIter.hasNext()) {
                    int cacheIndex = indexIter.next();
                    if (cacheIndex > frameIndex || frameIndex >= (cacheIndex + maxCacheSize)) {
                        Bitmap overCacheBitmap = bitmapCache.get(cacheIndex);
                        indexIter.remove();
                        reuseBitmap(overCacheBitmap);
                    }
                }
            }
            bitmapCache.put(frameIndex, bitmap);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 从缓存中获取对应帧
     */
    Bitmap getCacheBitmap(int frameIndex) {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            Bitmap cacheBitmap = bitmapCache.get(frameIndex);
            if (cacheBitmap == null) {
                if (ApngDrawable.enableDebugLog) {
                    Log.v(TAG, "can't get cache for frame " + frameIndex);
                }
            }
            return cacheBitmap;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 缓存中是否保存此Bitmap, 一般在复用Bitmap时进行判断, 如果Bitmap仍存在缓存中时不允许复用
     */
    boolean cacheContain(Bitmap bitmap) {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return bitmapCache.containsValue(bitmap);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 把bitmap放入复用池中
     */
    void reuseBitmap(Bitmap bitmap) {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            if (bitmap != null && !cacheContain(bitmap)) {
                bitmapReuse.add(bitmap);
            }
            if (ApngDrawable.enableDebugLog) {
                Log.v(TAG, "add a reuse, cache size:" + bitmapReuse.size());
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * 从复用池中取一个bitmap
     */
    Bitmap getReuseBitmap(int width, int height) {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            Bitmap bitmap = null;
            if (ApngDrawable.enableDebugLog) {
                Log.v(TAG, "get a reuse, cache size:" + bitmapReuse.size());
            }
            Iterator<Bitmap> iter = bitmapReuse.iterator();
            while (iter.hasNext()) {
                int reuseSize = width * height * 4;
                bitmap = iter.next();
                if (bitmap != null && bitmap.getAllocationByteCount() >= reuseSize) {
                    iter.remove();
                    if (bitmap.getWidth() != width || bitmap.getHeight() != height) {
                        bitmap.reconfigure(width, height, Bitmap.Config.ARGB_8888);
                    }
                    bitmap.eraseColor(0);
                    return bitmap;
                }
            }

            // 没有可用的, 则创建新的
            try {
                Bitmap.Config config = Bitmap.Config.ARGB_8888;
                bitmap = Bitmap.createBitmap(width, height, config);
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }
            return bitmap;
        } finally {
            lock.unlock();
        }
    }
}
