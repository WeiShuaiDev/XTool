package com.linwei.tool.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.HorizontalScrollView
import kotlin.math.abs

class ClashHorizontalScrollView(context: Context, attrs: AttributeSet) :
    HorizontalScrollView(context, attrs) {

    private var xDistance: Float = 0f
    private var yDistance: Float = 0f

    var xStart: Float = 0f
    var yStart: Float = 0f

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        val xEnd: Float
        val yEnd: Float
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                xDistance = 0f
                yDistance = 0f
                xStart = event.x
                yStart = event.y
            }

            MotionEvent.ACTION_MOVE -> {
                xEnd = event.x
                yEnd = event.y
                xDistance += abs(xEnd - xStart)
                yDistance += abs(yEnd - yStart)
                xStart = xEnd
                yStart = yEnd

                if (yDistance > xDistance) {
                    parent.requestDisallowInterceptTouchEvent(false)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }
}