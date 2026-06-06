package com.pluton.orbitscanner.feature.home.domain.model

sealed interface HomeItem {
    val id: String
    val name: String
    val dateModified: String
    val parentId: String?

    data class Folder(
        override val id: String,
        override val name: String,
        override val dateModified: String,
        override val parentId: String?,
        val itemCount: Int
    ) : HomeItem

    data class File(
        override val id: String,
        override val name: String,
        override val dateModified: String,
        override val parentId: String?,
        val size: String,
        val pageCount: Int,
        val isLocal: Boolean,
        val extension: String
    ) : HomeItem
}