package com.digidocx.core.model

enum class DocumentType {
    FOLDER, PDF, WORD, EXCEL, IMAGE, TEXT
}

data class DocumentItem(
    val id: String,
    val name: String,
    val type: DocumentType,
    val dateModified: String,
    val size: String = "",
    val pageCount: Int = 0,
    val isLocal: Boolean = false,
    val parentId: String? = null
)