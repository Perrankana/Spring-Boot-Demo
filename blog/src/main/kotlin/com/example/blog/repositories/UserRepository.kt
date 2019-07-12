package com.example.blog.repositories

import com.example.blog.entities.User
import org.springframework.data.repository.CrudRepository

interface UserRepository : CrudRepository<User, Long> {
    fun findByLogin(login: String): User
}