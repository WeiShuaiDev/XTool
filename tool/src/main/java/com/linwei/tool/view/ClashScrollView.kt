package com.linwei.tool.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ScrollView
import kotlin.math.abs

/**
 * HorizontalScrollView 只能横向滚动，纵向滚动释放
 */
class ClashScrollView(context: Context, attrs: AttributeSet) :
    ScrollView(context, attrs) {

    private var xDistance: Float = 0f
    private var yDistance: Float = 0f

    var xStart: Float = 0f
    var yStart: Float = 0f

    override fun onInterceptHoverEvent(event: MotionEvent): Boolean {
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

                if (xDistance > yDistance) {
                    return false
                }
            }
        }
        return super.onInterceptHoverEvent(event)
    }
}