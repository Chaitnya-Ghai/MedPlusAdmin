package com.example.medplusadmin.utils

import android.app.Activity
import android.graphics.Rect
import android.view.ViewGroup
import android.view.ViewTreeObserver


class AndroidBug5497Workaround (activity: Activity) {
    private val contentContainer = activity.findViewById(android.R.id.content) as ViewGroup
    private val rootView = contentContainer.getChildAt(0)
    private val rootViewLayoutParams = rootView.layoutParams as ViewGroup.MarginLayoutParams
    private val viewTreeObserver = rootView.viewTreeObserver
    private val contentAreaOfWindowBounds = Rect()
    private var usableHeightPrevious = 0

    private val listener = ViewTreeObserver.OnGlobalLayoutListener {
        possiblyResizeChildOfContent()
    }

    fun addListener() {
        viewTreeObserver.addOnGlobalLayoutListener(listener)
    }

    fun removeListener() {
        viewTreeObserver.removeOnGlobalLayoutListener(listener)
    }

    private fun possiblyResizeChildOfContent() {
        contentContainer.getWindowVisibleDisplayFrame(contentAreaOfWindowBounds)
        val usableHeightNow = contentAreaOfWindowBounds.height()

        if (usableHeightNow != usableHeightPrevious) {
            rootViewLayoutParams.height = usableHeightNow
            rootView.layout(
                contentAreaOfWindowBounds.left,
                contentAreaOfWindowBounds.top,
                contentAreaOfWindowBounds.right,
                contentAreaOfWindowBounds.bottom
            )
            rootView.requestLayout()
            usableHeightPrevious = usableHeightNow
        }
    }
}