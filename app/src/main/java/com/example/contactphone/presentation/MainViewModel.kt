package com.example.contactphone.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.contactphone.domain.model.Contact
import com.example.contactphone.domain.usecase.GetContactsUseCase
import com.example.contactphone.domain.usecase.ToggleFavoriteUseCase
import com.example.contactphone.util.HangulUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MainState(
    val contacts: List<Contact> = emptyList(),
    val filteredContacts: List<Contact> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val permissionGranted: Boolean = false
)

sealed class MainIntent {
    data class Search(val query: String) : MainIntent()
    data class ToggleFavorite(val contactId: String, val isFavorite: Boolean) : MainIntent()
    data class PermissionResult(val isGranted: Boolean) : MainIntent()
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getContactsUseCase: GetContactsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(MainState())
    val state = _state.asStateFlow()

    fun processIntent(intent: MainIntent) {
        when (intent) {
            is MainIntent.Search -> {
                _state.update {
                    val filtered = filterContacts(it.contacts, intent.query)
                    it.copy(searchQuery = intent.query, filteredContacts = filtered)
                }
            }
            is MainIntent.ToggleFavorite -> {
                viewModelScope.launch {
                    toggleFavoriteUseCase(intent.contactId, intent.isFavorite)
                }
            }
            is MainIntent.PermissionResult -> {
                _state.update { it.copy(permissionGranted = intent.isGranted) }
                if (intent.isGranted) {
                    loadContacts()
                } else {
                    _state.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    private fun loadContacts() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            getContactsUseCase().collect { contacts ->
                _state.update { currentState ->
                    val filtered = filterContacts(contacts, currentState.searchQuery)
                    currentState.copy(
                        contacts = contacts, 
                        filteredContacts = filtered,
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun filterContacts(contacts: List<Contact>, query: String): List<Contact> {
        if (query.isBlank()) return contacts
        return contacts.filter { 
            HangulUtils.match(it.name, query) || it.phoneNumber.contains(query) 
        }
    }
}
