package com.project.contacts.model

sealed class UserAction {
    object SearchIconClicked: UserAction()
    object CloseIconClicked: UserAction()
    data class TextFieldInput(val text: String): UserAction()
}