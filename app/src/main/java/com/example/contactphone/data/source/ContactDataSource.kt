package com.example.contactphone.data.source

import android.content.Context
import android.provider.ContactsContract
import com.example.contactphone.domain.model.Contact
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ContactDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun getSystemContacts(): List<Contact> {
        val contacts = mutableListOf<Contact>()
        val contentResolver = context.contentResolver
        
        try {
            val cursor = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                arrayOf(
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER,
                    ContactsContract.CommonDataKinds.Phone.PHOTO_URI
                ),
                null,
                null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
            )

            cursor?.use {
                val idIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
                val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                val photoIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI)

                while (it.moveToNext()) {
                    val id = if (idIndex != -1) it.getString(idIndex) else ""
                    val name = if (nameIndex != -1) it.getString(nameIndex) else "Unknown"
                    val number = if (numberIndex != -1) it.getString(numberIndex) else ""
                    val photoUri = if (photoIndex != -1) it.getString(photoIndex) else null

                    // Basic deduplication could happen here if one contact has multiple numbers, 
                    // but for now we list all phone entries as separate items or just take the first one?
                    // PRD says "list of contacts". Usually distinct by Contact ID.
                    // But Phone.CONTENT_URI returns one row per phone number.
                    // Let's keep it simple: one row per phone number.
                    
                    contacts.add(
                        Contact(
                            id = id,
                            name = name,
                            phoneNumber = number,
                            photoUri = photoUri,
                            isFavorite = false
                        )
                    )
                }
            }
        } catch (e: SecurityException) {
            // Permission not granted or other security issue
            e.printStackTrace()
            return emptyList()
        } catch (e: Exception) {
             e.printStackTrace()
             return emptyList()
        }
        
        return contacts
    }
}
