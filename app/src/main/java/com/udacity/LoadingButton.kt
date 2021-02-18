package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import kotlinx.android.synthetic.main.content_main.view.*
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var rCanvas: Canvas? = Canvas()

    private var widthSize = 0
    private var heightSize = 0

    private var buttonColor = 0
    private var buttonText: String = ""

    private var valueAnimator = ValueAnimator()

    // Animation
    private var currentWidth = 0
    private var finalWidth = 1300
    private var currentSweepAngle = 0

    var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when (new) {
            ButtonState.Clicked -> {
                // Perform button animation
                ButtonState.Loading
                startLinearAnim(duration)
                startCircularAnim(duration)
                setButtonText()
            }
            ButtonState.Loading -> {
                setButtonText()
                // Disable button during loading
                custom_button.isEnabled = false
            }
            ButtonState.Completed -> {
                setButtonText()
                // Enable button after download is complete
                custom_button.isEnabled = true
            }
        }

    }

    private val paintText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        color = Color.WHITE
        textSize = 80F
    }

    private val paintButton = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val paintAnimationButton = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val paintCircle: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = ContextCompat.getColor(context, R.color.colorAccent)
    }

    init {
      context.withStyledAttributes(attrs, R.styleable.LoadingButton){
          buttonColor = getColor(R.styleable.LoadingButton_btn_loading_color, 0)
      }
      setButtonText()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        rCanvas = canvas

        paintButton.color = buttonColor
        rCanvas?.drawRect(0F, height.toFloat(), width.toFloat(), 0F, paintButton)

        when (buttonState){
            is ButtonState.Loading -> {
                // Draw button animation
                paintAnimationButton.color = resources.getColor(R.color.colorPrimaryDark)
                rCanvas?.drawRect(0F, height.toFloat(), currentWidth.toFloat(), 0F, paintAnimationButton)

                paintCircle.color = resources.getColor(R.color.colorAccent)
                rCanvas?.drawArc(
                        (widthSize - 200f),
                        (heightSize / 2) - 50f,
                        (widthSize - 100f),
                        (heightSize / 2) + 50f,
                        270F, currentSweepAngle.toFloat(),
                        true,
                        paintCircle
                )

            }

            is ButtonState.Completed -> {
                // Reset background color
                paintAnimationButton.color = resources.getColor(R.color.colorPrimary)
                rCanvas?.drawRect(0F, height.toFloat(), currentWidth.toFloat(), 0F, paintAnimationButton)
            }
        }

        // Draw Text
        rCanvas?.drawText(buttonText,
                (widthSize / 2).toFloat(),
                (height / 2).toFloat() + 30,
                paintText
        )

    }


    private fun setButtonText() {
        buttonText = when (this.buttonState) {
            ButtonState.Loading -> context.getString(R.string.button_loading)
            else -> context.getString(R.string.button_download)
        }
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

    private fun startLinearAnim(dur: Long){
        valueAnimator = ValueAnimator.ofInt(0, finalWidth).apply {
            interpolator = AccelerateDecelerateInterpolator()
            duration = dur
            addUpdateListener {
                valueAnimator ->
                currentWidth = valueAnimator.animatedValue as Int
                invalidate()
            }
        }
        valueAnimator.start()
        this.buttonState = ButtonState.Loading
    }

    private fun startCircularAnim(dur: Long){
        valueAnimator = ValueAnimator.ofInt(1, 360).apply {
            interpolator = AccelerateInterpolator()
            duration = dur
            addUpdateListener {
                valueAnimator ->
                currentSweepAngle = valueAnimator.animatedValue as Int
                invalidate()
            }
        }
        this.buttonState = ButtonState.Loading
        valueAnimator.start()
    }



}