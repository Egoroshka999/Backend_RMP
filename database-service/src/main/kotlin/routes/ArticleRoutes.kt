package com.Backend_RMP.routes
import com.Backend_RMP.entity.Activities
import com.Backend_RMP.entity.Articles
import com.Backend_RMP.entity.LogMessage
import com.Backend_RMP.models.ActivityDTO
import com.Backend_RMP.models.ArticleDTO
import com.Backend_RMP.service.MessageProducerService
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq


fun Route.articleRoutes(producer: MessageProducerService) {
    route("/articles") {
        post {
            val article = call.receive<ArticleDTO>()

            producer.sendMessage(Json.encodeToString(LogMessage(
                message = "Received article with author: ${article.author}",
                source = "POST /articles"
            )))

            val id = transaction {
                Articles.insertAndGetId {
                    it[title] = article.title
                    it[content] = article.content
                    it[author] = article.author
                    it[imageUrl] = article.imageUrl
                }.value
            }

            val log = LogMessage(
                message = "Created article with ID: $id",
                source = "POST /articles"
            )
            producer.sendMessage(Json.encodeToString(log))

            call.respond(mapOf("id" to id))
        }

        get {
            producer.sendMessage(Json.encodeToString(LogMessage(
                message = "Fetching all articles",
                source = "GET /articles"
            )))

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

            producer.sendMessage(Json.encodeToString(LogMessage(
                message = "Retrieved ${articles.size} articles",
                source = "GET /articles"
            )))

            call.respond(articles)
        }

        delete("/{id}") {
            val articleId = call.parameters["id"]?.toIntOrNull() ?: return@delete call.respondText("Invalid ID")
            
            producer.sendMessage(Json.encodeToString(LogMessage(
                message = "Attempting to delete article with ID: $articleId",
                source = "DELETE /articles/{id}"
            )))

            val deleted = transaction {
                Articles.deleteWhere { Articles.id eq articleId } > 0
            }

            if (deleted) {
                producer.sendMessage(Json.encodeToString(LogMessage(
                    message = "Successfully deleted article with ID: $articleId",
                    source = "DELETE /articles/{id}"
                )))
                call.respond(mapOf("message" to "Article deleted successfully"))
            } else {
                producer.sendMessage(Json.encodeToString(LogMessage(
                    message = "Failed to delete article with ID: $articleId - not found",
                    source = "DELETE /articles/{id}"
                )))
                call.respondText("Article not found", status = io.ktor.http.HttpStatusCode.NotFound)
            }
        }
    }
}