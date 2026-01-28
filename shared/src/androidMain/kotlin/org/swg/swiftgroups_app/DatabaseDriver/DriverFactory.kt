package org.swg.swiftgroups_app.DatabaseDriver

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import org.swg.swiftgroups_app.AndroidApp

actual suspend fun provideDbDriver(
    schema: SqlSchema<QueryResult.Value<Unit>>
): SqlDriver {
    return AndroidSqliteDriver(schema, context = AndroidApp.getContext(), "swiftdata.db")
}