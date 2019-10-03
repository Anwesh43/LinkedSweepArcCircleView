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
    private val renderer : Renderer = Renderer(this)

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas, paint)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var prevScale : Float = 0f, var dir : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            scale += dir * scGap
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(delay)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class SACNode(var i : Int, val state : State = State()) {

        private var next : SACNode? = null
        private var prev : SACNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < nodes - 1) {
                next = SACNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawSACNode(i, state.scale, paint)
            next?.draw(canvas, paint)
        }

        fun update(cb : (Float) -> Unit) {
            state.update(cb)
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : SACNode {
            var curr : SACNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class SweepArcCircle(var i : Int) {

        private val root : SACNode = SACNode(0)
        private var curr : SACNode = root
        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            root.draw(canvas, paint)
        }

        fun update(cb : (Float) -> Unit) {
            curr.update {
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                cb(it)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            curr.startUpdating(cb)
        }
    }

    data class Renderer(var view : SweepArcCircleView) {

        private val animator : Animator = Animator(view)
        private val sac : SweepArcCircle = SweepArcCircle(0)

        fun render(canvas : Canvas, paint : Paint) {
            canvas.drawColor(backColor)
            sac.draw(canvas, paint)
            animator.animate {
                sac.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            sac.startUpdating {
                animator.start()
            }
        }
    }
}