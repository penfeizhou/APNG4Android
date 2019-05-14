package com.yupaopao.animation.apng;


import android.content.Context;

import com.yupaopao.animation.loader.ResourceStreamLoader;

/**
 * @Author: pengfei.zhou
 * @CreateDate: 2019/3/28
 * @see com.yupaopao.animation.loader.ResourceStreamLoader use this insted
 */
@Deprecated
public class APNGResourceLoader extends ResourceStreamLoader {
    public APNGResourceLoader(Context context, int resId) {
        super(context, resId);
    }
}
