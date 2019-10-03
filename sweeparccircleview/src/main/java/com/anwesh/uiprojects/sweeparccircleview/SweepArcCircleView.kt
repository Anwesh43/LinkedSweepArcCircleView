package com.anwesh.uiprojects.sweeparccircleview

/**
 * Created by anweshmishra on 03/10/19.
 */

import android.view.View
import android.view.MotionEvent
import android.app.Activity
import android.content.Context
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Canvas
import android.graphics.Color

val nodes : Int = 5
val parts : Int = 2
val scGap : Float = 0.01f
val delay : Long = 20
val strokeFactor : Int = 90
val sizeFactor : Float = 2.9f
val rFactor : Float = 2.8f
val foreColor : Int = Color.parseColor("#673AB7")
val backColor : Int = Color.parseColor("#BDBDBD")

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n

fun Canvas.drawSweepArcCircle(i : Int, sc : Float, size : Float, paint : Paint) {
    val deg : Float = 90f * sc.divideScale(i, parts)
    val r : Float = size / rFactor
    save()
    scale(1f - 2 * i, 1f)
    save()
    rotate(deg)
    paint.style = Paint.Style.FILL
    drawCircle(0f, -size, r, paint)
    restore()
    paint.style = Paint.Style.STROKE
    drawArc(RectF(-r, -r, r, r), 270f, deg, false, paint)
    restore()
}

fun Canvas.drawSweepArcCircles(sc : Float, size : Float, paint : Paint) {
    for (j in 0..(parts - 1)) {
        drawSweepArcCircle(j, sc, size, paint)
    }
}

fun Canvas.drawSACNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    val gap : Float = w / (nodes + 1)
    val size : Float = gap / sizeFactor
    paint.color = foreColor
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    save()
    translate(gap * (i + 1), h / 2)
    drawSweepArcCircles(scale, size, paint)
    restore()
}

class SweepArcCircleView(ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }
}