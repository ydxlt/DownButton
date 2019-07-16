package org.yzjt.library

import android.content.Context
import android.graphics.*
import android.text.TextUtils
import android.text.format.Formatter
import android.util.AttributeSet
import android.view.View

/**
 * Created by LT on 2019/7/15.
 */
open class ProgressButton(context: Context, attrs: AttributeSet) : View(context, attrs) {

    companion object {

        private val TAG = javaClass.simpleName
        const val STATE_IDLE = 0
        const val STATE_WAIT = 1
        const val STATE_DOWNLOADING = 2
        const val STATE_PAUSE = 3
        const val STATE_INSTALL = 4
        const val STATE_DONE = 5
        const val DEFAULT_CORNER = 10
        const val DEFAULT_MAX_PROGRESS = 100

        /**
         * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
         */
        fun dip2px(context: Context, dpValue: Float): Float {
            val scale = context.resources.displayMetrics.density
            return dpValue * scale + 0.5f
        }
    }

    private var mNormalColor: Int = Color.parseColor("#52b986")
    private var mProgressColor: Int = Color.parseColor("#27A768")
    private var mCorner: Int = DEFAULT_CORNER
    private var mPaint: Paint = Paint()
    private var mProgress: Int = 0
    private var mMaxProgress: Int = DEFAULT_MAX_PROGRESS
    private var mState: Int = STATE_IDLE
    private var mTotalSize: Long = 0L
    private var mTextPaint: Paint = Paint()
    private lateinit var mOnProgressChangeListener: OnProgressChangeListener
    private lateinit var mOnDownloadClickListener: OnDownloadClickListener
    private var mStateText: String = "下载"

    init {
        mPaint.isAntiAlias = true
        mPaint.color = mNormalColor
        mTextPaint.isAntiAlias = true
        mTextPaint.color = Color.WHITE
        mTextPaint.textSize = dip2px(getContext(), 14f)
        setOnClickListener {
            when (mState) {
                STATE_DONE -> {
                    if(mOnDownloadClickListener != null){
                        // 打开
                        mOnDownloadClickListener.onClick(it,mState)
                    }
                }
                STATE_WAIT,
                STATE_IDLE,
                STATE_PAUSE -> {
                    // 开始下载
                    setState(STATE_DOWNLOADING)
                    if(mOnDownloadClickListener != null){
                        mOnDownloadClickListener.onClick(it,mState)
                    }
                }
                STATE_DOWNLOADING -> {
                    // 暂停
                    setState(STATE_PAUSE)
                    if(mOnDownloadClickListener != null){
                        mOnDownloadClickListener.onClick(it,mState)
                    }
                }
                else -> {
                }
            }
        }
    }

    fun setStateText(stateText: String): ProgressButton {
        mStateText = stateText
        return this
    }

    fun setTotalSize(totalSize: Long): ProgressButton {
        mTotalSize = totalSize
        return this
    }

    fun setOnProgressChangeListener(onProgressChangeListener: OnProgressChangeListener): ProgressButton {
        this.mOnProgressChangeListener = onProgressChangeListener
        return this
    }

    fun setOnDownloadClickListener(onDownloadClickListener: OnDownloadClickListener): ProgressButton {
        this.mOnDownloadClickListener = onDownloadClickListener
        return this
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // 绘制背景
        mPaint.color = mNormalColor
        val bgRect = RectF()
        bgRect.left = 0f
        bgRect.top = 0f
        bgRect.right = width * 1f
        bgRect.bottom = height * 1f
        canvas.drawRoundRect(bgRect, mCorner.toFloat(), mCorner.toFloat(), mPaint)
        // 绘制进度
        mPaint.color = mProgressColor
        var right: Float = width * calculateProgress()
        var rect = RectF(0f, 0f, right, height.toFloat())
        canvas.drawRoundRect(rect, mCorner.toFloat(), mCorner.toFloat(), mPaint)
        if (rect.right <= width - mCorner) {
            // 擦除圆角
            canvas.drawRect(rect.right - mCorner, rect.top, rect.right, rect.bottom, mPaint)
        }
        // 绘制图标
        var progressText = ""
        var startX = 0f
        var text = ""
        var textBounds = Rect()
        var cornerPathEffect: CornerPathEffect?
        when (mState) {
            STATE_IDLE -> {
                text = "等待中"
                canvas.drawText(text, bgRect.centerX(), getBaseLineY(bgRect.centerY()), mTextPaint)
            }
            STATE_INSTALL -> {
                text = "安装中"
                canvas.drawText(text, bgRect.centerX(), getBaseLineY(bgRect.centerY()), mTextPaint)
            }
            STATE_DONE -> {
                text = "打 开"
                canvas.drawText(text, bgRect.centerX(), getBaseLineY(bgRect.centerY()), mTextPaint)
            }
            STATE_DOWNLOADING -> {
                mPaint.color = Color.WHITE
                mPaint.style = Paint.Style.FILL
                val plusWidth = 7f / 40 * height
                val plusHeight = 10f / 40 * height
                val plusPadding = 10f / 40 * height
                // 绘制当前进度百分比
                text = "$mProgress%"
                mTextPaint.getTextBounds(text, 0, text.length, textBounds)
                canvas.drawText(
                    text,
                    bgRect.centerX() + plusWidth / 2 + plusPadding / 2,
                    getBaseLineY(bgRect.centerY()),
                    mTextPaint
                )
                // 绘制暂停按钮
                cornerPathEffect = CornerPathEffect(dip2px(context, 1f))
                mPaint.pathEffect = cornerPathEffect
                startX = bgRect.centerX() - (plusWidth + plusPadding + textBounds.width().toFloat()) / 2
                canvas.drawRect(
                    startX,
                    bgRect.centerY() - plusHeight / 2,
                    startX + 2 / 7f * plusWidth,
                    bgRect.centerY() + plusHeight / 2,
                    mPaint
                )
                canvas.save()
                canvas.translate(5f / 7 * plusWidth, 0f)
                canvas.drawRect(
                    startX,
                    bgRect.centerY() - plusHeight / 2,
                    startX + 2 / 7f * plusWidth,
                    bgRect.centerY() + plusHeight / 2,
                    mPaint
                )
                canvas.restore()
            }
            STATE_PAUSE -> {
                // 开始按钮，三角形
                mPaint.color = Color.WHITE
                mPaint.style = Paint.Style.FILL
                val path = Path()
                val triangleWidth = 9f / 40 * height
                val triangleHeight = 11f / 40 * height
                val trianglePadding = 8f / 9 * triangleWidth
                progressText = "$mProgress%"
                mTextPaint.getTextBounds(progressText, 0, progressText.length, textBounds)
                startX = bgRect.centerX() - (triangleWidth + trianglePadding + textBounds.width().toFloat()) / 2
                // 绘制当前进度百分比
                canvas.drawText(
                    progressText,
                    bgRect.centerX() + triangleWidth / 2 + trianglePadding / 2,
                    getBaseLineY(bgRect.centerY()),
                    mTextPaint
                )
                // 绘制三角开始按钮
                cornerPathEffect = CornerPathEffect(dip2px(context, 3f))
                mPaint.pathEffect = cornerPathEffect
                path.moveTo(startX, bgRect.centerY() - triangleHeight / 2)
                path.lineTo(startX + triangleWidth, bgRect.centerY())
                path.lineTo(startX, bgRect.centerY() + triangleHeight / 2)
                path.lineTo(startX, bgRect.centerY() - triangleHeight / 2)
                path.lineTo(startX + triangleWidth, bgRect.centerY())
                canvas.drawPath(path, mPaint)
            }
            STATE_IDLE -> {
                // 绘制文字
                if (mTotalSize > 0) {
                    text = String.format("点击下载（%s）", Formatter.formatFileSize(context, mTotalSize))
                    val bounds = Rect()
                    mTextPaint.getTextBounds(text, 0, text.length, bounds)
                    canvas.drawText(text, bgRect.centerX(), getBaseLineY(bgRect.centerY()), mTextPaint)
                } else {
                    if (!TextUtils.isEmpty(mStateText)) {
                        canvas.drawText(mStateText, bgRect.centerX(), getBaseLineY(bgRect.centerY()), mTextPaint)
                    }
                }
            }
            else -> {
                if (!TextUtils.isEmpty(mStateText)) {
                    canvas.drawText(mStateText, bgRect.centerX(), getBaseLineY(bgRect.centerY()), mTextPaint)
                }
            }
        }
    }

    private fun getBaseLineY(centerY: Float): Float {
        mTextPaint.textAlign = Paint.Align.CENTER
        var fontMetrics = mTextPaint.fontMetrics
        return centerY - fontMetrics.top / 2 - fontMetrics.bottom / 2
    }

    fun setProgress(progress: Int) {
        var progress = progress
        if (progress >= mMaxProgress) {
            progress = mMaxProgress
        }
        if (mProgress != progress) {
            mProgress = progress
            if (mProgress == mMaxProgress) {
                // 自动发起安装
                if (mState != STATE_INSTALL) {
                    setState(STATE_INSTALL)
                }
            } else {
                if (mState != STATE_DOWNLOADING) {
                    setState(STATE_DOWNLOADING)
                }
            }
            invalidate()
            if (mOnProgressChangeListener != null) {
                mOnProgressChangeListener.onProgressChange(mProgress)
            }
        }
    }

    fun reset() {
        mProgress = 0
        setState(STATE_IDLE)
        invalidate()
    }

    fun setState(state: Int): ProgressButton {
        if(mState != state){
            mState = state
            invalidate()
            if(mOnProgressChangeListener != null){
                mOnProgressChangeListener.onStateChanged(mState)
            }
        }
        return this
    }

    fun done() {
        setState(STATE_DONE)
    }

    private fun calculateProgress(): Float {
        return mProgress * 1f / mMaxProgress
    }

    interface OnProgressChangeListener {
        fun onProgressChange(progress: Int)
        fun onStateChanged(state:Int)
    }

    interface OnDownloadClickListener {
        fun onClick(view: View, state: Int)
    }
}