package com.glide

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.drawable.Drawable
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.widget.ImageView

/**
 * Created by 李志云 2019/4/16 02:30
 */
class Target(val view:ImageView) {
    private var maxDisplayLength = -1
    var cb:SizeReadyCallback? = null
    var layoutListener:LayoutListener? = null
    class LayoutListener(var target: Target?):ViewTreeObserver.OnPreDrawListener{
        override fun onPreDraw(): Boolean {
            target?.checkCurrentDimens()
            return true
        }
        fun release(){
            target = null
        }
    }
    interface SizeReadyCallback{
        fun onSizeReady(width:Int,height:Int)
    }

    private fun getTargetHeight(): Int {
        val verticalPadding = view.getPaddingTop() + view.getPaddingBottom()
        val layoutParams = view.getLayoutParams()
        val layoutParamSize = if (layoutParams != null) layoutParams!!.height else 0
        return getTargetDimen(view.getHeight(), layoutParamSize, verticalPadding)
    }

    private fun getTargetWidth(): Int {
        //获得view的padding view的宽-padding才是内容的宽
        val horizontalPadding = view.getPaddingLeft() + view.getPaddingRight()
        //获得view的布局属性 1、给定的大小 2、wrap_content
        val layoutParams = view.getLayoutParams()
        val layoutParamSize = if (layoutParams != null) layoutParams!!.width else 0
        return getTargetDimen(view.getWidth(), layoutParamSize, horizontalPadding)
    }

    /**
     *
     * @param viewSize view.getXX
     * @param paramSize  LayoutParams
     * @param paddingSize padding
     * @return
     */
    private fun getTargetDimen(viewSize: Int, paramSize: Int, paddingSize: Int): Int {
        //1、如果是固定大小
        val adjustedParamSize = paramSize - paddingSize
        if (adjustedParamSize > 0) {
            return adjustedParamSize
        }

        //2、如果能够由 view.getWidth() 获得大小
        val adjustedViewSize = viewSize - paddingSize
        if (adjustedViewSize > 0) {
            return adjustedViewSize
        }

        //3、如果布局属性设置的是包裹内容并且我们不能接到回调了
        // 回调 是什么？ addOnPreDrawListener
        //表示不会回调 onPreDraw
        return if (!view.isLayoutRequested() && paramSize == ViewGroup.LayoutParams.WRAP_CONTENT) {
            getMaxDisplayLength(view.getContext())
        } else 0
    }

    /**
     * 获得一个最大允许的view大小
     * @param context
     * @return
     */
    private fun getMaxDisplayLength(context: Context): Int {
        if (maxDisplayLength == -1) {
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val display = windowManager.defaultDisplay
            val displayDimensions = Point()
            //获得屏幕大小
            display.getSize(displayDimensions)
            // 最大的屏幕大小
            maxDisplayLength = Math.max(displayDimensions.x, displayDimensions.y)
        }
        return maxDisplayLength
    }

    fun checkCurrentDimens(){
        if (null == cb) return
        val currentWidth = getTargetWidth()
        val currentHeight = getTargetHeight()
        if (currentWidth <= 0 || currentHeight <= 0){
            return
        }
        cb?.onSizeReady(currentWidth,currentHeight)
        cancel()
    }
    fun cancel(){
        val observer = view.viewTreeObserver
        if (observer.isAlive){
            observer.removeOnPreDrawListener(layoutListener)
        }
        layoutListener?.release()
        layoutListener = null
        cb = null
    }

    fun onLoadFailed(error:Drawable?){
        view.setImageDrawable(error)
    }

    fun onLoadStarted(placeholderDrawable:Drawable?){
        view.setImageDrawable(placeholderDrawable)
    }

    fun onResourceReady(bitmap: Bitmap){view.setImageBitmap(bitmap)}


    fun getSize(cb:SizeReadyCallback){
        val currentWidth = getTargetWidth()
        val currentHeight = getTargetHeight()
        if (currentHeight > 0 && currentWidth > 0){
            cb.onSizeReady(currentWidth,currentHeight)
            return
        }
        this.cb = cb
        if (layoutListener == null){
            view.viewTreeObserver.addOnPreDrawListener(LayoutListener(this))
        }
    }
}