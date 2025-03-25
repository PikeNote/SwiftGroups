package org.swg.swiftgroups_app.DatabaseDriver

import kotlinx.coroutines.runBlocking
import org.swg.swiftgroups_app.db.Database

object DBObject {

    lateinit var db: Database;

    init {
        runBlocking {
            db = Database(provideDbDriver(Database.Schema))
        }
    }
}