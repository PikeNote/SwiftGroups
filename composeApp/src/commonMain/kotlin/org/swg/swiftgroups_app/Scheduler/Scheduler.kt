package org.swg.swiftgroups_app.Scheduler

import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.subscribe
import kotlinx.datetime.Instant
import org.swg.swiftgroups_app.DatabaseDriver.DBObject
import org.swg.swiftgroups_app.Screens.Webview.WebviewScreen
import org.swg.swiftgroupsapp.db.AutoRegister

object GlobalTaskScheduler {
    val swiftdataQueries = DBObject.db.swiftdataQueries

    private val schedulerScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    fun scheduleTask(ag : AutoRegister, navigator: Navigator) {
        schedulerScope.launch {
            val now = Clock.System.now()
            val time = Instant.parse(ag.signup_time)

            if(now > time) {
                swiftdataQueries.removeAutoRegister(ag.eventId)
                return@launch
            }

            val delay = time - now

            delay(delay)

            withContext(Dispatchers.Main) {
                autoRegister(ag, navigator)
            }
        }
    }

    fun cancelAllTasks() {
        schedulerScope.cancel()
    }

    fun attachToLifecycle(lifecycle: Lifecycle, navigator: Navigator) {
        lifecycle.subscribe(
            onResume = {
                swiftdataQueries.fetchAutoRegister().executeAsList().forEach {
                    scheduleTask(it, navigator)
                }
            },
            onPause = {
                cancelAllTasks()
            }
        )
    }

    fun autoRegister(ag : AutoRegister, navigator: Navigator) {
        navigator.push(WebviewScreen(ag.link, "Registration - ${ag.eventId}" ))
    }


}

