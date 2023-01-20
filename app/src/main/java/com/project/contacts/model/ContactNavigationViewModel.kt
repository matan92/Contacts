package com.project.contacts.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.project.contacts.data.Contact

class ContactNavigationViewModel: ViewModel() {
    var contact by mutableStateOf<Contact?>(null)

    fun updateContact(newContact: Contact){
        contact= newContact
    }
}