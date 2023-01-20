package com.project.contacts.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.project.contacts.model.ContactNavigationViewModel


@Composable
fun ContactDetails(
    navController: NavHostController?,
    sharedContactViewModel: ContactNavigationViewModel
) {
    val contact = sharedContactViewModel.contact

    Column {

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            )
            {

                IconButton(
                    onClick = { navController?.navigateUp() },
                    modifier = Modifier.padding(6.dp)
                ) {
                    Icon(
                        painter = painterResource(id = com.project.contacts.R.drawable.ic_back),
                        contentDescription = "Back",
                    )
                }

                IconButton(
                    onClick = { navController?.navigate("edit_contact") },
                    modifier = Modifier.padding(6.dp)
                ) {
                    Icon(
                        painter = painterResource(id = com.project.contacts.R.drawable.ic_edit),
                        contentDescription = "Edit",
                    )
                }
            }

            contact?.firstName?.ifEmpty {
                contact.displayName
            }?.let {
                ContactPicture(it, contact.photo, 100.dp, 50.sp, 120.dp)
            }

            contact?.firstName?.let {
                DisplayContactName(it, contact.lastName, contact.displayName)
            }
        }

        Column(
            modifier = Modifier.padding(all = 16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        )
        {
            //Display all phones and emails type of the contact
            contact?.phoneList?.let { DisplayContactTypes(list = it) }
            contact?.emailList?.let { DisplayContactTypes(list = it) }
        }
    }
}


@Composable
fun DisplayContactName(firstName: String, lastName: String, displayName: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = if (firstName.isNotEmpty() && lastName.isNotEmpty())
                "$firstName $lastName" else displayName,
            style = MaterialTheme.typography.h4
        )
    }
}

@Composable
fun DisplayContactTypes(list: HashMap<String, String>) {
    list.forEach { entry ->
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = entry.key,
                style = MaterialTheme.typography.h6
            )

            Text(
                text = entry.value,
                style = MaterialTheme.typography.h6
            )
        }
    }
}



