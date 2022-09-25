package com.udacity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0

    private var progress = 0f

    private var buttonBackgroundColor =
        ResourcesCompat.getColor(resources, R.color.colorPrimary, null)
    private var progressBackgroundColor =
        ResourcesCompat.getColor(resources, R.color.colorPrimaryDark, null)
    private var textButtonColor =
        ResourcesCompat.getColor(resources, R.color.white, null)
    private var circleColor =
        ResourcesCompat.getColor(resources, R.color.colorAccent, null)

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var buttonText: String
    private var textWidth = 0f
    private var textSize: Float = resources.getDimension(R.dimen.default_text_size)
    private var circleXOffset = textSize / 2

    private var valueAnimator = ValueAnimator()

    private var rectf = RectF(0f, 0f, textSize, textSize)

    var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when (new) {
            ButtonState.Clicked -> {

            }
            ButtonState.Loading -> {
                buttonText = resources.getString(R.string.button_loading)
                valueAnimator = ValueAnimator.ofFloat(0f, widthSize.toFloat())
                valueAnimator.duration = 2000
                valueAnimator.addUpdateListener { value ->
                    progress = value.animatedValue as Float
                    invalidate()
                }
                valueAnimator.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        buttonState = ButtonState.Completed
                    }
                })
                valueAnimator.start()
            }
            ButtonState.Completed -> {
                valueAnimator.cancel()
                progress = 0f
                buttonText = resources.getString(R.string.button_download)
                invalidate()

            }
        }
    }


    init {
        buttonText = resources.getString(R.string.button_download)
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            buttonBackgroundColor = getColor(R.styleable.LoadingButton_buttonBackgroundColor, 0)
            progressBackgroundColor = getColor(R.styleable.LoadingButton_progressBackgroundColor, 0)
            textButtonColor = getColor(R.styleable.LoadingButton_textButtonColor, 0)
            circleColor = getColor(R.styleable.LoadingButton_circleColor, 0)

        }
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        paint.color = buttonBackgroundColor
        canvas?.drawRect(0f, 0f, widthSize.toFloat(), heightSize.toFloat(), paint)

        paint.color = progressBackgroundColor
        canvas?.drawRect(0f, 0f, progress, heightSize.toFloat(), paint)

        paint.color = textButtonColor
        paint.textSize = textSize
        textWidth = paint.measureText(buttonText)
        canvas?.drawText(
            buttonText,
            widthSize / 2 - textWidth / 2,
            heightSize / 2 - (paint.descent() + paint.ascent()) / 2,
            paint
        )


        canvas?.save()
        canvas?.translate(
            widthSize / 2 + textWidth / 2 + circleXOffset,
            heightSize / 2 - textSize / 2
        )
        paint.color = circleColor
        canvas?.drawArc(rectf, 0F, progress * 0.360f, true, paint)
        canvas?.restore()
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