package com.example.fieldforms.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.fieldforms.data.local.entity.FormNodeEntity
import com.example.fieldforms.data.local.entity.ResponseOptionEntity
import com.example.fieldforms.data.local.entity.ResponseSetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FormDao {

    @Query("SELECT * FROM form_nodes ORDER BY sortOrder")
    fun observeAllNodes(): Flow<List<FormNodeEntity>>

    @Query("SELECT * FROM response_sets")
    fun observeResponseSets(): Flow<List<ResponseSetEntity>>

    @Query("SELECT * FROM response_options ORDER BY sortOrder")
    fun observeResponseOptions(): Flow<List<ResponseOptionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNodes(nodes: List<FormNodeEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResponseSets(responseSets: List<ResponseSetEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResponseOptions(responseOptions: List<ResponseOptionEntity>)

    @Query("DELETE FROM form_nodes")
    suspend fun deleteAllNodes()

    @Query("DELETE FROM response_sets")
    suspend fun deleteAllResponseSets()

    @Query("DELETE FROM response_options")
    suspend fun deleteAllResponseOptions()

    @Transaction
    suspend fun replaceAll(
        nodes: List<FormNodeEntity>,
        responseSets: List<ResponseSetEntity>,
        responseOptions: List<ResponseOptionEntity>,
    ) {
        deleteAllResponseOptions()
        deleteAllResponseSets()
        deleteAllNodes()
        insertResponseSets(responseSets)
        insertResponseOptions(responseOptions)
        insertNodes(nodes)
    }
}
