package com.project.contacts.screens


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.project.contacts.model.ContactsViewModel
import com.project.contacts.model.ContactNavigationViewModel



@Composable
fun EditContact(sharedContactViewModel: ContactNavigationViewModel,
                viewModel: ContactsViewModel,
                navController: NavHostController?
)
{

    val contact= sharedContactViewModel.contact

    var editableFirstName=contact?.firstName
    var editableLastName=contact?.lastName
    var editablePhone= ArrayList<String>()
    var editableEmail= ArrayList<String>()
    val emailHashMap= HashMap<String,String>()
    val phoneHashMap= HashMap<String,String>()


    Column {

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            contact?.firstName?.ifEmpty { contact.displayName }?.let {
                ContactPicture(
                    it,
                    contact.photo,
                    100.dp,
                    50.sp,
                    120.dp
                )
            }
        }
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.SpaceEvenly
        ){

            editableFirstName= contact?.let { editName(name = it.firstName, label = "First Name") }.toString()
            editableLastName= contact?.let { editName(name = it.lastName, label = "Last Name") }.toString()
            editablePhone= contact?.phoneList?.let { phoneList ->
                editCommunicationType(
                    list= phoneList,
                    typesArray =
                    LocalContext.current.resources.getStringArray(
                        com.project.contacts.R.array.phones
                    ),
                    SpinnerLabel = "Mobile",
                    textFieldLabel = "Phone"
                )
            } as ArrayList<String>
            editableEmail= editCommunicationType(
                list= contact.emailList,
                typesArray = LocalContext.current.resources.getStringArray(
                    com.project.contacts.R.array.emails
                ),
                SpinnerLabel = "Home",
                textFieldLabel = "Email"
            )
        }
        Row(modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement= Arrangement.SpaceAround
        ) {
            Button(onClick =
            {

                editableFirstName?.let { firstName ->
                    editableLastName?.let { lastName ->
                        viewModel.editContact(
                            firstName = firstName,
                            lastName = lastName,
                            email = if (editableEmail.isNotEmpty()) editableEmail[1] else "",
                            phone = if (editablePhone.isNotEmpty()) editablePhone[1] else "",
                            contactId =contact?.id.toString())
                    }
                }

                if (editableFirstName.toString().isNotEmpty())
                    contact?.firstName= editableFirstName.toString()

                if (editableLastName.toString().isNotEmpty())
                    contact?.lastName= editableLastName.toString()

                if (editableFirstName.toString().isNotEmpty()
                    || editableLastName.toString().isNotEmpty())
                    contact?.displayName=
                        editableFirstName.toString()+" "+editableLastName.toString()

                if (editableEmail.isNotEmpty()){
                    emailHashMap[editableEmail[0]] = editableEmail[1]
                    contact?.emailList= emailHashMap
                }

                if (editablePhone.isNotEmpty()){
                    phoneHashMap[editablePhone[0]] = editablePhone[1]
                    contact?.phoneList= phoneHashMap
                }

                sharedContactViewModel.updateContact(contact!!)
                navController?.popBackStack()
            }
            )
            {
                Text(text = "Save")
            }
            
            Button(onClick = { navController?.navigateUp()}) {
                Text(text = "Cancel")
            }
        }
    }
}

@Composable
fun editName(name:String, label: String): String{
    var editText by remember { mutableStateOf(TextFieldValue(name)) }
    OutlinedTextField(
        value = editText,
        label = { Text(text = label) },
        onValueChange = {
            editText = it
        },
        modifier = Modifier.padding(10.dp)
    )
    return editText.text
}



@Composable
fun editCommunicationType(
    list: HashMap<String, String>,
    typesArray: Array<String>,
    SpinnerLabel: String,
    textFieldLabel: String) : ArrayList<String>
{

    var selectedText by remember { mutableStateOf(SpinnerLabel) }
    var expanded by remember { mutableStateOf(false) }
    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.TopStart) {
        Row(Modifier
            .padding(10.dp)
            .clickable {
                expanded = !expanded
            }
            .padding(8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = selectedText, fontSize = 18.sp, modifier = Modifier.padding(end = 8.dp))
            Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = "")
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false })
            {
                typesArray.forEach { type ->
                    DropdownMenuItem(onClick =
                    {
                        selectedText = type
                        expanded = false
                    }
                    )
                    {
                        Text(text = type)
                    }
                }
            }

        }
    }
    
    var editTextField by remember { mutableStateOf(list[selectedText]) }
    Column(horizontalAlignment = Alignment.Start) {
        TextField(
            value = if(editTextField != null) editTextField!! else "",
            onValueChange = { value->
                editTextField=value
            },
            placeholder = { Text(text = textFieldLabel) },
            modifier = Modifier.padding(10.dp)
        )
    }

    val arrayList= ArrayList<String>()
    if(editTextField != null) {
        arrayList.add(selectedText)
        arrayList.add(editTextField!!)
    }

    return arrayList
}



