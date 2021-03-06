package com.hyperether.getgoing.utils
/**
 * Created by nikola on 10/07/17.
 */
object Constants {
    // Global constants
/*
     * Define a request code to send to Google Play services
	 * This code is returned in Activity.onActivityResult
	 */
    const val CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000
    // Milliseconds per second
    const val MILLISECONDS_PER_SECOND = 1000
    // Update frequency in seconds
    const val UPDATE_INTERVAL_IN_SECONDS = 5
    // Update frequency in milliseconds
    const val UPDATE_INTERVAL =
        MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS.toLong()
    // The fastest update frequency, in seconds
    const val FASTEST_INTERVAL_IN_SECONDS = 5
    // A fast frequency ceiling in milliseconds
    const val FASTEST_INTERVAL =
        MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS.toLong()
    // Preference file
    const val PREF_FILE = "CBUserDataPref.txt"
    const val NODE_ADD_DISTANCE = 10
    const val REQUEST_RESOLVE_ERROR = 1001
    //Permission request TAG
    const val TAG_CODE_PERMISSION_LOCATION = 1
    const val RESULT_REQUESTED = 1
    const val METRIC = 0
    // Number picker
    const val NUMBER_PICKER_MAX_VALUE = 150
    const val NUMBER_PICKER_DEFAULT_WEIGHT = 60
    const val NUMBER_PICKER_DEFAULT_AGE = 20
    const val NUMBER_PICKER_MIN_VALUE = 0
    const val NUMBER_PICKER_VALUE_SIZE = 151
    // Requets TAG
    const val REQUEST_GPS_SETTINGS = 100
    //ActivitiesFragment
    const val AVG_SPEED_WALK = 1.5.toFloat()
    const val AVG_SPEED_RUN = 2.5.toFloat()
    const val AVG_SPEED_CYCLING = 5f
    const val CONST_LOW_DIST = 2500
    const val CONST_MEDIUM_DIST = 5000
    const val CONST_HIGH_DIST = 7500

    //ProfileFragment
    enum class gender {
        Male, Female, Other
    }

    const val WALK_ID = 1
    const val RUN_ID = 2
    const val RIDE_ID = 3
}
