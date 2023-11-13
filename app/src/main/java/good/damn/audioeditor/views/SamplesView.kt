package good.damn.audioeditor.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.Log
import android.view.View
import kotlin.math.absoluteValue

class SamplesView(context: Context) : View(context) {

    private val TAG = "SamplesView"

    private var mIntervalSample:Int = 0
    private var mPaint: Paint = Paint()

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
        mPaint.color = 0xffaaaaaa.toInt()
        mPaint.strokeWidth = 5.0f
        mPaint.strokeCap = Paint.Cap.ROUND
        mPaint.style = Paint.Style.STROKE
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas == null) {
            return
        }

        val offsetX = width / mSamples
        var currentX = 0.0f
        var interval = 0

        val midY = height / 2.0f - mPaint.strokeWidth

        for (i in 0 until mSamples) {
            canvas.drawLine(
                currentX,
                midY,
                currentX,
                midY + mInputSamples[interval] * midY,
                mPaint)
            interval += mIntervalSample
            currentX += offsetX
        }
    }

}