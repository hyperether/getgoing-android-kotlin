package com.hyperether.getgoing.listeners

import android.os.Bundle
import com.hyperether.getgoing.repository.room.Route

interface AdapterOnItemClickListener {
   fun onClick(route: Route,i:Int)
   fun onClickText(route:Route)
}