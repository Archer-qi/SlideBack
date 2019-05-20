package com.vshidai.slideback

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import org.jetbrains.annotations.Nullable
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.WindowManager


/**
 * @Description:
 *
 * @author          archer
 * @version         V1.0
 * @Date           2019/5/16
 */
class SlideBackView(context: Context, @Nullable attrs: AttributeSet?, defStyleAttr: Int) : View(context, attrs, defStyleAttr) {
    private val path: Path = Path()
    private val arrowPath: Path = Path()

    private val paint: Paint = Paint()
    private val arrowPaint: Paint = Paint()

    /**
     * 填充色
     */
    private val fillColor = "#B3000000"

    private val mContext: Context = context

    /**
     * 屏幕宽高
     */
    private var screenWidth: Int = 0
    private var screenHeight: Int = 0
    /**
     * 可拖动的宽
     */
    private var ratioWidth: Int = 0

    /**
     * 视图高度
     */
    private var viewHeight = 180f.dp2px()
    /**
     * 最高能拉起宽度
     */
    private var maxWidth = 30f.dp2px()
    /**
     * 拉起系数
     */
    private var offsetX = 0f
    /**
     * 曲线交界处的x轴坐标
     */
    private var lineX = 15f.dp2px()
    /**
     * 曲线交界处的y轴坐标
     */
    private var lineY = 65f.dp2px()
    /**
     * 第一段曲线锚点y轴坐标
     */
    private var anchorX = 35f.dp2px()
    /**
     * 是否从边缘触发屏幕触摸事件
     */
    private var isFromEdge = false
    /**
     * 判断是否从屏幕边缘划过来的y轴坐标值
     */
    private var edgeYLength = 16f.dp2px()
    /**
     * 触发返回的监听
     */
    private var onSlideBackListener: OnSlideBackListener? = null
    /**
     * y轴偏移量
     */
    private var offsetY = 0f
    /**
     * 箭头高度
     */
    private val arrowHeight = 8f.dp2px()
    /**
     * 箭头宽度
     */
    private val arrowWidth = 4f.dp2px()

    private val runnable = Runnable {
        while (offsetX > 0) {
            Thread.sleep(10)
            offsetX -= 0.1f
            postInvalidate(0, 0, maxWidth.toInt(), screenHeight)
        }
    }

    init {
        init()
    }

    constructor(context: Context) : this(context, null)

    constructor(context: Context, @Nullable attrs: AttributeSet?) : this(context, attrs, 0)

    private fun init() {

        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        screenWidth = displayMetrics.widthPixels
        screenHeight = displayMetrics.heightPixels
        ratioWidth = screenWidth / 3

        paint.run {
            isAntiAlias = true
            //描边并填充
            style = Paint.Style.FILL_AND_STROKE
            color = Color.parseColor(fillColor)
            strokeWidth = 1f
        }

        arrowPaint.run {
            isAntiAlias = true
            style = Paint.Style.STROKE
            color = Color.WHITE
            strokeCap = Paint.Cap.ROUND
            strokeWidth = 5f
        }
        alpha = 1f
    }

    override fun onDraw(canvas: Canvas) {

        //贝塞尔曲线
        path.reset()
        path.moveTo(0f, offsetY - viewHeight / 2)
        path.quadTo(0f, anchorX - viewHeight / 2 + offsetY, lineX * offsetX, lineY - viewHeight / 2 + offsetY)
        path.quadTo(maxWidth * offsetX, offsetY, lineX * offsetX, viewHeight - lineY - viewHeight / 2 + offsetY)
        path.quadTo(0f, viewHeight / 2 - anchorX + offsetY, 0f, viewHeight / 2 + offsetY)
        canvas.drawPath(path, paint)

        //箭头
        arrowPath.reset()
        arrowPath.moveTo((maxWidth / 4 + arrowWidth) * offsetX, offsetY - arrowHeight / 2)
        arrowPath.lineTo(maxWidth / 4 * offsetX, offsetY)
        arrowPath.lineTo((maxWidth / 4 + arrowWidth) * offsetX, offsetY + arrowHeight / 2)
        canvas.drawPath(arrowPath, arrowPaint)

        //设置视图透明度
        alpha = offsetX
    }

    @SuppressWarnings("all")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.rawX
        val y = event.rawY
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {    //手指按下
                if (x < edgeYLength) {
                    isFromEdge = true
                    offsetY = event.rawY
                    return true
                }
            }

            MotionEvent.ACTION_MOVE -> {    //手指滑动
                if (isFromEdge) {
                    offsetY = y
                    offsetX = x / ratioWidth
                    if (offsetX > 1.0) offsetX = 1.0f
                    invalidate(0, 0, maxWidth.toInt(), screenHeight)
                    return true
                }
            }
            MotionEvent.ACTION_UP -> {      //手指抬起
                if (isFromEdge) {
                    isFromEdge = false
                    if (offsetX == 1.0f) {
                        offsetX = 0f
                        onSlideBackListener?.onBack()
                    } else {
                        cancelBack()
                    }
                    invalidate(0, 0, maxWidth.toInt(), screenHeight)
                    return true
                }
            }
        }
        return super.onTouchEvent(event)
    }

    /**
     * 事件取消后的动画
     */
    private fun cancelBack() {
        val thread = Thread(runnable)
        thread.start()
    }

    /**
     * 返回事件触发监听
     */
    interface OnSlideBackListener {
        fun onBack()
    }

    fun setOnSlideBackListener(listener: OnSlideBackListener) {
        this.onSlideBackListener = listener
    }

    private fun Float.dp2px(): Float {
        val scale = resources.displayMetrics.density
        return this * scale + 0.5f
    }
}