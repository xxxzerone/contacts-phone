package com.example.contactphone.domain.usecase

import com.example.contactphone.domain.model.Contact
import com.example.contactphone.domain.repository.ContactRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetContactsUseCase @Inject constructor(
    private val repository: ContactRepository
) {
    operator fun invoke(): Flow<List<Contact>> {
        return repository.getContacts()
    }
}
