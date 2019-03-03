package com.onfido.android.techtask.util

import android.app.Activity
import android.support.v7.app.AlertDialog
import com.onfido.android.techtask.R

/**
 * Created by gokhan.alici on 04.03.2019
 */
fun showCameraPermissonDeniedDialog(context: Activity): AlertDialog {
    return AlertDialog.Builder(context)
        .setTitle(R.string.title_camera_permission_required)
        .setMessage(R.string.text_camera_permission_required)
        .setPositiveButton(R.string.action_ok) { dialog, _ ->
            dialog.dismiss()
            context.finish()
        }.create()
}