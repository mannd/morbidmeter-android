package org.epstudios.morbidmeter

/**
Copyright (C) 2025 EP Studios, Inc.
www.epstudiossoftware.com

Created by mannd on 4/11/25.

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
import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import java.util.Calendar

class BootReceiver : BroadcastReceiver() {
    companion object {
        private const val LOG_TAG = "BootReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d(LOG_TAG, "Received BOOT_COMPLETED")

            val updateWidgetIntext = Intent(context, MorbidMeterWidgetProvider::class.java)
            updateWidgetIntext.apply {
                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            }
            context.sendBroadcast(updateWidgetIntext)
        }
    }
}
