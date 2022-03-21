package com.hyperether.getgoing.listeners

import android.os.Bundle
import com.hyperether.getgoing.repository.room.Route

interface GgOnClickListener {
   abstract fun onClick(route: Route)

}