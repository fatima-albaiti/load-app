package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var loadingWidth = 0f
    private var loadingAngle = 0f

    private var valueAnimator = ValueAnimator()
    private var circleAnimator = ValueAnimator()

    private var buttonColor = 0
    private var buttonTextColor = 0
    private var buttonLoadingColor = 0
    private var loadingCircleColor = 0

    private var buttonText = ""
    var buttonState: ButtonState by Delegates.observable(ButtonState.Completed) { _, _, new ->
        when(new) {
            ButtonState.Loading -> {
                load()
                loadCircle()
            }
            ButtonState.Completed -> {
                resetButton()
            }
        }
    }

    private val paint = Paint().apply {
        textSize = 55f
        textAlign = Paint.Align.CENTER
    }

    init {
        buttonState = ButtonState.Clicked
        buttonText = context.getString(R.string.download)
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            buttonColor = getColor(R.styleable.LoadingButton_buttonColor, 0)
            buttonTextColor = getColor(R.styleable.LoadingButton_buttonTextColor, 0)
            buttonLoadingColor = getColor(R.styleable.LoadingButton_buttonLoadingColor, 0)
            loadingCircleColor = getColor(R.styleable.LoadingButton_circleLoadingColor, 0)
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawButton(canvas)
        drawLoadingRect(canvas)
        drawText(canvas, buttonText)
        drawCircle(canvas)
    }

    private fun drawButton(canvas: Canvas?) {
        paint.color = buttonColor
        canvas?.drawRect(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat(), paint)
    }

    private fun drawText(canvas: Canvas?, text: String) {
        paint.color = buttonTextColor
        canvas?.drawText(text, measuredWidth.toFloat()/2, measuredHeight.toFloat()/2 - (paint.descent() + paint.ascent())/2, paint)
    }

    private fun drawLoadingRect(canvas: Canvas?) {
        paint.color = buttonLoadingColor
        canvas?.drawRect(0f, 0f, loadingWidth, measuredHeight.toFloat(), paint)
    }

    private fun drawCircle(canvas: Canvas?){
        paint.color = loadingCircleColor
        canvas?.drawArc(measuredWidth-170f, measuredHeight/2-40f, measuredWidth-80f, measuredHeight/2+40f, 0f, loadingAngle, true, paint)
    }

    private fun load(){
        buttonText = context.getString(R.string.loading)
        valueAnimator = ValueAnimator.ofFloat(0f, measuredWidth.toFloat()).apply {
            duration = 2000
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
            addUpdateListener {
                loadingWidth = animatedValue as Float
                invalidate()
            }
        }
        valueAnimator.start()
    }

    private fun loadCircle(){
        circleAnimator = ValueAnimator.ofFloat(0f, 360f).apply {
            duration = 2000
            addUpdateListener {
                repeatCount = ValueAnimator.INFINITE
                repeatMode = ValueAnimator.RESTART
                loadingAngle = animatedValue as Float
                invalidate()
            }
        }
        circleAnimator.start()
    }
    private fun resetButton(){
        loadingWidth = 0f
        loadingAngle = 0f
        buttonText = context.getString(R.string.download)
        circleAnimator.cancel()
        valueAnimator.cancel()
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

}