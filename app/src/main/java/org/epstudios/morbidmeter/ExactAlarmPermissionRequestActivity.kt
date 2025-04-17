package org.epstudios.morbidmeter

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts


/**
Copyright (C) 2025 EP Studios, Inc.
www.epstudiossoftware.com

Created by mannd on 2/24/25.

This file is part of MorbidMeter.

MorbidMeter is free software: you can redistribute it and/or modify
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

class ExactAlarmPermissionRequestActivity : ComponentActivity() {

    companion object {
        private const val LOG_TAG = "PermissionRequest"
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            //This callback is only for permissions such as location, camera, etc.
            //This will never be called for SCHEDULE_EXACT_ALARM
            if (isGranted) {
                Log.d(LOG_TAG, "SCHEDULE_EXACT_ALARM Permission granted")
            } else {
                Log.d(LOG_TAG, "SCHEDULE_EXACT_ALARM Permission denied")
            }
        }

    private fun showAppSettings() {
        // The correct way is to use the ACTION_REQUEST_SCHEDULE_EXACT_ALARM intent
        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                data = Uri.fromParts("package", packageName, null)
            }
        } else {
            // Fallback for older Android versions if necessary.
            // In those version, you don't need a specific permission
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", packageName, null)
            }
        }
        startActivity(intent)
    }

    private fun showAppPermissionsRationaleDialog() {
        Log.d(LOG_TAG, "showAppPermissionsRationaleDialog")
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.exact_alarm_rationale_title) // Assuming you have this string resource
        builder.setMessage(R.string.exact_alarm_rationale_message) // And this string resource
        builder.setPositiveButton(R.string.go_to_settings) { _, _ -> // And this string resource
            showAppSettings() // Proceed to app settings
        }
        builder.setNegativeButton(R.string.cancel) { dialog, _ -> // And this string resource
            dialog.dismiss() // Dismiss the dialog
            finish() // Close this activity
        }
        builder.show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(LOG_TAG, "ExactAlarmPermissionRequestActivity.onCreate")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Log.d(LOG_TAG, "Checking for SCHEDULE_EXACT_ALARM permission")
            if (!getSystemService(android.app.AlarmManager::class.java).canScheduleExactAlarms()) {
                Log.d(LOG_TAG, "Requesting SCHEDULE_EXACT_ALARM permission")
                showAppPermissionsRationaleDialog()
            } else {
                Log.d(LOG_TAG, "SCHEDULE_EXACT_ALARM Permission already granted")
                finish()
            }
        } else {
            Log.d(LOG_TAG, "No need to request permission for older android versions")
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(LOG_TAG, "onResume")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (getSystemService(android.app.AlarmManager::class.java).canScheduleExactAlarms()) {
                Log.d(LOG_TAG, "Permission now granted.")
                finish()
            } else {
                Log.d(LOG_TAG, "Permission still denied.")
            }
        }
    }
}
//class ExactAlarmPermissionRequestActivity : ComponentActivity() {
//
//    companion object {
//        private const val LOG_TAG = "PermissionRequest"
//    }
//
//    private val requestPermissionLauncher =
//        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
//            //This callback is only for permissions such as location, camera, etc.
//            //This will never be called for SCHEDULE_EXACT_ALARM
//            if (isGranted) {
//                Log.d(LOG_TAG, "SCHEDULE_EXACT_ALARM Permission granted")
//            } else {
//                Log.d(LOG_TAG, "SCHEDULE_EXACT_ALARM Permission denied")
//                // This should not be called for SCHEDULE_EXACT_ALARM
//                if (!ActivityCompat.shouldShowRequestPermissionRationale(
//                        this,
//                        android.Manifest.permission.SCHEDULE_EXACT_ALARM
//                    )
//                ) {
//                    // This should not be called for SCHEDULE_EXACT_ALARM
//                    showAppSettings()
//                }
//            }
//        }
//
//    private fun showAppSettings() {
//        // The correct way is to use the ACTION_REQUEST_SCHEDULE_EXACT_ALARM intent
//        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
//                data = Uri.fromParts("package", packageName, null)
//            }
//        } else {
//            // Fallback for older Android versions if necessary.
//            // In those version, you don't need a specific permission
//            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
//                data = Uri.fromParts("package", packageName, null)
//            }
//        }
//        startActivity(intent)
//    }
//
//    private fun showAppPermissionsRationaleDialog() {
//        Log.d(LOG_TAG, "showAppPermissionsRationaleDialog")
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        Log.d(LOG_TAG, "ExactAlarmPermissionRequestActivity.onCreate")
//        setContentView(R.layout.permisionrequest)
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            Log.d(LOG_TAG, "Checking for SCHEDULE_EXACT_ALARM permission")
//            if (!getSystemService(android.app.AlarmManager::class.java).canScheduleExactAlarms()) {
//                Log.d(LOG_TAG, "Requesting SCHEDULE_EXACT_ALARM permission")
//                showAppPermissionsRationaleDialog()
//                showAppSettings()
//            } else {
//                Log.d(LOG_TAG, "SCHEDULE_EXACT_ALARM Permission already granted")
//                finish()
//            }
//        } else {
//            Log.d(LOG_TAG, "No need to request permission for older android versions")
//            finish()
//        }
//    }
//
//    override fun onResume() {
//        super.onResume()
//        Log.d(LOG_TAG, "onResume")
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            if (getSystemService(android.app.AlarmManager::class.java).canScheduleExactAlarms()) {
//                Log.d(LOG_TAG, "Permission now granted.")
//                finish()
//            } else {
//                Log.d(LOG_TAG, "Permission still denied.")
//            }
//        }
//    }
//}
