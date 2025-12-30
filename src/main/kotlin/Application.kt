package com.mapme

import com.mapme.data.database.DatabaseFactory
import com.mapme.plugins.configureHTTP
import com.mapme.plugins.configureMonitoring
import com.mapme.plugins.configureOpenAPI
import com.mapme.plugins.configureRouting
import com.mapme.plugins.configureSecurity
import com.mapme.plugins.configureSerialization
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    DatabaseFactory.init()
    configureSerialization()
    configureMonitoring()
    configureHTTP()
    configureSecurity()
    configureOpenAPI()
    configureRouting()
}
