package com.pluton.orbitscanner.core.database.dao

import com.pluton.orbitscanner.core.database.entity.DocumentEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockDocumentDao @Inject constructor() : DocumentDao {
    private val listState = MutableStateFlow(
        listOf(
            DocumentEntity("f1", "Rakesh", true, "10 May 2026", null, "1 item", 0, false, "", 1),
            DocumentEntity("f2", "Invoices", true, "10 May 2026", null, "3 files", 0, false, "", 3),
            DocumentEntity("d1", "Invoice_May_2024.pdf", false, "07 May 2026 02:34 PM", null, "2.4 MB", 3, false, "pdf"),
            DocumentEntity("d2", "Project_Proposal.docx", false, "07 May 2026 02:19 PM", null, "1.8 MB", 5, false, "docx"),
            DocumentEntity("d3", "Meeting_Notes.txt", false, "07 May 2026 01:24 PM", null, "320 KB", 1, false, "txt"),
            DocumentEntity("d4", "Annual_Report_2024.pdf", false, "07 May 2026 12:30 PM", null, "3.7 MB", 12, false, "pdf"),
            DocumentEntity("d5", "Notes_Image.jpg", false, "07 May 2026 08:30 AM", null, "1.7 MB", 1, false, "jpg"),
            DocumentEntity("d6", "Report_May2026.xlsx", false, "07 May 2026 11:22 AM", null, "12 KB", 1, false, "xlsx")
        )
    )

    override fun getRecentDocuments(): Flow<List<DocumentEntity>> = listState

    override fun getLocalDocuments(): Flow<List<DocumentEntity>> = listState.map {
        listOf(
            DocumentEntity("l1", "IMG-20260510-WA0020_compress.pdf", false, "2026/05/21", null, "2 pages", 2, true, "pdf"),
            DocumentEntity("l2", "RenamingLogicExp.pdf", false, "2026/05/21", null, "3 pages", 3, true, "pdf"),
            DocumentEntity("l3", "VIVEK MASTER DATA.pdf", false, "2026/05/16", null, "2 pages", 2, true, "pdf"),
            DocumentEntity("l4", "MediaQueryImplementation.pdf", false, "2026/05/15", null, "2 pages", 2, true, "pdf"),
            DocumentEntity("l5", "Scanning Method.pdf", false, "2026/05/15", null, "2 pages", 2, true, "pdf"),
            DocumentEntity("l6", "RPS_compress.pdf", false, "2026/05/14", null, "3 pages", 3, true, "pdf")
        )
    }

    override fun getDocumentsInFolder(parentId: String): Flow<List<DocumentEntity>> = listState.map {
        when (parentId) {
            "f1" -> listOf(
                DocumentEntity("d1_1", "2026-05-07 10:44.pdf", false, "11 May 2026 01:49 AM", "f1", "1.2 MB", 4, false, "pdf"),
                DocumentEntity("d1_2", "2026-05-07 21:46.pdf", false, "11 May 2026 01:49 AM", "f1", "312 KB", 1, false, "pdf"),
                DocumentEntity("d1_3", "2026-05-09 02:13.pdf", false, "09 May 2026 02:37 AM", "f1", "256 KB", 1, false, "pdf"),
                DocumentEntity("d1_4", "Report_May2026.xlsx", false, "09 May 2026 11:22 AM", "f1", "12 KB", 1, false, "xlsx"),
                DocumentEntity("d1_5", "Presentation.pptx", false, "08 May 2026 09:15 AM", "f1", "2.4 MB", 24, false, "pptx"),
                DocumentEntity("d1_6", "Notes_Image.jpg", false, "08 May 2026 08:30 AM", "f1", "1.7 MB", 1, false, "jpg")
            )
            "f2" -> listOf(
                DocumentEntity("d2_1", "INV-2026-0003.pdf", false, "10 May 2026 11:23 AM", "f2", "245 KB", 1, false, "pdf"),
                DocumentEntity("d2_2", "INV-2026-0002.pdf", false, "09 May 2026 04:15 PM", "f2", "192 KB", 1, false, "pdf"),
                DocumentEntity("d2_3", "INV-2026-0001.pdf", false, "07 May 2026 10:08 AM", "f2", "278 KB", 1, false, "pdf")
            )
            else -> emptyList()
        }
    }

    override suspend fun deleteDocument(id: String) {
        listState.value = listState.value.filterNot { it.id == id }
    }

    override suspend fun insertDocument(document: DocumentEntity) {
        listState.value = listOf(document) + listState.value
    }
}