# Animation Android
* Support APNG & Animated Webp & Gif in Android
* Efficient decoder
* Support Drawable usage and glide library module
* Support animation play control
* Support still image
* Low memory usage

## Changelog 2.1.0
* Fix handling APNG dispose to previous error.

## Changelog 2.0.0
* Android X Migration
* Upgrade Glide to 4.11

## Changelog 1.3.3
* Fix problems of small probability
    * Avoid the posibility that render a recycled bitmap
    * Fix Null point Exception when new thread
    * Avoid buffer size smaller than the target bitmap

## Changelog 1.3.1
* Fix ANR when switch visibility of animation drawable quickly.

## Changelog 1.2.0
* Fix vp8x chunk alpha in reserve digits
* Fix webp glitch error on display

## Usages

### Add dependency in build.gradle

```gradle
repositories {
    jcenter()
}
```
#### Animated WebP
```gradle
dependencies {
    implementation 'com.github.penfeizhou.android.animation:awebp:2.0.0'
}
```
#### APNG
```gradle
dependencies {
    implementation 'com.github.penfeizhou.android.animation:apng:2.0.0'
}
```
#### Gif
```gradle
dependencies {
    implementation 'com.github.penfeizhou.android.animation:gif:2.0.0'
}
```
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
    maven {
        url "https://dl.bintray.com/osborn/Android"
    }
}
dependencies {
    implementation 'com.github.penfeizhou.android.animation:glide-plugin:2.0.0'
}
```
### Direct use

```java
Glide.with(imageView).load("https://misc.aotu.io/ONE-SUNDAY/SteamEngine.png").into(imageView);
Glide.with(imageView).load("https://isparta.github.io/compare-webp/image/gif_webp/webp/2.webp").into(imageView);
```
