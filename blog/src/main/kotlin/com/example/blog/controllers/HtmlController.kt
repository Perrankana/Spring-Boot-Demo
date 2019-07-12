package com.example.blog.controllers

import com.example.blog.entities.Article
import com.example.blog.entities.User
import com.example.blog.repositories.ArticleRepository
import com.example.blog.extensions.format
import com.example.blog.repositories.UserRepository
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Controller
class HtmlController(private val repository: ArticleRepository) {
    @GetMapping("/")
    fun blog(model: Model): String {
        model["title"] = "Blog"
        model["articles"] = repository.findAllByOrderByAddedAtDesc().map { it.render() }
        return "blog"
    }

    @GetMapping("/article/{slug}")
    fun article(@PathVariable slug: String, model: Model): String {
        val article = repository
                .findBySlug(slug)
                ?.render()
                ?: throw IllegalArgumentException("Wrong article slug provided")
        model["title"] = article.title
        model["article"] = article
        return "article"
    }

    @RestController
    @RequestMapping("/api/article")
    class ArticleController(private val repository: ArticleRepository) {

        @GetMapping("/")
        fun findAll() = repository.findAllByOrderByAddedAtDesc()

        @GetMapping("/{slug}")
        fun findOne(@PathVariable slug: String) =
                repository.findBySlug(slug) ?: throw IllegalArgumentException("Wrong article slug provided")

    }

    @RestController
    @RequestMapping("/api/user")
    class UserController(private val repository: UserRepository) {

        @GetMapping("/")
        fun findAll() = repository.findAll()

        @GetMapping("/{login}")
        fun findOne(@PathVariable login: String) = repository.findByLogin(login)
    }

    fun Article.render() = RenderedArticle(
            slug,
            title,
            headline,
            content,
            author,
            addedAt.format()
    )

    data class RenderedArticle(
            val slug: String,
            val title: String,
            val headline: String,
            val content: String,
            val author: User,
            val addedAt: String)
}