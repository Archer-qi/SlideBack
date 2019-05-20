package com.archer.slideback

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import com.vshidai.slideback.SlideBackView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val container = window.decorView as FrameLayout
        val backView = SlideBackView(this)
        backView.setOnSlideBackListener(object : SlideBackView.OnSlideBackListener {
            override fun onBack() {
                finish()
            }
        })
        container.addView(backView)
    }
}
