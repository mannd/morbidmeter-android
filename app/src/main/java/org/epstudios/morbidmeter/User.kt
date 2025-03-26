/*  MorbidMeter - Lifetime in perspective
    Copyright (C) 2011 EP Studios, Inc.
    www.epstudiossoftware.com

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.epstudios.morbidmeter

import java.util.Calendar
import java.util.GregorianCalendar
import java.util.Locale

class User(@JvmField var name: String, birthDay: GregorianCalendar, longevity: Double) {
    private var birthDay: GregorianCalendar? = null
    @JvmField
    var longevity: Double

    init {
        setBirthDay(birthDay)
        this.longevity = longevity
    }

    @Suppress("unused")
    fun birthDay(): GregorianCalendar {
        return this.birthDay!!
    }

    fun deathDay(): GregorianCalendar {
        val deathDay = GregorianCalendar()
        deathDay.setTimeInMillis(deathDayMsec())
        return deathDay
    }

    @Suppress("unused")
    fun longevityFromDeathDate(year: Int, month: Int, dayOfMonth: Int): Double {
        return getLongevity(
            birthDay!!.get(Calendar.YEAR),
            birthDay!!.get(Calendar.MONTH),
            birthDay!!.get(Calendar.DAY_OF_MONTH), year, month, dayOfMonth
        )
    }

    val isDead: Boolean
        get() = msecAlive() + birthDayMsec() > deathDay().getTimeInMillis()

    fun deathDayMsec(): Long {
        return birthDayMsec() + lifeDurationMsec()
    }

    fun lifeDurationMsec(): Long {
        return (longevity * Companion.msecsPerYear).toLong()
    }

    fun birthDayMsec(): Long {
        return birthDay!!.getTimeInMillis()
    }

    fun msecAlive(): Long {
        return System.currentTimeMillis() - birthDayMsec()
        //return Calendar.getInstance().getTimeInMillis() - birthDayMsec();
    }

    fun reverseMsecAlive(): Long {
        // return deathDayMsec() - msecAlive();
        return lifeDurationMsec() - msecAlive()
    }

    fun secAlive(): Long {
        return msecAlive() / 1000
    }

    private fun daysAlive(): Double {
        return secAlive().toDouble() / 60 * 60 * 24.0
    }

    @Suppress("unused")
    fun minutesAlive(): Double {
        return secAlive().toDouble() / 60
    }

    @Suppress("unused")
    fun reverseMinutesAlive(): Double {
        return reverseSecAlive().toDouble() / 60
    }

    private fun reverseDaysAlive(): Double {
        return reverseSecAlive().toDouble() / 60 * 60 * 24.0
    }

    @Suppress("unused")
    fun yearsAlive(): Double {
        return daysAlive() / Companion.daysPerYear
    }

    // next 2 only used in tests so far
    fun reverseYearsAlive(): Double {
        return reverseDaysAlive() / Companion.daysPerYear
    }

    fun reverseSecAlive(): Long {
        return reverseMsecAlive() / 1000
    }

    fun percentAlive(): Double {
        return (msecAlive().toDouble()) / lifeDurationMsec()
    }

    private fun msecAlive(date: Calendar): Long {
        return date.getTimeInMillis() - birthDayMsec()
    }

    //    public double percentAlive(Calendar date) {
    //        return ((double) msecAlive(date)) / lifeDurationMsec();
    //    }
    fun percentAlive(date: Calendar): Double {
        return (msecAlive(date).toDouble()) / lifeDurationMsec()
    }

    val isSane: Boolean
        get() {
            var sane = longevity > 0 && longevity < 999
            val earliestBirthDay = GregorianCalendar.getInstance()
            earliestBirthDay.set(1800, 0, 0)
            val latestBirthDay = GregorianCalendar.getInstance()
            latestBirthDay.set(2100, 0, 0)
            sane = sane && birthDay!!.after(earliestBirthDay)
                    && birthDay!!.before(latestBirthDay)
            return sane
        }

    val apostrophedName: String
        get() {
            if (name.length > 0) {
                if (name.uppercase(Locale.getDefault())
                        .get(name.length - 1) == 'S'
                ) name += "'"
                else name += "'s"
            }
            return name
        }

    fun getBirthDay(): Calendar {
        return birthDay!!
    }

    fun setBirthDay(birthDay: GregorianCalendar) {
        this.birthDay = birthDay
        // normalize all birthdays to the stroke of midnight
        this.birthDay!!.set(Calendar.HOUR_OF_DAY, 0)
        this.birthDay!!.set(Calendar.MINUTE, 0)
        this.birthDay!!.set(Calendar.SECOND, 0)
        this.birthDay!!.set(Calendar.MILLISECOND, 0)
    }

    @get:Suppress("unused")
    val daysPerYear: Double
        get() = Companion.daysPerYear

    @get:Suppress("unused")
    val msecsPerYear: Long
        get() = Companion.msecsPerYear

    companion object {
        private const val daysPerYear = 365.25
        private val msecsPerYear = (daysPerYear * 24 * 60 * 60 * 1000).toLong()
        @JvmStatic
        fun getLongevity(
            birthYear: Int, birthMonth: Int,
            birthDayOfMonth: Int, deathYear: Int, deathMonth: Int,
            deathDayOfMonth: Int
        ): Double {
            val deathDate = GregorianCalendar.getInstance()
            deathDate.set(deathYear, deathMonth, deathDayOfMonth)
            // normalize all deathdays to the stroke of midnight
            deathDate.set(Calendar.HOUR_OF_DAY, 0)
            deathDate.set(Calendar.MINUTE, 0)
            deathDate.set(Calendar.SECOND, 0)
            deathDate.set(Calendar.MILLISECOND, 0)
            val birthDate = GregorianCalendar.getInstance()
            birthDate.set(birthYear, birthMonth, birthDayOfMonth)
            // normalize all birthdays to the stroke of midnight
            birthDate.set(Calendar.HOUR_OF_DAY, 0)
            birthDate.set(Calendar.MINUTE, 0)
            birthDate.set(Calendar.SECOND, 0)
            birthDate.set(Calendar.MILLISECOND, 0)
            val longevityInMsec = (deathDate.getTimeInMillis()
                    - birthDate.getTimeInMillis())
            if (longevityInMsec <= 0) {
                return 0.0
            } else {
                return longevityInMsec.toDouble() / msecsPerYear
            }
        }

        @JvmStatic
        fun getDeathDate(
            birthYear: Int, birthMonth: Int,
            birthDayOfMonth: Int, longevity: Double
        ): Calendar {
            val birthDate = GregorianCalendar.getInstance()
            birthDate.set(birthYear, birthMonth, birthDayOfMonth)
            // normalize all birthdays to the stroke of midnight
            birthDate.set(Calendar.HOUR_OF_DAY, 0)
            birthDate.set(Calendar.MINUTE, 0)
            birthDate.set(Calendar.SECOND, 0)
            birthDate.set(Calendar.MILLISECOND, 0)
            val deathDateInMsecs = (birthDate.getTimeInMillis()
                    + (longevity * msecsPerYear).toLong())
            val deathDate = GregorianCalendar.getInstance()
            deathDate.setTimeInMillis(deathDateInMsecs)
            return deathDate
        }
    }
}
