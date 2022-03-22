package com.hyperether.getgoing.ui.adapter

import android.content.Context
import android.util.SparseIntArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.hyperether.getgoing.R
import com.hyperether.getgoing.utils.Conversion


class HorizontalListAdapter(pData: SparseIntArray, pContext: Context) :
    RecyclerView.Adapter<HorizontalListAdapter.ViewHolder>() {

    private val mContext: Context = pContext
    private val mData: SparseIntArray = pData
    private val mInflater: LayoutInflater = LayoutInflater.from(mContext)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View
        val screenwidth: Float = Conversion.convertPixelToDp(
            mContext,
            mContext.resources.displayMetrics.widthPixels.toFloat()
        ).toFloat()

        view = when (screenwidth) {
            in 358.0..362.0 -> mInflater.inflate(R.layout.hlist_row_item_360, parent, false)
            in 390.0..396.0 -> mInflater.inflate(R.layout.hlist_row_item_390, parent, false)
            in 410.0..412.0 -> mInflater.inflate(R.layout.hlist_row_item_410, parent, false)
            in 455.0..462.0 -> mInflater.inflate(R.layout.hlist_row_item_460, parent, false)
            in 595.0..605.0 -> mInflater.inflate(R.layout.hlist_row_item_600, parent, false)
            else -> mInflater.inflate(R.layout.hlist_row_item_410, parent, false)
        }
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mData.size() * 10
    }

    override fun getItemId(position: Int): Long {
        val positionInList: Int = position % mData.size()
        return mData.get(positionInList).toLong()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val positionInList = position % mData.size()
        val img = mData.keyAt(positionInList)
        holder.img.setImageDrawable(mContext.getDrawable(img))
        holder.img.tag = img
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var img: ImageView = itemView.findViewById(R.id.iv_ri_pic)
    }
}