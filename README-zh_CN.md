# Android 动画播放库
* 支持常用动画格式APNG、Animated WebP、Gif、AVIF
* 解码实现高效，占用内存极低
* 支持按Resource、Assets、File等多种方式读取
* 提供Glide插件，可使用Glide直接加载
* 支持动画播放过程控制
* 支持静图展示

## [版本记录](https://github.com/penfeizhou/APNG4Android/releases)

## 使用说明

### 在build.gradle添加依赖

```gradle
repositories {
    mavenCentral()
}
```
#### Animated WebP
```gradle
dependencies {
    implementation 'com.github.penfeizhou.android.animation:awebp:${VERSION}'
}
```
#### APNG
```gradle
dependencies {
    implementation 'com.github.penfeizhou.android.animation:apng:${VERSION}'
}
```
#### Gif
```gradle
dependencies {
    implementation 'com.github.penfeizhou.android.animation:gif:${VERSION}'
}
```
#### AVIF
```gradle
dependencies {
    implementation 'com.github.penfeizhou.android.animation:avif:${VERSION}'
}
```
### `使用前请注意!`
`不要将APNG资源放置到drawable或者mipmap目录下!` 在Android app release构建过程中, aapt工具会压缩修改APNG资源的帧信息, 会导致播放不正常. 因此请将APNG资源放置到工程内的`raw`或者`assets`目录内.

### 使用

```java
// 从asset file中加载
AssetStreamLoader assetLoader = new AssetStreamLoader(context, "wheel.png");


// 从resource中加载
ResourceStreamLoader resourceLoader = new ResourceStreamLoader(context, R.drawable.sample);


// 从file中加载
FileStreamLoader fileLoader = new FileStreamLoader("/sdcard/Pictures/1.webp");


// 创建APNG Drawable
APNGDrawable apngDrawable = new APNGDrawable(assetLoader);

//创建 Animated webp drawable
WebPDrawable webpDrawable = new WebPDrawable(assetLoader);

//自动播放
imageView.setImageDrawable(apngDrawable);


//可覆盖动画中设置的播放次数
apngDrawable.setLoopLimit(10);


// 实现Animatable2Compat接口
drawable.registerAnimationCallback(new Animatable2Compat.AnimationCallback() {
    @Override
    public void onAnimationStart(Drawable drawable) {
        super.onAnimationStart(drawable);
    }
});
```
## Glide插件

### build.gradle中添加依赖

```gradle
repositories {
    ...
    mavenCentral()
}
dependencies {
    implementation 'com.github.penfeizhou.android.animation:glide-plugin:${VERSION}'
}
```
### 使用Glide加载图片

```java
Glide.with(imageView).load("https://misc.aotu.io/ONE-SUNDAY/SteamEngine.png").into(imageView);
Glide.with(imageView).load("https://isparta.github.io/compare-webp/image/gif_webp/webp/2.webp").into(imageView);
```
