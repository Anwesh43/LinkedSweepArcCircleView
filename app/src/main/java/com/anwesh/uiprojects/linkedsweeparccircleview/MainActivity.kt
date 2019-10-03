package com.anwesh.uiprojects.linkedsweeparccircleview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager

import com.anwesh.uiprojects.sweeparccircleview.SweepArcCircleView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SweepArcCircleView.create(this)
        fullScreen()
    }
}

fun MainActivity.fullScreen() {
    supportActionBar?.hide()
    window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
}