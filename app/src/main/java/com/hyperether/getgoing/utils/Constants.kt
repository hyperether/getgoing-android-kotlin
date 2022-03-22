package com.hyperether.getgoing.utils

/**
 * Created by nikola on 10/07/17.
 */
object Constants {
    const val CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000
    const val MILLISECONDS_PER_SECOND = 1000
    const val UPDATE_INTERVAL_IN_SECONDS = 5
    const val UPDATE_INTERVAL =
        MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS.toLong()
    const val FASTEST_INTERVAL_IN_SECONDS = 5
    const val FASTEST_INTERVAL =
        MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS.toLong()
    const val LOCATION_DISTANCE = 5
    const val PREF_FILE = "CBUserDataPref.txt"
    const val PREF_WALK_ROUTE_EXISTING = "walk_route_existing"
    const val PREF_RUN_ROUTE_EXISTING = "run_route_existing"
    const val PREF_RIDE_ROUTE_EXISTING = "ride_route_existing"
    const val NODE_ADD_DISTANCE = 10
    const val REQUEST_RESOLVE_ERROR = 1001
    const val TAG_CODE_PERMISSION_LOCATION = 1
    const val RESULT_REQUESTED = 1
    const val METRIC = 0
    const val NUMBER_PICKER_MAX_VALUE = 150
    const val NUMBER_PICKER_DEFAULT_WEIGHT = 60
    const val NUMBER_PICKER_DEFAULT_AGE = 20
    const val NUMBER_PICKER_MIN_VALUE = 0
    const val NUMBER_PICKER_VALUE_SIZE = 151
    const val REQUEST_GPS_SETTINGS = 100
    const val AVG_SPEED_WALK = 1.5.toFloat()
    const val AVG_SPEED_RUN = 2.5.toFloat()
    const val AVG_SPEED_CYCLING = 5f
    const val CONST_LOW_DIST = 2500
    const val CONST_MEDIUM_DIST = 5000
    const val CONST_HIGH_DIST = 7500

    enum class gender {
        Male, Female, Other
    }

    const val WALK_ID = 1
    const val RUN_ID = 2
    const val RIDE_ID = 3
    const val DATA_DETAILS_LABEL = "data_details_label";
    const val BUNDLE_PARCELABLE = "bundle_parcelable";
    const val TRACKING_ACTIVITY_KEY = "tracking_activity"
    const val ACTIVITY_STARTED = 0
    const val ACTIVITY_WALK_ID = 1
    const val ACTIVITY_RUN_ID = 2
    const val ACTIVITY_RIDE_ID = 3
    const val ACTIVITY_SHOW_DATA = "show_data_activity"
    const val ACTIVITY_SHOW_DATA2 = "show_data_activity"
    const val OPENED_FROM_LOCATION_ACT = 501
    const val OPENED_FROM_GG_ACT = 502
    const val OPENED_FROM_KEY = "from"
    const val WALK_TIME_ESTIMATE = "walk_time_estimate"
    const val RUN_TIME_ESTIMATE = "run_time_estimate"
    const val CYCLE_TIME_ESTIMATE = "cycle_time_estimate"
    const val SENT_FROM_FRAGMENT = 0
}
