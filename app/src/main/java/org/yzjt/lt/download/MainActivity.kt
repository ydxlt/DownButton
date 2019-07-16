package org.yzjt.lt.download

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View



class MainActivity : AppCompatActivity(), ProgressButton.OnProgressChangeListener,
    ProgressButton.OnDownloadClickListener {

    private var mProgress:Int = 0
    companion object {
        val TAG = "ProgressButton"
        const val MSG_DOWNLOAD = 1
    }

    private var mHandler = object: Handler(){
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            when(msg!!.what){
                MSG_DOWNLOAD -> {
                    mProgress++
                    if(mProgress > 100){
                        return
                    }
                    mProgressButton!!.setProgress(mProgress)
                    this.sendEmptyMessageDelayed(msg.what,100)
                }
            }
        }
    }


    override fun onClick(view: View, state: Int) {
        when(state){
            ProgressButton.STATE_PAUSE -> {
                // 暂停
                mHandler.removeMessages(MSG_DOWNLOAD)
            }
            ProgressButton.STATE_DOWNLOADING ->{
                mHandler.sendEmptyMessageDelayed(MSG_DOWNLOAD,100)
            }
            ProgressButton.STATE_DONE -> {
                Log.i(TAG,"打开")
            }
        }
    }

    override fun onProgressChange(progress: Int) {
        Log.i(TAG,"current progress:${progress}")
    }

    override fun onStateChanged(state: Int) {
        Log.i(TAG,"current state:${state}")
        if(state == ProgressButton.STATE_INSTALL){
            mHandler.postDelayed({
                mProgressButton!!.done()
            },2000)
        }
    }

    private var mProgressButton:ProgressButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mProgressButton = findViewById(R.id.progress_button)
        mProgressButton!!.setTotalSize(98 * 1024 * 1024)
            .setOnProgressChangeListener(this).setOnDownloadClickListener(this)
    }
}
