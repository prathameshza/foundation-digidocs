package com.digidocx.feature.home.domain.repository

import com.digidocx.feature.home.domain.model.HomeItem
import kotlinx.coroutines.flow.Flow

interface HomeRepository {
    fun getRecentItems(): Flow<List<HomeItem>>
    fun getLocalItems(): Flow<List<HomeItem>>
    fun getItemsInFolder(folderId: String): Flow<List<HomeItem>>
    suspend fun deleteItem(itemId: String)
}