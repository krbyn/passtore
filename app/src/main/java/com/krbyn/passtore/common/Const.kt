package com.krbyn.passtore.common

class Const private constructor() {
    companion object {
        /**
         * Application level log TAG
         */
        const val LOG_TAG = "PASSTORE_APP"
        /**
         * ID used to get shared preferences
         */
        const val SHARED_PREFERENCE_ID = "com.krbyn.passtore.SHARED_PREF_ID"
        /**
         * Password key in shared preferences
         */
        const val SHARED_PREF_PASS_KEY = "SHARED_PREF_ID_KEY"
    }
}