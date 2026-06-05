package com.digidocx.core.database.entity

data class DocumentEntity(
    val id: String,
    val name: String,
    val isFolder: Boolean,
    val dateModified: String,
    val parentId: String?,
    val size: String,
    val pageCount: Int,
    val isLocal: Boolean,
    val extension: String,
    val itemCount: Int = 0
)