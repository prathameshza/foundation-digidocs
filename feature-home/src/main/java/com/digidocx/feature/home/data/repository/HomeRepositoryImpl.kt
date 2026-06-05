package com.digidocx.feature.home.data.repository

import com.digidocx.core.database.dao.DocumentDao
import com.digidocx.feature.home.data.mapper.toDomainModel
import com.digidocx.feature.home.domain.model.HomeItem
import com.digidocx.feature.home.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val documentDao: DocumentDao
) : HomeRepository {

    override fun getRecentItems(): Flow<List<HomeItem>> {
        return documentDao.getRecentDocuments().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getLocalItems(): Flow<List<HomeItem>> {
        return documentDao.getLocalDocuments().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getItemsInFolder(folderId: String): Flow<List<HomeItem>> {
        return documentDao.getDocumentsInFolder(folderId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun deleteItem(itemId: String) {
        documentDao.deleteDocument(itemId)
    }
}