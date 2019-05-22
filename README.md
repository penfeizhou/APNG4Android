# Animation Android
* Support APNG & Animated Webp & Gif in Android
* Efficient decoder
* Support Drawable usage and glide library module
* Support animation play control
* Support still image
* Low memory usage

## Usages

### Add dependency in build.gradle

```gradle
repositories {
        maven {
            url "https://dl.bintray.com/osborn/Android"
        }
}
dependencies {
    implementation 'com.yupaopao.android.animation:awebp:0.2.0'
    implementation 'com.yupaopao.android.animation:apng:0.2.1'
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
    implementation 'com.yupaopao.android.animation:glide-plugin:0.2.0'
}
```
### Direct use

```java
Glide.with(imageView).load("https://misc.aotu.io/ONE-SUNDAY/SteamEngine.png").into(imageView);
Glide.with(imageView).load("https://isparta.github.io/compare-webp/image/gif_webp/webp/2.webp").into(imageView);
```