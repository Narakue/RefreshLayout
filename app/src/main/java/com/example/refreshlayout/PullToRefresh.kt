package com.example.refreshlayout

import android.content.Context
import android.os.AsyncTask
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

    /**
     * 避免onLayout循环执行
     */
    private var loadOnce = false

    /**
     * 记录手指按下位置，根据下拉距离实现下拉头下拉
     */
    private var actionDown = 0F

    /**
     * 下拉头会弹速度
     */
    private val SCROLL_SPEED = 15


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
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                actionDown = event.rawY
            }
            MotionEvent.ACTION_MOVE -> {
                val actionMove = event.rawY
                val distance = actionMove - actionDown
                height = (distance + hideHeaderHeight).toInt()
            }
            MotionEvent.ACTION_UP -> {
                HideHeaderTask().execute()
            }
        }
        return true
    }

    fun setHeight(height: Int) {
        layoutParams.topMargin = height
        header.layoutParams = layoutParams
        Log.d("zyz", "${layoutParams.topMargin}")
    }

    inner class HideHeaderTask : AsyncTask<Void, Int, Int>() {
        override fun doInBackground(vararg params: Void?): Int {
            var topMargin = layoutParams.topMargin
            while (topMargin > hideHeaderHeight) {
                topMargin -= SCROLL_SPEED
                publishProgress(topMargin)
                Thread.sleep(10)
            }
            return topMargin
        }

        override fun onPostExecute(result: Int?) {
            super.onPostExecute(result)
            if (result != null) {
                height = result
            }
        }

        override fun onProgressUpdate(vararg values: Int?) {
            super.onProgressUpdate(*values)
            height = values[0]!!
        }
    }
}