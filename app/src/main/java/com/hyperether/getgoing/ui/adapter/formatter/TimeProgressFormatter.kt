package com.hyperether.getgoing.ui.adapter.formatter

import android.annotation.SuppressLint
import com.dinuscxj.progressbar.CircleProgressBar.ProgressFormatter

class TimeProgressFormatter(private val mData: Long) : ProgressFormatter {
    @SuppressLint("DefaultLocale")
    override fun format(progress: Int, max: Int): CharSequence {
        return (mData / 60000).toString()
    }
}