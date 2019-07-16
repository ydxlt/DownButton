[![](https://jitpack.io/v/ydxlt/DownButton.svg)](https://jitpack.io/#ydxlt/DownButton)

Step1: Add the JitPack repository to your project build.gradle 


	allprojects {
	    repositories {
	        maven { url 'https://jitpack.io' }
	    }
	}

Step2:Add the dependency to your app build.gradle

	 implementation 'com.github.ydxlt:DownButton:v1.0.0'
	 
## 演示效果

![https://github.com/ydxlt/DownButton/blob/master/images/Picture.gif](https://github.com/ydxlt/DownButton/blob/master/images/Picture.gif)


## 使用步骤

1： xml布局

	<org.yzjt.library.ProgressButton android:layout_width="match_parent" 	android:layout_height="40dp"
	 	android:id="@+id/progress_button"
	 	android:layout_margin="12dp"/>

2：初始化View

	mProgressButton!!.setTotalSize(98 * 1024 * 1024)
	            .setOnProgressChangeListener(this)
	            .setOnDownloadClickListener(this) 

3：监听回调中根据State参数做相应的操作


