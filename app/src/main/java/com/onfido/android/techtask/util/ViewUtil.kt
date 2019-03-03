package com.onfido.android.techtask.util

import android.app.Activity
import android.support.annotation.IdRes
import android.view.View

/**
 * Created by gokhan.alici on 04.03.2019
 */
fun <T : View> Activity.bind(@IdRes idRes: Int): Lazy<T> {
    @Suppress("UNCHECKED_CAST")
    return unsafeLazy { findViewById(idRes) as T }
}

fun <T : View> View.bind(@IdRes idRes: Int): Lazy<T> {
    @Suppress("UNCHECKED_CAST")
    return unsafeLazy { findViewById(idRes) as T }
}

private fun <T> unsafeLazy(initializer: () -> T) = lazy(LazyThreadSafetyMode.NONE, initializer)