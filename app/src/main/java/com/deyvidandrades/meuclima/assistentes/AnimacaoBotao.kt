package com.deyvidandrades.meuclima.assistentes

import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.Interpolator
import com.deyvidandrades.meuclima.R
import kotlin.math.cos
import kotlin.math.pow

object AnimacaoBotao : Interpolator {

    override fun getInterpolation(time: Float): Float {
        return (-1 * Math.E.pow(-time / .1) * cos(.1 * time) + 1).toFloat()
    }

    fun animar(view: View) {
        val myAnim: Animation = AnimationUtils.loadAnimation(view.context, R.anim.image_click)
        myAnim.interpolator = this
        view.startAnimation(myAnim)
    }
}