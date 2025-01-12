package com.rakesh.chargingnorch.preference


import kotlinx.coroutines.flow.Flow


interface Preferences {

    suspend fun saveX(xValues: Float)
    suspend fun saveY(yValues: Float)
    suspend fun saveR(rValues: Float)


    fun getX(): Flow<Float>
    fun getY(): Flow<Float>
    fun getR(): Flow<Float>


    companion object {
        const val KEY_X = "x"
        const val KEY_Y = "Y"
        const val KEY_R = "r"

    }
}