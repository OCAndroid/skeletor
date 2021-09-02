# Skeletor

> Generate skeleton screens in an Android app.

Note: This is a fork of [ericktijerou/koleton](https://github.com/ericktijerou/koleton).
The library was forked at: `v1.0.0-beta01`

[![Skletor Release](https://jitpack.io/v/OCAndroid/skeletor.svg)](https://jitpack.io/#OCAndroid/skeletor)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.5.10-blueviolet.svg)](https://kotlinlang.org)
![Minimum SDK Version](https://img.shields.io/badge/minSdkVersion-14-brightgreen.svg)

An Android library that provides an easy way to show skeleton of any view.

![img](image/journey_list.gif)
![img](image/paging.gif)
![img](image/journey_detail.gif)

## Installation

```
dependencies {
    implementation "com.github.OCAndroid:skeletor:${skeletor_version}"
}
```

## Quick Start
To load the skeleton of a `View`, use the `loadSkeleton` extension function:

```kotlin
// ConstraintLayout
constraintLayout.loadSkeleton()

// TextView
textView?.loadSkeleton(length = 20)

// RecyclerView
recyclerView.loadSkeleton(R.layout.item_example)
```

Skeletons can be configured with an optional trailing lambda:

```kotlin
// ConstraintLayout
constraintLayout.loadSkeleton {
    color(R.color.colorSkeleton)
    cornerRadius(radiusInPixel)
    shimmer(false)
    lineSpacing(spacingInPixel)
}

// TextView
textView?.loadSkeleton(length = 20) {
    color(R.color.colorSkeleton)
    ...
}

// RecyclerView
recyclerView.loadSkeleton(R.layout.item_example) {
    itemCount(3)
    ...
}
```

To hide the skeleton, use the `hideSkeleton` extension function:

```kotlin
view.hideSkeleton()
```

### SkeletonLoader
Skeletor will lazily create a `SkeletonLoader` with the default values.
If you want to set the default values, you can set a default `SkeletonLoader` instance by either:

```kotlin
// In your Application
class MyApplication : Application(), SkeletonLoaderFactory {

    override fun newSkeletonLoader(): SkeletonLoader {
        return SkeletonLoader.Builder(this)
            ...
            .color(R.color.colorSkeleton)
            .cornerRadius(radiusInPixel)
            .build()
    }
}
```

Or calling `Skeletor.setSkeletonLoader`
```kotlin
val skeletonLoader = SkeletonLoader.Builder(context)
    ...
    .color(R.color.colorSkeleton)
    .cornerRadius(radiusInPixel)
    .build()
Skeletor.setSkeletonLoader(skeletonLoader)
```

The default `SkeletonLoader` can be retrieved like so:
```kotlin
val skeletonLoader = Skeletor.skeletonLoader(context)
```

### Paging
Skeletor works well with `Paging` library with the help of `SkeletorView`.

If you want to show a skeleton when you scroll to the bottom of your list, use the `generateSkeleton` extension function:

```kotlin
// In your PagedListAdapter
override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sample, parent, false)
    return when (viewType) {
        ...
        TYPE_SKELETON -> SkeletonViewHolder(view.generateSkeleton())
        ...
    }
}

...

class SkeletonViewHolder(val skeletorView: SkeletorView) : RecyclerView.ViewHolder(skeletorView) {
    fun showSkeleton() {
        skeletorView.showSkeleton()
    }
}
```

### Shimmer effect
Skeletor works with Facebook’s [shimmer](https://github.com/facebook/shimmer-android) library. If you want to create a custom shimmer effect, you need to include in your dependencies:

```gradle
// In your module's `build.gradle.kts`
dependencies {
    implementation("com.facebook.shimmer:shimmer:0.5.0")
}
```

And set the custom shimmer in the lambda expression:

```kotlin
constraintLayout.loadSkeleton {
    val customShimmer = Shimmer.AlphaHighlightBuilder()
        .setDirection(Shimmer.Direction.TOP_TO_BOTTOM)
        ...
        .build()
    shimmer(customShimmer)
}
```

![img](image/custom_shimmer.gif)

You can find more information about Facebook's shimmer effect on the [shimmer-android](http://facebook.github.io/shimmer-android) page.

## Contributions

Constributions are welcomed, however please see if the original project 
at [https://github.com/ericktijerou/koleton](ericktijerou/koleton) to see
if fits your needs. At the time of this writing(09/2021), this fork is 
almost identical to the original.

## License

```
Copyright 2020-2021 ericktijerou, Bladymir Tellez<btellez@gmail.com>

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
