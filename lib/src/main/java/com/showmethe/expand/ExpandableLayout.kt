package com.showmethe.expand

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.children
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import kotlin.math.roundToInt


class ExpandableLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var isInit = false
    private val duration  = 300L
    private val interpolator  = FastOutSlowInInterpolator()
    private var animator: ValueAnimator? = null
    var expanded = false
    private var isAnimating = false
    private lateinit var childView :View
    private var expansion = 0f
    private var state  = "state"
    private  val expansion_state  = "expansion"
    private  val super_state  ="super_state "
    private var patchOffset = 0f
    private var expansionDelta = 0f

    override fun onSaveInstanceState(): Parcelable? {
        val superState  = super.onSaveInstanceState()
        val bundle = Bundle()
        bundle.putFloat(expansion_state,expansion)
        bundle.putBoolean(state,expanded)
        bundle.putParcelable(super_state, superState)
        return bundle
    }

    override fun onRestoreInstanceState(parcelable: Parcelable) {
        val bundle = parcelable as Bundle
        expansion = bundle.getFloat(expansion_state)
        expanded = bundle.getBoolean(state)
        val superState = bundle.getParcelable<Parcelable>(super_state)
        super.onRestoreInstanceState(superState)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if(childCount>1){
            throw IllegalStateException("childCount more than 1")
        }

        childView = children.first()
        if(parent is ExpandableParentLayout){
            patchOffset = (parent as ExpandableParentLayout).getScaleOffset()
        }


        expansionDelta = measuredHeight - (measuredHeight * expansion)
        childView.translationY = - expansionDelta
        translationY = - patchOffset
        setMeasuredDimension(measuredWidth, (measuredHeight - expansionDelta).roundToInt())
    }


    override fun onConfigurationChanged(newConfig: Configuration?) {
        animator?.cancel()
        super.onConfigurationChanged(newConfig)
    }


    fun toggleStateImmediately(expanded: Boolean){
        expansion = if(expanded){
            1.0f
        }else{
            0.0f
        }
        this.expanded = expanded
        onStateChangeListener?.invoke(expanded)
        post {
            visibility = if(expanded){
                View.VISIBLE
            }else{
                View.INVISIBLE
            }
            requestLayout()
        }
    }


    fun toggle(animate:Boolean = true){
        if(visibility == View.GONE || isAnimating){
            return
        }
        if(!isInit){
            buildAnimator()
            isInit = false
        }
        synchronized(this){
            if(!expanded){
                // 展开
                expand(animate)
            }else{
                //收回
                collapse(animate)
            }
        }
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
            if(!expanded){
                visibility = View.VISIBLE
            }
        }
        override fun onAnimationStart(animation: Animator?) {
            isAnimating = true
        }
        override fun onAnimationEnd(animation: Animator?) {
            isAnimating = false
            expanded = !expanded
            if(!expanded){
                visibility = View.INVISIBLE
            }
            onStateChangeListener?.invoke(expanded)
        }
    }

    private fun buildAnimator(){
        animator = ValueAnimator.ofFloat(0f, 1f)
        animator?.interpolator = interpolator
        animator?.duration = duration
        animator?.addUpdateListener(updateListener)
        animator?.addListener(listener)
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
            expanded = !expanded
            onStateChangeListener?.invoke(expanded)
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
            expanded = !expanded
            onStateChangeListener?.invoke(expanded)
        }
    }

    private var onStateChangeListener : ((expand:Boolean)->Unit)? = null
    fun setOnStateChangeListener(onStateChangeListener : ((expand:Boolean)->Unit)){
        this.onStateChangeListener = onStateChangeListener
    }


}