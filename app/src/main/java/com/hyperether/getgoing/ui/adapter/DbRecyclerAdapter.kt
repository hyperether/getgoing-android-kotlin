package com.hyperether.getgoing.ui.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hyperether.getgoing.R
import com.hyperether.getgoing.listeners.GgOnClickListener
import com.hyperether.getgoing.repository.room.Route
import com.hyperether.getgoing.utils.Constants
import com.hyperether.getgoing.utils.Constants.BUNDLE_PARCELABLE

class DbRecyclerAdapter(private val routes:List<Route>,
private var listener:GgOnClickListener) : RecyclerView.Adapter<DbRecyclerAdapter.DbRecyclerAdapterViewHolder>() {

    class DbRecyclerAdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var chartProgress:ProgressBar = itemView.findViewById(R.id.chart_progress)
        var chartDate:TextView = itemView.findViewById(R.id.chart_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DbRecyclerAdapterViewHolder {
        val itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.show_data_row_item, parent, false)
        val view: DbRecyclerAdapterViewHolder = DbRecyclerAdapterViewHolder(itemView)
        return view
    }

    override fun onBindViewHolder(holder: DbRecyclerAdapterViewHolder, position: Int) {
        var route:Route = routes.get(position)
        if ("null" != route.date) {
            holder.chartProgress.max = route.goal
            holder.chartProgress.progress = route.length.toInt()
            holder.chartDate.text = route.date.substring(0, 6)
            holder.chartDate.setOnClickListener {
                val bundle = Bundle()
                //bundle.putParcelable(BUNDLE_PARCELABLE, routes.get(position))
                //listener.onClick(bundle)
            }
        }
    }

    override fun getItemCount(): Int {
        return routes.size
    }

}