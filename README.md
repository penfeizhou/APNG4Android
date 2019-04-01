# APNG4Android
* 实现APNG格式的解码与播放，
* 实时解析，不产生临时文件
* 加载速度快
* 内存占用低
* 支持PNG静态图片
## 使用示例
```java
// 从Asset中加载
APNGAssetLoader assetLoader = new APNGAssetLoader(context, "wheel.png");
 
 
// 从Resource中加载
APNGResourceLoader resourceLoader = new APNGResourceLoader(context, R.drawable.sample);
 
 
// 从文件系统加载
APNGFileLoader fileLoader = new APNGFileLoader("/sdcard/Pictures/wheel.png");
 
 
// 创建Drawable
APNGDrawable apngDrawable = new APNGDrawable(assetLoader);
 
 
// 设置后自动播放
imageView.setImageDrawable(apngDrawable);
 
 
// 设置重复次数
apngDrawable.setLoopLimit(10);
 
 
// 已实现Animatable2Compat接口
drawable.registerAnimationCallback(new Animatable2Compat.AnimationCallback() {
    @Override
    public void onAnimationStart(Drawable drawable) {
        super.onAnimationStart(drawable);
    }
});
```
