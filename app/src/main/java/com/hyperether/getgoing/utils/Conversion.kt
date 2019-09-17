package com.hyperether.getgoing.utils

class Conversion {
    /**
     * Convert number of milliseconds into form HH:mm:ss
     *
     * @param seconds elapsed time
     */
    fun getDurationString(seconds: Long): String {
        var seconds = seconds
        val hours = seconds / 3600
        val minutes = seconds % 3600 / 60
        seconds = seconds % 60

        return twoDigitString(hours) + " : " + twoDigitString(minutes) + " : " +
                twoDigitString(seconds)
    }

    /**
     * Formatting method for time output
     *
     * @param number time
     */
    fun twoDigitString(number: Long): String {
        if (number == 0L) {
            return "00"
        }

        return if (number / 10 == 0L) {
            "0$number"
        } else number.toString()

    }

    /**
     * This method only works if the points are close enough that you can omit that
     * earth is not regular shape
     *
     * @param lat_a first point lat
     * @param lng_a first point lng
     * @param lat_b second point lat
     * @param lng_b second point lng
     */
    companion object {
        fun gps2m(lat_a: Double?, lng_a: Double?, lat_b: Double?, lng_b: Double?): Double {
            if (lat_a != null && lng_a != null && lat_b != null && lng_b != null) {
                val pk = 180 / 3.14169

                val a1: Double = lat_a / pk
                val a2: Double = lng_a / pk
                val b1: Double = lat_b / pk
                val b2: Double = lng_b / pk

                val t1 = Math.cos(a1) * Math.cos(a2) * Math.cos(b1) * Math.cos(b2)
                val t2 = Math.cos(a1) * Math.sin(a2) * Math.cos(b1) * Math.sin(b2)
                val t3 = Math.sin(a1) * Math.sin(b1)
                val tt = Math.acos(t1 + t2 + t3)
                return 6366000 * tt
            }
            return 0.0
        }
    }
}