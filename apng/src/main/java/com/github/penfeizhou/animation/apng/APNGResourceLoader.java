package com.github.penfeizhou.animation.apng;


import android.content.Context;

import com.github.penfeizhou.animation.loader.ResourceStreamLoader;

/**
 * @Author: pengfei.zhou
 * @CreateDate: 2019/3/28
 * @see com.github.penfeizhou.animation.loader.ResourceStreamLoader use this insted
 */
@Deprecated
public class APNGResourceLoader extends ResourceStreamLoader {
    public APNGResourceLoader(Context context, int resId) {
        super(context, resId);
    }
}
