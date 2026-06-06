package com.pluton.orbitscanner.core.database.dao

import com.pluton.orbitscanner.core.database.entity.DocumentEntity
import kotlinx.coroutines.flow.Flow

interface DocumentDao {
    fun getRecentDocuments(): Flow<List<DocumentEntity>>
    fun getLocalDocuments(): Flow<List<DocumentEntity>>
    fun getDocumentsInFolder(parentId: String): Flow<List<DocumentEntity>>
    suspend fun deleteDocument(id: String)
    suspend fun insertDocument(document: DocumentEntity)
}