package com.project.contacts.model

import android.annotation.SuppressLint
import android.app.Application
import android.content.ContentProviderOperation
import android.content.ContentResolver
import android.database.Cursor
import android.provider.ContactsContract
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.AndroidViewModel
import com.project.contacts.data.Contact


class ContactsViewModel(application: Application) : AndroidViewModel(application) {

    // I am using Android View Model in order to get access to content resolver context

    var state by mutableStateOf(ContactsState())

    var contactsStateList = getContacts()


    @SuppressLint("Range", "Recycle")
    fun getContacts(): MutableList<Contact> {

        val list: MutableList<Contact> = arrayListOf()

        val cr = getApplication<Application>().applicationContext.contentResolver
        val cur = cr.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
            null, null, null
        )

        if (cur!!.count > 0) {
            while (cur.moveToNext()) {
                val id =
                    cur.getInt(
                        cur.getColumnIndex
                            (ContactsContract.CommonDataKinds.Phone.NAME_RAW_CONTACT_ID)
                    )


                // Name Types
                var firstName = ""
                var lastName = ""
                var displayName = ""

                val whereName =
                    ContactsContract.Data.MIMETYPE + " = ? AND " +
                            ContactsContract.CommonDataKinds.StructuredName.CONTACT_ID + " = ?"
                val whereNameParams = arrayOf(
                    ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE, id.toString()
                )
                val nameCur: Cursor = cr.query(
                    ContactsContract.Data.CONTENT_URI,
                    null,
                    whereName,
                    whereNameParams,
                    ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME
                )!!
                while (nameCur.moveToNext()) {
                    firstName =
                        nameCur.getString(
                            nameCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME))
                    if (
                        nameCur.getString(
                            nameCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME)
                        ) != null)
                        lastName =
                            nameCur.getString(
                                nameCur.getColumnIndex(
                                    ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME))
                    displayName =
                        nameCur.getString(
                            nameCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME))
                }
                nameCur.close()

                if (firstName.isEmpty() || lastName.isEmpty())
                    displayName = cur.getString(
                        cur.getColumnIndex
                            (ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                    )
                // Email Types

                val emailCur: Cursor? = cr.query(
                    ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                    arrayOf(id.toString()),
                    null
                )

                val emailList: HashMap<String, String> = HashMap()

                while (emailCur!!.moveToNext()) {
                    val emailIndex: Int =
                        emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA)
                    val emailType: Int =
                        emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE)
                    when (emailCur.getInt(emailType)) {
                        ContactsContract.CommonDataKinds.Email.TYPE_HOME ->
                            emailList["Home"] = emailCur.getString(emailIndex)
                        ContactsContract.CommonDataKinds.Email.TYPE_WORK ->
                            emailList["Work"] = emailCur.getString(emailIndex)
                        ContactsContract.CommonDataKinds.Email.TYPE_MOBILE ->
                            emailList["Mobile"] = emailCur.getString(emailIndex)
                        ContactsContract.CommonDataKinds.Email.TYPE_OTHER ->
                            emailList["Other"] = emailCur.getString(emailIndex)
                        ContactsContract.CommonDataKinds.Email.TYPE_CUSTOM ->
                            emailList["Custom"] = emailCur.getString(emailIndex)
                    }

                }

                emailCur.close()

                // Phone Types

                val phoneList: HashMap<String, String> = HashMap()

                val pCur = cr.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                            + " = ?", arrayOf(id.toString()), null
                )

                if (pCur != null) {
                    while (pCur.moveToNext()) {
                        val phoneIndex: Int =
                            pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                        val phoneType: Int =
                            pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE)
                        when (pCur.getInt(phoneType)) {
                            ContactsContract.CommonDataKinds.Phone.TYPE_WORK ->
                                phoneList["Work"] = pCur.getString(phoneIndex)
                            ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE ->
                                phoneList["Mobile"] = pCur.getString(phoneIndex)
                            ContactsContract.CommonDataKinds.Phone.TYPE_HOME ->
                                phoneList["Home"] = pCur.getString(phoneIndex)
                            ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM ->
                                phoneList["Custom"] = pCur.getString(phoneIndex)
                            ContactsContract.CommonDataKinds.Phone.TYPE_ASSISTANT ->
                                phoneList["Assistant"] = pCur.getString(phoneIndex)
                            ContactsContract.CommonDataKinds.Phone.TYPE_OTHER ->
                                phoneList["Other"] = pCur.getString(phoneIndex)
                            ContactsContract.CommonDataKinds.Phone.TYPE_CALLBACK ->
                                phoneList["Callback"] = pCur.getString(phoneIndex)
                            ContactsContract.CommonDataKinds.Phone.TYPE_CAR ->
                                phoneList["Car"] = pCur.getString(phoneIndex)
                            ContactsContract.CommonDataKinds.Phone.TYPE_COMPANY_MAIN ->
                                phoneList["Company"] = pCur.getString(phoneIndex)
                            ContactsContract.CommonDataKinds.Phone.TYPE_FAX_HOME ->
                                phoneList["FaxHome"] = pCur.getString(phoneIndex)
                            ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK ->
                                phoneList["FaxWork"] = pCur.getString(phoneIndex)
                            ContactsContract.CommonDataKinds.Phone.TYPE_OTHER_FAX ->
                                phoneList["OtherFax"] = pCur.getString(phoneIndex)
                            ContactsContract.CommonDataKinds.Phone.TYPE_ISDN ->
                                phoneList["ISDN"] = pCur.getString(phoneIndex)
                            ContactsContract.CommonDataKinds.Phone.TYPE_MAIN ->
                                phoneList["Main"] = pCur.getString(phoneIndex)
                            ContactsContract.CommonDataKinds.Phone.TYPE_MMS ->
                                phoneList["MMS"] = pCur.getString(phoneIndex)
                            ContactsContract.CommonDataKinds.Phone.TYPE_PAGER ->
                                phoneList["Pager"] = pCur.getString(phoneIndex)
                            ContactsContract.CommonDataKinds.Phone.TYPE_RADIO ->
                                phoneList["Radio"] = pCur.getString(phoneIndex)
                            ContactsContract.CommonDataKinds.Phone.TYPE_TELEX ->
                                phoneList["Telex"] = pCur.getString(phoneIndex)
                            ContactsContract.CommonDataKinds.Phone.TYPE_TTY_TDD ->
                                phoneList["TTY_TDD"] = pCur.getString(phoneIndex)
                            ContactsContract.CommonDataKinds.Phone.TYPE_WORK_MOBILE ->
                                phoneList["WorkMobile"] = pCur.getString(phoneIndex)
                            ContactsContract.CommonDataKinds.Phone.TYPE_WORK_PAGER ->
                                phoneList["WorkPager"] = pCur.getString(phoneIndex)
                        }
                    }
                }

                pCur?.close()

                if (phoneList.isEmpty())
                    phoneList["Mobile"] = cur.getString(
                        cur.getColumnIndex
                            (ContactsContract.CommonDataKinds.Phone.NUMBER)
                    )

                if (phoneList.values.all { value-> !value[0].isDigit() })
                    phoneList["Mobile"] = phoneList["Mobile"].toString().
                    replace("{","").replace("}","").
                    split("=")[1].trim()

                // Contact Photo
                val photo = cur.getString(
                    cur.getColumnIndex(ContactsContract.CommonDataKinds.Photo.PHOTO_URI)
                )

                list.add(
                    Contact(
                        id, firstName, lastName, displayName, emailList, phoneList,
                        photo?.toString() ?: ""
                    )
                )
            }
        }

        list.sortBy { contact -> contact.displayName }
        return list
    }


    fun editContact(firstName: String,
                    lastName: String,
                    email: String,
                    phone: String,
                    contactId: String)
    {

        val contentResolver: ContentResolver =
            getApplication<Application>().applicationContext.contentResolver
        val where =
            ContactsContract.Data.CONTACT_ID + " = ? AND " +
                    ContactsContract.Data.MIMETYPE + " = ?"
        val emailParams =
            arrayOf(contactId, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
        val nameParams =
            arrayOf(contactId, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
        val numberParams =
            arrayOf(contactId, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
        val ops = ArrayList<ContentProviderOperation>()
        if (email.isNotEmpty()) {
            ops.add(
                ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                    .withSelection(where, emailParams)
                    .withValue(ContactsContract.CommonDataKinds.Email.DATA, email)
                    .build()
            )
        }
        if (firstName.isNotEmpty()) {
            ops.add(
                ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                    .withSelection(where, nameParams)
                    .withValue(
                        ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, firstName
                    )
                    .build()
            )
        }
        if (lastName.isNotEmpty()) {
            ops.add(
                ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                    .withSelection(where, nameParams)
                    .withValue(
                        ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, lastName
                    )
                    .build()
            )
        }
        if (phone.isNotEmpty()) {
            ops.add(
                ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                    .withSelection(where, numberParams)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone)
                    .build()
            )
        }
        contentResolver.applyBatch(ContactsContract.AUTHORITY, ops)
    }


    fun onAction(userAction: UserAction) {
        when (userAction) {
            UserAction.SearchIconClicked -> {
                state = state.copy(isSearchBarVisible = true)
            }
            UserAction.CloseIconClicked -> {
                state = state.copy(isSearchBarVisible = false)
            }
            is UserAction.TextFieldInput -> {
                state = state.copy(searchText = userAction.text)
                searchContact(userAction.text)
            }
        }
    }


    private fun searchContact(query: String) {
        val newList = getContacts().filter { contact ->
            contact.displayName.contains(query, ignoreCase = true)
        }
        contactsStateList = newList as MutableList<Contact>
    }
}

