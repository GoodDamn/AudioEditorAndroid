package good.damn.audioeditor.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.MotionEvent
import android.view.View

class SamplesView(context: Context)
        : View(context),
        View.OnTouchListener {

    private val TAG = "SamplesView"

    private var mRectCrop = Rect()

    private var mIsCropMode = false

    private var mHalfMidY = 5

    private var mDownX = 0f
    private var mDownY = 0f

    private var mIntervalSample:Int = 0
    private var mPaintSamples = Paint()
    private var mPaintCrop = Paint()

    private var mTempSamplesScale = 5
    private var mTempAmplitudeScale = 1.0f

    var mAmplitude: Float = 1.0f
        set(value) {
            field = value
            invalidate()
        }

    var mInputSamples: FloatArray = floatArrayOf(0.0f)
        set(value) {
            field = value
            mIntervalSample = field.size / mSamples
            invalidate()
        }

    var mSamples: Int = 1
        set(value) {
            field = value
            if (field <= 0) {
                field = 1
            }
            mIntervalSample = mInputSamples.size / field
            invalidate()
        }

    init {
        mPaintSamples.color = 0xffaaaaaa.toInt()
        mPaintSamples.strokeWidth = 5.0f
        mPaintSamples.strokeCap = Paint.Cap.ROUND
        mPaintSamples.style = Paint.Style.STROKE

        mPaintCrop.color = 0xFF018786.toInt()

        setOnTouchListener(this)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        mRectCrop.left = 0
        mRectCrop.right = (width * 0.35f).toInt()
        mRectCrop.top = 0
        mRectCrop.bottom = height

        mHalfMidY = (height * 0.25f).toInt()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas == null) {
            return
        }

        canvas.drawRect(mRectCrop,mPaintCrop)

        val offsetX = width / mSamples
        var currentX = 0.0f
        var interval = 0

        val midY = height / 2.0f - mPaintSamples.strokeWidth

        for (i in 0 until mSamples) {
            canvas.drawLine(
                currentX,
                midY,
                currentX,
                midY + mInputSamples[interval] * midY * mAmplitude,
                mPaintSamples)
            interval += mIntervalSample
            currentX += offsetX
        }
    }

    override fun onTouch(view: View?,
                         motion: MotionEvent?): Boolean {


        val x = motion!!.rawX.toInt()

        when (motion.action) {
            MotionEvent.ACTION_DOWN -> {

                mDownX = motion.rawX
                mDownY = motion.rawY

                if (x > mRectCrop.left && x < mRectCrop.right) {
                    mIsCropMode = true
                    return true
                }

                mTempSamplesScale = mSamples
                mTempAmplitudeScale = mAmplitude

                return true
            }
            MotionEvent.ACTION_MOVE -> {
                if (mIsCropMode) {
                    val cropWidth = mRectCrop.width()

                    mRectCrop.left = x - cropWidth/2
                    mRectCrop.right = mRectCrop.left + cropWidth
                    invalidate()
                    return true
                }

                // Scale mode and volume mode
                mSamples = mTempSamplesScale + ((motion.rawX - mDownX) * 0.1f).toInt()
                mAmplitude = mTempAmplitudeScale + (motion.rawY - mDownY) / mHalfMidY * 0.1f

                return true
            }

            MotionEvent.ACTION_UP -> {
                mIsCropMode = false
            }
        }

        return false
    }

    fun getCropLeftFraction(): Float {
        return mRectCrop.left.toFloat() / width
    }

    fun getCropRightFraction(): Float {
        return mRectCrop.right.toFloat() / width
    }
}