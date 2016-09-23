DexFixPlugin
====

[![](https://jitpack.io/v/eyyoung/DexFixPlugin.svg)](https://jitpack.io/#eyyoung/DexFixPlugin)

## Purpose

Fix "Too many classes in –main-dex-list"

## Need To Know

* Gradle Plugin Must >=2.2.0-beta2
* Avoid the Main Application Class Dep too much Class
* Not Support Instant Run Function
* JDK >= 1.8

# Install

Add Class Path

```Gradle
allprojects {
	repositories {
		...
		maven { url "https://jitpack.io" }
	}
}

dependencies {
    compile 'com.github.eyyoung:DexFixPlugin:1.0.0'
}

```

# Options

Use Custom Rules DSL

```Gradle
dexFix {
    mainDexRules './mainDexClasses.rules'
}
```

# Rules

Use Proguard Rules to Keep class in main dex and Prevent Class not found Exception

## Example 1

```
-keep public class * extends android.app.Instrumentation {
	<init>();
}
-keep public class * extends android.app.Application {
	<init>();
	void attachBaseContext(android.content.Context);
}
```

## Example 2

Keep Extend and Annotation Class

```
  -keep public class * extends com.nd.smartcan.commons.util.language.AppFactoryJSBridge {
       *;
  }

  -keep class com.nd.smartcan.commons.util.language.AppFactoryJSBridge.**{*;}

  # We need to keep all annotation classes because proguard does not trace annotation attribute
  # it just filter the annotation attributes according to annotation classes it already kept.
  -keep public class * extends java.lang.annotation.Annotation {
	*;
  }
```
