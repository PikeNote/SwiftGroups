package org.swg.swiftgroups_app.CGAPI

import com.multiplatform.webview.cookie.Cookie
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.setCookie
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json


object CGAPI {
    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(contentType = ContentType.Any, json = Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
    }
    var cookies : List<Cookie> = emptyList()

    suspend fun grabMyEvents(): UpcomingEvents {
        var cookieHeader = ""
        val response: HttpResponse = client.get("https://community.case.edu/mobile_ws/v18/mobile_events_new?view=events_i_am_attending&limit=15&range=0") {
            method = HttpMethod.Get
            cookies.forEach { ck ->
                cookieHeader += "${ck.name}=${ck.value};"
            }
            headers {
                append(HttpHeaders.Host, "community.case.edu")
                append(HttpHeaders.Cookie, cookieHeader)
            }
        }

        if (response.status.value in 200..299) {
            println("Successful response!")

            val upcomingEventData: UpcomingEvents = response.body()

            return upcomingEventData;
        } else {
            return UpcomingEvents(0, emptyList(), 0)
        }

    }
}