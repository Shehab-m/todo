package com.cheesecake.todo.data.models.response

import java.io.Serializable

data class SignUpValue(
    val userId: String,
    val username: String
) : Serializable
