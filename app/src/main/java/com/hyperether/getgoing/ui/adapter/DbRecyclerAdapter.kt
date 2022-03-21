package com.hyperether.getgoing.ui.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.hyperether.getgoing.R
import com.hyperether.getgoing.repository.room.Route
import com.hyperether.getgoing.utils.Constants

class DbRecyclerAdapter(
    private val routes: List<Route>,val context:Context
) : RecyclerView.Adapter<DbRecyclerAdapter.DbRecyclerAdapterViewHolder>() {

    class DbRecyclerAdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imgView: ImageView = itemView.findViewById(R.id.imageViewCard)
        var txtCard: TextView = itemView.findViewById(R.id.txtCard)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DbRecyclerAdapterViewHolder {
        val itemView: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.card_show_data_fragment, parent, false)
        val view: DbRecyclerAdapterViewHolder = DbRecyclerAdapterViewHolder(itemView)
        return view
    }

    override fun onBindViewHolder(holder: DbRecyclerAdapterViewHolder, position: Int) {
        var route: Route = routes.get(position)
        Log.d(DbRecyclerAdapter::class.simpleName, "fromAdapter: $route")
        holder.txtCard.text = route.date
        if (route.activity_id == Constants.WALK_ID) {
            holder.imgView.setImageDrawable(
                AppCompatResources.getDrawable(
                    context,
                    R.drawable.ic_light_walking_icon_active
                )
            )
        }
        if (route.activity_id == Constants.RUN_ID) {
            holder.imgView.setImageDrawable(
                AppCompatResources.getDrawable(
                    context,
                    R.drawable.ic_light_running_icon_active
                )
            )

        }
        if (route.activity_id == Constants.RIDE_ID) {
            holder.imgView.setImageDrawable(
                AppCompatResources.getDrawable(
                    context,
                    R.drawable.ic_light_bicycling_icon_active
                )
            )
        }
    }

    override fun getItemCount(): Int {
        return routes.size
    }

}