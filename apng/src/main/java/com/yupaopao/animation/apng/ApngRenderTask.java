package com.yupaopao.animation.apng;

import android.graphics.Bitmap;
import android.os.SystemClock;
import android.util.Log;

import java.util.concurrent.TimeUnit;

/**
 * @author jiahongyu
 * @description 负责调度每帧的解码和绘制
 * @date 18-6-19
 */
public class ApngRenderTask implements Runnable {
    private ApngDrawable apngDrawable;
    private ApngFrameDecode apngDecode;

    public ApngRenderTask(ApngDrawable apngDrawable, ApngFrameDecode apngDecode) {
        this.apngDrawable = apngDrawable;
        this.apngDecode = apngDecode;
    }

    @Override
    public void run() {
        int nextFrame = apngDrawable.currentFrame;
        if (nextFrame >= apngDecode.frameCount) {
            if (apngDrawable.needRepeat()) {
                apngDrawable.currentFrame = 0;
                nextFrame = 0;
            } else {
                return;
            }
        }
        if (ApngDrawable.enableDebugLog) {
            Log.v(ApngDrawable.TAG, "render frame:" + nextFrame);
        }
        long startTime = SystemClock.uptimeMillis();
        // 创建
        Bitmap bitmap = apngDecode.createFrameBitmap(nextFrame);
        if (apngDrawable.frameBp != null && apngDrawable.frameBp != bitmap) {
            apngDrawable.bitmapCache.reuseBitmap(apngDrawable.frameBp);
        }
        apngDrawable.frameBp = bitmap;
        long takeTime = SystemClock.uptimeMillis() - startTime;
        int delay = apngDecode.getFrameDelay(nextFrame);
        if (ApngDrawable.enableDebugLog) {
            Log.v(ApngDrawable.TAG, "frame delay:" + delay + ", takeTime:" + takeTime + ", real delay:" + (delay - takeTime));
        }
        // 把解码耗的时间减掉
        delay -= takeTime;

        // 定时下一次任务
        apngDrawable.excutor.schedule(this, delay, TimeUnit.MILLISECONDS);

        // 通知ui刷新
        if (apngDrawable.isVisible() && apngDrawable.isRunning() && !apngDrawable.invalidationHandler.hasMessages(0)) {
            apngDrawable.invalidationHandler.sendEmptyMessageAtTime(0, 0);
        }
    }
}
