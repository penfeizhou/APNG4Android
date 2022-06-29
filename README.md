# Animation Android
* Support APNG & Animated Webp & Gif in Android
* Efficient decoder
* Support Drawable usage and glide library module
* Support animation play control
* Support still image
* Low memory usage

## [中文文档](https://github.com/penfeizhou/APNG4Android/blob/master/README-zh_CN.md)

## [Released versions](https://github.com/penfeizhou/APNG4Android/releases) ![](https://img.shields.io/maven-central/v/com.github.penfeizhou.android.animation/glide-plugin)

## Usages

### Add dependency in build.gradle

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

### `Notice Before Use!`
`Don't put APNG resources in your drawable or mipmap directory!` During the process of release building of an Android app, the aapt tool will zip & modify the frame info of the APNG file, which will lead to an abnormal behavior when playing it. Thus, please put the APNG resources in `raw` or `assets` folder instead.

### Use

```java
// Load from asset file
AssetStreamLoader assetLoader = new AssetStreamLoader(context, "wheel.png");


// Load form Resource
ResourceStreamLoader resourceLoader = new ResourceStreamLoader(context, R.drawable.sample);


// Load from file
FileStreamLoader fileLoader = new FileStreamLoader("/sdcard/Pictures/1.webp");


// Create APNG Drawable
APNGDrawable apngDrawable = new APNGDrawable(assetLoader);

//Create Animated webp drawable
WebPDrawable webpDrawable = new WebPDrawable(assetLoader);

// Auto play
imageView.setImageDrawable(apngDrawable);


// Not needed.default controlled by content
apngDrawable.setLoopLimit(10);


// Implement Animatable2Compat
drawable.registerAnimationCallback(new Animatable2Compat.AnimationCallback() {
    @Override
    public void onAnimationStart(Drawable drawable) {
        super.onAnimationStart(drawable);
    }
});
```
## Glide support

### Add dependency in build.gradle

```gradle
repositories {
    ...
    mavenCentral()
}
dependencies {
    implementation 'com.github.penfeizhou.android.animation:glide-plugin:${VERSION}'
}
```
### Direct use

```java
Glide.with(imageView).load("https://misc.aotu.io/ONE-SUNDAY/SteamEngine.png").into(imageView);
Glide.with(imageView).load("https://isparta.github.io/compare-webp/image/gif_webp/webp/2.webp").into(imageView);
```
### Welcome to join the talk
![image](https://user-images.githubusercontent.com/9526211/176335782-65f28250-b595-456b-8ee9-4b1c5139acba.png)
