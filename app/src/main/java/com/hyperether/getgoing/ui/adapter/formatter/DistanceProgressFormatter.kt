package com.hyperether.getgoing.ui.adapter.formatter

import com.dinuscxj.progressbar.CircleProgressBar
import java.text.DecimalFormat

class DistanceProgressFormatter(pData: Double) : CircleProgressBar.ProgressFormatter {
    var mData: Double = pData
    var df: DecimalFormat = DecimalFormat("#.##")

    companion object {
        fun newInstance(pData: Double) = DistanceProgressFormatter(pData)
    }

    override fun format(progress: Int, max: Int): CharSequence {
        if (mData > 1000) {
            return df.format(mData / 1000) + " km"
        } else {
            return df.format(mData.toInt()).toString() + " m"
        }
    }
}