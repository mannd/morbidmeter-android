package org.epstudios.morbidmeter

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.util.Log.*
import android.webkit.PermissionRequest
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

/**
Copyright (C) 2025 EP Studios, Inc.
www.epstudiossoftware.com

Created by mannd on 2/24/25.

This file is part of TestAppWidget.

TestAppWidget is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

TestAppWidget is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with TestAppWidget.  If not, see <http://www.gnu.org/licenses/>.
 */
class PermissionRequestActivity : ComponentActivity() {

    companion object {
        private const val LOG_TAG = "PermissionRequest"
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Log.d(LOG_TAG, "SCHEDULE_EXACT_ALARM Permission granted")
            } else {
                Log.d(LOG_TAG, "SCHEDULE_EXACT_ALARM Permission denied")
                if (!ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        android.Manifest.permission.SCHEDULE_EXACT_ALARM
                    )
                ) {
                    showAppSettings()
                }
            }
            //finish()
        }

    private fun showAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(LOG_TAG, "PermissionRequestActivity.onCreate")
        setContentView(R.layout.permisionrequest)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Log.d(LOG_TAG, "Checking for SCHEDULE_EXACT_ALARM permission")
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.SCHEDULE_EXACT_ALARM)
                != PackageManager.PERMISSION_GRANTED) {
                Log.d(LOG_TAG, "Requesting SCHEDULE_EXACT_ALARM permission")
                requestPermissionLauncher.launch(android.Manifest.permission.USE_EXACT_ALARM)
                requestPermissionLauncher.launch(android.Manifest.permission.SCHEDULE_EXACT_ALARM)
            } else {
                finish()
            }
        }
    }
}
