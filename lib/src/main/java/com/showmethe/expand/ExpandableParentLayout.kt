package com.showmethe.expand

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.AnticipateOvershootInterpolator
import android.view.animation.LinearInterpolator
import android.widget.LinearLayout
import androidx.core.view.children
import androidx.interpolator.view.animation.FastOutSlowInInterpolator

class ExpandableParentLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val duration  = 300L
    private lateinit var mainView :View
    private lateinit var expandView : ExpandableLayout
    private var isInit = false
    private val scaleDelta = 0.95f
    private var isAnimating = false
    private val interpolator  = FastOutSlowInInterpolator()
    private var animator: ValueAnimator? = null
    private var expansion = 0.0f
    private var patchOffset = 0f


    fun getScaleOffset() = patchOffset

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if(childCount > 2){
            throw IllegalStateException("childCount more than 2")
        }
        if(children.last() !is ExpandableLayout){
            throw IllegalStateException("Second ChildView should be ExpandableLayout")
        }
        mainView =  children.first()
        expandView = children.last() as ExpandableLayout

        patchOffset = mainView.measuredHeight - mainView.measuredHeight * expansion.coerceAtLeast(scaleDelta)

        mainView.scaleX = expansion.coerceAtLeast(scaleDelta)
        mainView.scaleY = expansion.coerceAtLeast(scaleDelta)
        expandView.scaleX = expansion.coerceAtLeast(scaleDelta)
    }



    private fun buildAnimator(){
        animator = ValueAnimator.ofFloat(scaleDelta, 1f)
        animator?.interpolator = interpolator
        animator?.duration = duration
        animator?.addUpdateListener(updateListener)
        animator?.addListener(listener)
    }

    private val updateListener  = ValueAnimator.AnimatorUpdateListener {
        expansion = it.animatedValue as Float
        switchState()
    }

    private fun switchState(){
        requestLayout()
    }

    private val listener = object : AnimatorListenerAdapter() {
        override fun onAnimationStart(animation: Animator?, isReverse: Boolean) {

        }
        override fun onAnimationStart(animation: Animator?) {
            isAnimating = true
        }
        override fun onAnimationEnd(animation: Animator?) {
            isAnimating = false
        }
    }

    fun toggleStateImmediately(expanded: Boolean){
        expansion = if(expanded){
            1.0f
        }else{
            scaleDelta
        }
        post {
            switchState()
            expandView.toggleStateImmediately(expanded)
        }
    }

    fun toggle(animate:Boolean = true){
        if(visibility == View.GONE|| viewTreeObserver == null || isAnimating){
            return
        }
        if(!isInit){
            buildAnimator()
            isInit = false
        }
        synchronized(this){
            if(!expandView.expanded){
                expand(animate)
            }else{
                collapse(animate)
            }
            expandView.toggle(animate)
        }
    }

    private fun expand(animate:Boolean = true) {
        if(animate){
            if (animator != null) {
                animator?.cancel()
            }
            animator?.start()
        }else{
            expansion = 0f
            switchState()
            expandView.expanded = !expandView.expanded
        }
    }

    private fun collapse(animate:Boolean = true) {
        if(animate){
            if (animator != null) {
                animator?.cancel()
            }
            animator?.reverse()
        }else{
            expansion = 1f
            switchState()
            expandView.expanded = !expandView.expanded
        }
    }

}