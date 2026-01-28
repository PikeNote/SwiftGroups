package org.swg.swiftgroups_app

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import org.swg.swiftgroups_app.DataStore.createDataStore
import org.swg.swiftgroups_app.DataStore.dataStoreFileName

val androidModule = module {
    single<DataStore<Preferences>> {
        createDataStore {
            androidContext().filesDir.resolve(dataStoreFileName).absolutePath
        }
    }
}