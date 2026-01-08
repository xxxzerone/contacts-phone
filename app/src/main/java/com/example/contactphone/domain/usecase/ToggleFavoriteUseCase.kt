package com.example.contactphone.domain.usecase

import com.example.contactphone.domain.repository.ContactRepository
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
    private val repository: ContactRepository
) {
    suspend operator fun invoke(contactId: String, isFavorite: Boolean) {
        repository.toggleFavorite(contactId, isFavorite)
    }
}
