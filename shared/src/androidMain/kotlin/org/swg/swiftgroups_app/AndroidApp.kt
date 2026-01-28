package org.swg.swiftgroups_app

import android.content.Context
import java.lang.ref.WeakReference

object AndroidApp {
    private var contextRef: WeakReference<Context>? = null

    fun init(context: Context) {
        contextRef = WeakReference(context.applicationContext)
    }

    fun getContext(): Context {
        return contextRef?.get()
            ?: throw IllegalStateException("AndroidApp.init(context) was not called in your Application class!")
    }
}