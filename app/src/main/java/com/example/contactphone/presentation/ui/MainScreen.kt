package com.example.contactphone.presentation.ui

import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.contactphone.domain.model.Contact
import com.example.contactphone.presentation.MainIntent
import com.example.contactphone.presentation.MainViewModel

@Composable
fun MainScreen(viewModel: MainViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            viewModel.processIntent(MainIntent.PermissionResult(isGranted))
        }
    )

    LaunchedEffect(Unit) {
        val permission = android.Manifest.permission.READ_CONTACTS
        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
            viewModel.processIntent(MainIntent.PermissionResult(true))
        } else {
            permissionLauncher.launch(permission)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            SearchBar(
                query = state.searchQuery,
                onQueryChange = { viewModel.processIntent(MainIntent.Search(it)) }
            )
        }
    ) { innerPadding ->
        if (!state.permissionGranted) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("Permission needed to load contacts.")
            }
        } else if (state.isLoading) {
             Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            ContactList(
                contacts = state.filteredContacts,
                onToggleFavorite = { id, isFav -> 
                    viewModel.processIntent(MainIntent.ToggleFavorite(id, !isFav)) 
                },
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit
) {
    androidx.compose.material3.SearchBar(
        query = query,
        onQueryChange = onQueryChange,
        onSearch = {},
        active = false,
        onActiveChange = {},
        placeholder = { Text("초성 검색 (예: ㅎㄱㄷ)") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {}
}

@Composable
fun ContactList(
    contacts: List<Contact>,
    onToggleFavorite: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(contacts, key = { it.id }) { contact ->
            ContactItem(contact = contact, onToggleFavorite = onToggleFavorite)
        }
    }
}

@Composable
fun ContactItem(
    contact: Contact,
    onToggleFavorite: (String, Boolean) -> Unit
) {
    ListItem(
        headlineContent = { Text(contact.name) },
        supportingContent = { Text(contact.phoneNumber) },
        trailingContent = {
            IconButton(onClick = { onToggleFavorite(contact.id, contact.isFavorite) }) {
                Icon(
                    imageVector = if (contact.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (contact.isFavorite) Color.Red else Color.Gray
                )
            }
        }
    )
    HorizontalDivider()
}
