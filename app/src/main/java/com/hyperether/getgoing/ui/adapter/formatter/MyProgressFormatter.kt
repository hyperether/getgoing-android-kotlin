package com.hyperether.getgoing.ui.adapter.formatter

import android.annotation.SuppressLint
import com.dinuscxj.progressbar.CircleProgressBar.ProgressFormatter
import java.text.DecimalFormat


class MyProgressFormatter(pData: Double) : ProgressFormatter {
    private val mData: Double = pData / 1000
    private val df: DecimalFormat = DecimalFormat("#.##")

    @SuppressLint("DefaultLocale")
    override fun format(progress: Int, max: Int): CharSequence {
        return df.format(mData).plus("km")
    }
}