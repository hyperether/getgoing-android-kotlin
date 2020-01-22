package com.hyperether.getgoing.ui.adapter.formatter

import android.annotation.SuppressLint
import com.dinuscxj.progressbar.CircleProgressBar.ProgressFormatter


class MyProgressFormatter3 : ProgressFormatter {
    @SuppressLint("DefaultLocale")
    override fun format(progress: Int, max: Int): CharSequence {
        return ""
    }
}