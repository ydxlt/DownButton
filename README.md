1,project build.gradle 


	allprojects {
	    repositories {
	        maven { url 'https://jitpack.io' }
	    }
	}

2,app build.gradle

	 implementation 'com.github.ydxlt:DownButton:v1.0.0'
