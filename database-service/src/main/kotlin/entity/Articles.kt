package com.Backend_RMP.entity

import org.jetbrains.exposed.dao.id.IntIdTable

object Articles : IntIdTable() {
    val title = varchar("title", 255)
    val content = text("content")
    val author = varchar("author", 100)
    val imageUrl = varchar("image_url", 255).nullable()
}