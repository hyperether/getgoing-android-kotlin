package com.hyperether.getgoing.ui.adapter.formatter

import com.dinuscxj.progressbar.CircleProgressBar

class TimeProgressFormatter(var mData:Long): CircleProgressBar.ProgressFormatter {

    var data = mData

    companion object{
        fun newInstance(mData: Long) = TimeProgressFormatter(mData)
    }

    override fun format(progress: Int, max: Int): CharSequence {
        return (mData / 60000).toString()
    }


}