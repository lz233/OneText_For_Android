package com.lz233.onetext.view

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

open class AdjustImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : AppCompatImageView(context, attrs, defStyleAttr) {
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        drawable?.let {
            val width = MeasureSpec.getSize(widthMeasureSpec)
            val height = Math.ceil(width.toDouble() * it.intrinsicHeight / it.intrinsicWidth).toInt()
            setMeasuredDimension(width, height)
        } ?: super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }
}