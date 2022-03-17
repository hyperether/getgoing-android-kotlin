package com.hyperether.getgoing.location

import android.app.PendingIntent
import android.content.Intent
import android.location.Location
import android.os.Handler
import android.os.HandlerThread
import com.hyperether.getgoing.App
import com.hyperether.getgoing.R
import com.hyperether.getgoing.SharedPref
import com.hyperether.getgoing.repository.room.GgRepository
import com.hyperether.getgoing.repository.room.MapNode
import com.hyperether.getgoing.repository.room.Route
import com.hyperether.getgoing.ui.activity.LocationActivity
import com.hyperether.getgoing.utils.Conversion
import com.hyperether.toolbox.HyperNotification
import com.hyperether.toolbox.location.HyperLocationService

class GGLocationService : HyperLocationService() {

    private var longitude: Double? = null
    private var longitude_old: Double? = null
    private var latitude: Double? = null
    private var latitude_old: Double? = null

    private var mCurrentLocation: Location? = null
    private var secondsCumulative: Int = 0
    private var oldTime: Long = 0
    private var time: Long = 0
    private var timeCumulative: Long = 0
    private var distanceCumulative: Double = 0.0

    private lateinit var thread: HandlerThread
    private lateinit var handler: Handler
    private var weight: Double = 0.0;
    private var previousTimeStamp: Long = 0;
    private var routeId: Long = 0
    private var profileID: Int = 0
    private var kcalCumulative = 0.0
    private var velocityAvg = 0.0
    private var currentRoute:Route? = null
    // add calculator

    override fun onCreate() {
        super.onCreate()
        var sharedPref: SharedPref = SharedPref.newInstance()
        weight = sharedPref.getWeight().toDouble()
        previousTimeStamp = System.currentTimeMillis()
        App.getHandler().post(Runnable {
            currentRoute = GgRepository.getLastRoute2()
            if (currentRoute != null) {
                routeId = currentRoute!!.id
                profileID = currentRoute!!.activity_id
                distanceCumulative = currentRoute!!.length
                kcalCumulative = currentRoute!!.energy
                velocityAvg = currentRoute!!.avgSpeed
                timeCumulative = currentRoute!!.duration
                secondsCumulative = timeCumulative.toInt()/1000
            }
        })
    }

    override fun startForeground() {
        super.startForeground()

        val intent = Intent(this, LocationActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        startForeground(
            1123, HyperNotification.getInstance().getForegroundServiceNotification(
                this,
                getString(R.string.notification_title),
                getString(R.string.notification_text),
                R.drawable.ic_launher,
                R.mipmap.ic_launcher,
                pendingIntent
            )
        )

        thread = HandlerThread("ggthread")
        thread.start()
        handler = Handler(thread.looper)
    }

    override fun onLocationUpdate(location: Location?) {
        handler.post {
            time = System.currentTimeMillis() - oldTime
            timeCumulative += System.currentTimeMillis() - oldTime
            secondsCumulative = timeCumulative.toInt() / 1000
            oldTime = System.currentTimeMillis()

            val previousTimestamp = System.currentTimeMillis()
            val elapsedTime: Long = System.currentTimeMillis() - previousTimestamp

            mCurrentLocation = location
            if (latitude == null)
                latitude = mCurrentLocation?.latitude
            if (longitude == null)
                longitude = mCurrentLocation?.longitude
            latitude_old = latitude
            longitude_old = longitude
            latitude = mCurrentLocation?.latitude
            longitude = mCurrentLocation?.longitude

            val distance = Conversion.gps2m(latitude, longitude, latitude_old, longitude_old)
            distanceCumulative += distance
            velocityAvg = distanceCumulative / secondsCumulative

            //speed is average value from gps and calculated value
            val velocity: Double = (location!!.speed+(distance/elapsedTime))/2
            val kcalCurrent: Double = 100.00 // fix this later


            val node = MapNode(
                0, latitude, longitude, 0.0f, 0, 0
            )

          //  currentRoute!!.currentSpeed = velocity location throws error
            currentRoute!!.energy = kcalCurrent
            currentRoute!!.avgSpeed = velocityAvg

            GgRepository.insert(node)
            GgRepository.updateRoute(currentRoute!!)
        }
    }
}