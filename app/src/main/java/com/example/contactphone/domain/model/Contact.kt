package com.example.contactphone.domain.model

data class Contact(
    val id: String,
    val name: String,
    val phoneNumber: String,
    val photoUri: String?,
    val isFavorite: Boolean = false
)
