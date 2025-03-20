package org.swg.swiftgroups_app.DatabaseDriver

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import org.swg.swiftgroups_app.db.Database;

expect suspend fun provideDbDriver(
    schema: SqlSchema<QueryResult.Value<Unit>>
): SqlDriver
