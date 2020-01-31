package com.boris.test.view

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.TypedValue
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import com.boris.test.R
import kotlin.math.abs
import kotlin.math.min


class CustomProgressView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
    ): View(context, attributeSet, defStyleAttr, defStyleRes) {

    private val DEFAULT_WIDTH = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2f, resources.displayMetrics)
    private val DEFAULT_COLOR = R.color.colorAccent

    private val DIFF_ANIMATION_DURATION = 1000L
    private val MIN_ANIMATION_DURATION = 300L

    private val DEFAULT_SIZE = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48f, resources.displayMetrics)

    private val circlePaint = Paint()
    private val animation = ValueAnimator()

    private var ovalRectF = RectF()
    private var size = 0

    private var currentProgress = 0f

    init {
        val typedArray = getContext().obtainStyledAttributes(attributeSet, R.styleable.CustomProgressView)
        val color = typedArray.getColor(R.styleable.CustomProgressView_cpv_color, resources.getColor(DEFAULT_COLOR))
        val strokeWidth = typedArray.getDimension(R.styleable.CustomProgressView_cpv_stroke_width, DEFAULT_WIDTH)
        typedArray.recycle()

        circlePaint.apply {
            isAntiAlias = true
            this.strokeWidth = strokeWidth
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            this.color = color
        }

        initAnimation()
    }

    private fun initAnimation() {
        animation.apply {
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener {
                currentProgress = (it.animatedValue as Float)
                invalidate()
            }
        }
    }

    override fun onDetachedFromWindow() {
        animation.cancel()
        super.onDetachedFromWindow()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        var height = 0
        var width = 0

        val desiredSize = DEFAULT_SIZE.toInt()

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = min(desiredSize, widthSize);
        } else {
            width = desiredSize
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = min(desiredSize, heightSize)
        } else {
            height = desiredSize
        }

        size = min(width, height)

        setMeasuredDimension(size, size)

        val padding = circlePaint.strokeWidth / 2
        ovalRectF.set(0 + padding, 0 + padding, size - padding, size - padding)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.drawArc(ovalRectF, 90f, 360f * currentProgress, false, circlePaint)
    }

    fun setProgress(progress: Int) {
        post {
            if (progress in 0..100) {
                animation.apply {
                    if (isRunning)
                        cancel()
                    setFloatValues(currentProgress, progress.toFloat() / 100)
                    duration =
                        (abs(currentProgress - progress / 100) * DIFF_ANIMATION_DURATION).toLong() + MIN_ANIMATION_DURATION
                    start()
                }
            } else {
                Toast.makeText(context, "Incorrect data", Toast.LENGTH_LONG).show()
            }
        }
    }
}