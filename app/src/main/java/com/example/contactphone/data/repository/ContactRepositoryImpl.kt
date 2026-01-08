package com.example.contactphone.data.repository

import com.example.contactphone.data.local.dao.FavoriteDao
import com.example.contactphone.data.local.entity.FavoriteEntity
import com.example.contactphone.data.source.ContactDataSource
import com.example.contactphone.domain.model.Contact
import com.example.contactphone.domain.repository.ContactRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ContactRepositoryImpl @Inject constructor(
    private val contactDataSource: ContactDataSource,
    private val favoriteDao: FavoriteDao
) : ContactRepository {

    override fun getContacts(): Flow<List<Contact>> {
        val systemContactsFlow = flow {
            emit(contactDataSource.getSystemContacts())
        }.flowOn(Dispatchers.IO)

        return combine(systemContactsFlow, favoriteDao.getFavorites()) { contacts, favorites ->
            val favoriteIds = favorites.map { it.contactId }.toSet()
            contacts.map { contact ->
                contact.copy(isFavorite = favoriteIds.contains(contact.id))
            }
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun toggleFavorite(contactId: String, isFavorite: Boolean) {
        if (isFavorite) {
            favoriteDao.insert(FavoriteEntity(contactId))
        } else {
            favoriteDao.delete(contactId)
        }
    }
}
