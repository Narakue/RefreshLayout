package com.example.refreshlayout

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.RelativeLayout

class PullToRefresh(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs),
    View.OnTouchListener {
    private var header: View
    private lateinit var listView: ListView
    private var hideHeaderHeight = 0
    private lateinit var layoutParams: MarginLayoutParams
    private var loadOnce = false


    init {
        header = LayoutInflater.from(context).inflate(R.layout.pull_to_refresh, null, true);
//        refreshUpdatedAtValue();
        orientation = VERTICAL;
        addView(header, 0);
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        if (!loadOnce) {
            listView = getChildAt(1) as ListView
            hideHeaderHeight = -header.height
            layoutParams = header.layoutParams as MarginLayoutParams
            height = hideHeaderHeight
            listView.setOnTouchListener(this)
            loadOnce = true
        }
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        var moveDown = 0F
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                moveDown = event.rawY
                Log.d("zyz", "actionDown1 : ${moveDown}")
            }
            MotionEvent.ACTION_MOVE -> {
                val actionMove = event.rawY
                Log.d("zyz", "actionDown2 : ${moveDown}")
                val distance = actionMove - moveDown
                height = (distance + hideHeaderHeight).toInt()
            }
            MotionEvent.ACTION_UP -> {
                height = hideHeaderHeight
            }
        }
        return true
    }

    fun setHeight(height: Int) {
        layoutParams.topMargin = height
        header.layoutParams = layoutParams
        Log.d("zyz", "${layoutParams.topMargin}")
    }
}