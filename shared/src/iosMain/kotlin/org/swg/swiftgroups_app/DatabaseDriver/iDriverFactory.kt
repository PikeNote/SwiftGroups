package org.swg.swiftgroups_app.DatabaseDriver


import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import org.swg.swiftgroups_app.db.Database

actual suspend fun provideDbDriver(
    schema: SqlSchema<QueryResult.Value<Unit>>
): SqlDriver {
    return NativeSqliteDriver(schema,"swiftgroups.db")
}