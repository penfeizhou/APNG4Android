package com.yupaopao.apng;

/**
 * @author jiahongyu
 * @description
 * @date 18-6-19
 */
public interface ApngPlayListener {
    void onAnimationStart(ApngDrawable drawable);

    void onAnimationEnd(ApngDrawable drawable);

    void onAnimationFailed();
}
