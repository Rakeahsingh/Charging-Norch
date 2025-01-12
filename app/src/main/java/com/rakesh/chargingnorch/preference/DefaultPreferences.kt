package com.rakesh.chargingnorch.preference

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<androidx.datastore.preferences.core.Preferences> by preferencesDataStore(
    "default_pref"
)

private val x= floatPreferencesKey(Preferences.KEY_X)
private val y= floatPreferencesKey(Preferences.KEY_Y)
private val r= floatPreferencesKey(Preferences.KEY_R)


class DefaultPreferences(private val dataStore: DataStore<androidx.datastore.preferences.core.Preferences>) :
    Preferences {
    override suspend fun saveX(xValues: Float) {
        dataStore.edit {
            it[x] = xValues

        }
    }

    override suspend fun saveY(yValues: Float) {
        dataStore.edit {
            it[y] = yValues

        }
    }

    override suspend fun saveR(rValues: Float) {
        dataStore.edit {
            it[r] = rValues

        }
    }

    override fun getX(): Flow<Float> = dataStore.data.map { preference ->
        preference[x] ?: 0f
    }

    override fun getY(): Flow<Float>  = dataStore.data.map { preference ->
        preference[y] ?: 0f
    }
    override fun getR(): Flow<Float>  = dataStore.data.map { preference ->
        preference[r] ?: 0f
    }

}