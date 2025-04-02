package org.epstudios.morbidmeter

import android.content.Context

/**
Copyright (C) 2025 EP Studios, Inc.
www.epstudiossoftware.com

Created by mannd on 3/20/25.

This file is part of morbidmeter-android.

morbidmeter-android is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

morbidmeter-android is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with morbidmeter-android.  If not, see <http://www.gnu.org/licenses/>.
 */
class Frequency(private val frequencyType: FrequencyType) {

    fun getFrequency(): Int {
        when (frequencyType) {
            FrequencyType.ONE_SEC -> return 1000
            FrequencyType.FIVE_SEC -> return 1000 * 5
            FrequencyType.FIFTEEN_SEC -> return 1000 * 15
            FrequencyType.THIRTY_SEC -> return 1000 * 30
            FrequencyType.ONE_MIN -> return 1000 * 60
            FrequencyType.FIFTEEN_MIN -> return 1000 * 60 * 15
            FrequencyType.THIRTY_MIN -> return 1000 * 60 * 30
            FrequencyType.ONE_HOUR -> return 1000 * 60 * 60
            FrequencyType.SIX_HOUR -> return 1000 * 60 * 60 * 6
            FrequencyType.TWELVE_HOUR -> return 1000 * 60 * 60 * 12
            FrequencyType.ONE_DAY -> return 1000 * 60 * 60 * 24
            FrequencyType.NONE -> return -1
        }
    }

    enum class FrequencyType {
        ONE_SEC,
        FIVE_SEC,
        FIFTEEN_SEC,
        THIRTY_SEC,
        ONE_MIN,
        FIFTEEN_MIN,
        THIRTY_MIN,
        ONE_HOUR,
        SIX_HOUR,
        TWELVE_HOUR,
        ONE_DAY,
        NONE
    }

    companion object {
        @JvmStatic
        fun fromInt(value: Int): FrequencyType? {
            return FrequencyType.entries.find { it.ordinal == value }
        }

        @JvmStatic
        public val frequencyNameIds = intArrayOf(
            R.string.f_one_sec,
            R.string.f_five_sec,
            R.string.f_fifteen_sec,
            R.string.f_thirty_sec,
            R.string.f_one_min,
            R.string.f_fifteen_min,
            R.string.f_thirty_min,
            R.string.f_one_hour,
            R.string.f_six_hour,
            R.string.f_twelve_hour,
            R.string.f_one_day,
        )

        @JvmStatic
        public val frequencyIdToFrequencyType = mapOf(
            R.string.f_one_sec to FrequencyType.ONE_SEC,
            R.string.f_five_sec to FrequencyType.FIVE_SEC,
            R.string.f_fifteen_sec to FrequencyType.FIFTEEN_SEC,
            R.string.f_thirty_sec to FrequencyType.THIRTY_SEC,
            R.string.f_one_min to FrequencyType.ONE_MIN,
            R.string.f_fifteen_min to FrequencyType.FIFTEEN_MIN,
            R.string.f_thirty_min to FrequencyType.THIRTY_MIN,
            R.string.f_one_hour to FrequencyType.ONE_HOUR,
            R.string.f_six_hour to FrequencyType.SIX_HOUR,
            R.string.f_twelve_hour to FrequencyType.TWELVE_HOUR,
            R.string.f_one_day to FrequencyType.ONE_DAY,
        )

        @JvmStatic
        fun getFrequencyIdToFrequencyType(frequencyId: Int): FrequencyType {
            return frequencyIdToFrequencyType[frequencyId]!!
        }

        @JvmStatic
        fun indexFromString(stringId: Int): Int {
            return frequencyNameIds.indexOf(stringId)
        }

        @JvmStatic
        fun getStringArray(context: Context): Array<String> {
            return Frequency.frequencyNameIds.map {
                context.getString(it)
            }.toTypedArray()
        }

        internal fun getFrequency(frequencyId: Int): Int {
            val frequencyType =
                getFrequencyIdToFrequencyType(frequencyId)
            val frequency = Frequency(frequencyType)
            return frequency.getFrequency()
        }
    }

}
