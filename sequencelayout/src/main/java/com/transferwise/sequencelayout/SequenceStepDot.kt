package com.transferwise.sequencelayout

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.GradientDrawable.OVAL
import android.graphics.drawable.StateListDrawable
import android.support.annotation.ColorInt
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.step_tracker_dot.view.*

class SequenceStepDot(context: Context?, attrs: AttributeSet?, defStyleAttr: Int)
    : FrameLayout(context, attrs, defStyleAttr) {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    private lateinit var pulseAnimator: AnimatorSet

    init {
        View.inflate(getContext(), R.layout.step_tracker_dot, this)

        setupAnimator()

        isEnabled = false
        clipToPadding = false
        clipChildren = false

        onFinishInflate()
    }

    fun setDotBackground(@ColorInt color: Int, @ColorInt backgroundColor: Int) {
        with(StateListDrawable()) {
            setEnterFadeDuration(resources.getInteger(R.integer.step_tracker_step_duration))
            setExitFadeDuration(resources.getInteger(R.integer.step_tracker_step_duration))

            addState(intArrayOf(android.R.attr.state_activated),
                    with(GradientDrawable()) {
                        shape = OVAL
                        setColor(color)
                        this
                    })
            addState(intArrayOf(android.R.attr.state_enabled),
                    with(GradientDrawable()) {
                        shape = OVAL
                        setColor(color)
                        setStroke(1.toDp, Color.TRANSPARENT)
                        this
                    })
            addState(intArrayOf(),
                    with(GradientDrawable()) {
                        shape = OVAL
                        setColor(backgroundColor)
                        setStroke(1.toDp, Color.TRANSPARENT)
                        this
                    })
            dotView.background = this
        }
    }

    fun setPulseColor(@ColorInt color: Int) {
        with(GradientDrawable()) {
            shape = OVAL
            setColor(color)
            pulseView.background = this
        }
    }

    private fun startAnimation() {
        if (pulseAnimator.isStarted) {
            return
        }

        pulseView.visibility = VISIBLE
        pulseAnimator.start()
    }

    private fun stopAnimation() {
        if (!pulseAnimator.isStarted) {
            return
        }
        pulseAnimator.end()
        pulseView.visibility = GONE
    }

    private fun setupAnimator() {
        pulseAnimator = AnimatorInflater.loadAnimator(context, R.animator.fading_pulse) as AnimatorSet
        pulseAnimator.setTarget(pulseView)
        pulseAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animator: Animator) {
                if (isActivated) {
                    animator.start()
                }
            }
        })
    }

    override fun setActivated(activated: Boolean) {
        super.setActivated(activated)
        if (!activated) {
            stopAnimation()
        } else {
            startAnimation()
        }
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        if (!enabled) {
            stopAnimation()
        } else {
            startAnimation()
        }
    }

    override fun onDetachedFromWindow() {
        pulseAnimator.cancel()
        super.onDetachedFromWindow()
    }
}