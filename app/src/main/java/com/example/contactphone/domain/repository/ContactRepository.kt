package com.example.contactphone.domain.repository

import com.example.contactphone.domain.model.Contact
import kotlinx.coroutines.flow.Flow

interface ContactRepository {
    fun getContacts(): Flow<List<Contact>>
    suspend fun toggleFavorite(contactId: String, isFavorite: Boolean)
}
