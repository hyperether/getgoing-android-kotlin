package com.hyperether.getgoing.model

import com.hyperether.getgoing.utils.Constants

data class CBDataFrame(
    var measurementSystemId: Int = Constants.METRIC,
    var weight: Int = 0, var height: Int = 0,
    var gender: Constants.gender = Constants.gender.Male, var age: Int = 0,
    var profileId: Int = 0, var profileName: String = ""
) {
    companion object {
        private var INSTANCE: CBDataFrame? = null

        fun getInstance(): CBDataFrame? {
            if (INSTANCE == null)
                INSTANCE = CBDataFrame()

            return INSTANCE.also { INSTANCE = it }
        }
    }
}