package com.digidocx.feature.home.data.mapper

import com.digidocx.core.database.entity.DocumentEntity
import com.digidocx.feature.home.domain.model.HomeItem

fun DocumentEntity.toDomainModel(): HomeItem {
    return if (isFolder) {
        HomeItem.Folder(
            id = id,
            name = name,
            dateModified = dateModified,
            parentId = parentId,
            itemCount = itemCount
        )
    } else {
        HomeItem.File(
            id = id,
            name = name,
            dateModified = dateModified,
            parentId = parentId,
            size = size,
            pageCount = pageCount,
            isLocal = isLocal,
            extension = extension
        )
    }
}