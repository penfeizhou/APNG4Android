package com.yupaopao.animation.executor;

import android.os.HandlerThread;
import android.os.Looper;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Description: com.github.penfeizhou.animation.executor
 * @Author: pengfei.zhou
 * @CreateDate: 2019-11-21
 */
public class FrameDecoderExecutor {
    private static int sPoolNumber = 4;
    private ArrayList<Looper> mLooperGroup = new ArrayList<>();
    private AtomicInteger counter = new AtomicInteger(0);

    private FrameDecoderExecutor() {
    }

    static class Inner {
        static final FrameDecoderExecutor sInstance = new FrameDecoderExecutor();
    }

    public void setPoolSize(int size) {
        sPoolNumber = size;
    }

    public static FrameDecoderExecutor getInstance() {
        return Inner.sInstance;
    }

    public Looper getLooper(int taskId) {
        int idx = taskId % sPoolNumber;
        if (idx >= mLooperGroup.size()) {
            HandlerThread handlerThread = new HandlerThread("FrameDecoderExecutor-" + idx);
            handlerThread.start();
            Looper looper = handlerThread.getLooper();
            mLooperGroup.add(looper);
            return looper;
        } else {
            return mLooperGroup.get(idx);
        }
    }

    public int generateTaskId() {
        return counter.getAndIncrement();
    }
}
