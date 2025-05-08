package com.Backend_RMP.routes
import com.Backend_RMP.entity.Activities
import com.Backend_RMP.entity.Articles
import com.Backend_RMP.models.ActivityDTO
import com.Backend_RMP.models.ArticleDTO
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.articleRoutes() {
    route("/articles") {
        post {
            val article = call.receive<ArticleDTO>()
            val id = transaction {
                Articles.insertAndGetId {
                    it[title] = article.title
                    it[content] = article.content
                    it[author] = article.author
                    it[imageUrl] = article.imageUrl
                }.value
            }
            call.respond(mapOf("id" to id))
        }

        get {
            val articles = transaction {
                Articles.selectAll()
                    .map {
                        ArticleDTO(
                            title = it[Articles.title],
                            content = it[Articles.content],
                            author = it[Articles.author],
                            imageUrl = it[Articles.imageUrl]
                        )
                    }
            }
            call.respond(articles)
        }
    }
}