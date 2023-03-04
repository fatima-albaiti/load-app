package com.udacity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.animation.addListener
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

    private var buttonText = "Download"
    var buttonState: ButtonState by Delegates.observable(ButtonState.Completed) { p, old, new ->
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
        color = Color.BLUE
        textSize = 55f
        textAlign = Paint.Align.CENTER
    }

    init {
        buttonState = ButtonState.Clicked
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawButton(canvas)
        drawLoadingRect(canvas)
        drawText(canvas, buttonText)
        drawCircle(canvas)
    }

    private fun drawButton(canvas: Canvas?) {
        paint.color = Color.BLUE
        canvas?.drawRect(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat(), paint)
    }

    private fun drawText(canvas: Canvas?, text: String) {
        paint.color = Color.WHITE
        canvas?.drawText(text, measuredWidth.toFloat()/2, measuredHeight.toFloat()/2 - (paint.descent() + paint.ascent())/2, paint)
    }

    private fun drawLoadingRect(canvas: Canvas?) {
        paint.color = Color.DKGRAY
        canvas?.drawRect(0f, 0f, loadingWidth, measuredHeight.toFloat(), paint)
    }

    private fun drawCircle(canvas: Canvas?){
        paint.color = Color.RED
        canvas?.drawArc(0f, 0f, 100f, 100f, 0f, loadingAngle, true, paint)
    }

    private fun load(){
        buttonText = "Loading"
        valueAnimator = ValueAnimator.ofFloat(0f, measuredWidth.toFloat()).apply {
            duration = 3000
            addUpdateListener {
                loadingWidth = animatedValue as Float
                invalidate()
            }
        }
        valueAnimator.addListener(object: AnimatorListenerAdapter(){
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                buttonState = ButtonState.Completed
            }
        })
        valueAnimator.start()
    }

    private fun loadCircle(){
        circleAnimator = ValueAnimator.ofFloat(0f, 360f).apply {
            duration = 3000
            addUpdateListener {
                loadingAngle = animatedValue as Float
                invalidate()
            }
        }
        circleAnimator.addListener(object: AnimatorListenerAdapter(){
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                buttonState = ButtonState.Completed
            }
        })
        circleAnimator.start()
    }
    private fun resetButton(){
        loadingWidth = 0f
        loadingAngle = 0f
        buttonText = "Download"
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