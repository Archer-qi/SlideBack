package com.archer.slideback

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.FrameLayout
import com.vshidai.slideback.SlideBackView

/**
 * @Description:
 *
 * @author          archer
 * @version         V1.0
 * @Date           2019/5/20
 */
open class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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